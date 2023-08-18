/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.lang.reflect.Method;

import org.springframework.core.ResolvableType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.wcardinal.controller.TaskAbortException;
import org.wcardinal.controller.TaskResultFailed;
import org.wcardinal.controller.TaskResult;
import org.wcardinal.controller.data.internal.SArrayNodeImpl;
import org.wcardinal.controller.data.internal.SChange;
import org.wcardinal.controller.data.internal.SClassImpl;
import org.wcardinal.controller.data.internal.SLongImpl;
import org.wcardinal.controller.data.internal.SParent;
import org.wcardinal.controller.data.internal.SStringImpl;
import org.wcardinal.controller.internal.info.StaticDataTask;
import org.wcardinal.util.reflection.AbstractMethods;
import org.wcardinal.util.reflection.ExceptionHandlerMethod;
import org.wcardinal.util.reflection.LockRequirements;
import org.wcardinal.util.reflection.MethodHook;
import org.wcardinal.util.reflection.MethodResult;
import org.wcardinal.util.reflection.MethodResultData;
import org.wcardinal.util.reflection.MethodResultException;
import org.wcardinal.util.reflection.MethodResultVoid;
import org.wcardinal.util.reflection.TaskMethods;
import org.wcardinal.util.reflection.TrackingData;
import org.wcardinal.util.reflection.TypedExceptionHandlerMethods;
import org.wcardinal.util.thread.Unlocker;

public class TaskController implements ControllerOnChangeHandler {
	final Controller controller;
	final TaskInternalQueue tasks;
	final String name;
	final Object instance;
	final TaskMethods<Object> methods;
	final ControllerBaggage baggage;
	final StaticDataTask taskData;
	final boolean isNonNull;

	final SLongImpl $id;
	final SArrayNodeImpl $v;
	final SClassImpl<TaskType> $vt;
	final SLongImpl $pid;
	final SClassImpl<Object> $r;
	final SStringImpl $rt;

	public TaskController( final String name, final Controller controller, final TaskInternalQueue tasks, final StaticDataTask taskData ) {
		this.controller = controller;
		this.tasks = tasks;
		this.name = name;
		this.instance = controller.instance;
		this.methods = controller.factory.tasks;
		this.baggage = controller.baggage;
		this.taskData = taskData;

		final boolean isReadOnly = taskData.properties.contains(Property.READ_ONLY);
		this.isNonNull = taskData.properties.contains(Property.NON_NULL);

		$id = new SLongImpl();
		$id.setParent( controller );
		$id.setLock( controller.lock );
		$id.setNonNull( true );
		$id.setReadOnly( isReadOnly );
		controller.put( "$id@"+name, $id );

		$v = new SArrayNodeImpl();
		$v.setParent( controller );
		$v.setLock( controller.lock );
		$v.setNonNull( false );
		$v.setReadOnly( isReadOnly );
		controller.put( "$v@"+name, $v );

		$vt = new SClassImpl<TaskType>();
		$vt.setParent( controller );
		$vt.setLock( controller.lock );
		$vt.setNonNull( false );
		$vt.setReadOnly( isReadOnly );
		$vt.setGenericType( ResolvableType.forClass( TaskType.class ) );
		controller.put( "$vt@"+name, $vt );

		$pid = new SLongImpl();
		$pid.setParent( controller );
		$pid.setLock( controller.lock );
		$pid.setNonNull( true );
		$pid.setReadOnly( isReadOnly );
		controller.put( "$pid@"+name, $pid );

		$r = new SClassImpl<Object>();
		$r.setParent( controller );
		$r.setLock(controller.lock);
		$r.setNonNull( false );
		$r.setReadOnly( isReadOnly );
		$r.setGenericType( ResolvableType.forClass( Object.class ) );
		$r.setSoft( true );
		controller.put( "$r@"+name, $r );

		$rt = new SStringImpl();
		$rt.setParent( controller );
		$rt.setLock( controller.lock );
		$rt.setNonNull( false );
		$rt.setReadOnly( isReadOnly );
		$rt.setSoft( true );
		controller.put( "$rt@"+name, $rt );

		controller.put( "$id@"+name, this );
	}

	boolean isValidParameterNode( final ArrayNode parameterNode ) {
		if( parameterNode == null ) return false;
		if( isNonNull ) {
			for( int i=0; i<parameterNode.size(); i++ ){
				final JsonNode parameter = parameterNode.get( i );
				if( parameter == null || parameter.isNull() ) return false;
			}
		}

		return true;
	}

	@Override
	public void handle( final SParent origin, final SParent parent, final String name, final SChange schange ) {
		if( $id.equals($pid) != true ){
			final long id = $id.get();
			final TaskType type = $vt.get();
			final ArrayNode parameterNode = $v.get();
			$v.compact( $v.getRevision() );
			if( type == TaskType.TASK && isValidParameterNode( parameterNode ) ) {
				call( new TaskInternalImpl( this, id ), parameterNode );
			} else {
				call( new TaskInternalImplCanceled( this, id, false ), null );
			}
		}
	}

	void call( final TaskInternal task, final ArrayNode parameterNode ) {
		tasks.add( task );
		if( task.isDone() != true ) {
			final Object[] parameters = AbstractMethods.toParameters( parameterNode, 0 );

			final TrackingData trackingData = methods.getTrackingData( name, controller, instance );
			final MethodHook hook = new TaskMethodHook( controller, task );
			final MethodResult<Object> result = methods.call(controller, name, trackingData, hook, LockRequirements.REQUIRED, instance, parameters);
			if( result != null ) {
				complete( task, result );
			} else if( methods.containsLockNotRequired( name ) || methods.containsLockUnspecified( name ) ) {
				baggage.scheduler.submit(new Runnable(){
					@Override
					public void run() {
						final MethodResult<Object> result = methods.call(controller, name, trackingData, hook, LockRequirements.NOT_REQUIRED_OR_UNSPECIFIED, instance, parameters);
						try( final Unlocker unlocker = controller.lock() ) {
							complete( task, result );
						}
					}
				});
			} else {
				task.cancel( TaskResultType.NO_SUCH_TASK );
				cleanup();
			}
		} else {
			cleanup();
		}
	}

	void complete( final TaskInternal task, final Object result ){
		if( result instanceof MethodResultData ) {
			final Object data = ((MethodResultData<?>)result).data;
			if( data instanceof TaskResult ) {
				if( data instanceof TaskResultFailed ){
					final TaskResultFailed<?> taskResultFailed = (TaskResultFailed<?>) data;
					task.cancel( taskResultFailed.getReason() );
				} else {
					final TaskResult<?> taskResult = (TaskResult<?>) data;
					if( isNonNull && taskResult.result == null ) {
						task.cancel( TaskResultType.NULL_RESULT );
					} else {
						task.complete( taskResult.result, taskResult.runnable );
					}
				}
			} else {
				if( data == null && isNonNull ) {
					task.cancel( TaskResultType.NULL_RESULT );
				} else {
					task.complete( data, null );
				}
			}
		} else if( result instanceof MethodResultException ){
			final MethodResultException<?> resultException = (MethodResultException<?>) result;
			final Throwable throwable = resultException.getInvocationTargetThrowable();
			if( throwable != null ) {
				if( throwable instanceof TaskAbortException ) {
					task.cancel( throwable.getMessage() );
				} else {
					final Class<? extends Throwable> throwableClass = throwable.getClass();
					if( handleException( task, controller, name, throwableClass, throwable, resultException.getMethod() ) != true ) {
						task.cancel( TaskResultType.EXCEPTION );
						controller.handle( resultException );
					}
				}
			} else {
				task.cancel( TaskResultType.EXCEPTION );
				controller.handle( resultException );
			}
		} else if( result instanceof MethodResultVoid ){
			task.complete( null, null );
		} else {
			task.cancel( TaskResultType.NO_SUCH_TASK );
		}
		cleanup();
	}

	boolean handleException( final TaskInternal task, final Controller controller, final String name, final Class<? extends Throwable> throwableClass, final Throwable throwable, final Method method ) {
		final TypedExceptionHandlerMethods<String> handlers = controller.factory.taskExceptionHandlers;
		final ExceptionHandlerMethod<String> handler = handlers.find( name, throwableClass );
		if( handler != null ) {
			final TrackingData handlerTrackingData = handlers.getTrackingData( name, controller, controller.instance );
			if( LockRequirements.REQUIRED_OR_UNSPECIFIED.contains( handler.getLockRequirement() ) ) {
				final MethodResult<String> handlerResult = handler.call( controller, handlerTrackingData, null, controller.instance, throwable, method );
				if( handlerResult instanceof MethodResultData ) {
					task.cancel( ((MethodResultData<String>)handlerResult).data );
				} else {
					task.cancel( TaskResultType.EXCEPTION );
				}
				controller.handle( handlerResult );
			} else {
				controller.baggage.scheduler.execute(new Runnable(){
					@Override
					public void run() {
						final MethodResult<String> handlerResult = handler.call( controller, handlerTrackingData, null, controller.instance, throwable, method );
						try( final Unlocker unlocker = controller.lock() ) {
							if( handlerResult instanceof MethodResultData ) {
								task.cancel( ((MethodResultData<String>)handlerResult).data );
							} else {
								task.cancel( TaskResultType.EXCEPTION );
							}
							cleanup();
							controller.handle( handlerResult );
						}
					}
				});
			}
			return true;
		}

		if( (controller instanceof SharedComponentController) != true ) {
			final String newName = controller.getName()+"."+name;
			for( final Controller parent: controller.parents ) {
				if( handleException( task, parent, newName, throwableClass, throwable, method ) ) {
					return true;
				}
			}
		}

		return false;
	}

	void cleanup() {
		while( true ) {
			final TaskInternal task = tasks.peek();
			if( task == null ) break;
			if( task.isCanceled() ) {
				if( task == tasks.peek() ) {
					tasks.poll();
					task.getController().cancel( task );
				}
			} else if( task.isDone() ) {
				tasks.poll();
				task.getController().apply( task );
			} else {
				break;
			}
		}
	}

	void cancel( final TaskInternal task ){
		if( $id.equals(task.getId()) ) {
			$pid.set( task.getId() );
		}
		if( task.hasResult() ) {
			$rt.set( task.getResultType() );
		}
	}

	void apply( final TaskInternal task ){
		$pid.set( task.getId() );
		$rt.set( task.getResultType() );
		$r.set( task.getResult() );
		final Runnable runnable = task.getRunnable();
		if( runnable != null ) runnable.run();
	}
}
