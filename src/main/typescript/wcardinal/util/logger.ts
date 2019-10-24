/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { UnsupportedOperationException } from "../exception/unsupported-operation-exception";

export type LogLevel = "debug"|"info"|"warn"|"error"|"log";

/**
 * Provides configurable logging functions.
 * Please note that the default log level is `debug`.
 *
 *     logger.log( "log message" );
 */
export abstract class Logger {
	/**
	 * Sets the log level.
	 * The log level must be one of the following:
	 *
	 * * debug
	 * * info
	 * * wrn
	 * * error
	 * * log
	 *
	 * The order of levels is `log` > `error` > `warn` > `info` > `debug`.
	 * Logs which are higher than or equals to the log level are logged.
	 * Setting the log level to `warn`, for instance, logs of the levels `error` and `warn` are logged.
	 * The default log level is `debug`.
	 *
	 *     logger.setLevel( "debug" );
	 *
	 * @param newLevel the log level
	 * @returns this;
	 */
	abstract setLevel( newLevel: LogLevel ): Logger;

	/**
	 * Returns the log level.
	 *
	 *     logger.getLevel();
	 *
	 * @returns the log level
	 */
	abstract getLevel(): string;

	/**
	 * Logs the specified message at the `debug` level.
	 *
	 *     logger.debug( "debug message" );
	 *
	 * @returns
	 */
	abstract debug( ...args: unknown[] ): void;

	/**
	 * Logs the specified message at the `info` level.
	 *
	 *     logger.info( "infomation message" );
	 *
	 * @returns
	 */
	abstract info( ...args: unknown[] ): void;

	/**
	 * Logs the specified message at the `info` level.
	 *
	 *     logger.warn( "warning message" );
	 *
	 * @returns
	 */
	abstract warn( ...args: unknown[] ): void;

	/**
	 * Logs the specified message at the `error` level.
	 *
	 *     logger.error( "error message" );
	 *
	 * @returns
	 */
	abstract error( ...args: unknown[] ): void;

	/**
	 * Logs the specified message at the `log` level.
	 *
	 *     logger.log( "log message" );
	 *
	 * @returns {void}
	 */
	abstract log( ...args: unknown[] ): void;

	// Instance
	static getInstance(): Logger {
		throw UnsupportedOperationException.create();
	}
}
