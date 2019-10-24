/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { LoggerImpl as Logger } from "../util/internal/logger-impl";
import { InternalIo } from "./internal-io";

export class InternalWebSocketIo extends InternalIo {
	private _ws: WebSocket | null = null;

	constructor( baseUrl: string ) {
		super( baseUrl );
	}

	protected init__(): void {
		const url = this.updateUrl_();
		try {
			const ws = this._ws = new WebSocket( url );
			ws.onopen    = () => this.onOpen_();
			ws.onerror   = () => this.onError_();
			ws.onclose   = () => this.onClose_();
			ws.onmessage = ( message ) => this.onMessage_( message.data );
		} catch ( e ) {
			Logger.getInstance().error( e );
		}
	}

	protected cleanup_(): void {
		const ws = this._ws;
		if( ws != null ) {
			this._ws = null;
			ws.onopen    = null;
			ws.onerror   = null;
			ws.onclose   = null;
			ws.onmessage = null;
		}
	}

	ping_( ssid: string ): boolean {
		return this.send_( "[]", ssid );
	}

	send_( message: string | number, ssid: string ): boolean {
		if( ! this.isOpened_() ) {
			return false;
		}
		try {
			this._ws!.send( JSON.stringify([ssid, message]) );
		} catch ( e ) {
			Logger.getInstance().error( e );
			return false;
		}
		return true;
	}
}
