/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Exception } from "./exception";

/**
 * Thrown to indicate that a method has been invoked with an index which is out of bounds.
 *
 *     throw IndexOutOfBoundsException.create();
 *
 * Must use `isInstance` static method instead of the `instanceof` to check
 * whether an error is an instance of this class.
 * This limitation comes from the fact that the custom exceptions inheriting
 * from the Error class are not printed well in the console.
 *
 *     const e = IndexOutOfBoundsException.create();
 *
 *     console.log( e instanceof IndexOutOfBoundsException ); // => false
 *     console.log( IndexOutOfBoundsException.isInstance( e ) ); // => true
 */
export class IndexOutOfBoundsException extends Exception {
	constructor() {
		super();
	}

	static getId(): string {
		return "IndexOutOfBoundsException";
	}

	static getMessage(): string {
		return "index out of bounds";
	}
}
