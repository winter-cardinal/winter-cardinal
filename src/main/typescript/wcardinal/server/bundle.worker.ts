/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import * as ServerConstructorWorkerModule from "./internal/server-constructor-worker";

export namespace server {
	export import Server = ServerConstructorWorkerModule.ServerConstructorWorker;
}
