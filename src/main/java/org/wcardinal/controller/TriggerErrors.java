/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller;

/**
 * Errors occured while triggering events.
 */
public enum TriggerErrors {
	/**
	 * A response received from a browser was malformed.
	 */
	MALFORMED,

	/**
	 * Timeout occurred while waiting for a response from a browser.
	 */
	TIMEOUT,

	/**
	 * Exceeded maximum queue size.
	 *
	 * @see org.wcardinal.configuration.WCardinalConfiguration#getMaximumTriggerQueueSize()
	 * @see org.wcardinal.configuration.WCardinalConfiguration#setMaximumTriggerQueueSize(int)
	 */
	EXCEEDED
}
