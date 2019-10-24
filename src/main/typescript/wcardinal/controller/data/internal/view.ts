/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Connectable } from "../../../event/connectable";
import { ViewMemory } from "./view-memory";
import { ViewTarget } from "./view-target";

/**
 * A base class for all view classes.
 */
export class View<M extends ViewMemory<T, W>, W extends Connectable, T extends ViewTarget> extends Connectable {
	constructor( protected readonly __mem__: M ) {
		super();
	}

	/**
	 * Returns true if this is read-only.
	 *
	 * @returns true if this is read-only
	 */
	isReadOnly() {
		return this.__mem__.isReadOnly_();
	}

	/**
	 * Returns true if this is non-null.
	 *
	 * @returns true if this is non-null.
	 */
	isNonNull() {
		return this.__mem__.isNonNull_();
	}

	/**
	 * Returns true if this is initialized.
	 *
	 * @returns true if this is initialized.
	 */
	isInitialized() {
		return this.__mem__.isInitialized_();
	}

	/**
	 * Locks this data.
	 *
	 * @returns this
	 */
	lock() {
		return this.__mem__.lock_();
	}

	/**
	 * Returns true if this data is locked.
	 *
	 * @returns true if this data is locked
	 */
	isLocked() {
		return this.__mem__.isLocked_();
	}

	/**
	 * Unlocks this data.
	 *
	 * @returns this
	 */
	unlock() {
		return this.__mem__.unlock_();
	}
}
