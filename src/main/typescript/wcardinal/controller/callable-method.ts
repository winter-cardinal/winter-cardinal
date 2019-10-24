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
	 * Invokes the method and returns a wcardinal.util.Thenable instance.
	 * The returned value is passed the `done` handler of the instance.
	 * If the method invocation goes to failure, the `fail` handler of the instance
	 * is invoked with the string representing a reason.
	 *
	 *     call( "John" )
	 *     .then(function( returnedValue ){
	 *         // success
	 *     })
	 *     .catch(function( reason ){
	 *         // failure
	 *     });
	 *
	 * @param args method arguments
	 * @returns the thenable object
	 */
	call( ...args: ARGUMENTS ): Promise<RESULT>;
}
