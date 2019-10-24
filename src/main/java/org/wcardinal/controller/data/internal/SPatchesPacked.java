/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.io.IOException;
import java.util.NavigableMap;

import com.fasterxml.jackson.core.JsonGenerator;

public interface SPatchesPacked<V, P extends SPatch> {
	long getStartRevision();
	long getEndRevision();
	P getReset();
	NavigableMap<Long, P> getRevisionToMap();
	void serialize( JsonGenerator gen ) throws IOException;
	boolean isApplicable( long revision, boolean hasNoPatches, boolean isSoft );
	boolean isEmpty();
	SChange apply( final long revision, final V value );
}
