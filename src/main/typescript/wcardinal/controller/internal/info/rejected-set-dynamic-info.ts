/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { now } from "../../../util/lang/now";
import { DynamicInfo } from "../settings";
import { RejectionInfo } from "./rejection-info";

export class RejectedDynamicInfo {
	public readonly _rejectionInfo: RejectionInfo;
	public readonly _dynamicInfo: DynamicInfo;
	public readonly _createdAt: number;

	constructor( rejectionInfo: RejectionInfo, dynamicInfo: DynamicInfo ) {
		this._rejectionInfo = rejectionInfo;
		this._dynamicInfo = dynamicInfo;
		this._createdAt = now();
	}
}
