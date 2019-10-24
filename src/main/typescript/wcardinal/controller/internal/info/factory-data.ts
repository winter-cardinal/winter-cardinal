/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { DynamicInfo } from "../settings";

export interface FactoryData {
	name: string;
	args: unknown[];
}

export interface ReceivedFactoryData {
	name: string;
	args: unknown[];
	senderId: number;
	dynamic: DynamicInfo | null;
}
