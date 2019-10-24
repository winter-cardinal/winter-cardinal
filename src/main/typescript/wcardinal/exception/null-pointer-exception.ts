/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Exception } from "./exception";

/**
 * Thrown to indicate that a method has been invoked with a null argument which is not supported.
 *
 *     throw NullPointerException.create();
 *
 * Must use `isInstance` static method instead of the `instanceof` to check
 * whether an error is an instance of this class.
 * This limitation comes from the fact that the custom exceptions inheriting
 * from the Error class are not printed well in the console.
 *
 *     const e = NullPointerException.create();
 *
 *     console.log( e instanceof NullPointerException ); // => false
 *     console.log( NullPointerException.isInstance( e ) ); // => true
 */
export class NullPointerException extends Exception {
	constructor() {
		super();
	}

	static getId(): string {
		return "NullPointerException";
	}
	static getMesage(): string {
		return "unexpected null";
	}
}
