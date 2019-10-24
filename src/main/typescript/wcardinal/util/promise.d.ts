/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

interface Promise<T> {
	/**
	 * Returns a thenable which will have the same result of this thenable.
	 * The only exception is if this thenable is not fulfilled or rejected before
	 * the specified milliseconds, the returned thenable will be rejected.
	 * If the `onTimeoutOrReason` is a function, the `onTimeoutOrReason` will be
	 * invoked without arguments immediately before rejecting the returned thenable.
	 * And then, the returned thenable will be rejected with the return value of the
	 * `onTimeoutOrReason` as a rejection reason. If the `onTimeoutOrReason` is not
	 * a function, the returned thenable will be rejected with the `onTimeoutOrReason`
	 * as a rejection reason.
	 *
	 * @param timeout a timeout in milliseconds
	 * @param onTimeoutOrReason a function to be executed when the timeout occurs, or a rejection reason
	 * @returns a thenable
	 */
	timeout( timeout: number, onTimeoutOrReason: any ): Promise<T>;
}
