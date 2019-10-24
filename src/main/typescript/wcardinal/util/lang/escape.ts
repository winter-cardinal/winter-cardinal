/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { escapeHandler, escapeRegEx } from "./internal/escape";

export const escape = ( target: unknown ): string => {
	return String( target ).replace( escapeRegEx, escapeHandler );
};
