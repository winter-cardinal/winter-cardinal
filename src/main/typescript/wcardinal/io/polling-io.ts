/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Ajax } from "../util/ajax";
import { LoggerImpl as Logger } from "../util/internal/logger-impl";
import { isNotEmptyArray } from "../util/lang/is-empty";
import { Io } from "./io";
import { IoSettings } from "./io-settings";
import { Ios } from "./ios";

/**
 * Provides the polling-based connection.
 */
export class PollingIo extends Io {
	private readonly _interval: number;
	private readonly _timeout: number;
	private _timeoutId: number | null;
	private _poller: () => void;
	private _stop: boolean;

	constructor( parameters: IoSettings ) {
		parameters = parameters || {};
		const interval = (parameters.interval != null ? parameters.interval : 100);
		const timeout = (parameters.timeout != null ? parameters.timeout : 15000);

		super( `Polling-${interval}`, "http", "/wcardinal-polling" );

		this._interval = interval;
		this._timeout = timeout;
		this._timeoutId = null;
		this._poller = () => { this.poll_(); };
		this._stop = false;
	}

	protected disconnect_(): boolean {
		if( this._stop ) {
			return false;
		}
		this._stop = true;

		if( this._timeoutId != null ) {
			self.clearTimeout( this._timeoutId );
			this._timeoutId = null;
		}

		return true;
	}

	protected connect_(): boolean {
		if( this._stop ) {
			return false;
		}
		try {
			this._poller();
		} catch (e) {
			Logger.getInstance().error( e );
			return false;
		}
		return true;
	}

	poll_(): void {
		const url = this.updateUrl_();
		Ajax.getInstance().get({
			url,
			timeout: this._timeout,
			success: ( data: string ) => {
				if( ! this._isConnected ) {
					this.onOpen();
				}
				if( data != null && isNotEmptyArray( data ) ) {
					Io.onMessage( this, data );
				}
				if( this._isConnected ) {
					if( ! this._stop ) {
						this._timeoutId = self.setTimeout( this._poller, this._interval );
					} else {
						this._stop = false;
						this._timeoutId = null;
						this.onClose();
					}
				}
			},
			error: () => {
				if( this._isConnected ) {
					this.onClose();
				}
			}
		});
	}

	send( message: string ): boolean {
		if( ! this._isConnected ) {
			return false;
		}
		try {
			const url = this.updateUrl_();
			Ajax.getInstance().post({
				url,
				cache: false,
				headers: {
					"Content-Type": "application/json"
				},
				data: message,
				timeout: this._timeout,
				success: ( data: string ) => {
					if( ! this._isConnected ) {
						this.onOpen();
					}
					if( data != null && isNotEmptyArray( data ) ) {
						Io.onMessage( this, data );
					}
				},
				error: () => {
					if( this._isConnected ) {
						this.onClose();
					}
				}
			});
		} catch (e) {
			Logger.getInstance().error( e );
			return false;
		}
		return true;
	}
}

Ios.getInstance().add( "polling", PollingIo );
