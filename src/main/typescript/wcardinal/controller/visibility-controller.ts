/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Controller } from "./controller";

/**
 * Provides visibility functions.
 */
export interface VisibilityController extends Controller {
	/**
	 * Returns the display name.
	 *
	 * @returns display name
	 */
	getDisplayName(): string | null;

	/**
	 * Sets the display name to the specified string.
	 *
	 * @param displayName display name
	 * @returns this
	 */
	setDisplayName( displayName: string ): this;
}
