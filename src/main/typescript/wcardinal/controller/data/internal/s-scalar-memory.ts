/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Connectable } from "../../../event/connectable";
import { LoggerImpl as Logger } from "../../../util/internal/logger-impl";
import { Iteratee } from "../../../util/lang/each";
import { hasOwn } from "../../../util/lang/has-own";
import { isArray } from "../../../util/lang/is-array";
import { isNotEmptyArray } from "../../../util/lang/is-empty";
import { isEqual } from "../../../util/lang/is-equal";
import { isPlainObject } from "../../../util/lang/is-plain-object";
import { sizeObject } from "../../../util/lang/size";
import { checkNonNullValue } from "../../internal/check-non-null-value";
import { checkSupported } from "../../internal/check-supported";
import { Comparator } from "../../internal/comparator";
import { Properties } from "../../internal/properties";
import { ArrayMaps, DynamicInfoData, DynamicInfoDataMap } from "../../internal/settings";
import { Lock } from "../../lock";
import { EventArgumentList, EventArgumentMap, EventInfoDataMap } from "./event-info";
import { EVENT_CHANGE, EVENT_CHANGE_VALUE, EVENT_INIT, EVENT_VALUE } from "./event-name";
import { SScalarParentMemory } from "./s-scalar-parent-memory";
import { SType } from "./s-type";
import { WrapperConstructor } from "./wrapper-constructor";

export abstract class SScalarMemory<V = unknown> {
	private _wrapper: Connectable;

	private _parent: SScalarParentMemory;
	private _name: string;
	private _lock: Lock;

	private _isChanged = false;
	private _isInitialized = false;
	private _properties: Properties;

	private _type: SType;

	private _revision = 0;
	private _authorizedRevision = -1;
	private _sentRevision = -1;
	private _sentSenderId = -1;

	private _isEventStopped = false;

	private _value: V | null;
	private _forcibly = false;

	constructor(
		parent: SScalarParentMemory, name: string, properties: Properties,
		lock: Lock, wrapperConstructor: WrapperConstructor, type: SType
	) {
		this._wrapper = new wrapperConstructor( this );

		this._parent = parent;
		this._name = name;
		this._lock = lock;
		this._properties = properties;
		this._type = type;
		this._value = this.cast_( null );

		const wrapper = this.getWrapper_();
		wrapper.onon( EVENT_INIT, ( connection ) => {
			if( this._isInitialized ) {
				connection.triggerDirect( wrapper, null, [ null, this._value ] );
			}
		});

		wrapper.onon( EVENT_VALUE, ( connection ) => {
			if( this._isInitialized ) {
				const value = this._value;
				connection.triggerDirect( wrapper, null, [ null, value, value ] );
			}
		});
	}

	getWrapper_(): Connectable {
		return this._wrapper;
	}

	getType_(): SType {
		return this._type;
	}

	getName_(): string {
		return this._name;
	}

	startEvent_(): this {
		this._isEventStopped = true;
		return this;
	}

	isEventStopped_(): boolean {
		return this._isEventStopped;
	}

	stopEvent_(): this {
		this._isEventStopped = false;
		return this;
	}

	getRevision_(): number {
		return this._revision;
	}

	getAuthorizedRevision_(): number {
		return this._authorizedRevision;
	}

	setAuthorizedRevision_( authorizedRevision: number ) {
		this._authorizedRevision = Math.max(this._authorizedRevision, authorizedRevision);
	}

	resetAuthorizedRevision_() {
		this._authorizedRevision = -1;
		if( this._properties.isReadOnly_() ) {
			this._revision = 0;
		}
	}

	getSentSenderId_(): number {
		return this._sentSenderId;
	}

	setSentSenderId_( senderId: number ) {
		this._sentSenderId = senderId;
		this._sentRevision = this._revision;
	}

	resetSentSenderId_(): void {
		this._sentSenderId = -1;
		this.setAuthorizedRevision_( this._sentRevision );
	}

	getSentRevision_(): number {
		return this._sentRevision;
	}

	isReadOnly_(): boolean {
		return this._properties.isReadOnly_();
	}

	isNonNull_(): boolean {
		return this._properties.isNonNull_();
	}

	isLocal_(): boolean {
		return this._properties.isLocal_();
	}

	lock_(): void {
		this._lock.lock();
	}

	isLocked_(): boolean {
		return this._lock.isLocked();
	}

	unlock_(): void {
		// Propagate the change flag
		if( this._isChanged ) {
			this._isChanged = false;
			if( ! this._properties.isLocal_() ) {
				this._revision += 1;
				const parent = this._parent;
				if( parent != null ) {
					parent.update_();
				}
			}
		}

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

	initialize_(): void {
		this.lock_();
		try {
			if( ! this._isInitialized ) {
				this._isChanged = true;
				this._isInitialized = true;

				const value = this._value;
				this.pushEvent_( EVENT_INIT, [ null, value ]);
				this.pushEvent_( EVENT_VALUE, [ null, value, value ]);
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

	pack_( dynamicInfoDataMap: DynamicInfoDataMap | null, senderId: number ): DynamicInfoDataMap {
		this.setSentSenderId_( senderId );
		const packed: DynamicInfoData = [ this._revision, this.getType_(), this._value ];
		return ArrayMaps.put_( dynamicInfoDataMap, this._name, packed );
	}

	protected getAndSet_( newValue: V | null, forcibly: boolean, update: boolean ): V | null {
		const oldValue = this._value;

		if( ! this._isInitialized ) {
			this.pushEvent_( EVENT_INIT, [ null, newValue ]);
		}

		if( this._forcibly || forcibly || ! isEqual( newValue, oldValue ) ) {
			this._value = newValue;
			if( update ) {
				this._isChanged = true;
			}
			this._isInitialized = true;
			this.pushEvent_( EVENT_CHANGE_VALUE, [ null, newValue, oldValue ]);
		} else {
			if( ! this._isInitialized ) {
				this._isInitialized = true;
				this.pushEvent_( EVENT_VALUE, [ null, newValue, oldValue ]);
			}
		}

		return oldValue;
	}

	private unpack__( newValue: V | null, revision: number ): void {
		if( this.isNonNull_() && newValue == null ) {
			if( this._value == null ) {
				this.forceRevision_( revision );
			} else {
				this._isChanged = true;
			}
		} else {
			this.forceRevision_( revision );
			this.getAndSet_( newValue, false, false );
		}
	}

	unpack_( origin: unknown, value: unknown, revision: number ): void {
		this.lock_();
		try {
			this.unpack__( this.cast_(value), revision );
		} catch( e ) {
			Logger.getInstance().error( e );
			this._isChanged = true;
		} finally {
			this.unlock_();
		}
	}

	pushEvent_( name: string, args: unknown[] ): void {
		const parent = this._parent;
		if( parent != null ) {
			const eventInfo = parent.getEventInfo_();
			if( eventInfo != null ) {
				let eventInfoDataMap: EventInfoDataMap | undefined = eventInfo.data;
				if( eventInfoDataMap == null ) {
					eventInfoDataMap = {};
					eventInfo.data = eventInfoDataMap;
				}
				if( this._name in eventInfoDataMap ) {
					switch( name ) {
					case EVENT_INIT:
						this.pushEventInit_( eventInfoDataMap, args );
						break;
					case EVENT_VALUE:
						this.pushEventValue_( eventInfoDataMap, args );
						break;
					case EVENT_CHANGE_VALUE:
						this.pushEventChangeValue_( eventInfoDataMap, args );
						break;
					}
				} else {
					const nameToArgs: EventArgumentMap = {};
					nameToArgs[ name ] = args;
					eventInfoDataMap[ this._name ] = [ this, nameToArgs, [], false ];
				}
			}
		}
	}

	private pushEventInit_( eventInfoDataMap: EventInfoDataMap, args: unknown[] ): void {
		const eventData = eventInfoDataMap[ this._name ];
		const eventArgumentMap: EventArgumentMap = eventData[ 1 ];
		eventArgumentMap[ EVENT_INIT ] = args;
	}

	private pushEventValue_( eventInfoDataMap: EventInfoDataMap, args: unknown[] ): void {
		const eventData = eventInfoDataMap[ this._name ];
		const eventArgumentMap: EventArgumentMap = eventData[ 1 ];
		const valueArgs = eventArgumentMap[ EVENT_VALUE ];
		if( valueArgs != null ) {
			valueArgs[ 1 ] = args[ 1 ];
		} else {
			const changeValueArgs = eventArgumentMap[ EVENT_CHANGE_VALUE ];
			if( changeValueArgs != null ) {
				changeValueArgs[ 1 ] = args[ 1 ];
			} else {
				eventArgumentMap[ EVENT_VALUE ] = args;
			}
		}
	}

	private pushEventChangeValue_( eventInfoDataMap: EventInfoDataMap, args: unknown[] ): void {
		const eventData = eventInfoDataMap[ this._name ];
		const eventArgumentMap: EventArgumentMap = eventData[ 1 ];
		const valueArgs = eventArgumentMap[ EVENT_VALUE ];
		if( valueArgs != null ) {
			eventArgumentMap[ EVENT_CHANGE_VALUE ] = valueArgs;
			valueArgs[ 1 ] = args[ 1 ];
			delete eventArgumentMap[ EVENT_VALUE ];
		} else {
			const changeValueArgs = eventArgumentMap[ EVENT_CHANGE_VALUE ];
			if( changeValueArgs != null ) {
				changeValueArgs[ 1 ] = args[ 1 ];
			} else {
				eventArgumentMap[ EVENT_CHANGE_VALUE ] = args;
			}
		}
	}

	triggerEvents_( eventArgumentMap: EventArgumentMap, eventArgumentList: EventArgumentList, isFirst: boolean ): void {
		const wrapper = this.getWrapper_();

		const initArgs = eventArgumentMap[ EVENT_INIT ];
		if( initArgs != null ) {
			wrapper.triggerDirect( EVENT_INIT, null, initArgs, null );
		}

		const valueArgs = eventArgumentMap[ EVENT_VALUE ];
		if( valueArgs != null ) {
			wrapper.triggerDirect( EVENT_VALUE, null, valueArgs, null );
		}

		const changeValueArgs = eventArgumentMap[ EVENT_CHANGE_VALUE ];
		if( changeValueArgs != null ) {
			wrapper.triggerDirect( EVENT_CHANGE, null, changeValueArgs, null );
			wrapper.triggerDirect( EVENT_VALUE, null, changeValueArgs, null );
		}
	}

	size_(): number {
		const values = this._value;
		if( isArray( values ) ) {
			return values.length;
		} else if( isPlainObject( values ) ) {
			return sizeObject( values );
		} else if( values != null ) {
			return 1;
		}
		return 0;
	}

	isEmpty_(): boolean {
		return this.size_() <= 0;
	}

	isNull_(): boolean {
		return this._value == null;
	}

	isNotNull_(): boolean {
		return this._value != null;
	}

	get_(): V | null {
		return this._value;
	}

	set_( value: V | null, forcibly: boolean ): V | null {
		checkSupported( this );
		checkNonNullValue( this, value );

		this.lock_();
		try {
			return this.checkAndSet_( value, forcibly );
		} finally {
			this.unlock_();
		}
	}

	private checkAndSet_( value: unknown, forcibly: boolean ): V | null {
		const newValue = this.cast_( value );
		checkNonNullValue( this, newValue );
		return this.getAndSet_( newValue, forcibly, true );
	}

	reset_(): V | null {
		checkSupported( this );

		this.lock_();
		try {
			return this.reset__();
		} finally {
			this.unlock_();
		}
	}

	private reset__(): V | null {
		const value = this._value;

		if( ! this.isInitialized_() ) {
			this.forceInitilized_();
			this.pushEvent_( EVENT_INIT, [ null, value ] );
		}

		this._value = value;
		this._isChanged = true;
		this.pushEvent_( EVENT_CHANGE_VALUE, [ null, value, value ] );

		return value;
	}

	compareAndSet_( expected: V | null, update: V | null, forcibly: boolean ): boolean {
		checkSupported( this );
		checkNonNullValue( this, update );
		this.lock_();
		try {
			if( isEqual( expected, this._value ) ) {
				this.checkAndSet_( update, forcibly );
				return true;
			}
		} finally {
			this.unlock_();
		}
		return false;
	}

	equals_( value: V | null ): boolean {
		return isEqual( this._value, value );
	}

	compareTo_( other: V | null ): number {
		const value = this._value;
		if( value != null ) {
			if( other != null ) {
				return (value < other ? -1 : (value > other ? +1 : 0));
			} else {
				return +1;
			}
		} else {
			if( other != null ) {
				return -1;
			} else {
				return 0;
			}
		}
	}

	each_( iteratee: Iteratee<any, any, any>, thisArg: unknown, reverse: boolean ): void {
		const values = this._value;
		if( values == null ) {
			return;
		}

		const wrapper = this._wrapper;
		if( isArray(values) ) {
			if( reverse ) {
				for( let i = values.length - 1; 0 <= i; --i ) {
					if( iteratee.call( thisArg, values[ i ], i, wrapper ) === false ) {
						return;
					}
				}
			} else {
				for( let i = 0, imax = values.length; i < imax; ++i ) {
					if( iteratee.call( thisArg, values[ i ], i, wrapper ) === false ) {
						return;
					}
				}
			}
		} else if( isPlainObject( values ) ) {
			for( const key in values ) {
				if( hasOwn( values, key ) ) {
					if( iteratee.call( thisArg, values[ key ], key, wrapper ) === false ) {
						return;
					}
				}
			}
		} else if( values != null ) {
			if( iteratee.call( thisArg, values, null, wrapper ) === false ) {
				return;
			}
		}
	}

	find_( predicate: Iteratee<any, any, any>, thisArg: unknown, reverse: boolean ): unknown {
		let found = null;
		this.each_(( value, key, wrapper ) => {
			if( predicate.call( thisArg, value, key, wrapper ) === true ) {
				found = value;
				return false;
			}
			return true;
		}, null, reverse);
		return found;
	}

	indexOf_( value: unknown, comparator: Comparator, thisArg: unknown ): number {
		const values = this._value;
		if( isArray( values ) ) {
			for( let i = 0, imax = values.length; i < imax; ++i ) {
				if( comparator.call( thisArg, values[ i ], value) ) {
					return i;
				}
			}
		}
		return -1;
	}

	lastIndexOf_( value: unknown, comparator: Comparator, thisArg: unknown ): number {
		const values = this._value;
		if( isArray( values ) ) {
			for( let i = values.length - 1; 0 <= i; --i ) {
				if( comparator.call( thisArg, values[ i ], value) ) {
					return i;
				}
			}
		}
		return -1;
	}

	contains_( value: unknown, comparator: Comparator, thisArg: unknown ): boolean {
		let result = false;
		this.each_(( target ) => {
			if( comparator.call( thisArg, value, target ) ) {
				result = true;
				return false;
			}
			return true;
		}, thisArg, false );
		return result;
	}

	containsAll_( values: ArrayLike<unknown>, comparator: Comparator, thisArg: unknown): boolean {
		if( values != null && isNotEmptyArray( values ) ) {
			for( let i = 0, imax = values.length; i < imax; ++i ) {
				if( this.contains_( values[ i ], comparator, thisArg ) !== true ) {
					return false;
				}
			}
		}
		return true;
	}

	toDirty_(): void {
		checkSupported( this );
		this.lock_();
		try {
			this._isChanged = true;

			if( ! this._isInitialized ) {
				this._isInitialized = true;

				const value = this._value;
				this.pushEvent_( EVENT_INIT, [ null, value ] );
				this.pushEvent_( EVENT_VALUE, [ null, value, value ] );
			}
		} finally {
			this.unlock_();
		}
	}

	toJson_(): unknown {
		return this.get_();
	}

	fromJson_( json: unknown ): void {
		this.set_( this.cast_(json), false );
	}

	toString_(): string {
		return JSON.stringify( this.get_() );
	}

	fromString_( str: string ): void {
		this.fromJson_( JSON.parse( str ) );
	}

	forcibly_() {
		this._forcibly = true;
	}

	forceValue_( value: V | null ) {
		this._value = value;
	}

	forceToUpdate_( value: V | null ) {
		this._value = value;
		this._revision += 1;
	}

	forceRevision_( revision: number ) {
		this._revision = revision;
		this._authorizedRevision = revision;
	}

	forceInitilized_() {
		this._isInitialized = true;
	}

	abstract cast_( value: unknown ): V | null;
}
