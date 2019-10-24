/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Iteratee } from "../../util/lang/each";
import { isString } from "../../util/lang/is-string";
import { Properties } from "../internal/properties";
import { Lock } from "../lock";
import { SAscendingMapMemory } from "./internal/s-ascending-map-memory";
import { SContainerParentMemory } from "./internal/s-container-parent-memory";
import { SDescendingMapMemory } from "./internal/s-descending-map-memory";
import { SMapBase } from "./internal/s-map-base";
import { SNavigableMapMemory } from "./internal/s-navigable-map-memory";
import { SNavigableMapPatch } from "./internal/s-navigable-map-patch";
import { SNavigableMapPatches } from "./internal/s-navigable-map-patches";
import { SType } from "./internal/s-type";
import { STypeToClass } from "./internal/s-type-to-class";
import { SMapEntry } from "./s-map-entry";
import { SNavigableMapSubMap } from "./s-navigable-map-sub-map";

/**
 * A synchronized map that further provides an ordering on its keys.
 *
 * @param V a value type
 */
export class SNavigableMap<V>
	extends SMapBase<V, SNavigableMapPatch<V>, SNavigableMapPatches<V>, SNavigableMapMemory<V>> {

	static SubMap: typeof SNavigableMapSubMap = SNavigableMapSubMap;

	constructor( memory: SNavigableMapMemory<V> ) {
		super( memory );
	}

	/**
	 * Iterates over elements of this map, calling the iteratee for each element.
	 * The iteratee is bound to the thisArg and invoked with three arguments: value, key and this map.
	 * The iteratee may exit iteration early by explicitly returning false.
	 *
	 * @param iteratee the function called per iteration
	 * @param thisArg the this binding of the iteratee
	 * @param reverse true to iterate in the reverse order
	 * @returns this
	 */
	each( iteratee: Iteratee<string, V | null, this>, thisArg: unknown= this, reverse= false ): this {
		this.__mem__.each_( iteratee, thisArg, reverse );
		return this;
	}

	filter( iteratee: Iteratee<string, V | null, this>, thisArg: unknown= this ): this {
		this.__mem__.filter_( iteratee, thisArg );
		return this;
	}

	/**
	 * Returns the first field value which the specified predicate returns true, or null if there is no such field.
	 * The predicate is bound to the thisArg and invoked with three arguments: field value, field name and this map.
	 *
	 * @param predicate the function called per iteration
	 * @param thisArg the this binding of the predicate
	 * @param reverse true to iterate in the reverse order
	 * @returns the first field value which the specified predicate returns true, or null if there is no such field.
	 */
	find( predicate: Iteratee<string, V | null, this>, thisArg: unknown= this, reverse= false ): unknown {
		return this.__mem__.find_( predicate, thisArg, reverse );
	}

	/**
	 * Returns the lowest key in this map.
	 *
	 * @returns the lowest key in this map
	 * @throws Error if this map is empty
	 */
	firstKey(): string {
		return this.__mem__.firstKey_();
	}

	/**
	 * Returns the highest key in this map.
	 *
	 * @returns the highest key in this map
	 * @throws Error if this map is empty
	 */
	lastKey(): string {
		return this.__mem__.lastKey_();
	}

	/**
	 * Returns a key-value mapping of the lowest key in this map, or null if this map is empty.
	 *
	 * @returns a key-value mapping of the lowest key in this map, or null if this map is empty
	 */
	firstEntry(): SMapEntry<V> | null {
		return this.__mem__.firstEntry_();
	}

	/**
	 * Returns a key-value mapping of the highest key in this map, or null if this map is empty.
	 *
	 * @returns a key-value mapping of the highest key in this map, or null if this map is empty
	 */
	lastEntry(): SMapEntry<V> | null {
		return this.__mem__.lastEntry_();
	}

	/**
	 * Returns a key-value mapping of the highest key lower than or equal to the specified key,
	 * or null if there is no such key.
	 *
	 * @param key a key
	 * @returns a key-value mapping of the highest key lower than or equal to the specified key,
	 * or null if there is no such key
	 */
	floorEntry( key: string ): SMapEntry<V> | null {
		return this.__mem__.floorEntry_( key );
	}

	/**
	 * Returns a key-value mapping of the lowest key higher than or equal to the specified key,
	 * or null if there is no such key.
	 *
	 * @param key a key
	 * @returns a key-value mapping of the lowest key higher than or equal to the specified key,
	 * or null if there is no such key
	 */
	ceilingEntry( key: string ): SMapEntry<V> | null {
		return this.__mem__.ceilingEntry_( key );
	}

	/**
	 * Returns the highest key lower than or equal to the specified key, or null if there is no such key.
	 *
	 * @param key a key
	 * @returns the highest key lower than or equal to the specified key, or null if there is no such key.
	 */
	floorKey( key: string ): string | null {
		return this.__mem__.floorKey_( key );
	}

	/**
	 * Returns the lowest key higher than or equal to the specified key, or null if there is no such key.
	 *
	 * @param key a key
	 * @returns the lowest key higher than or equal to the specified key, or null if there is no such key
	 */
	ceilingKey( key: string ): string | null {
		return this.__mem__.ceilingKey_( key );
	}

	/**
	 * Returns a key-value mapping of the highest key lower than the specified key,
	 * or null if there is no such key.
	 *
	 * @param key a key
	 * @returns a key-value mapping of the highest key lower than the specified key,
	 * or null if there is no such key
	 */
	lowerEntry( key: string ): SMapEntry<V> | null {
		return this.__mem__.lowerEntry_( key );
	}

	/**
	 * Returns a key-value mapping of the lowest key higher than the specified key,
	 * or null if there is no such key.
	 *
	 * @param key a key
	 * @returns a key-value mapping of the lowest key higher than the specified key,
	 * or null if there is no such key
	 */
	higherEntry( key: string ): SMapEntry<V> | null {
		return this.__mem__.higherEntry_( key );
	}

	/**
	 * Returns the highest key lower than the specified key, or null if there is no such key.
	 *
	 * @param key a key
	 * @returns the highest key lower than the specified key, or null if there is no such key.
	 */
	lowerKey( key: string ): string | null {
		return this.__mem__.lowerKey_( key );
	}

	/**
	 * Returns the lowest key higher than the specified key, or null if there is no such key.
	 *
	 * @param key a key
	 * @returns the lowest key higher than the specified key, or null if there is no such key
	 */
	higherKey( key: string ): string | null {
		return this.__mem__.higherKey_( key );
	}

	/**
	 * Returns a view of the portion of this map whose keys are lower than (or equal to,
	 * if inclusive is true) the specified key `toKey`.
	 *
	 * @param toKey high endpoint of the keys in the view
	 * @param inclusive true if the specified key `toKey` is to be included in the view
	 * @returns a view of the specified portion of this map
	 */
	headMap( toKey: string, inclusive= false ): SNavigableMapSubMap<V> {
		return this.__mem__.headMap_<SNavigableMapSubMap<V>>( toKey, inclusive, SNavigableMapSubMap ).getWrapper_();
	}

	/**
	 * Returns a view of the portion of this map whose keys are higher than (or equal to,
	 * if inclusive is true) the specified key `fromKey`.
	 *
	 * @param fromKey low endpoint of the keys in the view
	 * @param inclusive true if the specified key `fromKey` is to be included in the view
	 * @returns a view of the specified portion of this map
	 */
	tailMap( fromKey: string, inclusive= true ): SNavigableMapSubMap<V> {
		return this.__mem__.tailMap_<SNavigableMapSubMap<V>>( fromKey, inclusive, SNavigableMapSubMap ).getWrapper_();
	}

	/**
	 * Returns a view of the portion of this map whose keys range from `fromKey` to `toKey`.
	 *
	 * @param fromKey low endpoint of the keys in the view
	 * @param toKey high endpoint of the keys in the view
	 * @returns a view of the specified portion of this map
	 * @throws Error if `fromKey` is higher than `toKey`
	 */
	subMap( fromKey: string, toKey: string ): SNavigableMapSubMap<V>;

	/**
	 * Returns a view of the portion of this map whose keys range from `fromKey` to `toKey`.
	 *
	 * @param fromKey low endpoint of the keys in the view
	 * @param includeFrom true if the low endpoint is to be included in the view
	 * @param toKey high endpoint of the keys in the view
	 * @param includeTo true if the high endpoint is to be included in the view
	 * @returns a view of the specified portion of this map
	 * @throws Error if `fromKey` is higher than `toKey`
	 */
	subMap( fromKey: string, includeFrom: boolean, toKey: string, includeTo: boolean ): SNavigableMapSubMap<V>;

	subMap(
		fromKey: string, includeFromOrToKey: string|boolean, toKeyOrIncludeTo?: string|boolean, includeTo?: boolean
	): SNavigableMapSubMap<V> {
		let includeFrom = true;
		let toKey: string | null = null;
		if( arguments.length === 2 ) {
			toKey = includeFromOrToKey as string;
			includeTo = false;
		} else if( arguments.length === 3 ) {
			if( isString( includeFromOrToKey ) ) {
				toKey = includeFromOrToKey;
				includeTo = toKeyOrIncludeTo as boolean;
			} else {
				toKey = toKeyOrIncludeTo as string;
				includeFrom = includeFromOrToKey;
			}
		} else {
			includeFrom = includeFromOrToKey as boolean;
			toKey = toKeyOrIncludeTo as string;
		}

		if( includeFrom == null ) {
			includeFrom = true;
		}

		if( includeTo == null ) {
			includeTo = false;
		}

		return this.__mem__.subMap_<SNavigableMapSubMap<V>>(
			fromKey, includeFrom, toKey, includeTo, SNavigableMapSubMap
		).getWrapper_();
	}

	/**
	 * Returns a reverse-order view of this map.
	 *
	 * @returns reverse-order view of this map
	 */
	descendingMap(): SNavigableMapSubMap<V> {
		return this.__mem__.descendingMap_<SNavigableMapSubMap<V>>( SNavigableMapSubMap ).getWrapper_();
	}
}

STypeToClass.put_({
	create_( parent: SContainerParentMemory, name: string, properties: Properties, lock: Lock ) {
		return new SAscendingMapMemory<unknown>( parent, name, properties, lock, SNavigableMap );
	},

	getConstructor_() {
		return SNavigableMap;
	},

	getType_() {
		return SType.ASCENDING_MAP;
	}
});

STypeToClass.put_({
	create_( parent: SContainerParentMemory, name: string, properties: Properties, lock: Lock ) {
		return new SDescendingMapMemory<unknown>( parent, name, properties, lock, SNavigableMap );
	},

	getConstructor_() {
		return SNavigableMap;
	},

	getType_() {
		return SType.DESCENDING_MAP;
	}
});
