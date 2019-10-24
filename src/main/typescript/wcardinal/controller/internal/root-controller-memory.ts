/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Server } from "../../server/server";
import { addEventListener } from "../../util/dom/add-event-listener";
import { isNotEmptyArray } from "../../util/lang/is-empty";
import { now } from "../../util/lang/now";
import { WrapperConstructor } from "../data/internal/wrapper-constructor";
import { RootController } from "../root-controller";
import { LocalRootControllerMemory } from "./local-root-controller-memory";
import { DynamicInfo, Settings, SyncSettings } from "./settings";

export class RootControllerMemory extends LocalRootControllerMemory<RootController> {
	private _server: Server;

	private _isHistoryReady: boolean;
	private _baseTitle: string;
	private _separators: [ string, string ];

	private _lastSendTime = 0;

	private _sync: SyncSettings;

	private _runBound: () => void;
	private _runTimeoutId: number | null = null;

	private _senderId = -1;

	private _sendUpdateBound: () => void;
	private _sendUpdateTimeoutId: number | null = null;

	private _onRequestBound: ( messages: string[] ) => void;

	constructor(
		name: string, server: Server, settings: Settings,
		wrapperConstructor: WrapperConstructor<RootController>
	) {
		super( name, settings, wrapperConstructor );

		this._server = server;
		this._sync = settings.sync;
		this._runBound = () => { this.run(); };

		// Init
		this.init_( settings );

		// Requests handling
		const interval = settings.sync.process.interval;
		this._onRequestBound = ( messages: string[] ) => {
			this.lock_();
			try {
				for( let i = 0, imax = messages.length; i < imax; ++i ) {
					this.processRequest( messages[ i ] );
				}
			} finally {
				this.unlock_();
			}
		};
		this._server.addHandler( this._onRequestBound );

		this._sendUpdateBound = () => {
			this.sendUpdate();
			this._sendUpdateTimeoutId = self.setTimeout( this._sendUpdateBound, interval );
		};
		this._sendUpdateTimeoutId = null;

		// History handling
		this._baseTitle = document.title;
		this._separators = settings.title.separators;
		this._isHistoryReady = settings.info.historical && (typeof window !== "undefined");
		if( this._isHistoryReady ) {
			this.popHistory();
			this.pushHistory_();
			addEventListener( window, "hashchange", () => { this.popHistory(); } );
		}

		// Send an ack
		if( settings.info.senderId != null ) {
			this._server.send( "a", settings.info.senderId );
		}

		// Establish a connection
		this.connect();
	}

	connect() {
		if( this._runTimeoutId == null ) {
			this._runTimeoutId = self.setTimeout(this._runBound, this._sync.update.interval);
			this._sendUpdateBound();
		}
	}

	sendUpdate() {
		if( this._hasUpdate ) {
			this._hasUpdate = false;
			this._lastSendTime = now();
			const senderId = ++this._senderId;
			const dynamicInfo = this.getDynamicInfo_( senderId );
			if( dynamicInfo != null ) {
				this._server.send( "u", [ senderId, dynamicInfo ] );
			}
		}
	}

	destroy_() {
		super.destroy_();

		if( this._runTimeoutId != null ) {
			self.clearTimeout( this._runTimeoutId );
			this._runTimeoutId = null;
		}

		if( this._sendUpdateTimeoutId != null ) {
			self.clearTimeout( this._sendUpdateTimeoutId );
			this._sendUpdateTimeoutId = null;
		}

		if( this._server != null ) {
			if( this._onRequestBound != null ) {
				this._server.removeHandler( this._onRequestBound );
			}
		}
	}

	run() {
		const current = now();

		// Cleanup rejections
		const rejectedDynamicInfos = this._rejectedDynamicInfos;
		for( let i = 0, imax = rejectedDynamicInfos.length, ioffset = 0; i < imax; ++i ) {
			const rejectedInfo = rejectedDynamicInfos[ i - ioffset ];
			if( this._sync.update.interval <= current - rejectedInfo._createdAt ) {
				rejectedDynamicInfos.splice( i - ioffset, 1 );
				ioffset += 1;
			}
		}

		// Send update requests
		const elapsedTime = current - this._lastSendTime;
		if( this._sync.update.interval <= elapsedTime ) {
			this._hasUpdate = true;
		}
		const next = Math.max( 0, this._lastSendTime + this._sync.update.interval - now() );
		self.setTimeout(this._runBound, next);
	}

	pushHistoryState_(): { url: string, title: string } {
		const state = this.getHistoryState_();
		const title = this.getHistoryTitle_( this._separators[ 1 ] );
		return {
			url: (isNotEmptyArray( state ) ? `#!${state}` : "#!/"),
			title: (isNotEmptyArray( title ) ? this._baseTitle + this._separators[ 0 ] + title : this._baseTitle)
		};
	}

	popHistoryState_( state: string ): void {
		const parts = state.split("/");
		if( isNotEmptyArray( parts ) && parts[ 0 ] === "#!" ) {
			const index = this.setHistoryState_( 1, parts );
			if( index < parts.length ) {
				this.pushHistory_();
			}
		} else {
			this.pushHistory_();
		}
	}

	pushHistory_(): void {
		if( this._isHistoryReady ) {
			const state = this.pushHistoryState_();
			if( state.url != null && state.url !== window.location.hash ) {
				window.location.hash = state.url;
			}
			if( state.title != null && state.title !== document.title ) {
				document.title = state.title;
			}
		}
	}

	popHistory() {
		if( this._isHistoryReady ) {
			this.lock_();
			try {
				this.popHistoryState_( window.location.hash );
			} finally {
				this.unlock_();
			}
		}
	}

	getServer_(): Server {
		return this._server;
	}

	setPartialDynamicInfo_( senderId: number, receivedDynamicInfo: DynamicInfo ): void {
		this._server.send( "a", senderId );

		this.lock_();
		try {
			this.setDynamicInfo_( receivedDynamicInfo, false, false );
		} finally {
			try {
				if( this._events != null ) {
					const events = this._events;
					this._events = null;
					this.handleEvents_( events );
				}
			} finally {
				this.unlock_();
			}
		}
	}

	processRequest( message: string ): void {
		const type = message[ 0 ];
		const content = message.substring( 1, message.length );
		switch( type ) {
		case "c":
			this.resetAuthorizedRevision_();
			this.setDynamicInfo_( JSON.parse( content ), true, true );
			break;
		case "d":
			this.resetAuthorizedRevision_();
			this._hasUpdate = true;
			break;
		case "a":
			this.setAuthorizedRevision_( JSON.parse( content ) );
			break;
		case "u":
			this.setDynamicInfo_( JSON.parse( content ), false, true );
			break;
		case "x":
			{
				const args = JSON.parse( content );
				this.setAuthorizedRevision_( args[ 0 ] );
				this.setDynamicInfo_( args[ 1 ], false, true );
			}
			break;
		}
	}
}
