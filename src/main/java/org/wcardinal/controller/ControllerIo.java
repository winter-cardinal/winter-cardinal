/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller;

public interface ControllerIo {
	/**
	 * Returns the session ID.
	 *
	 * @return the session ID
	 */
	String getSessionId();

	/**
	 * Returns the sub session ID.
	 *
	 * @return the sub session ID
	 */
	String getSubSessionId();

	/**
	 * Returns the idle time.
	 *
	 * @return the idle time
	 */
	long getIdleTime();

	/**
	 * Returns the last accessed time.
	 *
	 * @return the last accessed time
	 */
	long getLastAccessedTime();

	/**
	 * Returns true if successfully the last accessed time is updated.
	 *
	 * @return true if successfully the last accessed time is updated
	 */
	boolean touch();

	/**
	 * Returns the maximum idle time.
	 *
	 * @return the maximum idle time
	 */
	long getMaximumIdleTime();

	/**
	 * Returns true if a network connection seems to be available.
	 * An availability of a network connection is not guaranteed even when this method returns true.
	 *
	 * @return true if a network connection seems to be available
	 */
	boolean hasConnection();

	/**
	 * Returns true once a network connection has established
	 * regardless of whether a network connection is now available or not.
	 *
	 * @return true once a network connection has established regardless of whether a network connection is now available or not
	 */
	boolean hadConnection();
}
