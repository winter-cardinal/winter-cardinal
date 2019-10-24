/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { RejectionInfoDataMap } from "./rejection-info-data-map";
import { RejectionInfoMap } from "./rejection-info-map";

export interface RejectionInfo {
	[ 0 ]: RejectionInfoDataMap | null;
	[ 1 ]: RejectionInfoMap | null;
}
