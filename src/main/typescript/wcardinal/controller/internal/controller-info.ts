/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { ControllerType } from "./controller-type";

export interface ControllerInfo {
	type: ControllerType;
	properties: number;
}
