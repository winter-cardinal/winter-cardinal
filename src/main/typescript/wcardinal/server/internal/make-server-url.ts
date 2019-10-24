/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Settings } from "../../controller/internal/settings";

export const makeServerUrl = ( settings: Settings ): string => {
	const path = settings.path;
	const href = `${location.protocol}//${location.host}${location.pathname}`;
	return href.substr( 0, href.lastIndexOf( "/" ) ) + path.content + path.query;
};
