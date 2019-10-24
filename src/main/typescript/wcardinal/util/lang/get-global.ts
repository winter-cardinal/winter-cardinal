/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

/** @hidden */
let globalCache: any = null;

export const getGlobal = (): any => {
	if (globalCache == null) {
		if (typeof self !== "undefined") {
			globalCache = self;
		} else if (typeof window !== "undefined") {
			globalCache = window;
		} else if (typeof global !== "undefined") {
			globalCache = global;
		}
	}
	return globalCache;
};
