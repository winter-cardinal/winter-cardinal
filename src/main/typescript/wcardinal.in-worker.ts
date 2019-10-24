/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import "./wcardinal/io/polling-io";
import "./wcardinal/io/shared-polling-io";
import "./wcardinal/io/shared-web-socket-io";
import "./wcardinal/io/web-socket-io";
import { ServerInWorker } from "./wcardinal/server/internal/server-in-worker";

ServerInWorker.run();
