/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { InternalPollingIo } from "./internal-polling-io";
import { IoSettings } from "./io-settings";
import { Ios } from "./ios";
import { SharedIo } from "./shared-io";

const SharedPollingInstances = {};

/**
 * Provides the shared polling connection.
 */
export class SharedPollingIo extends SharedIo {
	constructor( parameters: IoSettings ) {
		parameters = parameters || {};
		super( `SharedPolling-${parameters.interval}`, "http", "/wcardinal-polling", ( baseUrl: string ) => {
			return new InternalPollingIo( baseUrl, parameters );
		}, SharedPollingInstances );
	}
}

Ios.getInstance().add( "shared-polling", SharedPollingIo );
