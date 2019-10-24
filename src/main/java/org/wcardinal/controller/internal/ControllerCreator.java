/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.util.List;

import com.fasterxml.jackson.databind.node.ArrayNode;

public interface ControllerCreator<T extends Controller> {
	T createDynamic( final String id, final ArrayNode arguments );
	T destroyDynamic( final String id );
	T getDynamic( final String name );
	void initDynamic( final String name, final Controller controller, final ArrayNode args, final Object[] arguments );
	List<FactoryData> getFactoryData();
	boolean isPostCreated();
}
