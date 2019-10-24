/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { checkTag } from "./internal/check-tag";
import { checkType } from "./internal/check-type";

export const isString = ( target: any ): target is string => {
	return checkType( target, "string" ) || checkTag( target, "[object String]" );
};
