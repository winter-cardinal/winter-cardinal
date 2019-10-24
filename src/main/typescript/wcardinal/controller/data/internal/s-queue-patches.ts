/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { SPatches } from "./s-patches";
import { SQueueData } from "./s-queue-data";
import { SQueuePatch } from "./s-queue-patch";
import { SQueuePatchMap } from "./s-queue-patch-map";
import { SQueuePatchReset } from "./s-queue-patch-reset";

const SQUEUE_PATCH_RESET_INSTANCE = new SQueuePatchReset<unknown>();
const SQUEUE_PATCH_MAP_DUMMY = new SQueuePatchMap<unknown>();

export class SQueuePatches<V> extends SPatches<SQueueData<V>, SQueuePatch<V>> {
	newPatchMap_() {
		return new SQueuePatchMap();
	}

	newPatchReset_() {
		return SQUEUE_PATCH_RESET_INSTANCE;
	}

	getPatchMapDummy_() {
		return SQUEUE_PATCH_MAP_DUMMY;
	}

	add_( revision: number, value: V | null ): void {
		this.getOrCreate_( revision ).add_( value );
	}

	addAll_( revision: number, values: ArrayLike<V | null> ): void {
		this.getOrCreate_( revision ).addAll_( values );
	}

	remove_( revision: number ): void {
		this.getOrCreate_( revision ).remove_();
	}

	capacity_( revision: number, capacity: number ): void {
		this.getOrCreate_( revision ).capacity_( capacity );
	}

	packReset_( authorizedRevision: number, revision: number, data: SQueueData<V> ): unknown[] {
		return [ authorizedRevision, revision - authorizedRevision, 0, data._queue, data._capacity ];
	}
}
