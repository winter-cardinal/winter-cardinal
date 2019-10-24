/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import org.wcardinal.controller.data.SNumber;

public abstract class SNumberImpl<T extends Number> extends SScalarImpl<T> implements SNumber<T> {
	public SNumberImpl( final SType type ) {
		super( type.ordinal() );
	}
}
