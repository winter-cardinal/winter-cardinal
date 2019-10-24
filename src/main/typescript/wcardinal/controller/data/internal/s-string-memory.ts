/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { IndexOutOfBoundsException } from "../../../exception/index-out-of-bounds-exception";
import { isString } from "../../../util/lang/is-string";
import { Properties } from "../../internal/properties";
import { Lock } from "../../lock";
import { SScalarMemory } from "./s-scalar-memory";
import { SScalarParentMemory } from "./s-scalar-parent-memory";
import { SType } from "./s-type";
import { WrapperConstructor } from "./wrapper-constructor";

export class SStringMemory extends SScalarMemory<string> {
	constructor(
		parent: SScalarParentMemory, name: string, properties: Properties,
		lock: Lock, wrapperConstructor: WrapperConstructor
	) {
		super( parent, name, properties, lock, wrapperConstructor, SType.STRING );
	}

	cast_( value: unknown ): string | null {
		if( isString(value) ) {
			return value;
		}

		return null;
	}

	compareToIgnoreCase_( other: string | null ): number {
		const value = this.get_();
		if( value != null ) {
			if( other != null ) {
				const a = value.toLowerCase();
				const b = other.toLowerCase();
				return (a < b ? -1 : (a > b ? +1 : 0));
			} else {
				return +1;
			}
		} else {
			if( other != null ) {
				return -1;
			} else {
				return 0;
			}
		}
	}

	concat_( string: string | null ): string | null {
		const value = this.get_();
		if( value == null ) {
			return null;
		}
		if( string != null ) {
			return value + string;
		} else {
			return value;
		}
	}

	contains_( sequence: string ): boolean {
		return 0 <= this.indexOfString_( sequence, 0 );
	}

	endsWith_( suffix: string ): boolean {
		const value = this.get_();
		if( value == null ) {
			return false;
		}

		const length = value.length;
		return (value.substring( length - suffix.length, length ) === suffix);
	}

	equalsIgnoreCase_( other: string | null ): boolean {
		const value = this.get_();
		if( other != null ) {
			if( value != null ) {
				return value.toLowerCase() === other.toLowerCase();
			} else {
				return value === other;
			}
		} else {
			return value === other;
		}
	}

	indexOfString_( substring: string, fromIndex: number ): number {
		const value = this.get_();
		if( value == null ) {
			return -1;
		}
		return value.indexOf( substring, fromIndex );
	}

	isEmpty_(): boolean {
		return this.length_() <= 0;
	}

	lastIndexOfString_( substring: string, fromIndex: number ): number {
		const value = this.get_();
		if( value == null ) {
			return -1;
		}
		return value.lastIndexOf( substring, fromIndex );
	}

	length_(): number {
		const value = this.get_();
		if( value == null ) {
			return -1;
		}
		return value.length;
	}

	matches_( regex: RegExp|string ): boolean {
		const value = this.get_();
		if( value == null ) {
			return false;
		}
		return value.match( regex ) != null;
	}

	startsWith_( prefix: string, offset: number ): boolean {
		const value = this.get_();
		if( value == null ) {
			return false;
		}
		return (value.substring( offset, offset + prefix.length ) === prefix);
	}

	substring_( beginIndex: number, endIndex: number ): string {
		const value = this.get_();
		if( value == null ) {
			throw IndexOutOfBoundsException.create();
		}

		if( beginIndex < 0 || endIndex < beginIndex ) {
			throw IndexOutOfBoundsException.create();
		}

		return value.substring( beginIndex, endIndex );
	}

	toLowerCase_(): string | null {
		const value = this.get_();
		if( value == null ) {
			return null;
		}
		return value.toLowerCase();
	}

	toUpperCase_(): string | null {
		const value = this.get_();
		if( value == null ) {
			return null;
		}
		return value.toUpperCase();
	}

	trim_(): string | null {
		const value = this.get_();
		if( value == null ) {
			return null;
		}
		return value.trim();
	}

	toString_(): string {
		return String(this.get_());
	}

	fromString_( str: string ): void {
		this.set_( str, false );
	}
}
