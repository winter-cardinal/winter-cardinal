/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { PlainObject } from "../../../util/lang/plain-object";
import { SScalarMemory } from "./s-scalar-memory";
import { SScalarParentMemory } from "./s-scalar-parent-memory";

export interface SContainerParentMemory extends SScalarParentMemory {
	putDynamicInfoHandlerDataName_( dataName: string, handlerName: string ): void;
	putDynamicInfoHandler_( name: string, handler: ( data: PlainObject<null> ) => string | null ): void;
	putData_( name: string, data: SScalarMemory ): void;
}
