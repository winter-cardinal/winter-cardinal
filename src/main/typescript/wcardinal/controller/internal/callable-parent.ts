/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

export interface CallableParentMemory<R> {
	call_( data: [ string, unknown[] ], timeout: number, isAjaxMode: boolean ): Promise<R>;
}
