/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { ID_KEY } from "./internal/id-key";

/**
 * Central class for custom exceptions.
 *
 *     throw Exception.create();
 *
 * Must use [[isInstance]] static method instead of the `instanceof` to check
 * whether an error is an instance of this class.
 * This limitation comes from the fact that the custom exceptions inheriting
 * from the Error class are not printed well in the console.
 *
 *     const e = Exception.create();
 *
 *     console.log( e instanceof Exception ); // => false
 *     console.log( Exception.isInstance( e ) ); // => true
 */
export class Exception {
	/**
	 * Must not call this constructor.
	 *
	 * @private
	 */
	constructor() {
		throw new Error( "Must not call a constructor" );
	}

	/**
	 * Returns a new exception.
	 * The returned instance is not an instance of this class in the prototypical sense.
	 * Thus must use [[isInstance]] to check whether or not an instance is an instance of this class.
	 *
	 * @param message a message of a new exception
	 * @returns a new exception
	 */
	static create( message: string = this.getMessage() ): Error {
		const result = new Error( message );
		(result as any)[ ID_KEY ] = this.getId();
		return result;
	}

	/**
	 * Returns true if the specified error is an instance of this class.
	 *
	 * @param error an error to be checked
	 * @returns true if the specified error is an instance of this class
	 */
	static isInstance( error: Error ): boolean {
		return (error as any)[ ID_KEY ] === this.getId();
	}

	static getId(): string {
		return "Exception";
	}

	static getMessage(): string {
		return "";
	}
}
