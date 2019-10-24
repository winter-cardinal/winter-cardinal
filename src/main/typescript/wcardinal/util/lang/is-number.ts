/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { checkTag } from "./internal/check-tag";
import { checkType } from "./internal/check-type";

export const isNumber = ( target: any ): target is number => {
	return checkType( target, "number" ) || checkTag( target, "[object Number]" );
};
