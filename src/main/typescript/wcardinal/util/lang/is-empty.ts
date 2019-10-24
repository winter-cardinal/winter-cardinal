/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { hasOwn } from "./has-own";
import { isArrayLike } from "./is-array-like";

export const isEmptyObject = ( target: any ): boolean => {
	for( const key in target ) {
		if( hasOwn( target, key ) ) {
			return false;
		}
	}
	return true;
};

export const isEmptyArray = ( target: ArrayLike<any> ): boolean => {
	return target.length <= 0;
};

export const isNotEmptyArray = ( target: ArrayLike<any> ): boolean => {
	return 0 < target.length;
};

export const isEmpty = ( target: any ): boolean => {
	return ( isArrayLike( target ) ? isEmptyArray( target ) : isEmptyObject( target ) );
};
