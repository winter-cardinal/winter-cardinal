/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

export const isFunction = ( target: any ): target is Function => {
	const targetTag = Object.prototype.toString.call( target );
	return targetTag === "[object Function]" ||
		targetTag === "[object GeneratorFunction]" ||
		targetTag === "[object AsyncFunction]" ||
		targetTag === "[object Proxy]";
};
