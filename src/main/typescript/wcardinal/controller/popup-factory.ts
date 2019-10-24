/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Factory } from "./factory";
import { Popup } from "./popup";

/**
 * A popup factory that creates/destroys components dynamically.
 */
export interface PopupFactory extends Factory<Popup> {}
