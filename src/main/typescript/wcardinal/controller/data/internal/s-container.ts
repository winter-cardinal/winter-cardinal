/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Connectable } from "../../../event/connectable";
import { SContainerMemory } from "./s-container-memory";
import { SPatch } from "./s-patch";
import { SPatches } from "./s-patches";

/**
 * A base class for all container classes.
 */
export abstract class SContainer<
	V, P extends SPatch, PS extends SPatches<V, P>,
	M extends SContainerMemory<V, P, PS>>
	extends Connectable {
	constructor( protected readonly __mem__: M ) {
		super();
	}

	/**
	 * Returns true if this is read-only.
	 *
	 * @returns true if this is read-only
	 */
	isReadOnly(): boolean {
		return this.__mem__.isReadOnly_();
	}

	/**
	 * Returns true if this is non-null.
	 *
	 * @returns true if this is non-null.
	 */
	isNonNull(): boolean {
		return this.__mem__.isNonNull_();
	}

	uninitialize(): this {
		this.__mem__.uninitialize_();
		return this;
	}

	/**
	 * Returns true if this is initialized.
	 *
	 * @returns true if this is initialized.
	 */
	isInitialized(): boolean {
		return this.__mem__.isInitialized_();
	}

	initialize(): this {
		this.__mem__.initialize_();
		return this;
	}

	/**
	 * Locks this data.
	 *
	 * @returns this
	 */
	lock(): this {
		this.__mem__.lock_();
		return this;
	}

	/**
	 * Returns true if this data is locked.
	 *
	 * @returns true if this data is locked
	 */
	isLocked(): boolean {
		return this.__mem__.isLocked_();
	}

	/**
	 * Unlocks this data.
	 *
	 * @returns this
	 */
	unlock(): this {
		this.__mem__.unlock_();
		return this;
	}

	/**
	 * Returns the JSON representing this container.
	 * Must not change the returned JSON.
	 *
	 * @returns the JSON representing this container
	 */
	abstract toJson(): unknown;

	/**
	 * Returns the string representation of this container.
	 *
	 * @returns the string representation of this container
	 */
	toString(): string {
		return this.__mem__.toString_();
	}
}
