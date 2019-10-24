/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { NullPointerException } from "../../exception/null-pointer-exception";

export const checkNonNull = ( value: unknown ): void => {
	if( value == null ) {
		throw NullPointerException.create();
	}
};
