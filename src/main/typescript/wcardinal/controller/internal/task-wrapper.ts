/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { TaskCall } from "../task-call";
import { TaskMethod } from "../task-method";

export interface TaskWrapper<RESULT, ARGUMENTS extends unknown[], RETURN>
	extends TaskMethod<RESULT, ARGUMENTS>, TaskCall<ARGUMENTS, RETURN> {
}
