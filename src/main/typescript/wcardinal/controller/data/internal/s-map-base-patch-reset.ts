/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { SMapBasePatch } from "./s-map-base-patch";

export class SMapBasePatchReset<V> implements SMapBasePatch<V> {
	put_() {
		// DO NOTHING
	}

	putAll_() {
		// DO NOTHING
	}

	remove_() {
		// DO NOTHING
	}

	removeAll_() {
		// DO NOTHING
	}

	getWeight_() {
		return 1;
	}

	isReset_() {
		return true;
	}

	serialize_( result: unknown[] ): void {
		// DO NOTHING
	}
}
