/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { NoSuchElementException } from "../../exception/no-such-element-exception";

export const checkNoSuchElement = ( expectTrue: boolean ): void => {
	if( ! expectTrue ) {
		throw NoSuchElementException.create();
	}
};
