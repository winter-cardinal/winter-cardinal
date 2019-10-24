/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { isNumber } from "./is-number";

export const isNaN = ( typeof Number.isNaN !== "undefined" ? Number.isNaN : ( target: any ): boolean => {
	return isNumber( target ) && target !== target;
});
