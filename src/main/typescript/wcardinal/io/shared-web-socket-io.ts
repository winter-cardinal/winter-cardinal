/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { InternalWebSocketIo } from "./internal-web-socket-io";
import { IoSettings } from "./io-settings";
import { Ios } from "./ios";
import { SharedIo } from "./shared-io";

const SharedWebSocketInstances = {};

/**
 * Provides the shared WebSocket connection.
 */
export class SharedWebSocketIo extends SharedIo {
	constructor( parameters: IoSettings ) {
		super( "SharedWebSocket", "ws", "/wcardinal-websocket", ( baseUrl: string ) => {
			return new InternalWebSocketIo( baseUrl );
		}, SharedWebSocketInstances );
	}
}

Ios.getInstance().add( "shared-websocket", SharedWebSocketIo );
