/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { hasOwn } from "./has-own";
import { isArray } from "./is-array";
import { isPlainObject } from "./is-plain-object";
import { sizeObject } from "./size";

export const isEqualOther = ( a: any, b: any ): boolean => {
	return (a === b) || (a !== a && b !== b);
};

export const isEqualArray = ( a: ArrayLike<any>, b: ArrayLike<any> ): boolean => {
	if( a.length !== b.length ) {
		return false;
	}
	for( let i = 0, imax = a.length; i < imax; ++i ) {
		if( !isEqual( a[ i ], b[ i ] ) ) {
			return false;
		}
	}

	return true;
};

export const isEqualObject = ( Object.keys != null ? ( a: any, b: any ): boolean => {
	const keys = Object.keys( a );
	const length = keys.length;
	if( length !== sizeObject( b ) ) {
		return false;
	}
	for( let i = 0; i < length; ++i ) {
		const key = keys[ i ];
		if( ! hasOwn( b, key ) || ! isEqual( a[ key ], b[ key ] ) ) {
			return false;
		}
	}

	return true;
} : ( a: any, b: any ): boolean => {
	// For a
	for( const key in a ) {
		if( hasOwn( a, key ) ) {
			if( ! hasOwn( b, key ) || ! isEqual( a[ key ], b[ key ] ) ) {
				return false;
			}
		}
	}

	// For b
	for( const key in b ) {
		if( hasOwn( b, key ) && ! hasOwn( a, key ) ) {
			return false;
		}
	}

	return true;
});

export const isEqual = ( a: any, b: any ): boolean => {
	if( isEqualOther( a, b ) ) {
		return true;
	} else {
		if( isArray( a ) ) {
			if( isArray( b ) ) {
				return isEqualArray( a, b );
			} else {
				return false;
			}
		} else if( isPlainObject( a ) ) {
			if( isPlainObject( b ) ) {
				return isEqualObject( a, b );
			} else {
				return false;
			}
		}
		return false;
	}
};
