/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { SPatch } from "./s-patch";

export interface SListBasePatch<V> extends SPatch {
	add_( index: number, value: V | null ): void;
	addAll_( index: number, values: ArrayLike<V | null> ): void;
	remove_( index: number ): void;
	set_( index: number, value: V | null ): void;
}
