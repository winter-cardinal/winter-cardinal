/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { IndexOutOfBoundsException } from "../../exception/index-out-of-bounds-exception";

export const checkRange = ( index: number, from: number, to: number ): void => {
	if( index < from || to <= index ) {
		throw IndexOutOfBoundsException.create();
	}
};
