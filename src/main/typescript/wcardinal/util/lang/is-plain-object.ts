/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { checkTag } from "./internal/check-tag";
import { checkType } from "./internal/check-type";
import { PlainObject } from "./plain-object";

export const isPlainObject = (Object.getPrototypeOf != null ? ( target: any ): target is PlainObject => {
	if( target != null && checkType(target, "object") && checkTag(target, "[object Object]") ) {
		const prototype = Object.getPrototypeOf( target );
		return prototype == null || prototype === Object.prototype;
	}
	return false;
} : ( target: any ): target is PlainObject<any> => {
	return ( target != null && checkType(target, "object") && checkTag(target, "[object Object]") );
});
