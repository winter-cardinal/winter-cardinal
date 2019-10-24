/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { LoggerImpl as Logger } from "../util/internal/logger-impl";
import { PlainObject } from "../util/lang/plain-object";
import { InternalIo } from "./internal-io";
import { Io } from "./io";
import { IO_EVENT_CLOSE, IO_EVENT_ERROR, IO_EVENT_MESSAGE, IO_EVENT_OPEN } from "./io-events";

type InternalIoFactory = ( baseUrl: string ) => InternalIo;

/**
 * Provides the shared connection.
 */
export class SharedIo extends Io {
	private _internalIoFactory: InternalIoFactory;
	private _internalIoInstances: PlainObject<InternalIo>;

	constructor(
		name: string, protocol: string, contentPath: string, internalIoFactory: InternalIoFactory,
		internalIoInstances: PlainObject<InternalIo>
	) {
		super( name, protocol, contentPath );
		this._internalIoFactory = internalIoFactory;
		this._internalIoInstances = internalIoInstances;
	}

	protected disconnect_(): boolean {
		const baseUrl = this.getBaseUrl();
		if( baseUrl == null )  {
			return false;
		}

		const internal = this._internalIoInstances[ baseUrl ];
		if( internal == null ) {
			return false;
		}

		const ssid = this.getSsid();
		internal.off( `.${ssid}` );
		return internal.remove_( this );
	}

	protected connect_(): boolean {
		const baseUrl = this.getBaseUrl();
		if( baseUrl == null ) {
			return false;
		}

		const internals = this._internalIoInstances;
		let internal = internals[ baseUrl ];
		if( internal == null || internal.isClosed_() ) {
			this.disconnect_();
			internal = this._internalIoFactory( baseUrl );
			internals[ baseUrl ] = internal;
		}

		const ssid = this.getSsid();
		internal.on( `${IO_EVENT_OPEN}.${ssid}`, () => this.onOpen());
		internal.on( `${IO_EVENT_ERROR}.${ssid}`, () => this.onError() );
		internal.on( `${IO_EVENT_CLOSE}.${ssid}`, () => this.onClose() );
		internal.on( `${IO_EVENT_MESSAGE}.${ssid}`, ( e: {}, message: string ) => Io.onMessage( this, message ) );
		return internal.add_( this );
	}

	ping(): boolean {
		if( ! this._isConnected ) {
			return false;
		}

		const baseUrl = this.getBaseUrl();
		if( baseUrl == null ) {
			return false;
		}

		const ssid = this.getSsid();
		if( ssid == null ) {
			return false;
		}

		const internal = this._internalIoInstances[ baseUrl ];
		if( internal == null ) {
			return false;
		}

		try {
			return internal.ping_( ssid );
		} catch ( e ) {
			Logger.getInstance().error( e );
			return false;
		}
	}

	send( message: string ): boolean {
		if( ! this._isConnected ) {
			return false;
		}

		const baseUrl = this.getBaseUrl();
		if( baseUrl == null ) {
			return false;
		}

		const ssid = this.getSsid();
		if( ssid == null ) {
			return false;
		}

		const internal = this._internalIoInstances[ baseUrl ];
		if( internal == null ) {
			return false;
		}

		try {
			return internal.send_( message, ssid );
		} catch ( e ) {
			Logger.getInstance().error( e );
			return false;
		}
	}
}
