/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Connectable } from "../../../event/connectable";
import { PlainObject } from "../../../util/lang/plain-object";
import { newMemory } from "../../internal/new-memory";
import { Properties } from "../../internal/properties";
import { Property } from "../../internal/property";
import { ArrayMaps, DynamicInfoData, DynamicInfoDataMap } from "../../internal/settings";
import { Lock } from "../../lock";
import { SClass } from "../s-class";
import { SInteger } from "../s-integer";
import { SLong } from "../s-long";
import { EventArgumentList, EventArgumentMap, EventInfoDataMap } from "./event-info";
import { EVENT_CHANGE, EVENT_CHANGE_VALUE, EVENT_INIT, EVENT_VALUE } from "./event-name";
import { SClassMemory } from "./s-class-memory";
import { SContainerParentMemory } from "./s-container-parent-memory";
import { toSContainerId } from "./s-containers";
import { SIntegerMemory } from "./s-integer-memory";
import { SLongMemory } from "./s-long-memory";
import { SPatch } from "./s-patch";
import { SPatches } from "./s-patches";
import { WrapperConstructor } from "./wrapper-constructor";

const emptySet = function<T>( this: T ): T {
	// DO NOTHING
	return this;
};

export abstract class SContainerMemory<
	V = unknown, P extends SPatch = SPatch,
	PS extends SPatches<V, P> = SPatches<V, P>> {
	protected _values: V;
	private _wrapper: Connectable;

	private _parent: SContainerParentMemory;
	private _name: string;
	private _lock: Lock;

	private _isInitialized = false;
	private _properties: Properties;

	protected _patches: PS;
	private _revision = 0;
	private _authorizedRevision = -1;

	private _$arn: string;
	private _$afn: string;
	private _$an: string;
	private _$brn: string;
	private _$bfn: string;
	private _$bn: string;

	private _$ar: SLongMemory;
	private _$af: SIntegerMemory;
	private _$a: SClassMemory<unknown[]>;
	private _$br: SLongMemory;
	private _$bf: SIntegerMemory;
	private _$b: SClassMemory<unknown>;

	constructor(
		parent: SContainerParentMemory, name: string, properties: Properties,
		lock: Lock, wrapperConstructor: WrapperConstructor, patches: PS
	) {

		this._wrapper = this.initWrapper_( new wrapperConstructor( this ) );

		this._parent = parent;
		this._name = name;
		this._lock = lock;
		this._properties = properties;

		this._values = this.makeInitValues_();

		this._patches = patches;

		const $arn = this._$arn = toSContainerId( name, "ar" );
		const $afn = this._$afn = toSContainerId( name, "af" );
		const $an  = this._$an  = toSContainerId( name, "a" );
		const $brn = this._$brn = toSContainerId( name, "br" );
		const $bfn = this._$bfn = toSContainerId( name, "bf" );
		const $bn  = this._$bn  = toSContainerId( name, "b" );

		const cp = properties.clone_().retain_( Property.LOCAL );
		const cprn = cp.clone_().add_( Property.READ_ONLY | Property.NON_NULL );
		const cpr = cp.clone_().add_( Property.READ_ONLY );
		const cpn = cp.clone_().add_( Property.NON_NULL );
		const $ar = this._$ar = newMemory( SLongMemory, SLong, parent, $arn, cprn, lock );
		const $af = this._$af = newMemory( SIntegerMemory, SInteger, parent, $afn, cprn, lock );
		const $a  = this._$a  = newMemory<SClassMemory<unknown[]>, SContainerParentMemory, SClass<unknown[]>>(
			SClassMemory, SClass, parent, $an, cpr, lock
		);
		const $br = this._$br = newMemory( SLongMemory, SLong, parent, $brn, cpn, lock );
		const $bf = this._$bf = newMemory( SIntegerMemory, SInteger, parent, $bfn, cpn, lock );
		const $b  = this._$b  = newMemory<SClassMemory<unknown>, SContainerParentMemory, SClass<unknown>>(
			SClassMemory, SClass, parent, $bn, cp, lock
		);

		$ar.stopEvent_();
		$ar.forcibly_();
		$af.stopEvent_();
		$a.stopEvent_();

		$br.stopEvent_();
		$bf.stopEvent_();
		$b.stopEvent_();

		if( properties.isLocal_() ) {
			$b.set_ = emptySet;
		}

		$b.pack_ = ( dynamicInfoDataMap: DynamicInfoDataMap, senderId: number ): DynamicInfoDataMap => {
			$b.setSentSenderId_( senderId );
			if( $b.get_() != null && ! properties.isReadOnly_() ) {
				// $af
				const forceReset = ($af.get_() !== 0);
				if( forceReset ) {
					$af.forceToUpdate_( 0 );
					dynamicInfoDataMap = $af.pack_( dynamicInfoDataMap, senderId );
				}

				// $b
				const packed: DynamicInfoData = [
					$b.getRevision_(),
					$b.getType_(),
					this._patches.pack_(
						this._authorizedRevision, forceReset, this._revision,
						this._values, properties.isSoft_()
					)
				];
				return ArrayMaps.put_( dynamicInfoDataMap, $b.getName_(), packed );
			}
			return dynamicInfoDataMap;
		};

		parent.putDynamicInfoHandlerDataName_( $an, name );
		parent.putDynamicInfoHandlerDataName_( $arn, name );
		parent.putDynamicInfoHandlerDataName_( $afn, name );
		parent.putDynamicInfoHandler_( name, ( data ) => {
			return this.dynamicInfoHandler_( data );
		});
	}

	protected toUpdated_() {
		this._$b.set_( this._$b, true );
	}

	private initWrapper_( wrapper: Connectable ): Connectable {
		wrapper.onon( EVENT_INIT, ( connection ): void => {
			if( this._isInitialized ) {
				connection.triggerDirect( wrapper, null, this.newInitArgs_() );
			}
		});

		wrapper.onon( EVENT_VALUE, ( connection ): void => {
			if( this._isInitialized ) {
				connection.triggerDirect( wrapper, null, this.newValueArgs_( this.newInitArgs_() ) );
			}
		});

		return wrapper;
	}

	getWrapper_(): Connectable {
		return this._wrapper;
	}

	getRevision_(): number {
		return this._revision;
	}

	getAndIncrementRevision_(): number {
		return this._revision++;
	}

	isReadOnly_(): boolean {
		return this._properties.isReadOnly_();
	}

	isNonNull_(): boolean {
		return this._properties.isNonNull_();
	}

	lock_(): void {
		this._lock.lock();
	}

	isLocked_(): boolean {
		return this._lock.isLocked();
	}

	unlock_(): void {
		// Unlock
		const count = this._lock.getHoldCount();
		if( count <= 1 ) {
			const parent = this._parent;
			if( parent != null ) {
				parent.onUnlock_();
			} else {
				this._lock.unlock();
			}
		} else {
			// Otherwise
			this._lock.unlock();
		}
	}

	pushEvent_( name: string, args: unknown[] ): void {
		const parent = this._parent;
		if( parent != null ) {
			const eventInfo = parent.getEventInfo_();
			if( eventInfo != null ) {
				if( eventInfo.data == null ) {
					eventInfo.data = {};
				}
				this.pushEvent__( eventInfo.data, name, args );
			}
		}
	}

	private pushEvent__( eventInfoDataMap: EventInfoDataMap, name: string, args: unknown[] ): void {
		if( this._name in eventInfoDataMap ) {
			switch( name ) {
			case EVENT_INIT:
				this.pushEventInitEvent_( eventInfoDataMap, args );
				break;
			case EVENT_VALUE:
				this.pushEventValueEvent_( eventInfoDataMap, args );
				break;
			case EVENT_CHANGE_VALUE:
				this.pushEventChangeValueEvent_( eventInfoDataMap, args );
				break;
			}
		} else {
			const nameToArgs: EventArgumentMap = {};
			nameToArgs[ name ] = args;
			eventInfoDataMap[ this._name ] = [ this, nameToArgs, [], true ];
		}
	}

	private pushEventInitEvent_( eventInfoDataMap: EventInfoDataMap, args: unknown[] ): void {
		const eventData = eventInfoDataMap[ this._name ];
		const eventMap: EventArgumentMap = eventData[ 1 ];
		eventMap[ EVENT_INIT ] = args;
	}

	private pushEventValueEvent_( eventInfoDataMap: EventInfoDataMap, args: unknown[] ): void {
		const eventData = eventInfoDataMap[ this._name ];
		const eventMap: EventArgumentMap = eventData[ 1 ];
		const isFirst: boolean = eventData[ 3 ];
		const valueArgs = eventMap[ EVENT_VALUE ];
		if( valueArgs != null ) {
			this.mergeEvents_( args, valueArgs, isFirst );
			eventData[ 3 ] = false;
		} else {
			const changeValueArgs = eventMap[ EVENT_CHANGE_VALUE ];
			if( changeValueArgs != null ) {
				this.mergeEvents_( args, changeValueArgs, isFirst );
				eventData[ 3 ] = false;
			} else {
				eventMap[ EVENT_VALUE ] = args;
			}
		}
	}

	private pushEventChangeValueEvent_( eventInfoDataMap: EventInfoDataMap, args: unknown[] ): void {
		const eventData = eventInfoDataMap[ this._name ];
		const eventMap: EventArgumentMap = eventData[ 1 ];
		const isFirst: boolean = eventData[ 3 ];

		const valueArgs = eventMap[ EVENT_VALUE ];
		if( valueArgs != null ) {
			eventMap[ EVENT_CHANGE_VALUE ] = valueArgs;
			delete eventMap[ EVENT_VALUE ];
			this.mergeEvents_( args, valueArgs, isFirst );
			eventData[ 3 ] = false;
		} else {
			const changeValueArgs = eventMap[ EVENT_CHANGE_VALUE ];
			if( changeValueArgs != null ) {
				this.mergeEvents_( args, changeValueArgs, isFirst );
				eventData[ 3 ] = false;
			} else {
				eventMap[ EVENT_CHANGE_VALUE ] = args;
			}
		}
	}

	pushChangeEvent_( initArg: unknown | null, changeArgs: unknown[] ): void {
		if( this._isInitialized !== true ) {
			this._isInitialized = true;
			const initArgs = ( initArg != null ? [ null, initArg ] : this.newInitArgs_() );
			this.pushEvent_( EVENT_INIT, initArgs );
		}
		this.pushEvent_( EVENT_CHANGE_VALUE, changeArgs );
	}

	triggerEvents_( eventMap: EventArgumentMap, eventList: EventArgumentList, isFirst: boolean ): void {
		const initArgs = eventMap[ EVENT_INIT ];
		if( initArgs != null ) {
			this._wrapper.triggerDirect( EVENT_INIT, null, initArgs, null );
		}

		const valueArgs = eventMap[ EVENT_VALUE ];
		if( valueArgs != null ) {
			if( ! isFirst ) {
				this.onMergedEvent_( valueArgs );
			}
			this._wrapper.triggerDirect( EVENT_VALUE, null, valueArgs, null );
		}

		const changeValueArgs = eventMap[ EVENT_CHANGE_VALUE ];
		if( changeValueArgs != null ) {
			if( ! isFirst ) {
				this.onMergedEvent_( changeValueArgs );
			}
			this._wrapper.triggerDirect( EVENT_CHANGE, null, changeValueArgs, null );
			this._wrapper.triggerDirect( EVENT_VALUE, null, changeValueArgs, null );
		}
	}

	mergeEvents_( argsA: unknown[], argsB: unknown[], isFirst: boolean ): void {
		// DO NOTHING
	}

	onMergedEvent_( args: unknown[] ): void {
		// DO NOTHING
	}

	resetAuthorizedRevision_(): void {
		if( this._properties.isReadOnly_() ) {
			this._revision = 0;
			this._patches.clear_();
		}
	}

	private dynamicInfoHandler_( dataNames: PlainObject<null> ): string | null {
		const has$an = (this._$an in dataNames);
		let isNotRejected = true;
		if( has$an ) {
			isNotRejected = this.dynamicInfoHandlerAi_();
		}
		if( isNotRejected ) {
			if( this._$arn in dataNames ) {
				this.dynamicInfoHandlerAr_();
			}

			if( this._$afn in dataNames ) {
				this.dynamicInfoHandlerAf_();
			}

			if( has$an ) {
				this.dynamicInfoHandlerA_();
			}

			return null;
		} else {
			return this._$bfn;
		}
	}

	private dynamicInfoHandlerAi_() {
		const apatches = this._$a.get_() as unknown[];
		if( apatches != null ) {
			const startRevision = apatches[ 0 ] as number;
			const endRevision = startRevision + (apatches[ 1 ] as number);

			if( this.isPatchApplicable_( apatches, this._revision, this._patches.isEmpty_() ) ) {
				this._$bf.compareAndSet_( 1, 0, false );
			} else {
				if( this._revision <= endRevision ) {
					this._$bf.compareAndSet_( 0, 1, false );
					return false;
				}
			}
		}
		return true;
	}

	private dynamicInfoHandlerAr_() {
		const arevision = this._$ar.get_();
		if( arevision != null && arevision <= this._revision ) {
			this._patches.compact_( arevision );
			this._authorizedRevision = Math.max(this._authorizedRevision, arevision);
			if( arevision < this._revision ) {
				this._$b.set_( this._$b, true );
			}
		}
	}

	private dynamicInfoHandlerAf_() {
		const aflag = this._$af.get_();
		if( aflag !== 0 ) {
			this._$b.set_( this._$b, true );
		}
	}

	private dynamicInfoHandlerA_() {
		const apatches = this._$a.get_() as unknown[];
		this._$a.forceValue_( null );
		if( apatches == null ) {
			return;
		}

		const startRevision = apatches[ 0 ] as number;
		const endRevision = startRevision + ( apatches[ 1 ] as number );

		if( this._revision <= endRevision ) {
			if( this.isPatchEmpty_( apatches ) ) {
				this._patches.clear_();
				this._revision = endRevision;
				this._authorizedRevision = Math.max(this._authorizedRevision, endRevision);

				if( this._$br.get_() !== endRevision ) {
					this._$br.set_( endRevision, true );
				}

				if( this._isInitialized !== true ) {
					this._isInitialized = true;
					const initArgs = this.newInitArgs_();
					this.pushEvent_( EVENT_INIT, initArgs );
					this.pushEvent_( EVENT_VALUE, this.newValueArgs_( initArgs ) );
				}
			} else {
				const patchResult = this.patch_( apatches );
				this._patches.clear_();
				this._revision = endRevision;
				this._authorizedRevision = Math.max(this._authorizedRevision, endRevision);

				if( this._$br.get_() !== endRevision ) {
					this._$br.set_( endRevision, true );
				}

				let initArgs = null;
				if( this._isInitialized !== true ) {
					this._isInitialized = true;
					initArgs = this.newInitArgs_();
					this.pushEvent_( EVENT_INIT, initArgs );
				}
				if( patchResult != null ) {
					this.pushEvent_( EVENT_CHANGE_VALUE, patchResult );
				} else {
					if( initArgs != null ) {
						this.pushEvent_( EVENT_VALUE, this.newValueArgs_( initArgs ) );
					}
				}
			}
		}

		apatches.length = 0;
	}

	isPatchApplicable_( patches: unknown[], revision: number, hasNoPatches: boolean ): boolean {
		const startRevision = patches[ 0 ];
		const isReset = ( patches[ 2 ] === 0 );
		return ( ( hasNoPatches && startRevision === revision ) || isReset );
	}

	isPatchEmpty_( patches: unknown[] ): boolean {
		const type = patches[ 2 ];
		if( type === 0 ) {
			return false;
		} else {
			return patches.length <= 3;
		}
	}

	initialize_(): void {
		this.lock_();
		try {
			if( this._isInitialized !== true ) {
				this._isInitialized = true;
				this._$b.toDirty_();

				const initArgs = this.newInitArgs_();
				this.pushEvent_( EVENT_INIT, initArgs );
				this.pushEvent_( EVENT_VALUE, this.newValueArgs_( initArgs ) );
			}
		} finally {
			this.unlock_();
		}
	}

	isInitialized_(): boolean {
		return this._isInitialized;
	}

	uninitialize_(): void {
		this._isInitialized = false;
		if( this._parent != null ) {
			this._parent.uninitialize_();
		}
	}

	toString_(): string {
		return JSON.stringify( this._values );
	}

	fromString_( str: string ): void {
		this.fromJson_( JSON.parse( str ) );
	}

	protected abstract makeInitValues_(): V;
	protected abstract newInitArgs_(): unknown[];
	protected abstract newValueArgs_( initArgs: unknown[] ): unknown[];
	abstract patch_( patches: unknown[] ): unknown[] | null;
	abstract toJson_(): unknown;
	abstract fromJson_( json: unknown ): void;
}
