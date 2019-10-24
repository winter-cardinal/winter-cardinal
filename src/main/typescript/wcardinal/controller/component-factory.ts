/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Component } from "./component";
import { Factory } from "./factory";

/**
 * A component factory that creates/destroys components dynamically.
 */
export interface ComponentFactory extends Factory<Component> {}
