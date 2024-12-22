/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.EnumSet;

import org.springframework.core.ResolvableType;

import org.wcardinal.controller.data.SLockable;
import org.wcardinal.controller.internal.Property;
import org.wcardinal.util.thread.AutoCloseableReentrantLock;

public interface SContainer<V, P> extends SLockable {
	void init( final String name, final SContainerParent parent, final AutoCloseableReentrantLock lock, final ResolvableType type, final EnumSet<Property> properties );
	void addOrigin( final Object origin );
	void removeOrigin( final Object origin );

	String getName();

	boolean isInitialized();
	void initialize( Object except );

	long getRevision();
	void setRevision( long revision );

	SContainerParent getParent();
	AutoCloseableReentrantLock getLock();
	ResolvableType getType();
	V getValue();

	void onPatches( Object except, P patches );

	boolean isSoft();
	boolean compact();
}
