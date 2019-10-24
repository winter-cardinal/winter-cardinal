/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { SQueuePatch } from "./s-queue-patch";

export class SQueuePatchReset<V> implements SQueuePatch<V> {
	add_(): void {
		// DO NOTHING
	}

	addAll_(): void {
		// DO NOTHING
	}

	remove_(): void {
		// DO NOTHING
	}

	capacity_(): void {
		// DO NOTHING
	}

	getWeight_(): number {
		return 1;
	}

	isReset_(): boolean {
		return true;
	}

	serialize_( result: unknown[] ): void {
		// DO NOTHING
	}
}
