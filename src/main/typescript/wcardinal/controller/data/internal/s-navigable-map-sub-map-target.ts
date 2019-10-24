/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Connectable } from "../../../event/connectable";
import { PlainObject } from "../../../util/lang/plain-object";
import { AddedMapItems, RemovedMapItems, UpdatedMapItems } from "../s-map";
import { ViewTarget } from "./view-target";

export interface SNavigableMapSubMapTarget<V> extends ViewTarget {
	getValues_(): PlainObject<V | null>;
	getKeys_(): string[];

	getAndIncrementRevision_(): number;

	getWrapper_(): Connectable;

	put_( key: string, value: V | null ): V | null;
	reput_( key: string ): V | null;
	putAll_( mappings: PlainObject<V | null> ): void;
	remove_( key: string ): V | null;

	toEventData_(
		added: AddedMapItems<V> | null, removed: RemovedMapItems<V> | null,
		updated: UpdatedMapItems<V> | null
	): unknown[];
	onRemoveAll_( removed: RemovedMapItems<V> ): void;
	pushChangeEvent_( initArg: unknown, changeArgs: unknown[] ): void;
}
