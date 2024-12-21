/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

export type TaskCall<ARGUMENTS extends unknown[], RETURN> = (...args: ARGUMENTS) => RETURN;
