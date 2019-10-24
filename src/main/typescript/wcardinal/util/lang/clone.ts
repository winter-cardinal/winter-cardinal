/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { hasOwn } from "./has-own";
import { isArray } from "./is-array";
import { isPlainObject } from "./is-plain-object";
import { PlainObject } from "./plain-object";

export const cloneArray = <T>( target: T[] ): T[] => {
	const result: T[] = [];
	for( let i = 0, imax = target.length; i < imax; ++i ) {
		result.push( target[ i ] );
	}
	return result;
};

export const cloneObject = <T>( target: PlainObject<T> ): PlainObject<T> => {
	const result: PlainObject<T> = {};
	for( const key in target ) {
		if( hasOwn( target, key ) ) {
			result[ key ] = target[ key ];
		}
	}
	return result;
};

export const clone = <T>( target: T ): T => {
	if( isArray( target ) ) {
		return cloneArray<any>( target ) as any;
	} else if( isPlainObject( target ) ) {
		return cloneObject<any>( target ) as any;
	}
	return target;
};
