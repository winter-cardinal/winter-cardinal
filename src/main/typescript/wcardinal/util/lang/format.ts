/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

export const format = ( formatString: string, ...parameters: unknown[] ): string => {
	return doFormat(formatString, parameters);
};

export const doFormat = ( formatString: string, parameters: unknown[] ): string => {
	return formatString.replace(/\{(\w+)\}/g, (i, j) => {
		return String(parameters[ parseInt(j, 10) ]);
	});
};
