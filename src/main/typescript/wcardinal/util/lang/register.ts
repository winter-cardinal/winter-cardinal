/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { getGlobal } from "./get-global";

export const register = <T>( path: string, instance: T, lowerCase: boolean= false, root: any= getGlobal() ): T => {
	const splittedPath = path.trim().split(".");
	let cd = root;
	for (let i = 0, imax = splittedPath.length - 1; i < imax; ++i) {
		const j = splittedPath[i];
		cd[j] = cd[j] || {};
		cd = cd[j];
	}

	let last = splittedPath[splittedPath.length - 1];
	if ( lowerCase ) {
		last = last[0].toLowerCase() + last.slice(1);
	}
	cd[last] = instance;

	return instance;
};
