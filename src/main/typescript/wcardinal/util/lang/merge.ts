/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { hasOwn } from "./has-own";
import { isArray } from "./is-array";
import { isPlainObject } from "./is-plain-object";

export const mergeCloneDeep = <T>( target: T ): T => {
	if( isArray( target ) ) {
		return mergeArray( [] as any, target );
	} else if( isPlainObject( target ) ) {
		return mergeObject( {} as any, target );
	}
	return target;
};

export const mergeArray = <TA extends unknown[], TB extends unknown[]>( a: TA, b: TB ): TA => {
	const imin = Math.min( a.length, b.length );

	for( let i = 0; i < imin; ++i ) {
		const bi = b[ i ];
		if( isArray( bi ) ) {
			const ai = a[ i ];
			if( isArray( ai ) ) {
				mergeArray( ai, bi );
			} else {
				a[ i ] = mergeArray( [] as any, bi );
			}
		} else if( isPlainObject( bi ) ) {
			const ai = a[ i ];
			if( isPlainObject( ai ) ) {
				mergeObject( ai, bi );
			} else {
				a[ i ] = mergeObject( {} as any, bi );
			}
		} else if( bi !== undefined ) {
			a[ i ] = bi;
		}
	}

	for( let i = imin, imax = b.length; i < imax; ++i ) {
		a[ i ] = mergeCloneDeep( b[ i ] );
	}

	return a;
};

export const mergeObject = <TA, TB>( a: TA, b: TB ): TA => {
	for( const key in b ) {
		if( hasOwn( b, key ) ) {
			const bv = b[ key ];
			if( key in a ) {
				if( isArray( bv ) ) {
					const av = (a as any)[ key ];
					if( isArray( av ) ) {
						mergeArray( av, bv );
					} else {
						(a as any)[ key ] = mergeArray( [], bv );
					}
				} else if( isPlainObject( bv ) ) {
					const av = (a as any)[ key ];
					if( isPlainObject( av ) ) {
						mergeObject( av, bv );
					} else {
						(a as any)[ key ] = mergeObject( {}, bv );
					}
				} else if( bv !== undefined ) {
					(a as any)[ key ] = bv;
				}
			} else {
				(a as any)[ key ] = mergeCloneDeep( bv );
			}
		}
	}

	return a;
};

// tslint:disable-next-line:only-arrow-functions
export const merge: <T>( a: T, ...bs: unknown[] ) => T = function<T>( a: T ): T {
	const args = arguments;
	for( let i = 1, imax = args.length; i < imax; ++i ) {
		mergeObject( a, args[ i ] );
	}
	return a;
};
