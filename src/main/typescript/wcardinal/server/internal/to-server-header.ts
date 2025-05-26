/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { PlainObject } from "../../util/lang/plain-object";

export const toServerHeaders = ( mode: string, ssid: string, headers: PlainObject<string> | null ): PlainObject<string> => {
	const result: PlainObject<string> = {
		"X-WCardinal-Mode": mode,
		"X-WCardinal-SSID": ssid
	};
	if (headers != null) {
		for (const key in headers) {
			if (Object.prototype.hasOwnProperty.call(headers, key)) {
				result[key] = headers[key];
			}
		}
	}
	return result;
};
