/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { base64ToString, stringToBase64 } from "../lang/base64";
import { bind } from "../lang/bind";
import { clone } from "../lang/clone";
import { cloneDeep } from "../lang/clone-deep";
import { each } from "../lang/each";
import { eachRight } from "../lang/each-right";
import { escape } from "../lang/escape";
import { getGlobal } from "../lang/get-global";
import { hasOwn } from "../lang/has-own";
import { isArray } from "../lang/is-array";
import { isArrayLike } from "../lang/is-array-like";
import { isBoolean } from "../lang/is-boolean";
import { isEmpty } from "../lang/is-empty";
import { isEqual } from "../lang/is-equal";
import { isFunction } from "../lang/is-function";
import { isNaN } from "../lang/is-nan";
import { isNumber } from "../lang/is-number";
import { isPlainObject } from "../lang/is-plain-object";
import { isString } from "../lang/is-string";
import { isThenable } from "../lang/is-thenable";
import { merge } from "../lang/merge";
import { now } from "../lang/now";
import { register } from "../lang/register";
import { size } from "../lang/size";
import { sortedIndex } from "../lang/sorted-index";
import { Util } from "../util";

export class UtilImpl implements Util {
	private static INSTANCE: UtilImpl | null = null;

	register = register;
	getGlobal = getGlobal;
	btoa = stringToBase64;
	atob = base64ToString;
	bind = bind;
	now = now;
	isNumber = isNumber;
	isNaN = isNaN;
	isString = isString;
	isFunction = isFunction;
	isThenable = isThenable;
	isBoolean = isBoolean;
	isArray = isArray;
	isArrayLike = isArrayLike;
	isPlainObject = isPlainObject;
	isEmpty = isEmpty;
	size = size;
	isEqual = isEqual;
	equals = isEqual;
	sortedIndex = sortedIndex;
	each = each;
	eachRight = eachRight;
	merge = merge;
	clone = clone;
	cloneDeep = cloneDeep;
	hasOwn = hasOwn;
	escape = escape;

	static getInstance(): Util {
		if( this.INSTANCE == null ) {
			this.INSTANCE = new UtilImpl();
		}
		return this.INSTANCE;
	}
}
