/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Connectable } from "../event/connectable";
import { LoggerImpl as Logger } from "../util/internal/logger-impl";
import { PlainObject } from "../util/lang/plain-object";
import { IO_EVENT_CLOSE, IO_EVENT_ERROR, IO_EVENT_MESSAGE, IO_EVENT_OPEN } from "./io-events";
import { SharedIo } from "./shared-io";

const enum State {
	INITIAL,
	CONNECTING,
	CONNECTED,
	DISCONNECTED
}

interface SubSession {
	io: SharedIo;
	state: State;
}

export abstract class InternalIo extends Connectable {
	private _baseUrl: string;
	private _url: string | null;
	private _state: State;
	private _subSessions: PlainObject<SubSession>;

	constructor( baseUrl: string ) {
		super();
		this._baseUrl = baseUrl;
		this._url = null;
		this._state = State.INITIAL;
		this._subSessions = {};
	}

	init_(): boolean {
		if( this._state !== State.INITIAL ) {
			return false;
		}

		this._state = State.CONNECTING;
		const subSessions = this._subSessions;
		for( const ssid in subSessions ) {
			subSessions[ ssid ].state = State.CONNECTING;
		}
		this.init__();
		return true;
	}

	protected abstract init__(): void;

	onOpen_(): void {
		if( this._state !== State.CONNECTING ) {
			return;
		}

		this._state = State.CONNECTED;
		const subSessions = this._subSessions;
		for( const ssid in subSessions ) {
			const subSession = subSessions[ ssid ];
			if( subSession.state === State.INITIAL ) {
				subSession.state = State.CONNECTED;
				this.add__( ssid );
			} else if( subSession.state === State.CONNECTING ) {
				subSession.state = State.CONNECTED;
			}
			this.triggerDirect( IO_EVENT_OPEN, ["", ssid], null, null );
		}
	}

	add_( io: SharedIo ): boolean {
		if( this._state === State.DISCONNECTED ) {
			return false;
		}

		const ssid = io.getSsid();
		if( ssid == null ) {
			return false;
		}

		// Add/update sub session
		let subSession = this._subSessions[ ssid ];
		if( subSession == null ) {
			subSession = { io, state: State.INITIAL };
			this._subSessions[ ssid ] = subSession;
		} else {
			subSession.io = io;
		}

		// Initialize sub session
		switch( this._state ) {
		case State.INITIAL:
			this.init_();
			break;
		case State.CONNECTED:
			subSession.state = State.CONNECTED;
			this.add__( ssid );
			this.triggerDirect( IO_EVENT_OPEN, ["", ssid], null, null );
			break;
		}

		return true;
	}

	protected add__( ssid: string ): void {
		this.send_( 0, ssid );
	}

	remove_( io: SharedIo ): boolean {
		const ssid = io.getSsid();
		if( ssid == null ) {
			return false;
		}

		const subSessions = this._subSessions;
		if( ssid in subSessions ) {
			delete subSessions[ ssid ];
			return true;
		} else {
			return false;
		}
	}

	protected updateUrl_(): string {
		const ssids: string[] = Object.keys( this._subSessions );
		this._url = `${this._baseUrl}?ssids=${ssids.join(",")}`;
		return this._url;
	}

	onError_(): void {
		this.triggerDirect( IO_EVENT_ERROR, null, null, null );
	}

	onClose_(): void {
		if( this._state === State.DISCONNECTED ) {
			return;
		}

		this._state = State.DISCONNECTED;
		this._subSessions = {};
		this.cleanup_();
		this.triggerDirect( IO_EVENT_CLOSE, null, null, null );
	}

	protected abstract cleanup_(): void;

	isOpened_(): boolean {
		return (this._state === State.CONNECTED);
	}

	isClosed_(): boolean {
		return (this._state === State.DISCONNECTED);
	}

	onMessage_( message: string ): void {
		try {
			const index = message.indexOf( ":" );
			const ssid = message.substring( 0, index );
			this.triggerDirect( IO_EVENT_MESSAGE, ["", ssid], [ null, message.substring( index + 1, message.length ) ], null );
		} catch( e ) {
			Logger.getInstance().error( e );
		}
	}

	abstract ping_( ssid: string ): boolean;

	abstract send_( message: string | number, ssid: string ): boolean;
}
