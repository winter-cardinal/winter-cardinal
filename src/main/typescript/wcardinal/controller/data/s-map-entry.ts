/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

/**
 * A map entry (key-value pair).
 *
 * @param V a value type
 */
export class SMapEntry<V> {
	private __mem__: {
		_key: string,
		_value: V | null
	};

	constructor( key: string, value: V | null ) {
		this.__mem__ = {
			_key: key,
			_value: value
		};
	}

	/**
	 * Returns a key of this entry.
	 *
	 * @returns a key of this entry
	 */
	getKey(): string {
		return this.__mem__._key;
	}

	/**
	 * Returns a value of this entry.
	 *
	 * @returns a value of this entry
	 */
	getValue(): V | null {
		return this.__mem__._value;
	}
}
