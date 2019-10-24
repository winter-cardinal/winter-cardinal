/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { CallableCall } from "../callable-call";
import { CallableMethod } from "../callable-method";

export interface CallableWrapper<RESULT, ARGUMENTS extends unknown[]>
	extends CallableMethod<RESULT, ARGUMENTS>, CallableCall<RESULT, ARGUMENTS> {

}
