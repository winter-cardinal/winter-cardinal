/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import * as ServerConstructorImplModule from "./internal/server-constructor-impl";

export namespace server {
	export import Server = ServerConstructorImplModule.ServerConstructorImpl;
}
