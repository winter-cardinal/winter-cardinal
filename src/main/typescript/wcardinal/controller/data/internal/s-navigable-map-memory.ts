/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Connectable } from "../../../event/connectable";
import { Iteratee } from "../../../util/lang/each";
import { hasOwn } from "../../../util/lang/has-own";
import { isEmptyObject, isNotEmptyArray } from "../../../util/lang/is-empty";
import { isEqual } from "../../../util/lang/is-equal";
import { PlainObject } from "../../../util/lang/plain-object";
import { sortedIndex } from "../../../util/lang/sorted-index";
import { checkNoSuchElement } from "../../internal/check-no-such-element";
import { checkSubMapRange } from "../../internal/check-sub-map-range";
import { Properties } from "../../internal/properties";
import { Lock } from "../../lock";
import { AddedMapItems, RemovedMapItems, UpdatedMapItems } from "../s-map";
import { SMapEntry } from "../s-map-entry";
import { SContainerParentMemory } from "./s-container-parent-memory";
import { SMapBaseMemory } from "./s-map-base-memory";
import { SNavigableMapPatch } from "./s-navigable-map-patch";
import { SNavigableMapPatches } from "./s-navigable-map-patches";
import { SNavigableMapSubMapMemory } from "./s-navigable-map-sub-map-memory";
import { SNavigableMapSubMapTarget } from "./s-navigable-map-sub-map-target";
import { SType } from "./s-type";
import { WrapperConstructor } from "./wrapper-constructor";

const patchReset = <V>(
	values: PlainObject<V | null>,
	map: PlainObject<V | null>, keys: string[],
	added: AddedMapItems<V>, removed: RemovedMapItems<V>, updated: UpdatedMapItems<V>
): void => {
	for( const key in map ) {
		const oldValue = map[ key ];
		if( key in values ) {
			const newValue = values[ key ];
			if( isEqual( newValue, oldValue ) !== true ) {
				updated[ key ] = { newValue, oldValue };
				map[ key ] = newValue;
			}
		} else {
			removed[ key ] = oldValue;
			delete map[ key ];
			keys.splice( sortedIndex( keys, key ), 1 );
		}
	}

	for( const key in values ) {
		if( ! ( key in map ) ) {
			map[ key ] = added[ key ] = values[ key ];
			keys.splice( sortedIndex( keys, key ), 0, key );
		}
	}
};

const patchMap = <V>(
	cadded: AddedMapItems<V>, cremoved: string[], map: PlainObject<V | null>, keys: string[],
	padded: AddedMapItems<V>, premoved: RemovedMapItems<V>, pupdated: UpdatedMapItems<V>
): void => {
	let isDirty = false;

	// Removed
	for( let i = 0, imax = cremoved.length; i < imax; ++i ) {
		const key = cremoved[ i ];
		if( key in map ) {
			const value = map[ key ];
			delete map[ key ];
			delete pupdated[ key ];
			isDirty = true;
			if( key in padded ) {
				delete padded[ key ];
			} else {
				premoved[ key ] = value;
			}
		}
	}

	// Added
	for( const key in cadded ) {
		const newValue = cadded[ key ];
		if( key in map ) {
			const oldValue = map[ key ];
			const update = pupdated[ key ];
			if( update != null ) {
				update.newValue = newValue;
			} else {
				pupdated[ key ] = { newValue, oldValue };
			}
		} else {
			padded[ key ] = newValue;
			isDirty = true;
		}
		map[ key ] = newValue;
	}

	// Sort keys
	if( isDirty ) {
		let i = 0;
		for( const key in map ) {
			keys[ i++ ] = key;
		}
		keys.length = i;
		keys.sort();
	}
};

export class SNavigableMapMemory<V> extends SMapBaseMemory<V, SNavigableMapPatch<V>, SNavigableMapPatches<V>>
	implements SNavigableMapSubMapTarget<V> {

	private _keys: string[];
	private _reverse: boolean;

	constructor(
		parent: SContainerParentMemory, name: string, properties: Properties,
		lock: Lock, wrapperConstructor: WrapperConstructor, type: SType
	) {
		super( parent, name, properties, lock, wrapperConstructor, new SNavigableMapPatches( properties ) );

		this._keys = [];
		this._reverse = (type === SType.DESCENDING_MAP);
	}

	getValues_(): PlainObject<V | null> {
		return this._values;
	}

	getKeys_(): string[] {
		return this._keys;
	}

	size_(): number {
		return this._keys.length;
	}

	each_( iteratee: Iteratee<string, V | null, any>, thisArg: unknown, reverse: boolean= false ): void {
		const values = this._values;
		const keys = this._keys;
		const wrapper = this.getWrapper_();
		if( reverse ? !this._reverse : this._reverse ) {
			for( let i = keys.length - 1; 0 <= i; --i ) {
				const key = keys[ i ];
				if( iteratee.call( thisArg, values[ key ], key, wrapper ) === false ) {
					return;
				}
			}
		} else {
			for( let i = 0, imax = keys.length; i < imax; ++i ) {
				const key = keys[ i ];
				if( iteratee.call( thisArg, values[ key ], key, wrapper ) === false ) {
					return;
				}
			}
		}
	}

	find_( predicate: Iteratee<string, V | null, any>, thisArg: unknown, reverse: boolean = false ): unknown {
		const values = this._values;
		const keys = this._keys;
		const wrapper = this.getWrapper_();
		if( reverse ? !this._reverse : this._reverse ) {
			for( let i = keys.length - 1; 0 <= i; --i ) {
				const key = keys[ i ];
				if( predicate.call( thisArg, values[ key ], key, wrapper ) === true ) {
					return values[ key ];
				}
			}
		} else {
			for( let i = 0, imax = keys.length; i < imax; ++i ) {
				const key = keys[ i ];
				if( predicate.call( thisArg, values[ key ], key, wrapper ) === true ) {
					return values[ key ];
				}
			}
		}
		return null;
	}

	values_(): Array<V | null> {
		const result = [];
		const values = this._values;
		const keys = this._keys;
		if( this._reverse ) {
			for( let i = keys.length - 1; 0 <= i; --i ) {
				const key = keys[ i ];
				result.push( values[ key ] );
			}
		} else {
			for( let i = 0, imax = keys.length; i < imax; ++i ) {
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

	headMap_<T extends Connectable>(
		toKey: string, inclusive: boolean, wrapperConstructor: WrapperConstructor<T>
	): SNavigableMapSubMapMemory<V, T> {
		if( this._reverse ) {
			return new SNavigableMapSubMapMemory(
				this, wrapperConstructor, toKey, inclusive, null, false, this._reverse
			);
		} else {
			return new SNavigableMapSubMapMemory(
				this, wrapperConstructor, null, true, toKey, inclusive, this._reverse
			);
		}
	}

	tailMap_<T extends Connectable>(
		fromKey: string, inclusive: boolean, wrapperConstructor: WrapperConstructor<T>
	): SNavigableMapSubMapMemory<V, T> {
		if( this._reverse ) {
			return new SNavigableMapSubMapMemory(
				this, wrapperConstructor, null, true, fromKey, inclusive, this._reverse
			);
		} else {
			return new SNavigableMapSubMapMemory(
				this, wrapperConstructor, fromKey, inclusive, null, false, this._reverse
			);
		}
	}

	subMap_<T extends Connectable>(
		fromKey: string, includeFrom: boolean, toKey: string, includeTo: boolean, wrapperConstructor: WrapperConstructor<T>
	): SNavigableMapSubMapMemory<V, T> {
		if( this._reverse ) {
			checkSubMapRange( toKey, fromKey );
			return new SNavigableMapSubMapMemory(
				this, wrapperConstructor, toKey, includeTo, fromKey, includeFrom, this._reverse
			);
		} else {
			checkSubMapRange( fromKey, toKey );
			return new SNavigableMapSubMapMemory(
				this, wrapperConstructor, fromKey, includeFrom, toKey, includeTo, this._reverse
			);
		}
	}

	descendingMap_<T extends Connectable>( wrapperConstructor: WrapperConstructor<T> ): SNavigableMapSubMapMemory<V, T> {
		return new SNavigableMapSubMapMemory( this, wrapperConstructor, null, true, null, false, ! this._reverse );
	}

	private firstKey__( this: SNavigableMapMemory<V> ): string {
		const keys = this._keys;

		checkNoSuchElement( isNotEmptyArray( keys ) );
		return keys[ 0 ];
	}

	private lastKey__(): string {
		const keys = this._keys;

		checkNoSuchElement( isNotEmptyArray( keys ) );
		return keys[ keys.length - 1 ];
	}

	private firstEntry__(): SMapEntry<V> | null {
		const values = this._values;
		const keys = this._keys;

		if( isNotEmptyArray( keys ) ) {
			const key = keys[ 0 ];
			const value = values[ key ];
			return new SMapEntry( key, value );
		} else {
			return null;
		}
	}

	private lastEntry__(): SMapEntry<V> | null {
		const values = this._values;
		const keys = this._keys;

		if( isNotEmptyArray( keys ) ) {
			const key = keys[ keys.length - 1 ];
			const value = values[ key ];
			return new SMapEntry( key, value );
		} else {
			return null;
		}
	}

	private floorEntry__( key: string ): SMapEntry<V> | null {
		const values = this._values;
		const keys = this._keys;

		const index = sortedIndex( keys, key );
		if( index < keys.length ) {
			let fkey = keys[ index ];
			if( fkey <= key ) {
				const value = values[ fkey ];
				return new SMapEntry( fkey, value );
			} else if( 0 <= index - 1 ) {
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
		const values = this._values;
		const keys = this._keys;

		const index = sortedIndex( keys, key );
		if( index < keys.length ) {
			const fkey = keys[ index ];
			const value = values[ fkey ];
			return new SMapEntry<V>( fkey, value );
		} else {
			return null;
		}
	}

	private floorKey__( key: string ): string | null {
		const keys = this._keys;

		const index = sortedIndex( keys, key );
		if( index < keys.length ) {
			const fkey = keys[ index ];
			if( fkey <= key ) {
				return fkey;
			} else if( 0 <= index - 1 ) {
				return keys[ index - 1 ];
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private ceilingKey__( key: string ): string | null {
		const keys = this._keys;

		const index = sortedIndex( keys, key );
		if( index < keys.length ) {
			return keys[ index ];
		} else {
			return null;
		}
	}

	private lowerEntry__( key: string ): SMapEntry<V> | null {
		const values = this._values;
		const keys = this._keys;

		const index = sortedIndex( keys, key );
		if( 0 <= index - 1 ) {
			const fkey = keys[ index - 1 ];
			const value = values[ fkey ];
			return new SMapEntry( fkey, value );
		} else {
			return null;
		}
	}

	private higherEntry__( key: string ): SMapEntry<V> | null {
		const values = this._values;
		const keys = this._keys;

		const index = sortedIndex( keys, key );
		if( index < keys.length ) {
			let fkey = keys[ index ];
			if( key < fkey ) {
				const value = values[ fkey ];
				return new SMapEntry( fkey, value );
			} else if( index + 1 < keys.length ) {
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
		const keys = this._keys;

		const index = sortedIndex( keys, key );
		if( 0 <= index - 1 ) {
			return keys[ index - 1 ];
		} else {
			return null;
		}
	}

	private higherKey__( key: string ): string | null {
		const keys = this._keys;
		const index = sortedIndex( keys, key );
		if( index < keys.length ) {
			const fkey = keys[ index ];
			if( key < fkey ) {
				return fkey;
			} else if( index + 1 < keys.length ) {
				return keys[ index + 1 ];
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	protected filter__( iteratee: Iteratee<string, any, any>, thisArg: unknown ): void {
		let isChanged = false;
		const values = this._values;
		const keys = this._keys;
		const wrapper = this.getWrapper_();

		const removed: RemovedMapItems<V> = {};
		const removedIndices = [];
		if( this._reverse ) {
			for( let i = keys.length - 1; 0 <= i; --i ) {
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
			for( let i = 0, imax = keys.length; i < imax; ++i ) {
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
			const eventData = this.toEventData_( null, removed, null );

			for( let i = removedIndices.length - 1; 0 <= i; --i ) {
				const index = removedIndices[ i ];
				const key = keys.splice( index, 1 )[ 0 ];
				delete values[ key ];
			}

			this.onRemoveAll_( removed );
			this.getAndIncrementRevision_();
			this.pushChangeEvent_( eventData[ 1 ], eventData );
		}
	}

	protected put__( key: string, value: V | null ): V | null {
		const values = this._values;
		const keys = this._keys;

		let eventData = null;
		let oldValue = null;
		const index = sortedIndex( keys, key );
		if( index < keys.length && keys[ index ] === key ) {
			oldValue = values[ key ];
			const updated: UpdatedMapItems<V> = {};
			updated[ key ] = { newValue: value, oldValue };
			eventData = this.toEventData_( null, null, updated );
		} else {
			keys.splice( index, 0, key );
			const added: AddedMapItems<V> = {};
			added[ key ] = value;
			eventData = this.toEventData_( added, null, null );
		}

		values[ key ] = value;

		this.onPut_( key, value );
		this.getAndIncrementRevision_();
		this.pushChangeEvent_( eventData[ 1 ], eventData );

		return oldValue;
	}

	protected reput__( key: string ): V | null {
		const values = this._values;
		const keys = this._keys;

		let value = null;
		const index = sortedIndex( keys, key );
		if( index < keys.length && keys[ index ] === key ) {
			value = values[ key ];
			const updated: UpdatedMapItems<V> = {};
			updated[ key ] = { newValue: value, oldValue: value };
			const eventData = this.toEventData_( null, null, updated );
			this.onPut_( key, value );
			this.getAndIncrementRevision_();
			this.pushChangeEvent_( eventData[ 1 ], eventData );
		}

		return value;
	}

	protected putAll__( mappings: PlainObject<V | null> ): void {
		let isChanged = false;
		const values = this._values;
		const keys = this._keys;

		const added: AddedMapItems<V> = {};
		const updated: UpdatedMapItems<V> = {};
		for( const key in mappings ) {
			if( hasOwn(mappings, key) ) {
				const newValue = mappings[ key ];

				const index = sortedIndex( keys, key );
				if( index < keys.length && keys[ index ] === key ) {
					updated[ key ] = { newValue, oldValue: values[ key ] };
				} else {
					keys.splice( index, 0, key );
					added[ key ] = newValue;
				}

				values[ key ] = newValue;
				isChanged = true;
			}
		}

		if( isChanged ) {
			const eventData = this.toEventData_( added, null, updated );
			this.onPutAll_( mappings );
			this.getAndIncrementRevision_();
			this.pushChangeEvent_( eventData[ 1 ], eventData );
		}
	}

	protected clearAndPut__( key: string, value: V | null ): null {
		const values = this._values;
		const keys = this._keys;

		const removed: RemovedMapItems<V> = {};
		for( const rkey in values ) {
			if( rkey !== key ) {
				removed[ rkey ] = values[ rkey ];
				delete values[ rkey ];
			}
		}
		keys.splice( 0, keys.length );

		const added: AddedMapItems<V> = {};
		const updated: UpdatedMapItems<V> = {};
		if( key in values ) {
			updated[ key ] = { newValue: value, oldValue: values[ key ] };
		} else {
			added[ key ] = value;
		}
		keys.push( key );
		values[ key ] = value;

		const eventData = this.toEventData_( added, removed, updated );
		this.onClear_();
		this.onPut_( key, value );
		this.getAndIncrementRevision_();
		this.pushChangeEvent_( eventData[ 1 ], eventData );

		return null;
	}

	protected clearAndPutAll__( mappings: PlainObject<V | null> ): void {
		let isChanged = false;
		const values = this._values;
		const keys = this._keys;

		const removed: RemovedMapItems<V> = {};
		for( const key in values ) {
			if( !(key in mappings) ) {
				removed[ key ] = values[ key ];
				delete values[ key ];
				isChanged = true;
			}
		}
		keys.splice( 0, keys.length );

		const added: AddedMapItems<V> = {};
		const updated: UpdatedMapItems<V> = {};
		for( const key in mappings ) {
			if( hasOwn(mappings, key) ) {
				const newValue = mappings[ key ];
				const index = sortedIndex( keys, key );
				keys.splice( index, 0, key );
				if( key in values ) {
					updated[ key ] = { newValue, oldValue: values[ key ] };
				} else {
					added[ key ] = newValue;
				}
				values[ key ] = newValue;
				isChanged = true;
			}
		}

		if( isChanged ) {
			const eventData = this.toEventData_( added, removed, updated );
			this.onClear_();
			this.onPutAll_( mappings );
			this.getAndIncrementRevision_();
			this.pushChangeEvent_( eventData[ 1 ], eventData );
		}
	}

	protected remove__( key: string ): V | null {
		let result = null;

		const removed: RemovedMapItems<V> = {};
		const keys = this._keys;
		const index = sortedIndex( keys, key );
		if( index < keys.length && keys[ index ] === key ) {
			result = removed[ key ] = this._values[ key ];
			delete this._values[ key ];
			keys.splice( index, 1 );

			const eventData = this.toEventData_( null, removed, null );
			this.onRemove_( key );
			this.getAndIncrementRevision_();
			this.pushChangeEvent_( eventData[ 1 ], eventData );
		}

		return result;
	}

	protected clear__(): void {
		let isChanged = false;
		const removed: RemovedMapItems<V> = {};
		const values = this._values;
		for( const key in values ) {
			removed[ key ] = values[ key ];
			delete values[ key ];
			isChanged = true;
		}
		this._keys.splice( 0, this._keys.length );

		if( isChanged ) {
			const eventData = this.toEventData_( null, removed, null );
			this.onClear_();
			this.getAndIncrementRevision_();
			this.pushChangeEvent_( eventData[ 1 ], eventData );
		}
	}

	patch_( patches: unknown[] ): unknown[] | null {
		const revision = this.getRevision_();
		const map = this._values;
		const keys = this._keys;

		const added = {};
		const removed = {};
		const updated = {};

		const type = patches[ 2 ];
		if( type === 0 ) {
			patchReset(
				patches[ 3 ] as PlainObject<V | null>,
				map, keys,
				added, removed, updated
			);
		} else {
			const startRevision = patches[ 0 ] as number;
			const ceilingRevision = Math.max( revision - startRevision, 0 );
			for( let i = 3, imax = patches.length; i < imax; i += 3 ) {
				if( ceilingRevision <= (patches[ i + 0 ] as number) ) {
					patchMap(
						patches[ i + 1 ] as AddedMapItems<V>,
						patches[ i + 2 ] as string[],
						map, keys, added, removed, updated
					);
				}
			}
		}

		if( isEmptyObject( added ) && isEmptyObject( removed ) && isEmptyObject( updated ) ) {
			return null;
		} else {
			return [ null, added, removed, updated ];
		}
	}
}
