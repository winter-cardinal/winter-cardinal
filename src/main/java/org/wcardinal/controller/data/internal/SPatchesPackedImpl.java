/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.io.IOException;
import java.util.Map;
import java.util.NavigableMap;

import com.fasterxml.jackson.core.JsonGenerator;

public abstract class SPatchesPackedImpl<V, P extends SPatch> implements SPatchesPacked<V, P> {
	final long startRevision;
	final long endRevision;
	final P reset;
	final NavigableMap<Long, P> revisionToMap;

	public SPatchesPackedImpl( final long startRevision, final long endRevision, final P reset ) {
		this.startRevision = startRevision;
		this.endRevision = endRevision;
		this.reset = reset;
		this.revisionToMap = null;
	}

	public SPatchesPackedImpl( final long startRevision, final long endRevision, final NavigableMap<Long, P> revisionToPatch ) {
		this.startRevision = startRevision;
		this.endRevision = endRevision;
		this.reset = null;
		this.revisionToMap = revisionToPatch;
	}

	@Override
	public void serialize( final JsonGenerator gen ) throws IOException {
		gen.writeStartArray();
		gen.writeNumber(startRevision);
		gen.writeNumber( endRevision - startRevision );
		if( reset != null ) {
			gen.writeNumber( 0 );
			reset.serialize( gen );
		} else {
			gen.writeNumber( 1 );
			for( Map.Entry<Long, P> entry: revisionToMap.entrySet() ) {
				gen.writeNumber( entry.getKey() - startRevision );
				entry.getValue().serialize( gen );
			}
		}
		gen.writeEndArray();
	}

	@Override
	public boolean isApplicable( final long revision, final boolean hasNoPatches, final boolean isSoft ) {
		return ( (isSoft == false && hasNoPatches && startRevision == revision) || reset != null );
	}

	@Override
	public boolean isEmpty() {
		return ((revisionToMap == null || revisionToMap.isEmpty()) && reset == null);
	}

	@Override
	public long getStartRevision() {
		return startRevision;
	}

	@Override
	public long getEndRevision() {
		return endRevision;
	}

	@Override
	public P getReset() {
		return reset;
	}

	@Override
	public NavigableMap<Long, P> getRevisionToMap() {
		return revisionToMap;
	}
}
