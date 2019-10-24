/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { isFunction } from "./is-function";
import { isNumber } from "./is-number";

export const isArrayLike = ( target: any ): target is ArrayLike<unknown> => {
	if( target == null || isFunction( target ) ) {
		return false;
	}
	const length = target.length;
	return isNumber( length ) && 0 <= length && (length % 1) === 0;
};
