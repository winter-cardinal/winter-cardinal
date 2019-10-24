/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { SListBasePatch } from "./s-list-base-patch";

export interface SMovableListPatch<V> extends SListBasePatch<V> {
	move_( oldIndex: number, newIndex: number ): void;
}
