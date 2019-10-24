/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { NullPointerException } from "../../exception/null-pointer-exception";
import { each } from "../../util/lang/each";
import { PlainObject } from "../../util/lang/plain-object";
import { checkNonNull } from "./check-non-null";

export const checkNonNullValues = (
	target: { isNonNull_(): boolean },
	values: ArrayLike<unknown> | PlainObject<unknown> | null | undefined
): void => {
	if( values == null ) {
		throw NullPointerException.create();
	}
	if( target.isNonNull_() ) {
		each<any, unknown>( values, checkNonNull );
	}
};
