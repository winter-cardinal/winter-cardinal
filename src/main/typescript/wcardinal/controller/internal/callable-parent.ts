/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { PlainObject } from '../../util/lang/plain-object';

export interface CallableParentMemory<R> {
	call_( data: [ string, unknown[] ], timeout: number, headers: PlainObject<string> | null, isAjaxMode: boolean ): Promise<R>;
}
