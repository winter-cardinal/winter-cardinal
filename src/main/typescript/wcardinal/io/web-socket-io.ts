/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { LoggerImpl as Logger } from "../util/internal/logger-impl";
import { Io } from "./io";
import { IoSettings } from "./io-settings";
import { Ios } from "./ios";

/**
 * Provides the WebSocket connection.
 */
export class WebSocketIo extends Io {
	private _ws: WebSocket | null = null;

	constructor( parameters: IoSettings ) {
		super( "WebSocket", "ws", "/wcardinal-websocket" );
	}

	protected disconnect_(): boolean {
		const ws = this._ws;
		if( ws == null ) {
			return false;
		}

		ws.close();
		this._ws = null;
		return true;
	}

	protected connect_(): boolean {
		const ws1 = this._ws;
		if( ws1 != null ) {
			if( ws1.readyState !== WebSocket.CLOSED ) {
				return true;
			} else {
				ws1.onopen = null;
				ws1.onerror = null;
				ws1.onclose = null;
				ws1.onmessage = null;
			}
		}

		const url = this.getUrl();
		if( url != null ) {
			try {
				const ws2 = this._ws = new WebSocket( url );
				ws2.onopen    = () => this.onOpen();
				ws2.onerror   = () => this.onError();
				ws2.onclose   = () => this.onClose();
				ws2.onmessage = ( message ) => Io.onMessage( this, message.data );
				return true;
			} catch (e) {
				Logger.getInstance().error( e );
			}
		}
		return false;
	}

	ping(): boolean {
		return this.send( "[]" );
	}

	send( message: string ): boolean {
		if( ! this._isConnected ) {
			return false;
		}

		const ws = this._ws;
		if( ws == null ) {
			return false;
		}

		try {
			ws.send( message );
			return true;
		} catch (e) {
			Logger.getInstance().error( e );
			return false;
		}
	}
}

Ios.getInstance().add( "websocket", WebSocketIo );
