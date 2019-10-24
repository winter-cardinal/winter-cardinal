/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import org.wcardinal.controller.annotation.Decoratable;
import org.wcardinal.controller.annotation.Tracked;
import org.wcardinal.controller.annotation.Locked;
import org.wcardinal.controller.annotation.Unlocked;
import org.wcardinal.exception.IllegalDecorationException;

public abstract class AbstractMethodWrapper<T> {
	static final Logger logger = LoggerFactory.getLogger(AbstractMethodWrapper.class);

	final Method method;
	final LockRequirement lockRequirement;
	final boolean isTrackable;
	final boolean isVoid;

	public AbstractMethodWrapper( final Method method ){
		this.method = method;
		ReflectionUtils.makeAccessible( this.method );
		this.lockRequirement = toLockRequirement( method );
		this.isTrackable = isTrackable( method );
		this.isVoid = isVoid( method );
	}

	@SuppressWarnings("unchecked")
	private MethodResult<T> call( final Object instance, final Object[] parameters ) {
		try {
			if( isVoid ) {
				method.invoke(instance, parameters);
				return MethodResultVoid.getInstance();
			} else {
				return new MethodResultData<T>( (T) method.invoke(instance, parameters) );
			}
		} catch ( final Exception e ) {
			return new MethodResultException<T>( method, parameters, e );
		}
	}

	public MethodResult<T> call( final MethodContainer container, final TrackingData trackingData, final MethodHook hook, final Object instance, final Object[] parameters ) {
		if( isTrackable ) {
			final long next = trackingData.get( this );
			final TrackingId trackingId = container.<TrackingId>getWorkingData(this);
			trackingId.move(next);
			container.setTrackingIds( new TrackingIds( next, trackingId ) );
			final MethodResult<T> result;
			if( hook != null ) {
				hook.before();
				result = call( instance, parameters );
				hook.after();
			} else {
				result = call( instance, parameters );
			}
			container.setTrackingIds( null );
			return result;
		} else if( hook != null ) {
			hook.before();
			final MethodResult<T> result = call( instance, parameters );
			hook.after();
			return result;
		} else {
			return call( instance, parameters );
		}
	}

	LockRequirement toLockRequirement( final Method method ){
		final Locked locked = AnnotationUtils.findAnnotation( method, Locked.class );
		final Unlocked unlocked = AnnotationUtils.findAnnotation( method, Unlocked.class );

		if( locked != null ){
			if( unlocked != null ) {
				throw new IllegalDecorationException( String.format( "@Locked and @Unlocked are both specified at method '%s'", method ) );
			} else {
				return LockRequirement.REQUIRED;
			}
		} else if( unlocked != null ) {
			return LockRequirement.NOT_REQUIRED;
		} else {
			return LockRequirement.UNSPECIFIED;
		}
	}

	public LockRequirement getLockRequirement(){
		return lockRequirement;
	}

	Long getTrackingId( final MethodContainer container, final Object instance ){
		if( isTrackable ) {
			return container.<TrackingId>getWorkingData( this ).next();
		}
		return null;
	}

	boolean isTrackable( final Method method ) {
		if( AnnotationUtils.findAnnotation( method, Tracked.class ) != null ) {
			final Decoratable decoratable = AnnotationUtils.findAnnotation(method, Decoratable.class);
			if( decoratable != null ) return true;
			throw new IllegalDecorationException( String.format( "@Tracked can not be applied to method '%s' having no annotations annotated with @Decoratable meta-annotation", method ) );
		}

		return false;
	}

	boolean isVoid( final Method method ) {
		return (method.getReturnType() == Void.TYPE);
	}

	public void init( final MethodContainer container ) {
		if( isTrackable ) {
			container.putWorkingData( this, new TrackingId() );
		}
	}
}
