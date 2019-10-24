/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import * as ExceptionModule from "./exception";
import * as IllegalArgumentExceptionModule from "./illegal-argument-exception";
import * as IndexOutOfBoundsExceptionModule from "./index-out-of-bounds-exception";
import * as NoSuchElementExceptionModule from "./no-such-element-exception";
import * as NullArgumentExceptionModule from "./null-argument-exception";
import * as NullPointerExceptionModule from "./null-pointer-exception";
import * as UnsupportedOperationExceptionModule from "./unsupported-operation-exception";

export namespace exception {
	export import Exception = ExceptionModule.Exception;
	export import IllegalArgumentException = IllegalArgumentExceptionModule.IllegalArgumentException;
	export import IndexOutOfBoundsException = IndexOutOfBoundsExceptionModule.IndexOutOfBoundsException;
	export import NoSuchElementException = NoSuchElementExceptionModule.NoSuchElementException;
	export import NullArgumentException = NullArgumentExceptionModule.NullArgumentException;
	export import NullPointerException = NullPointerExceptionModule.NullPointerException;
	export import UnsupportedOperationException = UnsupportedOperationExceptionModule.UnsupportedOperationException;
}
