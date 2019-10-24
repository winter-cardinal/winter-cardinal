/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { mergeCloneDeep } from "./merge";

export const cloneDeep = <T>( target: T ): T => {
	return mergeCloneDeep( target );
};
