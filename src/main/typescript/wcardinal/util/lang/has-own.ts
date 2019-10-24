/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

export const hasOwn = ( target: unknown, key: string ): boolean => {
	return Object.prototype.hasOwnProperty.call( target, key );
};
