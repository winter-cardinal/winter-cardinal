/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { IllegalArgumentException } from "../../exception/illegal-argument-exception";

export const checkArgument = ( expectTrue: boolean ): void => {
	if( ! expectTrue ) {
		throw IllegalArgumentException.create();
	}
};
