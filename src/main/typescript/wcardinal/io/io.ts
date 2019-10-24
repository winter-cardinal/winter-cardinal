/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { addEventListener } from "../util/dom/add-event-listener";
import { removeEventListener } from "../util/dom/remove-event-listener";
import { LoggerImpl as Logger } from "../util/internal/logger-impl";
import { IO_EVENT_CONNECTED, IO_EVENT_DISCONNECTED, IO_EVENT_ERROR, IO_EVENT_FAILED } from "./io-events";
import { MessageEmitter } from "./message-emitter";

const makeBaseUrl = ( protocol: string, contentPath: string ): string => {
	protocol = protocol + (location.protocol === "https:" ? "s" : "");
	const host = location.host;
	const pathname = location.pathname;
	const basePath = pathname.substr( 0, pathname.lastIndexOf( "/" ) );
	return `${protocol}://${host}${basePath}${contentPath}`;
};

const makeUrl = ( baseUrl: string, ssid: string ): string => {
	return `${baseUrl}?ssid=${ssid}`;
};

/**
 * Provides methods for communicating with a server.
 */
export abstract class Io extends MessageEmitter<string> {
	private _onUnloadBound: (() => void) | null;
	private _ssid: string | null;
	private _name: string;
	private _protocol: string;
	private _contentPath: string;
	private _url: string | null;
	private _baseUrl: string | null;
	protected _isConnected: boolean;

	protected constructor( name: string, protocol: string, contentPath: string ) {
		super();

		if( typeof window !== "undefined" ) {
			this._onUnloadBound = () => { this.onUnload(); };
			addEventListener( window, "unload", this._onUnloadBound );
		} else {
			this._onUnloadBound = null;
		}

		this._ssid = null;
		this._name = name;
		this._protocol = protocol;
		this._contentPath = contentPath;
		this._url = null;
		this._baseUrl = null;
		this._isConnected = false;
	}

	getProtocol(): string {
		return this._protocol;
	}

	getContentPath(): string {
		return this._contentPath;
	}

	getSsid(): string | null {
		return this._ssid;
	}

	getBaseUrl(): string | null {
		return this._baseUrl;
	}

	getUrl(): string | null {
		return this._url;
	}

	protected updateUrl_(): string {
		return this._url = makeUrl( this._baseUrl!, this._ssid! );
	}

	/**
	 * Initializes this instance.
	 *
	 * @returns true if the initialization is finished successfully
	 */
	init(): boolean {
		this._baseUrl = makeBaseUrl( this._protocol, this._contentPath );
		return true;
	}

	/**
	 * Starts to connect to the server.
	 *
	 * @param ssid SSID of the connection
	 * @returns true if it started the connection successfully
	 */
	connect( ssid: string ): boolean {
		if( this._baseUrl == null ) {
			return false;
		}
		this._ssid = ssid;
		this.updateUrl_();
		Logger.getInstance().info(`${this._name} connection: ${this._url}`);
		return this.connect_();
	}

	protected abstract connect_(): boolean;

	/**
	 * Destroys this instance.
	 *
	 * @returns this
	 */
	destroy(): Io {
		if( this._onUnloadBound != null ) {
			if( typeof window !== "undefined" ) {
				removeEventListener( window, "unload", this._onUnloadBound );
			}
			this._onUnloadBound = null;
		}
		return this;
	}

	/**
	 * Starts to disconnect from the server.
	 *
	 * @returns {boolean} true if started to disconnect from the server successfully.
	 */
	disconnect(): boolean {
		if( ! this._isConnected ) {
			return false;
		}
		return this.disconnect_();
	}

	protected abstract disconnect_(): boolean;

	/**
	 * Called when the connection is established.
	 *
	 * @returns
	 */
	onOpen(): void {
		if( this._isConnected ) {
			return;
		}
		Logger.getInstance().info(`${this._name} opened`);
		this._isConnected = true;
		this.triggerDirect( IO_EVENT_CONNECTED, null, null, null );
	}

	/**
	 * Called when the connection is completed with errors.
	 *
	 * @returns
	 */
	onError(): void {
		Logger.getInstance().error(`${this._name} error`, arguments );
		this.triggerDirect( IO_EVENT_ERROR, null, null, null );
	}

	/**
	 * Returns true if the connection is established.
	 *
	 * @returns true if the connection is established
	 */
	isConnected(): boolean {
		return this._isConnected;
	}

	/**
	 * Called when the connection is closed.
	 *
	 * @returns
	 */
	onClose(): void {
		if( this._isConnected ) {
			Logger.getInstance().info(`${this._name} closed`);
			this._isConnected = false;
			this.triggerDirect( IO_EVENT_DISCONNECTED, null, null, null );
		} else {
			Logger.getInstance().info(`${this._name} failed`);
			this.triggerDirect( IO_EVENT_FAILED, null, null, null );
		}
	}

	/**
	 * Called when about to be unloaded.
	 * @returns
	 */
	onUnload(): void {
		Logger.getInstance().info(`${this._name} unloaded`);
		this.disconnect_();
	}

	/**
	 * Sends the ping message to the server.
	 *
	 * @returns
	 */
	ping(): boolean {
		return true;
	}

	/**
	 * Sends the specified message to the server.
	 *
	 * @param message the message to be sended
	 * @returns true if succeeded
	 */
	send( message: string ): boolean {
		return true;
	}

	/**
	 * Called when received a message.
	 *
	 * @param thisObj the target
	 * @param message the received message
	 * @returns
	 */
	static onMessage( io: Io, message: string ): void {
		io.triggerHandlers( message );
	}
}
