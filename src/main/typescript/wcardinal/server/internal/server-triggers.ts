/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

export interface ServerTriggers {
	_trigger( name: string ): void;
	_triggerAndGet( name: string, type: string[] | null, data: unknown[] | null ): unknown[];
	_triggerHandlers( messages: string[] ): void;
}
