/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import * as internalModule from "./internal/bundle";

import * as SArrayNodeModule from "./s-array-node";
import * as SBooleanModule from "./s-boolean";
import * as SClassModule from "./s-class";
import * as SDoubleModule from "./s-double";
import * as SFloatModule from "./s-float";
import * as SIntegerModule from "./s-integer";
import * as SJsonNodeModule from "./s-json-node";
import * as SListModule from "./s-list";
import * as SLongModule from "./s-long";
import * as SMapModule from "./s-map";
import * as SMovableListModule from "./s-movable-list";
import * as SNavigableMapModule from "./s-navigable-map";
import * as SObjectNodeModule from "./s-object-node";
import * as SQueueModule from "./s-queue";
import * as SROQueueModule from "./s-ro-queue";
import * as SStringModule from "./s-string";

export namespace data {
	export import internal = internalModule.internal;

	export import SArrayNode = SArrayNodeModule.SArrayNode;
	export import SBoolean = SBooleanModule.SBoolean;
	export import SClass = SClassModule.SClass;
	export import SDouble = SDoubleModule.SDouble;
	export import SFloat = SFloatModule.SFloat;
	export import SInteger = SIntegerModule.SInteger;
	export import SJsonNode = SJsonNodeModule.SJsonNode;
	export import SList = SListModule.SList;
	export import SLong = SLongModule.SLong;
	export import SMap = SMapModule.SMap;
	export import SMovableList = SMovableListModule.SMovableList;
	export import SNavigableMap = SNavigableMapModule.SNavigableMap;
	export import SObjectNode = SObjectNodeModule.SObjectNode;
	export import SQueue = SQueueModule.SQueue;
	export import SROQueue = SROQueueModule.SROQueue;
	export import SString = SStringModule.SString;
}
