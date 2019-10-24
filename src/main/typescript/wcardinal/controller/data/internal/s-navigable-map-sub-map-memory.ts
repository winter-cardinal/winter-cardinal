/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Connectable } from "../../../event/connectable";
import { Iteratee } from "../../../util/lang/each";
import { hasOwn } from "../../../util/lang/has-own";
import { PlainObject } from "../../../util/lang/plain-object";
import { sortedIndex } from "../../../util/lang/sorted-index";
import { checkArgument } from "../../internal/check-argument";
import { checkNoSuchElement } from "../../internal/check-no-such-element";
import { checkSubMapRange } from "../../internal/check-sub-map-range";
import { checkSupported } from "../../internal/check-supported";
import { Comparator } from "../../internal/comparator";
import { AddedMapItems, RemovedMapItems } from "../s-map";
import { SMapEntry } from "../s-map-entry";
import { EVENT_CHANGE, EVENT_INIT, EVENT_VALUE } from "./event-name";
import { SNavigableMapSubMapTarget } from "./s-navigable-map-sub-map-target";
import { ViewMemory } from "./view-memory";
import { WrapperConstructor } from "./wrapper-constructor";

const compare = ( a: string, b: string ): -1|0|1 => {
	if( a < b ) {
		return -1;
	} else if( a === b ) {
		return 0;
	} else {
		return +1;
	}
};

export class SNavigableMapSubMapMemory<V, W extends Connectable = Connectable>
	extends ViewMemory<SNavigableMapSubMapTarget<V>, W> {

	private _fromKey: string | null;
	private _includeFrom: boolean;
	private _toKey: string | null;
	private _includeTo: boolean;
	private _reverse: boolean;

	private _wrapperConstructor: WrapperConstructor<W>;

	constructor(
		target: SNavigableMapSubMapTarget<V>, wrapperConstructor: WrapperConstructor<W>,
		fromKey: string | null, includeFrom: boolean, toKey: string | null, includeTo: boolean, reverse: boolean
	) {
		super( target, wrapperConstructor );

		this._fromKey = fromKey;
		this._includeFrom = includeFrom;
		this._toKey = toKey;
		this._includeTo = includeTo;
		this._reverse = reverse;

		this._wrapperConstructor = wrapperConstructor;

		// Init/Value/Change event handling
		const wrapper = this.getWrapper_();
		const targetWrapper = target.getWrapper_();
		const onInitBound = ( e: unknown, mappings: PlainObject<V | null> ) => {
			wrapper.triggerDirect(
				EVENT_INIT, null,
				[ null, this.toMapping_( mappings ) ], null
			);
		};
		const onValueBound = ( e: unknown, added: AddedMapItems<V>, removed: RemovedMapItems<V> ) => {
			wrapper.triggerDirect(
				EVENT_VALUE, null,
				[ null, this.toMapping_( added ), this.toMapping_( removed ) ], null
			);
		};
		const onChangeBound = ( e: unknown, added: AddedMapItems<V>, removed: RemovedMapItems<V> ) => {
			wrapper.triggerDirect(
				EVENT_CHANGE, null,
				[ null, this.toMapping_( added ), this.toMapping_( removed ) ], null
			);
		};
		wrapper.onon( EVENT_INIT, ( connection, isFirst ) => {
			if( isFirst ) {
				targetWrapper.on( EVENT_INIT, onInitBound );
			} else if( this.isInitialized_() ) {
				connection.triggerDirect( this, null, [ null, this.toObject_() ] );
			}
		});
		wrapper.onon( EVENT_VALUE, ( connection, isFirst ) => {
			if( isFirst ) {
				targetWrapper.on( EVENT_VALUE, onValueBound );
			} else if( this.isInitialized_() ) {
				connection.triggerDirect( this, null, [ null, this.toObject_(), {} ] );
			}
		});
		wrapper.onon( EVENT_CHANGE, ( connection, isFirst ) => {
			if( isFirst ) {
				targetWrapper.on( EVENT_CHANGE, onChangeBound );
			}
		});
		wrapper.onoff( EVENT_INIT, ( connection, isLast ) => {
			if( isLast ) {
				targetWrapper.off( EVENT_INIT, onInitBound );
			}
		});
		wrapper.onoff( EVENT_VALUE, ( connection, isLast ) => {
			if( isLast ) {
				targetWrapper.off( EVENT_VALUE, onValueBound );
			}
		});
		wrapper.onoff( EVENT_CHANGE, ( connection, isLast ) => {
			if( isLast ) {
				targetWrapper.off( EVENT_CHANGE, onChangeBound );
			}
		});
	}

	private tooLow_( key: string, include: boolean ): boolean {
		if ( this._fromKey != null ) {
			if( key != null ) {
				const c = compare( key, this._fromKey );
				if ( c < 0 || (include && this._includeFrom !== true && c === 0) ) {
					return true;
				}
			} else {
				return true;
			}
		}
		return false;
	}

	private tooHigh_( key: string, include: boolean ): boolean {
		if ( this._toKey != null ) {
			if( key != null ) {
				const c = compare( key, this._toKey );
				if ( 0 < c || (include && this._includeTo !== true && c === 0) ) {
					return true;
				}
			} else {
				return true;
			}
		}
		return false;
	}

	private inRange_( key: string, include: boolean ): boolean {
		return !this.tooLow_( key, include ) && !this.tooHigh_( key, include );
	}

	private checkKeyArgument_( key: string, include: boolean ): void {
		checkArgument( this.inRange_( key, include ) );
	}

	private checkKeyArguments_( mappings: PlainObject ): void {
		for( const key in mappings ) {
			if( hasOwn( mappings, key ) ) {
				this.checkKeyArgument_( key, true );
			}
		}
	}

	private findFromIndex_(): number {
		const keys = this._target.getKeys_();
		const key = this._fromKey;
		if( key == null ) {
			return 0;
		} else {
			if( this._includeFrom === true ) {
				return sortedIndex( keys, key );
			} else {
				const index = sortedIndex( keys, key );
				for( let i = index, imax = keys.length; i < imax; ++i ) {
					if( keys[ i ] !== key ) {
						return i;
					}
				}
				return keys.length;
			}
		}
	}

	private findToIndex_(): number {
		const keys = this._target.getKeys_();
		const key = this._toKey;
		if( key == null ) {
			return keys.length;
		} else {
			if( this._includeTo !== true ) {
				return sortedIndex( keys, key );
			} else {
				const index = sortedIndex( keys, key );
				for( let i = index, imax = keys.length; i < imax; ++i ) {
					if( keys[ i ] !== key ) {
						return i;
					}
				}
				return keys.length;
			}
		}
	}

	private toMapping_( mapping: PlainObject<V | null> ): PlainObject<V | null> {
		const result: PlainObject<V | null> = {};
		for( const key in mapping ) {
			if( hasOwn( mapping, key ) && this.inRange_( key, true ) ) {
				result[ key ] = mapping[ key ];
			}
		}
		return result;
	}

	size_(): number {
		const fromIndex = this.findFromIndex_();
		const toIndex = this.findToIndex_();
		return toIndex - fromIndex;
	}

	isEmpty_(): boolean {
		return this.size_() <= 0;
	}

	containsKey_( key: string ): boolean {
		if( this.inRange_( key, true ) ) {
			return ( key in this._target.getValues_() );
		} else {
			return false;
		}
	}

	containsValue_( value: unknown, comparator: Comparator, thisArg: unknown ): boolean {
		const target = this._target;
		const values = target.getValues_();
		const keys = target.getKeys_();
		const fromIndex = this.findFromIndex_();
		const toIndex = this.findToIndex_();
		for( let i = fromIndex; i < toIndex; ++i ) {
			const key = keys[ i ];
			if( comparator.call( thisArg, values[ key ], value) ) {
				return true;
			}
		}
		return false;
	}

	each_( iteratee: Iteratee<string, V | null, any>, thisArg: unknown, reverse: boolean ): void {
		const target = this._target;
		const values = target.getValues_();
		const keys = target.getKeys_();
		const wrapper = this._wrapper;
		const fromIndex = this.findFromIndex_();
		const toIndex = this.findToIndex_();
		if( reverse ? !this._reverse : this._reverse ) {
			for( let i = toIndex - 1; fromIndex <= i; --i ) {
				const key = keys[ i ];
				if( iteratee.call( thisArg, values[ key ], key, wrapper ) === false ) {
					return;
				}
			}
		} else {
			for( let i = fromIndex; i < toIndex; ++i ) {
				const key = keys[ i ];
				if( iteratee.call( thisArg, values[ key ], key, wrapper ) === false ) {
					return;
				}
			}
		}
	}

	get_( key: string ): V | null {
		if( this.inRange_( key, true ) ) {
			const values = this._target.getValues_();
			if( key in values ) {
				return values[ key ];
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	filter_( iteratee: Iteratee<string, V | null, any>, thisArg: unknown ): void {
		const target = this._target;
		checkSupported( target );

		target.lock_();
		try {
			let isChanged = false;
			const values = target.getValues_();
			const keys = target.getKeys_();
			const wrapper = this._wrapper;

			const fromIndex = this.findFromIndex_();
			const toIndex = this.findToIndex_();

			const removed: RemovedMapItems<V> = {};
			const removedIndices = [];
			if( this._reverse ) {
				for( let i = toIndex - 1; fromIndex <= i; --i ) {
					const key = keys[ i ];
					const value = values[ key ];
					if( iteratee.call( thisArg, value, key, wrapper ) === true ) {
						continue;
					}
					removed[ key ] = value;
					removedIndices.push( i );
					isChanged = true;
				}
			} else {
				for( let i = fromIndex; i < toIndex; ++i ) {
					const key = keys[ i ];
					const value = values[ key ];
					if( iteratee.call( thisArg, value, key, wrapper ) === true ) {
						continue;
					}
					removed[ key ] = value;
					removedIndices.push( i );
					isChanged = true;
				}
			}

			if( isChanged ) {
				const eventData = target.toEventData_( null, removed, null );

				for( let i = removedIndices.length - 1; 0 <= i; --i ) {
					const index = removedIndices[ i ];
					const key = keys.splice( index, 1 )[ 0 ];
					delete values[ key ];
				}

				target.onRemoveAll_( removed );
				target.getAndIncrementRevision_();
				target.pushChangeEvent_( eventData[ 1 ], eventData );
			}
		} finally {
			target.unlock_();
		}
	}

	find_( predicate: Iteratee<string, V | null, any>, thisArg: unknown, reverse: boolean ): unknown {
		const target = this._target;
		const values = target.getValues_();
		const keys = target.getKeys_();
		const wrapper = this._wrapper;
		const fromIndex = this.findFromIndex_();
		const toIndex = this.findToIndex_();
		if( reverse ? !this._reverse : this._reverse ) {
			for( let i = toIndex - 1; fromIndex <= i; --i ) {
				const key = keys[ i ];
				if( predicate.call( thisArg, values[ key ], key, wrapper ) === true ) {
					return values[ key ];
				}
			}
		} else {
			for( let i = fromIndex; i < toIndex; ++i ) {
				const key = keys[ i ];
				if( predicate.call( thisArg, values[ key ], key, wrapper ) === true ) {
					return values[ key ];
				}
			}
		}
		return null;
	}

	values_(): Array<V | null> {
		const target = this._target;
		const result = [];
		const values = target.getValues_();
		const keys = target.getKeys_();
		const fromIndex = this.findFromIndex_();
		const toIndex = this.findToIndex_();
		if( this._reverse ) {
			for( let i = toIndex - 1; fromIndex <= i; --i ) {
				const key = keys[ i ];
				result.push( values[ key ] );
			}
		} else {
			for( let i = fromIndex; i < toIndex; ++i ) {
				const key = keys[ i ];
				result.push( values[ key ] );
			}
		}
		return result;
	}

	firstKey_(): string {
		return ( this._reverse ? this.lastKey__() : this.firstKey__() );
	}

	lastKey_(): string {
		return ( this._reverse ? this.firstKey__() : this.lastKey__() );
	}

	firstEntry_(): SMapEntry<V> | null {
		return ( this._reverse ? this.lastEntry__() : this.firstEntry__() );
	}

	lastEntry_(): SMapEntry<V> | null {
		return ( this._reverse ? this.firstEntry__() : this.lastEntry__() );
	}

	floorEntry_( key: string ): SMapEntry<V> | null {
		return ( this._reverse ? this.ceilingEntry__( key ) : this.floorEntry__( key ) );
	}

	ceilingEntry_( key: string ): SMapEntry<V> | null {
		return ( this._reverse ? this.floorEntry__( key ) : this.ceilingEntry__( key ) );
	}

	floorKey_( key: string ): string | null {
		return ( this._reverse ? this.ceilingKey__( key ) : this.floorKey__( key ) );
	}

	ceilingKey_( key: string ): string | null {
		return ( this._reverse ? this.floorKey__( key ) : this.ceilingKey__( key ) );
	}

	lowerEntry_( key: string ): SMapEntry<V> | null {
		return ( this._reverse ? this.higherEntry__( key ) : this.lowerEntry__( key ) );
	}

	higherEntry_( key: string ): SMapEntry<V> | null {
		return ( this._reverse ? this.lowerEntry__( key ) : this.higherEntry__( key ) );
	}

	lowerKey_( key: string ): string | null {
		return ( this._reverse ? this.higherKey__( key ) : this.lowerKey__( key ) );
	}

	higherKey_( key: string ): string | null {
		return ( this._reverse ? this.lowerKey__( key ) : this.higherKey__( key ) );
	}

	private firstKey__(): string {
		const fromIndex = this.findFromIndex_();
		const toIndex = this.findToIndex_();
		checkNoSuchElement( fromIndex < toIndex );
		return this._target.getKeys_()[ fromIndex ];
	}

	private lastKey__(): string {
		const fromIndex = this.findFromIndex_();
		const toIndex = this.findToIndex_();
		checkNoSuchElement( fromIndex < toIndex );
		return this._target.getKeys_()[ toIndex - 1 ];
	}

	private firstEntry__(): SMapEntry<V> | null {
		const fromIndex = this.findFromIndex_();
		const toIndex = this.findToIndex_();
		const target = this._target;
		const keys = target.getKeys_();
		const values = target.getValues_();
		if( fromIndex < toIndex ) {
			const key = keys[ fromIndex ];
			const value = values[ key ];
			return new SMapEntry( key, value );
		} else {
			return null;
		}
	}

	private lastEntry__(): SMapEntry<V> | null {
		const fromIndex = this.findFromIndex_();
		const toIndex = this.findToIndex_();
		const target = this._target;
		const keys = target.getKeys_();
		const values = target.getValues_();
		if( fromIndex < toIndex ) {
			const key = keys[ toIndex - 1 ];
			const value = values[ key ];
			return new SMapEntry( key, value );
		} else {
			return null;
		}
	}

	private floorEntry__( key: string ): SMapEntry<V> | null {
		const fromIndex = this.findFromIndex_();
		const toIndex = this.findToIndex_();
		const target = this._target;
		const keys = target.getKeys_();
		const values = target.getValues_();
		const index = sortedIndex( keys, key );
		if( fromIndex <= index && index < toIndex ) {
			let fkey = keys[ index ];
			if( fkey <= key ) {
				const value = values[ fkey ];
				return new SMapEntry( fkey, value );
			} else if( fromIndex <= index - 1 ) {
				fkey = keys[ index - 1 ];
				const value = values[ fkey ];
				return new SMapEntry( fkey, value );
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private ceilingEntry__( key: string ): SMapEntry<V> | null {
		const fromIndex = this.findFromIndex_();
		const toIndex = this.findToIndex_();
		const target = this._target;
		const keys = target.getKeys_();
		const values = target.getValues_();
		const index = sortedIndex( keys, key );
		if( fromIndex <= index && index < toIndex ) {
			const fkey = keys[ index ];
			const value = values[ fkey ];
			return new SMapEntry( fkey, value );
		} else {
			return null;
		}
	}

	private floorKey__( key: string ): string | null {
		const fromIndex = this.findFromIndex_();
		const toIndex = this.findToIndex_();
		const target = this._target;
		const keys = target.getKeys_();
		const index = sortedIndex( keys, key );
		if( fromIndex <= index && index < toIndex ) {
			const fkey = keys[ index ];
			if( fkey <= key ) {
				return fkey;
			} else if( fromIndex <= index - 1 ) {
				return keys[ index - 1 ];
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private ceilingKey__( key: string ): string | null {
		const fromIndex = this.findFromIndex_();
		const toIndex = this.findToIndex_();
		const target = this._target;
		const keys = target.getKeys_();
		const index = sortedIndex( keys, key );
		if( fromIndex <= index && index < toIndex ) {
			const fkey = keys[ index ];
			return fkey;
		} else {
			return null;
		}
	}

	private lowerEntry__( key: string ): SMapEntry<V> | null {
		const fromIndex = this.findFromIndex_();
		const target = this._target;
		const keys = target.getKeys_();
		const values = target.getValues_();
		const index = sortedIndex( keys, key );
		if( fromIndex <= index - 1 ) {
			const fkey = keys[ index - 1 ];
			const value = values[ fkey ];
			return new SMapEntry( fkey, value );
		} else {
			return null;
		}
	}

	private higherEntry__( key: string ): SMapEntry<V> | null {
		const toIndex = this.findToIndex_();
		const target = this._target;
		const keys = target.getKeys_();
		const values = target.getValues_();
		const index = sortedIndex( keys, key );
		if( index < toIndex ) {
			let fkey = keys[ index ];
			if( key < fkey ) {
				const value = values[ fkey ];
				return new SMapEntry( fkey, value );
			} else if( index + 1 < toIndex ) {
				fkey = keys[ index + 1 ];
				const value = values[ fkey ];
				return new SMapEntry( fkey, value );
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private lowerKey__( key: string ): string | null {
		const fromIndex = this.findFromIndex_();
		const target = this._target;
		const keys = target.getKeys_();
		const index = sortedIndex( keys, key );
		if( fromIndex <= index - 1 ) {
			return keys[ index - 1 ];
		} else {
			return null;
		}
	}

	private higherKey__( key: string ): string | null {
		const toIndex = this.findToIndex_();
		const target = this._target;
		const keys = target.getKeys_();
		const index = sortedIndex( keys, key );
		if( index < toIndex ) {
			const fkey = keys[ index ];
			if( key < fkey ) {
				return fkey;
			} else if( index + 1 < toIndex ) {
				return keys[ index + 1 ];
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	headMap_( toKey: string, inclusive= false ): SNavigableMapSubMapMemory<V, W> {
		this.checkKeyArgument_( toKey, inclusive );
		if( this._reverse ) {
			return new SNavigableMapSubMapMemory(
				this._target, this._wrapperConstructor, toKey, inclusive, this._toKey, this._includeTo, this._reverse
			);
		} else {
			return new SNavigableMapSubMapMemory(
				this._target, this._wrapperConstructor, this._fromKey, this._includeFrom, toKey, inclusive, this._reverse
			);
		}
	}

	tailMap_( fromKey: string, inclusive= true ): SNavigableMapSubMapMemory<V, W> {
		this.checkKeyArgument_( fromKey, inclusive );
		if( this._reverse ) {
			return new SNavigableMapSubMapMemory(
				this._target, this._wrapperConstructor, this._fromKey, this._includeFrom, fromKey, inclusive, this._reverse
			);
		} else {
			return new SNavigableMapSubMapMemory(
				this._target, this._wrapperConstructor, fromKey, inclusive, this._toKey, this._includeTo, this._reverse
			);
		}
	}

	subMap_( fromKey: string, includeFrom: boolean, toKey: string, includeTo: boolean ): SNavigableMapSubMapMemory<V, W> {
		this.checkKeyArgument_( fromKey, includeFrom );
		this.checkKeyArgument_( toKey, includeTo );
		if( this._reverse ) {
			checkSubMapRange( toKey, fromKey );
			return new SNavigableMapSubMapMemory(
				this._target, this._wrapperConstructor, toKey, includeTo, fromKey, includeFrom, this._reverse
			);
		} else {
			checkSubMapRange( fromKey, toKey );
			return new SNavigableMapSubMapMemory(
				this._target, this._wrapperConstructor, fromKey, includeFrom, toKey, includeTo, this._reverse
			);
		}
	}

	descendingMap_(): SNavigableMapSubMapMemory<V, W> {
		return new SNavigableMapSubMapMemory(
			this._target, this._wrapperConstructor,
			this._fromKey, this._includeFrom, this._toKey, this._includeTo, ! this._reverse
		);
	}

	put_( key: string, value: V | null ): V | null {
		this.checkKeyArgument_( key, true );
		return this._target.put_( key, value );
	}

	reput_( key: string ): V | null {
		this.checkKeyArgument_( key, true );
		return this._target.reput_( key );
	}

	putAll_( mappings: PlainObject<V | null> ): this {
		this.checkKeyArguments_( mappings );
		this._target.putAll_( mappings );
		return this;
	}

	remove_( key: string ): V | null {
		if( this.inRange_( key, false ) ) { // TODO: CHECK THIS
			return this._target.remove_( key );
		} else {
			return null;
		}
	}

	clear_(): void {
		const target = this._target;
		checkSupported( target );

		target.lock_();
		try {
			let isChanged = false;
			const keys = target.getKeys_();
			const values = target.getValues_();

			const fromIndex = this.findFromIndex_();
			const toIndex = this.findToIndex_();
			const removed: RemovedMapItems<V> = {};
			for( let i = toIndex - 1; fromIndex <= i; --i ) {
				const key = keys[ i ];
				const value = values[ key ];
				removed[ key ] = value;
				delete values[ key ];
				keys.splice( i, 1 );
				isChanged = true;
			}

			if( isChanged ) {
				const eventData = target.toEventData_( null, removed, null );
				target.onRemoveAll_( removed );
				target.getAndIncrementRevision_();
				target.pushChangeEvent_( eventData[ 1 ], eventData );
			}
		} finally {
			target.unlock_();
		}
	}

	toJson_(): PlainObject<V | null> {
		return this.toObject_();
	}

	fromJson_( json: unknown ): this {
		const plainObject = json as PlainObject<V | null>;
		this.checkKeyArguments_( plainObject );

		this._target.lock_();
		try {
			this.clear_();
			this.putAll_( plainObject );
		} finally {
			this._target.unlock_();
		}

		return this;
	}

	toObject_(): PlainObject<V | null> {
		const target = this._target;
		const result: PlainObject<V | null> = {};
		const values = target.getValues_();
		const keys = target.getKeys_();
		const fromIndex = this.findFromIndex_();
		const toIndex = this.findToIndex_();
		if( this._reverse ) {
			for( let i = toIndex - 1; fromIndex <= i; --i ) {
				const key = keys[ i ];
				result[ key ] = values[ key ];
			}
		} else {
			for( let i = fromIndex; i < toIndex; ++i ) {
				const key = keys[ i ];
				result[ key ] = values[ key ];
			}
		}
		return result;
	}

	toString_(): string {
		return JSON.stringify(this.toObject_());
	}
}
