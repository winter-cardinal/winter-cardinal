/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import * as ControllerMakerWorkerModule from "./controller-maker-worker";

export namespace internal {
	export import ControllerMaker = ControllerMakerWorkerModule.ControllerMakerWorker;
}
