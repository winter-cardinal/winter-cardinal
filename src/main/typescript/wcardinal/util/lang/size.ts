/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { hasOwn } from "./has-own";
import { isArrayLike } from "./is-array-like";

export const sizeObject = ( Object.keys != null ? ( target: any ): number => {
	return Object.keys( target ).length;
} : ( target: any ): number => {
	let result = 0;
	for( const key in target ) {
		if( hasOwn( target, key ) ) {
			result += 1;
		}
	}
	return result;
});

export const size = ( target: any ): number => {
	if( isArrayLike( target ) ) {
		return target.length;
	} else if( target != null ) {
		return sizeObject( target );
	}
	return 0;
};
