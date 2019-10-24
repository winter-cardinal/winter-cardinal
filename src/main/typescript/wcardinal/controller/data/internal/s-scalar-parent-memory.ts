/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { EventInfo } from "./event-info";

export interface SScalarParentMemory {
	onUnlock_(): void;
	update_(): void;
	uninitialize_(): void;
	getEventInfo_(): EventInfo | null;
}
