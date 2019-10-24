/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { hasOwn } from "./has-own";
import { isArrayLike } from "./is-array-like";
import { PlainObject } from "./plain-object";

export type Iteratee<K, V = unknown, T = unknown> = (value: V, key: K, target: T) => boolean | void;

export const eachObject = <T extends PlainObject<V>, V>(
	target: T, iteratee: Iteratee<string, V, T>, thisArg?: unknown
): T => {
	for( const key in target ) {
		if( hasOwn( target, key ) ) {
			if( iteratee.call( thisArg, target[ key ] as unknown as V, key, target ) === false ) {
				break;
			}
		}
	}
	return target;
};

export const eachArray = <T extends ArrayLike<V>, V>(
	target: T, iteratee: Iteratee<number, V, T>, thisArg?: unknown
): T => {
	for( let i = 0, imax = target.length; i < imax; ++i ) {
		if( iteratee.call( thisArg, target[ i ], i, target ) === false ) {
			break;
		}
	}
	return target;
};

export function each<T extends PlainObject<V>, V>( target: T, iteratee: Iteratee<string, V, T>, thisArg?: unknown ): T;
export function each<T extends ArrayLike<V>, V>( target: T, iteratee: Iteratee<number, V, T>, thisArg?: unknown ): T;
export function each<T, V>(
	target: T, iteratee: Iteratee<string, V, T> | Iteratee<number, V, T>, thisArg?: unknown
): T {
	if( isArrayLike( target ) ) {
		return eachArray<T & ArrayLike<V>, V>( target as T & ArrayLike<V>, iteratee as Iteratee<number, V, T>, thisArg );
	} else if( target != null ) {
		return eachObject<T & PlainObject<V>, V>( target as any, iteratee as Iteratee<string, V, T>, thisArg );
	}
	return target;
}
