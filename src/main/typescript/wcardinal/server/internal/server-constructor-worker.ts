/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Settings } from "../../controller/internal/settings";
import { IO_EVENT_CONNECTED, IO_EVENT_DISCONNECTED } from "../../io/io-events";
import { IoSettings } from "../../io/io-settings";
import { MessageEmitter } from "../../io/message-emitter";
import { AjaxErrorHandler, AjaxSuccessHandler } from "../../util/ajax";
import { LoggerImpl as Logger } from "../../util/internal/logger-impl";
import { PlainObject } from "../../util/lang/plain-object";
import { Server } from "../server";
import { ServerConstructor } from "../server-constructor";
import { getInWorkerUrl } from "./get-in-worker-url";
import { isBFCached } from "./is-bfcached";
import { sendAjaxRequest } from "./send-ajax-request";
import { ServerBase } from "./server-base";
import { MessageType } from "./server-constructor-message-type";
import { ServerImpl } from "./server-impl";

export const ServerConstructorWorker: ServerConstructor =
( typeof Worker !== "undefined" ?
	class ServerFacade extends MessageEmitter<string[]> implements Server {
		private static readonly IN_WORKER_URL = getInWorkerUrl();
		private _protocol: string | null;
		private _isConnected: boolean;
		// Do not rename the following variable to `_settings`.
		// Variables named `_settings` will not be mangled by UglifyJS.
		private readonly _sttgs: Settings;
		private readonly _worker: Worker;

		constructor( settings: Settings ) {
			super();

			this._protocol = null;
			this._isConnected = false;
			this._sttgs = settings;
			settings.bfcached = isBFCached( settings );

			Logger.getInstance().info( `WebWorker: ${ServerFacade.IN_WORKER_URL}` );
			const worker = this._worker = new Worker( ServerFacade.IN_WORKER_URL );
			worker.postMessage( [ MessageType.CREATE, settings ] );
			worker.addEventListener( "message", ( e ) => {
				const data = e.data;
				const type = data[ 0 ];
				switch( type ) {
				case MessageType.MESSAGE:
					this.triggerHandlers( data[ 1 ] );
					break;
				case MessageType.TRIGGER:
					switch( data[ 1 ] ) {
					case IO_EVENT_CONNECTED:
						this._isConnected = true;
						break;
					case IO_EVENT_DISCONNECTED:
						this._isConnected = false;
						break;
					}
					this.triggerDirect( data[ 1 ], null, null, null );
					break;
				case MessageType.TRIGGER_AND_GET:
					worker.postMessage([
						MessageType.TRIGGER_AND_GET,
						data[ 1 ],
						this.triggerDirect( data[ 1 ], data[ 2 ], data[ 3 ], [] )
					]);
					break;
				case MessageType.SET_PROTOCOL:
					this._protocol = data[ 1 ];
					break;
				}
			});
		}

		connect(): this {
			this._worker.postMessage( [ MessageType.CONNECT ] );
			return this;
		}

		disconnect(): this {
			this._worker.postMessage( [ MessageType.DISCONNECT ] );
			return this;
		}

		isConnected(): boolean {
			return this._isConnected;
		}

		setProtocol( protocol: string ): this {
			this._worker.postMessage( [ MessageType.SET_PROTOCOL, protocol ] );
			return this;
		}

		getProtocol( allowedProtocols?: PlainObject<IoSettings> ): string | null {
			return ServerBase.findProtocol_( this._protocol, allowedProtocols );
		}

		send( type: string, content: unknown ) {
			const messageString = ServerBase.toMessageString_( type, content );
			if( messageString != null ) {
				this._worker.postMessage( [ MessageType.SEND, messageString ] );
			}
			return this;
		}

		ajax(
			mode: string, data: BodyInit | null, timeout: number, onSuccess: AjaxSuccessHandler,
			onError: AjaxErrorHandler, context?: unknown
		): void {
			sendAjaxRequest( this._sttgs, mode, data, timeout, onSuccess, onError, context );
		}
	} :
	ServerImpl
);
