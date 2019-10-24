/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import * as ConnectableModule from "./connectable";
import * as ConnectionModule from "./connection";
import * as EventModule from "./event";

export namespace event {
	export import Event = EventModule.Event;
	export import Connection = ConnectionModule.Connection;
	export import Connectable = ConnectableModule.Connectable;
}
