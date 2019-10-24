/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import * as AjaxModule from "./ajax";
import * as LoggerImplModule from "./internal/logger-impl";
import * as UtilImplModule from "./internal/util-impl";
import * as MessageSourceModule from "./message-source";
import * as ThenableModule from "./thenable";

export namespace util {
	export import Util = UtilImplModule.UtilImpl;
	// tslint:disable-next-line:no-shadowed-variable
	export const util = UtilImplModule.UtilImpl.getInstance();
	export import Ajax = AjaxModule.Ajax;
	export const ajax = AjaxModule.Ajax.getInstance();
	export import Thenable = ThenableModule.Thenable;
	export import MessageSource = MessageSourceModule.MessageSource;
	export const messageSource = MessageSourceModule.MessageSource.getInstance();
	export import Logger = LoggerImplModule.LoggerImpl;
	export const logger = LoggerImplModule.LoggerImpl.getInstance();
}
