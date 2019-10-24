/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Logger, LogLevel } from "../logger";

export class LoggerImpl extends Logger {
	private static INSTANCE: Logger | null = null;
	private static _LOG_LEVELS: LogLevel[] = [ "debug", "info", "warn", "error", "log" ];
	private _level!: LogLevel;

	constructor() {
		super();
		this.setLevel( "debug" );
	}

	setLevel( newLevel: LogLevel ): Logger {
		const LOG_LEVELS = LoggerImpl._LOG_LEVELS;
		const index = LOG_LEVELS.indexOf( newLevel );
		if( 0 <= index ) {
			this._level = newLevel;

			const target = this as any;
			for( let i = 0; i < index; ++i ) {
				target[ LOG_LEVELS[ i ] ] = LoggerImpl.noop_;
			}

			for( let i = index, imax = LOG_LEVELS.length; i < imax; ++i ) {
				const level = LOG_LEVELS[ i ];
				target[ level ] = LoggerImpl.getConsoleMethod_( level );
			}
		}
		return this;
	}

	getLevel(): string {
		return this._level;
	}

	debug(): void {
		// DO NOTHING
	}

	info(): void {
		// DO NOTHING
	}

	warn(): void {
		// DO NOTHING
	}

	error(): void {
		// DO NOTHING
	}

	log(): void {
		// DO NOTHING
	}

	static getInstance(): Logger {
		if( this.INSTANCE == null ) {
			this.INSTANCE = new LoggerImpl();
		}
		return this.INSTANCE;
	}

	private static noop_() {
		/* DO NOTHING */
	}

	private static getConsoleMethod_( level: LogLevel ) {
		if( typeof console === "undefined" ) {
			return this.noop_;
		} else {
			if( Function.prototype.bind ) {
				const c = console;
				const m = c[ level ] || c.log;
				return Function.prototype.bind.call( m, c );
			} else {
				const c = console;
				const m = c[ level ] || c.log;
				// tslint:disable-next-line:only-arrow-functions
				return function() {
					Function.prototype.apply.call( m, c, arguments );
				};
			}
		}
	}
}
