/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

export interface SQueuePatch<V> {
	add_( value: V | null ): void;
	addAll_( values: ArrayLike<V | null> ): void;
	remove_(): void;
	capacity_( capacity: number ): void;
	getWeight_(): number;
	isReset_(): boolean;
	serialize_( result: unknown[] ): void;
}
