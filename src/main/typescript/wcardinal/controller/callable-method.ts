/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Connectable } from "../event/connectable";

export interface CallableMethod<RESULT, ARGUMENTS extends unknown[]> extends Connectable {
	/**
	 * Sets the timeout.
	 *
	 *     // Sets the timeout to 1 second.
	 *     // If the server does not responed within 1 second,
	 *     // a method invocation is considered as failure.
	 *     timeout( 1000 )
	 *
	 * @param timeout the timeout in milliseconds
	 * @returns this
	 */
	timeout( timeout: number ): CallableMethod<RESULT, ARGUMENTS>;

	/**
	 * Sets to the Ajax mode.
	 * In the Ajax mode, call requests of callable methods are sent via Ajax.
	 *
	 *     ajax()
	 *
	 * @returns this
	 */
	ajax(): CallableMethod<RESULT, ARGUMENTS>;

	/**
	 * Sets to the non-Ajax mode.
	 * In the non-Ajax mode, call requests of callable methods are sent via a persistent connection such as a WebSocket.
	 *
	 *     unajax()
	 *
	 * @returns this
	 */
	unajax(): CallableMethod<RESULT, ARGUMENTS>;

	/**
	 * Sets the safety flag to true.
	 * If the safety flags is true, all the instance variables are forced
	 * to be synchronized prior to the method invocation.
	 * This is an extremely expensive operation.
	 *
	 *     safe()
	 *
	 * @returns this
	 * @depreated Callable methods are always safe.
	 */
	safe(): CallableMethod<RESULT, ARGUMENTS>;

	/**
	 * Sets the safety flag to false.
	 *
	 *     unsafe()
	 *
	 * @returns this
	 * @depreated Callable methods are always safe.
	 */
	unsafe(): CallableMethod<RESULT, ARGUMENTS>;

	/**
	 * Invokes a callable method.
	 *
	 *     const returnedValue = await call("Cardinal");
	 *
	 * @param args method arguments
	 * @returns a promise
	 */
	call( ...args: ARGUMENTS ): Promise<RESULT>;
}
