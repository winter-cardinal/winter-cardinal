/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Exception } from "./exception";

/**
 * Thrown to indicate that a method has been invoked with a null argument which is not supported.
 *
 *     throw NullArgumentException.create();
 *
 * Must use `isInstance` static method instead of the `instanceof` to check
 * whether an error is an instance of this class.
 * This limitation comes from the fact that the custom exceptions inheriting
 * from the Error class are not printed well in the console.
 *
 *     const e = NullArgumentException.create();
 *
 *     console.log( e instanceof NullArgumentException ); // => false
 *     console.log( NullArgumentException.isInstance( e ) ); // => true
 */
export class NullArgumentException extends Exception {
	constructor() {
		super();
	}

	static getId(): string {
		return "NullArgumentException";
	}

	static getMessage(): string {
		return "unexpected null argument";
	}
}
