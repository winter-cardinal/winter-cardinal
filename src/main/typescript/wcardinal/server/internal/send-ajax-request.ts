/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Settings } from "../../controller/internal/settings";
import { Ajax, AjaxErrorHandler, AjaxSuccessHandler } from "../../util/ajax";
import { makeServerUrl } from "./make-server-url";
import { toServerHeaders } from "./to-server-header";
import { PlainObject } from '../../util/lang/plain-object';

export const sendAjaxRequest = (
	settings: Settings, mode: string, data: BodyInit | null, timeout: number, headers: PlainObject<string> | null,
	onSuccess: AjaxSuccessHandler, onError: AjaxErrorHandler, context: unknown
): void => {
	Ajax.getInstance().post({
		url: makeServerUrl( settings ),
		dataType: "json",
		headers: toServerHeaders( mode, settings.ssid, headers ),
		timeout,
		context,
		data,
		success: onSuccess,
		error: onError
	});
};
