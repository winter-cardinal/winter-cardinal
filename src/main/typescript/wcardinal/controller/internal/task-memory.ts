/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Connectable } from "../../event/connectable";
import { isNotEmptyArray } from "../../util/lang/is-empty";
import { isThenable } from "../../util/lang/is-thenable";
import { EventArgumentList, EventArgumentMap, EventInfoDataMap, EventOrigin } from "../data/internal/event-info";
import {
	EVENT_CALL, EVENT_CHANGE, EVENT_CREATE,
	EVENT_FAIL, EVENT_INIT, EVENT_SUCCESS, EVENT_VALUE
} from "../data/internal/event-name";
import { SArrayNodeMemory } from "../data/internal/s-array-node-memory";
import { SClassMemory } from "../data/internal/s-class-memory";
import { SLongMemory } from "../data/internal/s-long-memory";
import { SStringMemory } from "../data/internal/s-string-memory";
import { SArrayNode } from "../data/s-array-node";
import { SClass } from "../data/s-class";
import { SLong } from "../data/s-long";
import { SString } from "../data/s-string";
import { Lock } from "../lock";
import { checkNonNullValues } from "./check-non-null-values";
import { checkSupported } from "./check-supported";
import { newMemory } from "./new-memory";
import { Properties } from "./properties";
import { Property } from "./property";
import { TaskStaticData } from "./settings";
import { TaskParentMemory } from "./task-parent-memory";

const TASK_TYPE_TASK = "TASK";
const TASK_TYPE_CANCEL = "CANCEL";

const TASK_RESULT_TYPE_SUCCEEDED = "succeeded";
const TASK_RESULT_TYPE_CANCELED = "canceled";

const dummyEventOrigin: EventOrigin = {
	triggerEvents_() {
		// DO NOTHING
	}
};

export class TaskMemory<RESULT, ARGUMENTS extends unknown[]> {
	private _wrapper: Connectable;

	private _parent: TaskParentMemory;
	private _name: string;

	private _isInitialized = false;
	private _properties: Properties;

	private _$idn: string;
	private _$vtn: string;
	private _$vn: string;
	private _$pidn: string;
	private _$rtn: string;
	private _$rn: string;

	private _$id: SLongMemory;
	private _$vt: SClassMemory<string>;
	private _$v: SArrayNodeMemory;
	private _$pid: SLongMemory;
	private _$rt: SStringMemory;
	private _$r: SClassMemory<RESULT>;

	constructor(
		parent: TaskParentMemory, name: string, ssinfo: TaskStaticData,
		lock: Lock, makeWrapper: ( memory: TaskMemory<RESULT, ARGUMENTS> ) => Connectable
	) {
		this._wrapper = this.initWrapper_( makeWrapper( this ) );

		this._parent = parent;
		this._name = name;
		this._properties = new Properties( ssinfo[ 1 ] );

		const $idn  = this._$idn  = `$id@${name}`;
		const $vtn  = this._$vtn  = `$vt@${name}`;
		const $vn   = this._$vn   = `$v@${name}`;
		const $pidn = this._$pidn = `$pid@${name}`;
		const $rtn  = this._$rtn  = `$rt@${name}`;
		const $rn   = this._$rn   = `$r@${name}`;

		const cp = this._properties.clone_().retain_( Property.READ_ONLY | Property.LOCAL );
		const cpn = cp.clone_().add_( Property.NON_NULL );
		const $id  = this._$id  = newMemory( SLongMemory, SLong, parent, $idn, cpn, lock );
		const $vt  = this._$vt  = newMemory<SClassMemory<string>, TaskParentMemory, SClass<string>>(
			SClassMemory, SClass, parent, $vtn, cpn, lock
		);
		const $v   = this._$v   = newMemory( SArrayNodeMemory, SArrayNode, parent, $vn, cp, lock );
		const $pid = this._$pid = newMemory( SLongMemory, SLong, parent, $pidn, cpn, lock );
		const $rt  = this._$rt  = newMemory( SStringMemory, SString, parent, $rtn, cpn, lock );
		const $r   = this._$r   = newMemory<SClassMemory<RESULT>, TaskParentMemory, SClass<RESULT>>(
			SClassMemory, SClass, parent, $rn, cp, lock
		);

		parent.addDataEventHandler_(( nameToEvents ) => {
			this.dataEventHandler_( nameToEvents );
		});

		$vt.stopEvent_();
		if( this._properties.isHistorical_() ) {
			parent.putHistoricalData_( $vn, $v );
			parent.putDataHistoryHook_( $vn, ( _: unknown, part: string ) => {
				this.dataHistoryHook_( part );
			});
		} else {
			$v.stopEvent_();
		}
		$rt.stopEvent_();
		$r.stopEvent_();
	}

	private initWrapper_( wrapper: Connectable ) {
		wrapper.onon( EVENT_INIT, ( connection ) => {
			connection.triggerDirect( wrapper, null, [ null, wrapper ] );
		});

		wrapper.onon( EVENT_VALUE, ( connection ) => {
			connection.triggerDirect( wrapper, null, [ null, wrapper ] );
		});

		wrapper.onon( EVENT_SUCCESS, ( connection ) => {
			if( this.isDone_() ) {
				const type = this._$rt.get_();
				if( type != null ) {
					if( type === TASK_RESULT_TYPE_SUCCEEDED ) {
						connection.triggerDirect( wrapper, null, [ null, this._$r.get_(), wrapper ] );
					}
				}
			}
		});

		wrapper.onon( EVENT_FAIL, ( connection ) => {
			if( this.isDone_() ) {
				const type = this._$rt.get_();
				if( type != null ) {
					if( type !== TASK_RESULT_TYPE_SUCCEEDED ) {
						connection.triggerDirect( wrapper, null, [ null, type, wrapper ] );
					}
				}
			}
		});

		return wrapper;
	}

	getWrapper_() {
		return this._wrapper;
	}

	isReadOnly_(): boolean {
		return this._properties.isReadOnly_();
	}

	isNonNull_(): boolean {
		return this._properties.isNonNull_();
	}

	create_( args: ARGUMENTS ): void {
		checkSupported( this );
		checkNonNullValues( this, args );

		this._parent.lock_();
		try {
			this._$id.incrementAndGet_();
			this._$vt.set_( TASK_TYPE_TASK, false );
			this._$v.set_( args, true );
		} finally {
			this._parent.unlock_();
		}
	}

	dataHistoryHook_( part: string ): void {
		try {
			const str = decodeURIComponent( part );
			const args = JSON.parse( str );
			if( this._$v.equals_( args ) !== true ) {
				this.create_( args );
			}
		} catch( e ) {
			// DO NOTHING
		}
	}

	// Event
	dataEventHandler_( nameToEvents: EventInfoDataMap ): void {
		if( this._$idn in nameToEvents ) {
			nameToEvents[ this._$idn ][ 0 ] = dummyEventOrigin;
			this.dataEventHandlerId_( nameToEvents );
		}

		if( this._$pidn in nameToEvents ) {
			nameToEvents[ this._$pidn ][ 0 ] = dummyEventOrigin;
			this.dataEventHandlerPid_( nameToEvents );
		}
	}

	getEvents_( nameToEvents: EventInfoDataMap ): EventArgumentList {
		const name = this._name;
		if( name in nameToEvents ) {
			return nameToEvents[ name ][ 2 ];
		} else {
			const result: EventArgumentList = [];
			nameToEvents[ name ] = [ this, {}, result, false ];
			return result;
		}
	}

	dataEventHandlerId_( nameToEvents: EventInfoDataMap ) {
		if( this.isDone_() !== true ) {
			const type1 = this._$vt.get_();
			if( type1 != null ) {
				if( type1 === TASK_TYPE_TASK ) {
					const args = this._$v.get_();
					const result = this._$r.get_();
					const events = this.getEvents_( nameToEvents );

					events.push([EVENT_CREATE, [null, args, this._wrapper]]);
					const changeValueEventArgs = [null, args, result, this._wrapper];
					events.push([EVENT_CHANGE, changeValueEventArgs]);
					events.push([EVENT_VALUE, changeValueEventArgs]);
				}

				if( this._properties.isLocal_() ) {
					const id = this._$id.get_()!;
					const type2 = this._$vt.get_();
					if( type2 === TASK_TYPE_TASK ) {
						const args = [ null as unknown ].concat( this._$v.get_() );
						const results = this._wrapper.triggerDirect( EVENT_CALL, null, args, [] );
						if( isNotEmptyArray( results ) ) {
							const result = results[ 0 ];
							if( isThenable( result ) ) {
								result.then(
									( newResult: unknown ) => { this.completeWith_( id, newResult as (RESULT | null) ); },
									( reason: string ) => { this.cancelWith_( id, reason ); }
								);
							} else {
								this.completeWith_( id, result as (RESULT | null) );
							}
						}
					} else if( type2 === TASK_TYPE_CANCEL ) {
						this.cancelWith_( id, TASK_RESULT_TYPE_CANCELED );
					}
				}
			}
		}
	}

	dataEventHandlerPid_( nameToEvents: EventInfoDataMap ): void {
		if( this.isDone_() ) {
			const type = this._$rt.get_();
			if( type != null ) {
				const args = this._$v.get_();
				const result = this._$r.get_();
				const events = this.getEvents_( nameToEvents );
				if( type === TASK_RESULT_TYPE_SUCCEEDED ) {
					events.push([EVENT_SUCCESS, [null, result, this._wrapper]]);
				} else {
					events.push([EVENT_FAIL, [null, type, this._wrapper]]);
				}
				const changeValueEventArgs = [null, args, result, this._wrapper];
				events.push([EVENT_CHANGE, changeValueEventArgs]);
				events.push([EVENT_VALUE, changeValueEventArgs]);
			}
		}
	}

	completeWith_( id: number, result: RESULT | null ): void {
		if( this._$id.equals_( id ) ) {
			this._parent.lock_();
			try {
				this._$pid.set_( id, false );
				this._$rt.set_( TASK_RESULT_TYPE_SUCCEEDED, false );
				this._$r.set_( result, false );
			} finally {
				this._parent.unlock_();
			}
		}
	}

	cancel_(): void {
		checkSupported( this );

		this._parent.lock_();
		try {
			if( this._$id.equals_( this._$pid.get_() ) !== true ) {
				const type = this._$vt.get_();
				if( type === TASK_TYPE_TASK ) {
					this._$id.incrementAndGet_();
					this._$vt.set_( TASK_TYPE_CANCEL, false );
				}
			}
		} finally {
			this._parent.unlock_();
		}
	}

	cancelWith_( id: number, reason: string ): void {
		if( this._$id.equals_( id ) ) {
			this._parent.lock_();
			try {
				this._$pid.set_( id, false );
				this._$rt.set_( reason, false );
			} finally {
				this._parent.unlock_();
			}
		}
	}

	triggerEvents_( eventMap: EventArgumentMap, eventList: EventArgumentList, isFirst: boolean ) {
		for( let i = 0, imax = eventList.length; i < imax; ++i ) {
			const event = eventList[ i ];
			this._wrapper.triggerDirect( event[ 0 ], null, event[ 1 ], null );
		}
	}

	getArguments_(): ARGUMENTS {
		return this._$v.get_()! as ARGUMENTS;
	}

	getArgument_( index: number ): unknown {
		const args = this._$v.get_();
		if( args != null && 0 <= index && index < args.length ) {
			return args[ index ];
		} else {
			return null;
		}
	}

	getResult_(): RESULT | null {
		if( this._$rt.get_() === TASK_RESULT_TYPE_SUCCEEDED ) {
			return this._$r.get_();
		} else {
			return null;
		}
	}

	getReason_(): string | null {
		const type = this._$rt.get_();
		return ( (type != null && type !== TASK_RESULT_TYPE_SUCCEEDED) ? type : null );
	}

	isCanceled_(): boolean {
		if( this._$id.equals_( this._$pid.get_() ) ) {
			return ( this._$rt.get_() === TASK_RESULT_TYPE_CANCELED );
		} else {
			return false;
		}
	}

	isSucceeded_(): boolean {
		if( this._$id.equals_( this._$pid.get_() ) ) {
			return ( this._$rt.get_() === TASK_RESULT_TYPE_SUCCEEDED );
		} else {
			return false;
		}
	}

	isFailed_(): boolean {
		if( this._$id.equals_( this._$pid.get_() ) ) {
			const type = this._$rt.get_();
			if( type != null && type !== TASK_RESULT_TYPE_SUCCEEDED ) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	isDone_(): boolean {
		return this._$id.equals_( this._$pid.get_() );
	}

	uninitialize_(): void {
		this._$id.uninitialize_();
		this._$vt.uninitialize_();
		this._$v.uninitialize_();
		this._$pid.uninitialize_();
		this._$rt.uninitialize_();
		this._$r.uninitialize_();
	}

	isInitialized_(): boolean {
		return (
			this._$vt.isInitialized_() &&
			this._$v.isInitialized_() &&
			this._$pid.isInitialized_() &&
			this._$rt.isInitialized_() &&
			this._$r.isInitialized_()
		);
	}

	initialize_(): void {
		this._$id.initialize_();
		this._$vt.initialize_();
		this._$v.initialize_();
		this._$pid.initialize_();
		this._$rt.initialize_();
		this._$r.initialize_();
	}

	toJson_() {
		return {
			arguments: this.getArguments_(),
			result: this.getResult_(),
			isDone: this.isDone_(),
			isSucceeded: this.isSucceeded_(),
			reason: this.getReason_()
		};
	}

	toString_(): string {
		return JSON.stringify( this.toJson_() );
	}

	lock_(): void {
		this._parent.lock_();
	}

	isLocked_(): boolean {
		return this._parent.isLocked_();
	}

	unlock_(): void {
		this._parent.unlock_();
	}
}
