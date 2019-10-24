/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

export interface TaskCall<ARGUMENTS extends unknown[], RETURN> {
	// tslint:disable-next-line:callable-types
	(...args: ARGUMENTS): RETURN;
}
