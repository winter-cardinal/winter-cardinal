/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

export interface ViewTarget {
	isReadOnly_(): boolean;
	lock_(): void;
	unlock_(): void;
	isLocked_(): boolean;
	isInitialized_(): boolean;
	isNonNull_(): boolean;
}
