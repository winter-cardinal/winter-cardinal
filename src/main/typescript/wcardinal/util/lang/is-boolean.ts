/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { checkTag } from "./internal/check-tag";

export const isBoolean = ( target: any ): target is boolean => {
	return target === true || target === false || checkTag( target, "[object Boolean]" );
};
