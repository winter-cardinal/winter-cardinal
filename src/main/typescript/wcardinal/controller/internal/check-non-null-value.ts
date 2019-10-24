/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { checkNonNull } from "./check-non-null";

export const checkNonNullValue = ( target: { isNonNull_(): boolean }, value: unknown ): void => {
	if( target.isNonNull_() ) {
		checkNonNull( value );
	}
};
