/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { PlainObject } from "../../../util/lang/plain-object";
import { SPatch } from "./s-patch";

export interface SNavigableMapPatch<V> extends SPatch {
	put_( key: string, value: V | null ): void;
	putAll_( values: PlainObject<V | null> ): void;
	remove_( key: string ): void;
	removeAll_( values: PlainObject<V | null> ): void;
}
