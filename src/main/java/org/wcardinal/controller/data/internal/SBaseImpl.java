/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.wcardinal.util.thread.AutoCloseableReentrantLock;
import org.wcardinal.util.thread.Unlocker;

public abstract class SBaseImpl<T> implements SBase<T> {
	static final Logger logger = LoggerFactory.getLogger(SBaseImpl.class);

	boolean isChanged;
	long revision;
	boolean isReadOnly = false;
	boolean isNonNull = false;
	boolean isInitialized = true;
	boolean isSoft = false;
	boolean isWeak = false;
	boolean isLoose = false;
	SParent parent = null;
	final int type;
	AutoCloseableReentrantLock lock = null;
	Unlocker unlocker = new Unlocker( this );

	SBaseImpl( final int type ){
		this.type = type;
		this.revision = 0;
		this.isChanged = false;
	}

	@Override
	public void setLock( final AutoCloseableReentrantLock lock ){
		this.lock = lock;
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public long getRevision(){
		lock.lock();
		final long result = revision;
		lock.unlock();
		return result;
	}

	@Override
	public void setReadOnly(final boolean isReadOnly){
		lock.lock();
		this.isReadOnly = isReadOnly;
		lock.unlock();
	}

	@Override
	public boolean isReadOnly(){
		lock.lock();
		final boolean result = isReadOnly;
		lock.unlock();
		return result;
	}

	@Override
	public boolean isNonNull(){
		lock.lock();
		final boolean result = isNonNull;
		lock.unlock();
		return result;
	}

	@Override
	public void setSoft(final boolean isSoft){
		lock.lock();
		this.isSoft = isSoft;
		lock.unlock();
	}

	@Override
	public boolean isSoft(){
		lock.lock();
		final boolean result = isSoft;
		lock.unlock();
		return result;
	}

	@Override
	public void setWeak(final boolean isWeak){
		lock.lock();
		this.isWeak = isWeak;
		lock.unlock();
	}

	@Override
	public boolean isWeak(){
		lock.lock();
		final boolean result = isWeak;
		lock.unlock();
		return result;
	}

	@Override
	public void setLoose(final boolean isLoose){
		lock.lock();
		this.isLoose = isLoose;
		lock.unlock();
	}

	@Override
	public boolean isLoose(){
		lock.lock();
		final boolean result = isLoose;
		lock.unlock();
		return result;
	}

	@Override
	public void uninitialize(){
		lock.lock();
		isInitialized = false;
		lock.unlock();
	}

	@Override
	public boolean isInitialized(){
		lock.lock();
		final boolean result = isInitialized;
		lock.unlock();
		return result;
	}

	@Override
	public void setParent( final SParent parent ){
		// The lock is not required
		// because this method is called only once from constructors.
		this.parent = parent;
	}

	@Override
	public Unlocker lock(){
		lock.lock();
		return unlocker;
	}

	@Override
	public boolean tryLock(){
		return lock.tryLock();
	}

	@Override
	public boolean tryLock(final long timeout, final TimeUnit unit){
		try {
			return lock.tryLock(timeout, unit);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean isLocked(){
		return lock.isLocked();
	}

	@Override
	public void unlock() {
		// Propagate the change flag
		if( isChanged ){
			isChanged = false;
			revision += 1;
			if( parent != null ) {
				parent.update();
			}
		}

		// Unlock
		int count = lock.getHoldCount();
		if( count <= 1 ){
			// The last unlock
			if( parent != null ) {
				parent.onUnlock();
			}
		} else {
			// Otherwise
			lock.unlock();
		}
	}

	@Override
	public void unlock( final Object origin ) {
		unlock();
	}

	@Override
	public void override( final long revision ){
		this.revision = revision;
		this.isChanged = true;
	}
}
