/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

export type CallableCall<RESULT, ARGUMENTS extends unknown[]> = ( ...args: ARGUMENTS ) => Promise<RESULT>;
