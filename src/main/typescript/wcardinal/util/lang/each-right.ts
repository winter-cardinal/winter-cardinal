/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { eachObject, Iteratee } from "./each";
import { isArrayLike } from "./is-array-like";
import { PlainObject } from "./plain-object";

export function eachRightArray<T extends ArrayLike<V>, V>(
	target: T, iteratee: Iteratee<number, V, T>, thisArg?: unknown
): T {
	for( let i = target.length - 1; 0 <= i; --i ) {
		if( iteratee.call( thisArg, target[ i ], i, target ) === false ) {
			break;
		}
	}
	return target;
}

export function eachRight<T, V>( target: T, iteratee: Iteratee<string, V, T>, thisArg?: unknown ): T;
export function eachRight<T, V>( target: T, iteratee: Iteratee<number, V, T>, thisArg?: unknown ): T;
export function eachRight<T, V>(
	target: T, iteratee: Iteratee<string, V, T> | Iteratee<number, V, T>, thisArg?: unknown
): T {
	if( isArrayLike( target ) ) {
		return eachRightArray( target as T & ArrayLike<V>, iteratee as Iteratee<number, V, T & ArrayLike<V>>, thisArg );
	} else if( target != null ) {
		return eachObject( target as any, iteratee as Iteratee<string, V, T & PlainObject<V>>, thisArg );
	}
	return target;
}
