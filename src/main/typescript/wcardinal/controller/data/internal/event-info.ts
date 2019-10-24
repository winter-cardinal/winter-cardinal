/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { PlainObject } from "../../../util/lang/plain-object";

export type EventArgumentMap = PlainObject<unknown[] | null>;
export interface EventArgumentList extends Array<[ string, unknown[] ]> {}
export interface EventOrigin {
	triggerEvents_( eventMap: EventArgumentMap, eventList: EventArgumentList, isFirst: boolean ): void;
}
export type EventInfoData = [ EventOrigin, EventArgumentMap, EventArgumentList, boolean ];
export type EventInfoDataMap = PlainObject<EventInfoData>;
export type EventInfoMap = PlainObject<EventInfo>;
export type EventInfoSelf = [ string, string[] | null, unknown[] | null ];
export interface EventInfo {
	self?: EventInfoSelf[];
	data?: EventInfoDataMap;
	ctrlr?: EventInfoMap;
}
export type EventHandler = ( eventInfoDataMap: EventInfoDataMap, eventInfo: EventInfo ) => void;
