/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { isNumber } from "../../util/lang/is-number";
import { Comparator } from "../internal/comparator";
import { Properties } from "../internal/properties";
import { Lock } from "../lock";
import { SScalar, toSScalarValue } from "./internal/s-scalar";
import { SScalarParentMemory } from "./internal/s-scalar-parent-memory";
import { SStringMemory } from "./internal/s-string-memory";
import { SType } from "./internal/s-type";
import { STypeToClass } from "./internal/s-type-to-class";

/**
 * A synchronized string.
 */
export class SString extends SScalar<string, SStringMemory> {
	constructor( memory: SStringMemory ) {
		super( memory );
	}

	/**
	 * Compares this string and the specified string lexicographically, ignoring case differences.
	 *
	 * @param string string to be compared
	 * @returns a negative integer, zero, or a positive integer as this string is less than,
	 * equal to, or greater than the specified string
	 */
	compareToIgnoreCase( string: string | null ): number;
	compareToIgnoreCase( sscalar: SScalar<string> ): number;
	compareToIgnoreCase( stringOrSScalar: string | null | SScalar<string> ): number {
		return this.__mem__.compareToIgnoreCase_( toSScalarValue( stringOrSScalar ) );
	}

	/**
	 * Concatenates the specified string to the end of this string and returns a concatenated string.
	 * Returns null if this string is null.
	 *
	 * @param string string to be concatenated
	 * @returns concatenated string
	 */
	concat( string: string | null ): string | null {
		return this.__mem__.concat_( string );
	}

	/**
	 * Returns true if this string contains the specified string.
	 *
	 * @param sequence sequence to search for
	 * @returns true if this string contains the specified string
	 */
	contains( sequence: string ): boolean {
		return this.__mem__.contains_( sequence );
	}

	/**
	 * Returns true if this string ends with the specified suffix.
	 *
	 * @param suffix suffix to search for
	 * @returns true if this string ends with the specified suffix
	 */
	endsWith( suffix: string ): boolean {
		return this.__mem__.endsWith_( suffix );
	}

	/**
	 * Returns true if this string is equal to the specified string, ignoring case considerations.
	 *
	 * @param string string to be compared
	 * @returns true if this string is equal to the specified string ignoring case considerations
	 */
	equalsIgnoreCase( string: string | null ): boolean;
	equalsIgnoreCase( sscalar: SScalar<string> ): boolean;
	equalsIgnoreCase( stringOrSScalar: string | null | SScalar<string> ): boolean {
		return this.__mem__.equalsIgnoreCase_( toSScalarValue( stringOrSScalar ) );
	}

	/**
	 * Returns the index within this string of the first occurrence of the specified substring,
	 * starting at the specified index.
	 * Returns -1 if no such substring occurs in this string.
	 *
	 * @param substring substring to search for
	 * @param fromIndex where to begin searching the specified substring
	 * @returns index within this string of the first occurrence of the specified substring
	 */
	indexOf( substring: string, fromIndex?: number ): number;
	indexOf( value: unknown, comparator?: Comparator, thisArg?: unknown ): number;
	indexOf( substringOrValue: unknown, fromIndexOrComparator?: number | Comparator<string>, thisArg?: unknown ): number {
		const m = this.__mem__;
		if( fromIndexOrComparator == null ) {
			return m.indexOfString_( substringOrValue as string, 0 );
		} else if( isNumber(fromIndexOrComparator) ) {
			return m.indexOfString_( substringOrValue as string, fromIndexOrComparator );
		} else {
			return m.indexOf_( substringOrValue, fromIndexOrComparator as Comparator, thisArg );
		}
	}

	/**
	 * Returns true if length() is less than or equal to 0.
	 *
	 * @returns true if length() is less than or equal to 0
	 */
	isEmpty(): boolean {
		return this.__mem__.isEmpty_();
	}

	/**
	 * Returns the index within this string of the last occurrence of the specified substring,
	 * searching backward starting at the specified index.
	 * Returns -1 if no such substring occurs in this string.
	 *
	 * @param substring substring to search for
	 * @param fromIndex where to begin searching the specified substring
	 * @returns index within this string of the last occurrence of the specified substring
	 */
	lastIndexOf( substring: string, fromIndex?: number ): number;
	lastIndexOf( value: unknown, comparator?: Comparator, thisArg?: unknown ): number;
	lastIndexOf(
		substringOrValue: unknown, fromIndexOrComparator?: number | Comparator<string>, thisArg?: unknown
	): number {
		const m = this.__mem__;
		if( fromIndexOrComparator == null ) {
			return m.lastIndexOfString_( substringOrValue as string, +Infinity );
		} else if( isNumber( fromIndexOrComparator) ) {
			return m.lastIndexOfString_( substringOrValue as string, fromIndexOrComparator );
		} else {
			return m.lastIndexOf_( substringOrValue, fromIndexOrComparator as Comparator, thisArg );
		}
	}

	/**
	 * Returns the length of this string.
	 * Returns -1 if this string is null.
	 *
	 * @returns length of this string
	 */
	length(): number {
		return this.__mem__.length_();
	}

	/**
	 * Returns true if this string matches the specified regular expression.
	 * Returns false if this string is null.
	 *
	 * @param regex regular expression to which this string is to be matched
	 * @returns true if this string matches the specified regular expression
	 */
	matches( regex: string ): boolean;
	matches( regex: RegExp ): boolean;
	matches( regex: RegExp | string ): boolean {
		return this.__mem__.matches_( regex );
	}

	/**
	 * Returns true if the substring of this string beginning at the specified index starts with the specified prefix.
	 * Returns false if this string is null.
	 *
	 * @param prefix prefix to search for
	 * @param offset where to begin looking in this string
	 * @returns true if the substring of this string beginning at the specified index starts with the specified prefix
	 */
	startsWith( prefix: string, offset: number= 0 ): boolean {
		return this.__mem__.startsWith_( prefix, offset );
	}

	/**
	 * Returns a new string that is a substring of this string.
	 *
	 * @param beginIndex beginning index (inclusive)
	 * @param endIndex ending index (exclusive)
	 * @returns substring of this string
	 * @throws Error if beginIndex is negative or larger than the length of this string object
	 */
	substring( beginIndex: number, endIndex: number= this.length() ): string {
		return this.__mem__.substring_( beginIndex, endIndex );
	}

	/**
	 * Converts all of the characters in this string to lower case using the rules of the default locale and returns it.
	 * Returns null if this string is null.
	 *
	 * @returns string converted to lowercase
	 */
	toLowerCase(): string | null {
		return this.__mem__.toLowerCase_();
	}

	/**
	 * Converts all of the characters in this string to upper case using the rules of the default locale and returns it.
	 * Returns null if this string is null.
	 *
	 * @returns string converted to uppercase
	 */
	toUpperCase(): string | null {
		return this.__mem__.toUpperCase_();
	}

	/**
	 * Returns a copy of this string, with leading and trailing whitespace omitted.
	 * Returns null if this string is null.
	 *
	 * @returns a copy of this string without leading and trailing whitespace
	 */
	trim(): string | null {
		return this.__mem__.trim_();
	}

	toString(): string {
		return this.__mem__.toString_();
	}

	fromString( str: string ): this {
		this.__mem__.fromString_( str );
		return this;
	}
}

STypeToClass.put_({
	create_( parent: SScalarParentMemory, name: string, properties: Properties, lock: Lock ) {
		return new SStringMemory( parent, name, properties, lock, SString );
	},

	getConstructor_() {
		return SString;
	},

	getType_() {
		return SType.STRING;
	}
});
