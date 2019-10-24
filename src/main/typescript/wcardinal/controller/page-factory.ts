/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Factory } from "./factory";
import { Page } from "./page";

/**
 * A page factory that creates/destroys components dynamically.
 */
export interface PageFactory extends Factory<Page> {}
