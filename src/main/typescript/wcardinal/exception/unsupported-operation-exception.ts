/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Exception } from "./exception";

/**
 * Thrown to indicate that an operation is not supported.
 *
 *     throw UnsupportedOperationException.create();
 *
 * Must use `isInstance` static method instead of the `instanceof` to check
 * whether an error is an instance of this class.
 * This limitation comes from the fact that the custom exceptions inheriting
 * from the Error class are not printed well in the console.
 *
 *     const e = UnsupportedOperationException.create();
 *
 *     console.log( e instanceof UnsupportedOperationException ); // => false
 *     console.log( UnsupportedOperationException.isInstance( e ) ); // => true
 */
export class UnsupportedOperationException extends Exception {
	constructor() {
		super();
	}

	static getId(): string {
		return "UnsupportedOperationException";
	}

	static getMessage(): string {
		return "unsupported operation";
	}
}
