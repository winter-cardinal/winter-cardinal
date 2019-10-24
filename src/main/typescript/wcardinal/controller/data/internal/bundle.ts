/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import * as SContainerModule from "./s-container";
import * as SIntegralNumberModule from "./s-integral-number";
import * as SListBasePatchMapsMergeAndSortUpdatedModule from "./s-list-base-patch-maps-merge-and-sort-updated";
import * as SNumberModule from "./s-number";
import * as SRealNumberModule from "./s-real-number";
import * as SScalarModule from "./s-scalar";

export namespace internal {
	export import SScalar = SScalarModule.SScalar;
	export import SNumber = SNumberModule.SNumber;
	export import SIntegralNumber = SIntegralNumberModule.SIntegralNumber;
	export import SRealNumber = SRealNumberModule.SRealNumber;
	export import SContainer = SContainerModule.SContainer;
	export import mergeAndSortUpdated = SListBasePatchMapsMergeAndSortUpdatedModule.mergeAndSortUpdated;
}
