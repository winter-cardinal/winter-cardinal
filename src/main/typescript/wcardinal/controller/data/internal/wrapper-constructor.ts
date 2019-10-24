/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Connectable } from "../../../event/connectable";

export type WrapperConstructor<W = Connectable> = new ( memory: any ) => W;
