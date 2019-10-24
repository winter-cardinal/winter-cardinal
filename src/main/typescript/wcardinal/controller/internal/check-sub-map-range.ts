/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { checkArgument } from "./check-argument";

export const checkSubMapRange = ( fromKey: string, toKey: string ): void => {
	if( fromKey != null && toKey != null ) {
		checkArgument( fromKey <= toKey );
	}
};
