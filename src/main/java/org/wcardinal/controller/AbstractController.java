/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.node.ArrayNode;

import org.wcardinal.exception.NotReadyException;
import org.wcardinal.util.thread.Scheduler;
import org.wcardinal.util.thread.Unlocker;

/**
 * Provides additional functions of controllers.
 *
 * <pre><code> {@literal @}Controller
 * class MyController extends AbstractController {
 *   void foo(){
 *     System.out.println( getRemoteAddress() );
 *   }
 * }
 * </code></pre>
 */
public class AbstractController implements ControllerContext {
	protected ControllerContext controllerContext = null;

	ControllerContext getContext(){
		if( controllerContext != null ) {
			return controllerContext;
		} else {
			throw new NotReadyException();
		}
	}

	@Override
	public String getName(){
		return getContext().getName();
	}

	@Override
	public <T> T getParent(){
		return getContext().getParent();
	}

	@Override
	public Factory<?> getParentAsFactory(){
		return getContext().getParentAsFactory();
	}

	@Override
	public <T> Collection<T> getParents(){
		return getContext().getParents();
	}

	@Override
	public <T> T getActivePage(){
		return getContext().getActivePage();
	}

	@Override
	public void show() {
		getContext().show();
	}

	@Override
	public void hide() {
		getContext().hide();
	}

	@Override
	public boolean isShown() {
		return getContext().isShown();
	}

	@Override
	public boolean isHidden() {
		return getContext().isHidden();
	}

	@Override
	public boolean isReadOnly() {
		return getContext().isReadOnly();
	}

	@Override
	public boolean isNonNull() {
		return getContext().isNonNull();
	}

	@Override
	public boolean isHistorical() {
		return getContext().isHistorical();
	}

	@Override
	public String getRemoteAddress(){
		return getContext().getRemoteAddress();
	}

	@Override
	public Principal getPrincipal(){
		return getContext().getPrincipal();
	}

	@Override
	public Scheduler getScheduler(){
		return getContext().getScheduler();
	}

	@Override
	public String getSessionId(){
		return getContext().getSessionId();
	}

	@Override
	public String getSubSessionId(){
		return getContext().getSubSessionId();
	}

	@Override
	public Unlocker lock() {
		return getContext().lock();
	}

	@Override
	public boolean tryLock() {
		return getContext().tryLock();
	}

	@Override
	public boolean tryLock(long timeout, TimeUnit unit) {
		return getContext().tryLock(timeout, unit);
	}

	@Override
	public boolean isLocked() {
		return getContext().isLocked();
	}

	@Override
	public boolean isLockedByCurrentThread() {
		return getContext().isLockedByCurrentThread();
	}

	@Override
	public void unlock() {
		getContext().unlock();
	}

	@Override
	public void execute(final String name, final Object... parameters) {
		getContext().execute(name, parameters);
	}

	@Override
	public Future<?> execute(final Runnable runnable) {
		return getContext().execute(runnable);
	}

	@Override
	public <T> Future<T> execute(final Callable<T> callable) {
		return getContext().execute(callable);
	}

	@Override
	public long timeout(final String name, final long delay, final Object... parameters) {
		return getContext().timeout(name, delay, parameters);
	}

	@Override
	public long timeout(final Runnable runnable, final long delay) {
		return getContext().timeout(runnable, delay);
	}

	@Override
	public <T> TimeoutFuture<T> timeout(final Callable<T> callable, final long delay) {
		return getContext().timeout(callable, delay);
	}

	@Override
	public long interval(final String name, final long interval) {
		return getContext().interval(name, interval);
	}

	@Override
	public long interval(final String name, final long startAfter, final long interval, final Object... parameters) {
		return getContext().interval(name, startAfter, interval, parameters);
	}

	@Override
	public long interval(final Runnable runnable, final long interval) {
		return getContext().interval(runnable, interval);
	}

	@Override
	public long interval(final Runnable runnable, final long startAt, final long interval) {
		return getContext().interval(runnable, startAt, interval);
	}

	@Override
	public boolean cancel(final long id) {
		return getContext().cancel( id );
	}

	@Override
	public boolean cancel() {
		return getContext().cancel();
	}

	@Override
	public void cancelAll() {
		getContext().cancelAll();
	}

	@Override
	public boolean isCanceled() {
		return getContext().isCanceled();
	}

	@Override
	public boolean isHeadCall() {
		return getContext().isHeadCall();
	}

	@Override
	public void trigger( final String name, final Object... arguments ) {
		getContext().trigger(name, arguments);
	}

	@Override
	public void triggerDirect( final String name, final Object... arguments ) {
		getContext().triggerDirect(name, arguments);
	}

	@Override
	public TriggerResult triggerAndWait( final String name, final long timeout, final Object... arguments ) {
		return getContext().triggerAndWait(name, timeout, arguments);
	}

	@Override
	public String getParameter( final String key ) {
		return getContext().getParameter( key );
	}

	@Override
	public String[] getParameters( final String key ) {
		return getContext().getParameters( key );
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return getContext().getParameterMap();
	}

	@Override
	public ArrayNode getFactoryParameters() {
		return getContext().getFactoryParameters();
	}

	@Override
	public ControllerAttributes getAttributes() {
		return getContext().getAttributes();
	}

	@Override
	public List<Locale> getLocales() {
		return getContext().getLocales();
	}

	@Override
	public Locale getLocale() {
		return getContext().getLocale();
	}

	@Override
	public void notify( final String name, final Object... parameters ) {
		getContext().notify( name, parameters );
	}

	@Override
	public void notifyAsync( final String name, final Object... parameters) {
		getContext().notifyAsync( name, parameters );
	}

	@Override
	public boolean cancel( final String reason ) {
		return getContext().cancel( reason );
	}
}
