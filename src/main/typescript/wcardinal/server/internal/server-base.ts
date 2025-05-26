/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Settings } from "../../controller/internal/settings";
import { Io } from "../../io/io";
import { IO_EVENT_CONNECTED, IO_EVENT_DISCONNECTED, IO_EVENT_FAILED } from "../../io/io-events";
import { IoSettings } from "../../io/io-settings";
import { Ios } from "../../io/ios";
import { Ajax, AjaxErrorHandler, AjaxSuccessHandler } from "../../util/ajax";
import { LoggerImpl as Logger } from "../../util/internal/logger-impl";
import { isNotEmptyArray } from "../../util/lang/is-empty";
import { PlainObject } from "../../util/lang/plain-object";
import { checkConnectingResults } from "./check-connecting-results";
import { makeServerUrl } from "./make-server-url";
import { sendAjaxRequest } from "./send-ajax-request";
import { ServerTriggers } from "./server-triggers";
import { toServerHeaders } from "./to-server-header";

type AckInfo = [ "a" | "b" | null, number[] ];

interface UnsentMessage {
	message: string;
	id: number;
}

const WEBSOCKET_PROTOCOL = "websocket";
const POLLING_PROTOCOL = "polling";
const SHARED_PROTOCOL = "shared-";
const SHARED_WEBSOCKET_PROTOCOL = SHARED_PROTOCOL + WEBSOCKET_PROTOCOL;
const SHARED_POLLING_PROTOCOL = SHARED_PROTOCOL + POLLING_PROTOCOL;

export class ServerBase {
	static readonly _checkingServers: ServerBase[] = [];
	static readonly _keepingServers: ServerBase[] = [];
	static readonly _pingServers: ServerBase[] = [];

	// Do not rename the following variable to `_settings`.
	// Variables named `_settings` will not be mangled by UglifyJS.
	private readonly _sttgs: Settings;
	private readonly _unsentMessages: UnsentMessage[];
	private _unsentMessageId: number;
	private _isFirst: boolean;
	private _keepAliveTimeoutId: number | null;
	private _pingTimeoutId: number | null;
	private _checkServerTimeoutId: number | null;
	private readonly _checkServerBound: () => void;
	private readonly _keepServerBound: () => void;
	private readonly _keepServerAlwaysBound: () => void;
	private readonly _pingServerBound: () => void;
	private readonly _pingServerAlwaysBound: () => void;
	private readonly _onConnectedBound: () => void;
	private readonly _onDisconnectedBound: () => void;
	private readonly _onMessageBound: ( message: string ) => void;
	private readonly _onFailBound: () => void;
	private _io: Io | null;
	private _protocol: string | null;

	protected readonly _triggers: ServerTriggers;

	constructor( settings: Settings, triggers: ServerTriggers ) {
		Logger.getInstance().info( `Server: ${settings.ssid} **${settings.path.content}` );

		this._triggers = triggers;

		this._sttgs = settings;
		this._unsentMessages = [];
		this._unsentMessageId = 0;
		this._isFirst = true;
		this._keepAliveTimeoutId = null;
		this._pingTimeoutId = null;
		this._checkServerTimeoutId = null;
		this._io = null;
		this._protocol = null;

		// For the back-forward cache issue
		if( settings.bfcached === true ) {
			Logger.getInstance().info( "Server: Detected back-forward cache" );
			this._isFirst = false;
			settings.ssid = "_";
		}

		// Bound functions
		this._checkServerBound = () => {
			return this.checkServer_();
		};

		this._keepServerBound = () => {
			return this.keepServer_();
		};

		this._keepServerAlwaysBound = () => {
			this._keepAliveTimeoutId = self.setTimeout(
				this._keepServerBound,
				this.toRandomizedInterval_( settings.keep_alive.interval )
			);

			this.callNext_( ServerBase._keepingServers, this.sendKeepRequest_ );
		};

		this._pingServerBound = () => {
			return this.pingServer_();
		};

		this._pingServerAlwaysBound = () => {
			return this.pingServerAlways_();
		};

		this._onConnectedBound = () => {
			const io = this._io;
			if( io != null ) {
				this.sendUnsents_( this._unsentMessages, io );
			}
			this._triggers._trigger( IO_EVENT_CONNECTED );
		};

		this._onDisconnectedBound = () => {
			this._triggers._trigger( IO_EVENT_DISCONNECTED );
		};

		this._onMessageBound = this.makeOnMessageBound_( settings.sync.process.interval );

		this._onFailBound = () => {
			this._checkServerTimeoutId = self.setTimeout(
				this._checkServerBound,
				this.toRandomizedInterval_( settings.retry.delay )
			);
		};
	}

	getIo_(): Io | null {
		return this._io;
	}

	getSettings_(): Settings {
		return this._sttgs;
	}

	connect_(): this {
		this.disconnect_();

		let io = this._io;
		const settings = this._sttgs;
		if( io == null ) {
			const protocol = this.getProtocol_( settings.protocols );
			if( protocol != null ) {
				const ioConstructor = Ios.getInstance().get( protocol );
				if( ioConstructor != null ) {
					io = this._io = new ioConstructor( settings.protocols[ protocol ] );
				} else {
					Logger.getInstance().info( `No I/O class found for the protocol: ${protocol}` );
					return this;
				}
			} else {
				Logger.getInstance().info( "No protocols found", settings.protocols );
				return this;
			}
		}

		io.on( IO_EVENT_CONNECTED, this._onConnectedBound );
		io.on( IO_EVENT_DISCONNECTED, this._onDisconnectedBound );
		io.addHandler( this._onMessageBound );
		io.on( IO_EVENT_DISCONNECTED, this._onFailBound );
		io.on( IO_EVENT_FAILED, this._onFailBound );

		// Establish the connection
		io.init();
		if( this._isFirst ) {
			this._isFirst = false;
			io.connect( settings.ssid );
		} else {
			this._checkServerBound();
		}
		this._keepServerBound();
		this._pingServerBound();

		return this;
	}

	disconnect_(): this {
		const io = this._io;
		if( io != null ) {
			this._io = null;
			if( io.isConnected() ) {
				this._onDisconnectedBound();
			}
			io.off( IO_EVENT_CONNECTED, this._onConnectedBound);
			io.off( IO_EVENT_DISCONNECTED, this._onDisconnectedBound);
			io.removeHandler( this._onMessageBound );
			io.off( IO_EVENT_DISCONNECTED, this._onFailBound);
			io.off( IO_EVENT_FAILED, this._onFailBound);
			io.disconnect();
			io.destroy();
		}

		if( this._keepAliveTimeoutId != null ) {
			self.clearTimeout( this._keepAliveTimeoutId );
			this._keepAliveTimeoutId = null;
		}
		this.keepServerRemove_();

		if( this._pingTimeoutId != null ) {
			self.clearTimeout( this._pingTimeoutId );
			this._pingTimeoutId = null;
		}
		this.pingServerRemove_();

		return this;
	}

	isConnected_(): boolean {
		if( this._io == null ) {
			return false;
		}
		return this._io.isConnected();
	}

	setProtocol_( protocol: string ): this {
		if( Ios.getInstance().get( protocol ) != null ) {
			this._protocol = protocol;
		} else {
			Logger.getInstance().info( `No such protocol: ${protocol}` );
		}
		return this;
	}

	getProtocol_( allowedProtocols?: PlainObject<IoSettings> ): string | null {
		return ServerBase.findProtocol_( this._protocol, allowedProtocols );
	}

	send_( type: string, content: unknown ): this {
		const messageString = ServerBase.toMessageString_( type, content );
		if( messageString != null ) {
			this.send__( messageString );
		}
		return this;
	}

	send__( data: string ): this {
		if( data != null ) {
			const io = this._io;
			if( io == null || ! io.isConnected() || io.send( `[${data}]` ) !== true ) {
				this.addUnsent_( this._unsentMessageId++, this._unsentMessages, data );
			}
		}
		return this;
	}

	ajax_(
		mode: string, data: BodyInit | null, timeout: number, headers: PlainObject<string> | null,
		onSuccess: AjaxSuccessHandler, onError: AjaxErrorHandler, context?: unknown
	): void {
		sendAjaxRequest( this._sttgs, mode, data, timeout, headers, onSuccess, onError, context );
	}

	stopServerCheck_(): void {
		const checkServerTimeoutId = this._checkServerTimeoutId;
		if( checkServerTimeoutId != null ) {
			self.clearTimeout( checkServerTimeoutId );
		}
	}

	private preprocessMessage_( message: string, messageQueue: string[], ackInfo: AckInfo ): void {
		const type = message[ 0 ];
		switch( type ) {
		case "c": // Connect message with a dynamic info
		case "u": // Update message
		case "x": // Update and ack messages
			{
				const index = message.indexOf( type, 1 );
				if( 0 <= index ) {
					const senderId = +message.substring( 1, index );

					// AckInfo
					if( type === "c" ) {
						ackInfo[ 0 ] = "b";
					} else if( ackInfo[ 0 ] !== "b" ) {
						ackInfo[ 0 ] = "a";
					}
					ackInfo[ 1 ].push( senderId );

					// Message queue
					messageQueue.push( message.substring( index, message.length ) );
				}
			}
			break;
		case "d": // Connect message without a dynamic info
			ackInfo[ 0 ] = "b";
			messageQueue.push( message );
			break;
		case "a": // Ack message
			messageQueue.push( message );
			break;
		case "e": // Empty message
			break;
		}
	}

	private flush_( messageQueue: string[], ackInfo: AckInfo ): void {
		if( isNotEmptyArray( messageQueue ) ) {
			// Send ackInfo
			const type = ackInfo[ 0 ];
			if( type != null ) {
				const senderIds = ackInfo[ 1 ];
				this.send_( type, senderIds );
				ackInfo[ 0 ] = null;
				senderIds.length = 0;
			}

			// Trigger handlers
			this._triggers._triggerHandlers( messageQueue );
			messageQueue.length = 0;
		}
	}

	private makeOnMessageBound_( interval: number ): ( message: string ) => void {
		const ackInfo: AckInfo = [ null, [] ];
		const messageQueue: string[] = [];
		if( 0 < interval ) {
			const processMessageQueue = () => {
				this.flush_( messageQueue, ackInfo );
				self.setTimeout(processMessageQueue, interval);
			};
			processMessageQueue();
			const MAXIMUM_MESSAGE_QUEUE_SIZE = 10;
			return ( message: string ) => {
				this.preprocessMessage_( message, messageQueue, ackInfo );
				if( MAXIMUM_MESSAGE_QUEUE_SIZE <= messageQueue.length ) {
					this.flush_( messageQueue, ackInfo );
				}
			};
		} else {
			return ( message: string ) => {
				this.preprocessMessage_( message, messageQueue, ackInfo );
				this.flush_( messageQueue, ackInfo );
			};
		}
	}

	private sendUnsents_( unsents: UnsentMessage[], io: Io ): void {
		if( isNotEmptyArray( unsents ) ) {
			const messages = [];
			for( let i = 0, imax = unsents.length; i < imax; ++i ) {
				messages.push( unsents[ i ].message );
			}

			if( io.send( `[${messages.join(",")}]` ) ) {
				unsents.length = 0;
			}
		}
	}

	private addUnsent_( id: number, unsents: UnsentMessage[], messageString: string ): void {
		unsents.push({ message: messageString, id });
		self.setTimeout(() => {
			this.cleanupUnsents_( unsents, id );
		}, 10000);
	}

	private cleanupUnsents_( unsents: UnsentMessage[], id: number ): void {
		let size = unsents.length;
		if( size <= 0 ) {
			return;
		}

		for( let i = 0, imax = unsents.length; i < imax; ++i ) {
			const unsent = unsents[ i ];
			if( id < unsent.id ) {
				size = i;
				break;
			}
		}

		if( 0 < size ) {
			unsents.splice( 0, size );
		}
	}

	private toRandomizedInterval_( interval: number ): number {
		const half = interval * 0.5;
		return half + Math.round(Math.random() * half);
	}

	private callNext_( servers: ServerBase[], method: ( this: ServerBase ) => void ): void {
		servers.splice(0, 1);
		if( isNotEmptyArray( servers ) ) {
			method.call( servers[ 0 ] );
		}
	}

	private sendPingRequest_(): void {
		const io = this._io;
		if( io != null && io.isConnected() ) {
			io.ping();
		}
		this._pingServerAlwaysBound();
	}

	private pingServer_(): void {
		const pingServers = ServerBase._pingServers;
		pingServers.push( this );
		if( 1 < ServerBase._pingServers.length ) {
			return;
		}
		this.sendPingRequest_();
	}

	private pingServerAlways_(): void {
		const settings = this._sttgs;
		this._pingTimeoutId = self.setTimeout(
			this._pingServerBound,
			this.toRandomizedInterval_( settings.keep_alive.ping )
		);

		this.callNext_( ServerBase._pingServers, this.sendPingRequest_ );
	}

	private pingServerRemove_(): void {
		const pingServers = ServerBase._pingServers;
		for( let i = pingServers.length - 1; 0 <= i; --i ) {
			if( pingServers[ i ] === this ) {
				pingServers.splice( i, 1 );
			}
		}
	}

	private sendKeepRequest_(): void {
		const io = this._io;
		if( io == null || ! io.isConnected() ) {
			this._keepServerAlwaysBound();
			return;
		}

		const settings = this._sttgs;
		Ajax.getInstance().get({
			url: makeServerUrl( settings ),
			cache: false,
			headers: toServerHeaders( "keep-alive", settings.ssid, null ),
			timeout: settings.keep_alive.timeout,
			complete: this._keepServerAlwaysBound
		});
	}

	private keepServer_(): void {
		const keepingServers = ServerBase._keepingServers;
		keepingServers.push( this );
		if( 1 < keepingServers.length ) {
			return;
		}
		this.sendKeepRequest_();
	}

	private keepServerRemove_(): void {
		const keepingServers = ServerBase._keepingServers;
		for( let i = keepingServers.length - 1; 0 <= i; --i ) {
			if( keepingServers[ i ] === this ) {
				keepingServers.splice( i, 1 );
			}
		}
	}

	private sendRetryRequest_(): void {
		this._checkServerTimeoutId = null;
		const settings = this._sttgs;
		Ajax.getInstance().get({
			url: makeServerUrl( settings ),
			dataType: "json",
			cache: false,
			headers: toServerHeaders( "retry", settings.ssid, null ),
			timeout: settings.retry.timeout,
			success: ( s ) => {
				settings.ssid = s.ssid;
				const io = this._io;
				if( io != null ) {
					io.connect( settings.ssid );
				}
			},
			error: ( reason, xhr ) => {
				const results = this._triggers._triggerAndGet( "connecting", null, [ null, reason, xhr.status ] );
				if( checkConnectingResults( results ) ) {
					this._checkServerTimeoutId = self.setTimeout(
						this._checkServerBound,
						this.toRandomizedInterval_( settings.retry.interval )
					);
				}
			},
			complete: () => {
				this.callNext_( ServerBase._checkingServers, this.sendRetryRequest_ );
			}
		});
	}

	private checkServer_(): void {
		const checkingServers = ServerBase._checkingServers;
		checkingServers.push( this );
		if( 1 < checkingServers.length ) {
			return;
		}
		this.sendRetryRequest_();
	}

	static toMessageString_( type: string, content: unknown ): string | null {
		try {
			return `["${type}",[${JSON.stringify( content )}]]`;
		} catch ( e ) {
			Logger.getInstance().error( e );
		}
		return null;
	}

	static findProtocol_( protocol: string | null, allowedProtocols?: PlainObject<IoSettings> ): string | null {
		if( protocol == null ) {
			const hasWebSocket = ( typeof WebSocket !== "undefined" );
			if( allowedProtocols == null ) {
				if( hasWebSocket ) {
					return WEBSOCKET_PROTOCOL;
				} else {
					return POLLING_PROTOCOL;
				}
			} else {
				if( hasWebSocket ) {
					if( WEBSOCKET_PROTOCOL in allowedProtocols ) {
						return WEBSOCKET_PROTOCOL;
					}
					if( SHARED_WEBSOCKET_PROTOCOL in allowedProtocols ) {
						return SHARED_WEBSOCKET_PROTOCOL;
					}
				}
				if( POLLING_PROTOCOL in allowedProtocols ) {
					return POLLING_PROTOCOL;
				}
				if( SHARED_POLLING_PROTOCOL in allowedProtocols ) {
					return SHARED_POLLING_PROTOCOL;
				}
				return null;
			}
		} else {
			return protocol;
		}
	}
}
