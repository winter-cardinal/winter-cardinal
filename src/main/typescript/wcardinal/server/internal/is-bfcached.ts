/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Settings } from "../../controller/internal/settings";

export const isBFCached = ( settings: Settings ): boolean => {
	const storage = window.sessionStorage;
	if( storage != null ) {
		const key = `wcardinal-bfcache-${settings.path.content}`;
		const storedSsid = storage.getItem( key );
		if( storedSsid === settings.ssid ) {
			return true;
		} else {
			storage.setItem( key, settings.ssid );
			return false;
		}
	}
	return false;
};
