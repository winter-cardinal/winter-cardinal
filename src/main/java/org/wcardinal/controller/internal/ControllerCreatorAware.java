/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

public interface ControllerCreatorAware<T extends Controller> {
	void setControllerCreator( final ControllerCreator<T> creator );
	void createAsRequested( final FactoryData data );
	Object destroyByData( final FactoryData data );
}
