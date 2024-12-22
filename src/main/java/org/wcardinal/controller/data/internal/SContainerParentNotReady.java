package org.wcardinal.controller.data.internal;

import java.util.concurrent.TimeUnit;

import org.wcardinal.controller.internal.ControllerDynamicInfoHandler;
import org.wcardinal.exception.NotReadyException;
import org.wcardinal.util.thread.AutoCloseableReentrantLock;
import org.wcardinal.util.thread.AutoCloseableReentrantLockNotReady;
import org.wcardinal.util.thread.Unlockable;
import org.wcardinal.util.thread.Unlocker;

public class SContainerParentNotReady implements SContainerParent, Unlockable {
	public static SContainerParentNotReady INSTANCE = new SContainerParentNotReady();
	private final AutoCloseableReentrantLock lock = AutoCloseableReentrantLockNotReady.INSTANCE;

	@Override
	public void put(Object origin, String name, SBase<?> sbase) {
		throw new NotReadyException();
	}

	@Override
	public void put(ControllerDynamicInfoHandler handler) {
		throw new NotReadyException();
	}

	@Override
	public boolean isShared() {
		return true;
	}

	@Override
	public Unlocker lock() {
		throw new NotReadyException();
	}

	@Override
	public boolean tryLock() {
		throw new NotReadyException();
	}

	@Override
	public boolean tryLock(long timeout, TimeUnit unit) {
		throw new NotReadyException();
	}

	@Override
	public boolean isLocked() {
		return lock.isLocked();
	}

	@Override
	public boolean isLockedByCurrentThread() {
		return lock.isHeldByCurrentThread();
	}

	@Override
	public void unlock() {
		lock.unlock();
	}

	@Override
	public void onChange(SParent origin, SParent parent, String name, SChange change) {
		// DO NOTHING
	}

	@Override
	public void update() {
		throw new NotReadyException();
	}

	@Override
	public void onUnlock() {
		// DO NOTHING
	}
}
