/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

export const checkType = ( target: unknown, expected: unknown ): boolean => {
	return typeof target === expected;
};
