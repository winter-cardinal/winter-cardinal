/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { AddedListItems, RemovedListItems, UpdatedListItems } from "../s-list";
import { MovedListItems } from "../s-movable-list";

export type SMovableListEventData<V> = [
	unknown,
	AddedListItems<V>,
	RemovedListItems<V>,
	UpdatedListItems<V>,
	MovedListItems<V>,
	MovedListItems<V>
];
