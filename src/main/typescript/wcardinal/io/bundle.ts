/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import * as InternalIoModule from "./internal-io";
import * as InternalPollingIoModule from "./internal-polling-io";
import * as InternalWebSocketIoModule from "./internal-web-socket-io";
import * as IoModule from "./io";
import * as IosModule from "./ios";
import * as MessageEmitterModule from "./message-emitter";
import * as PollingIoModule from "./polling-io";
import * as SharedIoModule from "./shared-io";
import * as SharedPollingIoModule from "./shared-polling-io";
import * as SharedWebSocketIoModule from "./shared-web-socket-io";
import * as WebSocketIoModule from "./web-socket-io";

export namespace io {
	export import InternalIo = InternalIoModule.InternalIo;
	export import InternalPollingIo = InternalPollingIoModule.InternalPollingIo;
	export import InternalWebSocketIo = InternalWebSocketIoModule.InternalWebSocketIo;
	export import Io = IoModule.Io;
	export import Ios = IosModule.Ios;
	export import MessageEmitter = MessageEmitterModule.MessageEmitter;
	export import PollingIo = PollingIoModule.PollingIo;
	export import SharedIo = SharedIoModule.SharedIo;
	export import SharedPollingIo = SharedPollingIoModule.SharedPollingIo;
	export import SharedWebSocketIo = SharedWebSocketIoModule.SharedWebSocketIo;
	export import WebSocketIo = WebSocketIoModule.WebSocketIo;
}
