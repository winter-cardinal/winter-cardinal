/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { isFunction } from "./is-function";

export const isThenable = ( target: any ): target is PromiseLike<unknown> => {
	return (target != null && isFunction( target.then ));
};
