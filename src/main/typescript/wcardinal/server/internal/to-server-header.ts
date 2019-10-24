/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { PlainObject } from "../../util/lang/plain-object";

export const toServerHeaders = ( mode: string, ssid: string ): PlainObject<string> => {
	return {
		"X-WCardinal-Mode": mode,
		"X-WCardinal-SSID": ssid
	};
};
