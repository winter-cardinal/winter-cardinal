/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

export class Lock {
	private _count = 0;

	lock(): Lock {
		this._count += 1;
		return this;
	}

	isLocked(): boolean {
		return 0 < this._count;
	}

	unlock(): Lock {
		this._count -= 1;
		return this;
	}

	getHoldCount(): number {
		return this._count;
	}
}
