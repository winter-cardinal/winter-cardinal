/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

export interface CallableCall<RESULT, ARGUMENTS extends unknown[]> {
	// tslint:disable-next-line:callable-types
	( ...args: ARGUMENTS ): Promise<RESULT>;
}
