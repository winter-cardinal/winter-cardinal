/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { checkTag } from "./internal/check-tag";

export const isArray = Array.isArray || (( target: any ): target is unknown[] => {
	return checkTag( target, "[object Array]" );
});
