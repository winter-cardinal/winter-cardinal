/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { EventHandler } from "../data/internal/event-info";
import { SContainerMemory } from "../data/internal/s-container-memory";
import { SScalarMemory } from "../data/internal/s-scalar-memory";
import { SScalarParentMemory } from "../data/internal/s-scalar-parent-memory";

export interface TaskParentMemory extends SScalarParentMemory {
	putData_( name: string, data: SScalarMemory | SContainerMemory ): void;
	putHistoricalData_( name: string, data: SScalarMemory | SContainerMemory ): void;
	putDataHistoryHook_( name: string, hook: ( data: SScalarMemory | SContainerMemory, part: string ) => void ): void;
	addDataEventHandler_( handler: EventHandler ): void;
	lock_(): void;
	isLocked_(): boolean;
	unlock_(): void;
}
