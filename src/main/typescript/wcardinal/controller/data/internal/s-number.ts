/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { SNumberMemory } from "./s-number-memory";
import { SScalar } from "./s-scalar";

/**
 * Represents a number.
 */
export class SNumber<M extends SNumberMemory> extends SScalar<number, M> {
	constructor( memory: M ) {
		super( memory );
	}

	/**
	 * Adds the specified value to the current value if the current value is not null.
	 *
	 * @param delta value to add
	 * @returns updated value
	 * @throws Error if this is read-only
	 */
	addAndGet( delta: number ): number | null {
		return this.__mem__.addAndGet_( delta );
	}

	/**
	 * Adds the specified value to the current value if the current value is not null.
	 *
	 * @param delta value to add
	 * @returns previous value
	 * @throws Error if this is read-only
	 */
	getAndAdd( delta: number ): number | null {
		return this.__mem__.getAndAdd_( delta );
	}

	/**
	 * Decrements by one the current value if the current value is not null.
	 *
	 * @returns updated value
	 * @throws Error if this is read-only
	 */
	decrementAndGet(): number | null {
		return this.__mem__.decrementAndGet_();
	}

	/**
	 * Decrements by one the current value if the current value is not null.
	 *
	 * @returns previous value
	 * @throws Error if this is read-only
	 */
	getAndDecrement(): number | null {
		return this.__mem__.getAndDecrement_();
	}

	/**
	 * Increments by one the current value if the current value is not null.
	 *
	 * @returns previous value
	 * @throws Error if this is read-only
	 */
	getAndIncrement(): number | null {
		return this.__mem__.getAndIncrement_();
	}

	/**
	 * Increments by one the current value if the current value is not null.
	 *
	 * @returns updated value
	 * @throws Error if this is read-only
	 */
	incrementAndGet(): number | null {
		return this.__mem__.incrementAndGet_();
	}
}
