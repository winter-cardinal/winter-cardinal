/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Ajax } from "../util/ajax";
import { LoggerImpl as Logger } from "../util/internal/logger-impl";
import { isNotEmptyArray } from "../util/lang/is-empty";
import { InternalIo } from "./internal-io";
import { IoSettings } from "./io-settings";

export class InternalPollingIo extends InternalIo {
	private readonly _interval: number;
	private readonly _timeout: number;
	private _timeoutId: number | null;
	private readonly _poller: () => void;

	constructor( baseUrl: string, parameters: IoSettings ) {
		parameters = parameters || {};
		const interval = (parameters.interval != null ? parameters.interval : 100);
		const timeout = (parameters.timeout != null ? parameters.timeout : 15000);

		super( baseUrl );

		this._interval = interval;
		this._timeout = timeout;
		this._timeoutId = null;
		this._poller = () => { this.poll_(); };
	}

	protected init__(): void {
		this.poll_();
	}

	protected cleanup_(): void {
		if( this._timeoutId != null ) {
			self.clearTimeout( this._timeoutId );
			this._timeoutId = null;
		}
	}

	private poll_(): void {
		const url = this.updateUrl_();
		Ajax.getInstance().get({
			url,
			timeout: this._timeout,
			success: ( data: string ) => {
				if( ! this.isOpened_() ) {
					this.onOpen_();
				}
				if( data != null && isNotEmptyArray( data ) ) {
					this.onMessage_( data );
				}
				if( this.isOpened_() ) {
					this._timeoutId = self.setTimeout( this._poller, this._interval );
				}
			},
			error: () => {
				if( this.isOpened_() ) {
					this.onClose_();
				}
			}
		});
	}

	ping_( ssid: string ): boolean {
		// DO NOTHING
		return true;
	}

	send_( message: string|number, ssid: string ): boolean {
		if( ! this.isOpened_() ) {
			return false;
		}
		const url = this.updateUrl_();

		try {
			Ajax.getInstance().post({
				url,
				cache: false,
				headers: {
					"Content-Type": "application/json"
				},
				data: JSON.stringify([ssid, message]),
				timeout: this._timeout,
				success: ( data: string ) => {
					if( ! this.isOpened_() ) {
						this.onOpen_();
					}
					if( data != null && isNotEmptyArray( data ) ) {
						this.onMessage_( data );
					}
				},
				error: () => {
					if( this.isOpened_() ) {
						this.onClose_();
					}
				}
			});
		} catch ( e ) {
			Logger.getInstance().error( e );
			return false;
		}
		return true;
	}
}
