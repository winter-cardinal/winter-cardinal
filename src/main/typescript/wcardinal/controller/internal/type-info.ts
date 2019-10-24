/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { SType } from "../data/internal/s-type";

export const TaskType = -1;
export const CallableType = -2;
export type STypeOrTaskOrCallable = SType|-1|-2;
export interface TypeInfo {
	type: STypeOrTaskOrCallable;
	value?: unknown;
	properties: number;
}
