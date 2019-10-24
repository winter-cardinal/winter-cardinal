/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

export interface CallCallback {
	resolve( value?: unknown ): void;
	reject( reason?: unknown ): void;
}
