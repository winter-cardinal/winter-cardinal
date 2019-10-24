/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

export class SQueueData<V> {
	_queue: Array<V | null>;
	_capacity: number;

	constructor( queue: Array<V | null>= [], capacity: number= 2147483647 ) {
		this._queue = queue;
		this._capacity = capacity;
	}
}
