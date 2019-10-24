/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

const toString = Object.prototype.toString;

export const checkTag = ( target: unknown, expected: string ): boolean => {
	return toString.call( target ) === expected;
};
