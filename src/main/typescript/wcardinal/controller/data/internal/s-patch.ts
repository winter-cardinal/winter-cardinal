/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

export interface SPatch {
	getWeight_(): number;
	isReset_(): boolean;
	serialize_( result: unknown[] ): void;
}
