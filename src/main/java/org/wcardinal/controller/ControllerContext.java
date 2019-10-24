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

import org.jdeferred.Promise;

import com.fasterxml.jackson.databind.node.ArrayNode;

import org.wcardinal.util.doc.ThreadSafe;
import org.wcardinal.util.thread.Scheduler;
import org.wcardinal.util.thread.Unlockable;
import org.wcardinal.util.thread.Unlocker;

/**
 * Central interface to provide methods for controllers.
 */
public interface ControllerContext extends Unlockable {
	/**
	 * Locks this controller.
	 *
	 * <p>Please note that all the fields and controllers belonging to the same controller instance share a lock.
	 * Thus, the followings is not necessary:
	 *
	 * <pre><code> {@literal @}Controller
	 * class MyController {
	 *   {@literal @}Autowired
	 *   SString name;
	 *
	 *   {@literal @}Autowired
	 *   SInteger score;
	 *
	 *   {@literal @}OnChange("name")
	 *   void onChange(){
	 *     try( Unlocker unlocker = score.lock() ){ // Unnecessary
	 *       score.set( 0 );
	 *     }
	 *   }
	 * }
	 * </code></pre>
	 *
	 * When the 'onChange' method is called, the 'name' field is locked.
	 * And the 'name' field, the 'score' field and the 'MyController' controller share a lock.
	 * The 'score' field, therefore, is locked at the time the 'onChange' method is called.
	 *
	 * The only exception to this is a component annotated with {@link org.wcardinal.controller.annotation.SharedComponent}.
	 * Shared components and fields on them do not share a lock with the others.
	 *
	 * Because shared components do not share a lock, the lock ordering is necessary to avoid concurrency issues.
	 * If locking a shared component and one of its parents at once is unavoidable, must lock a parent at first.
	 * And then lock a shared component.
	 *
	 * <pre><code> {@literal @}SharedComponent
	 * class MySharedComponent{}
	 *
	 * {@literal @}Controller
	 * class MyController {
	 *   {@literal @}Autowired
	 *   MySharedComponent component;
	 *
	 *   void foo(){
	 *     // At first lock itself, the parent of the 'this.component'
	 *     try( Unlocker parent = lock() ) {
	 *       // Then lock a shared component
	 *       try( Unlocker child = component.lock() ) {
	 *         // Do something here
	 *       }
	 *     }
	 *   }
	 * }
	 * </code></pre>
	 *
	 * @return {@link org.wcardinal.util.thread.Unlocker Unlocker} instance for unlocking this lock
	 */
	@ThreadSafe
	Unlocker lock();

	/**
	 * Tries to lock this controller.
	 *
	 * @return true if succeeded
	 */
	@ThreadSafe
	boolean tryLock();

	/**
	 * Tries to lock this controller.
	 *
	 * @param timeout the timeout for this trial
	 * @param unit the unit of the timeout
	 * @return true if succeeded
	 */
	@ThreadSafe
	boolean tryLock( final long timeout, final TimeUnit unit );

	/**
	 * Returns true if this controller is locked.
	 *
	 * @return true if this controller is locked.
	 */
	@ThreadSafe
	boolean isLocked();

	/**
	 * Returns true if this controller is locked by the current thread.
	 *
	 * @return true if this controller is locked by the current thread.
	 */
	@ThreadSafe
	boolean isLockedByCurrentThread();

	/**
	 * Returns true if this controller is read-only.
	 *
	 * @return true if this controller is read-only.
	 */
	@ThreadSafe
	boolean isReadOnly();

	/**
	 * Returns true if this controller is non-null.
	 *
	 * @return true if this controller is non-null.
	 */
	@ThreadSafe
	boolean isNonNull();

	/**
	 * Returns true if this controller is historical.
	 *
	 * @return true if this controller is historical.
	 */
	@ThreadSafe
	boolean isHistorical();

	/**
	 * Unlocks this controller.
	 */
	@ThreadSafe
	@Override
	void unlock();

	/**
	 * Returns the name of this controller.
	 *
	 * @return the name of this controller
	 */
	@ThreadSafe
	String getName();

	/**
	 * Returns the parent.
	 *
	 * @param <T> the type of parent
	 * @return the parent instance
	 */
	@ThreadSafe
	<T> T getParent();

	/**
	 * Returns the parent as a factory.
	 *
	 * @return the parent instance
	 */
	@ThreadSafe
	Factory<?> getParentAsFactory();

	/**
	 * Returns the parents.
	 *
	 * @param <T> the type of parents
	 * @return the parent instances
	 */
	@ThreadSafe
	<T> Collection<T> getParents();

	/**
	 * Returns the active page or null.
	 *
	 * @param <T> the type of the active page
	 * @return the active page
	 */
	@ThreadSafe
	<T> T getActivePage();

	/**
	 * Returns the first parameter of the specified key.
	 * The parameters are taken from the query string or posted form data.
	 *
	 * <pre><code> // HTML
	 * {@literal <script src="my-controller?name=John"></script>}
	 *
	 * // Java
	 * {@literal @}Controller
	 * class MyController {
	 *   {@literal @}OnCreate
	 *   void onCreate(){
	 *      System.out.println( getParameter("name") ); // prints "John"
	 *   }
	 * }
	 * </code></pre>
	 *
	 * @param key the key of the parameter
	 * @return the first parameter of the specified key
	 */
	@ThreadSafe
	String getParameter( final String key );

	/**
	 * Returns the parameters of the specified key.
	 * The parameters are taken from the query string or posted form data.
	 *
	 * <pre><code> // HTML
	 * {@literal <script src="my-controller?name=John"></script>}
	 *
	 * // Java
	 * {@literal @}Controller
	 * class MyController {
	 *   {@literal @}OnCreate
	 *   void onCreate(){
	 *     System.out.println( Arrays.toString(getParameters("name")) ); // prints ["John"]
	 *   }
	 * }
	 * </code></pre>
	 *
	 * @param key the key of the parameters
	 * @return the parameters of the specified key
	 */
	@ThreadSafe
	String[] getParameters( final String key );


	/**
	 * Returns the map of parameters.
	 * The parameters are taken from the query string or posted form data.
	 *
	 * <pre><code> // HTML
	 * {@literal <script src="my-controller?name=John"></script>}
	 *
	 * // Java
	 * {@literal @}Controller
	 * class MyController {
	 *   {@literal @}OnCreate
	 *   void onCreate(){
	 *     System.out.println( getParameterMap("name") ); // prints {name=["John"]}
	 *   }
	 * }
	 * </code></pre>
	 *
	 * @return the map of parameters
	 */
	@ThreadSafe
	Map<String, String[]> getParameterMap();

	/**
	 * Returns the factory parameters.
	 * The factory parameters are the parameters given to
	 * the method {@link org.wcardinal.controller.Factory#create(Object...)}.
	 *
	 * <pre><code> {@literal @}Controller
	 * class MyController {
	 *   {@literal @}Autowired
	 *   {@literal ComponentFactory<MyComponent>} factory;
	 *
	 *   {@literal @}OnCreate
	 *   void onCreate(){
	 *     factory.create(1, "John");
	 *   }
	 * }
	 *
	 * class MyComponent extends AbstractComponent {
	 *   {@literal @}OnCreate
	 *   void onCreate(){
	 *     System.out.println( getFactoryParameters() ); // prints [1, "John"]
	 *   }
	 * }
	 * </code></pre>
	 *
	 * @return the factory parameters.
	 */
	@ThreadSafe
	ArrayNode getFactoryParameters();

	/**
	 * Returns the attributes.
	 *
	 * @return the attributes.
	 */
	@ThreadSafe
	ControllerAttributes getAttributes();

	/**
	 * Returns the locale objects in decreasing order starting with the preferred locale.
	 *
	 * @return the locale objects
	 */
	@ThreadSafe
	List<Locale> getLocales();

	/**
	 * Returns the preferred locale.
	 *
	 * @return the preferred locale
	 */
	@ThreadSafe
	Locale getLocale();

	/**
	 * Shows this controller.
	 */
	@ThreadSafe
	void show();

	/**
	 * Hides this controller.
	 */
	@ThreadSafe
	void hide();

	/**
	 * Returns true if this controller is shown.
	 *
	 * @return true if this controller is shown.
	 */
	@ThreadSafe
	boolean isShown();

	/**
	 * Returns true if this controller is hidden.
	 *
	 * @return true if this controller is hidden.
	 */
	@ThreadSafe
	boolean isHidden();

	/**
	 * Returns the user's remote address.
	 *
	 * @return user's remote address
	 */
	@ThreadSafe
	String getRemoteAddress();

	/**
	 * Returns the user principle.
	 *
	 * @return user principle
	 */
	@ThreadSafe
	Principal getPrincipal();

	/**
	 * Returns the scheduler.
	 *
	 * @return the scheduler
	 */
	Scheduler getScheduler();

	/**
	 * Requests to execute methods annotated with the {@link org.wcardinal.controller.annotation.OnTime} of the specified name.
	 *
	 * @param name method name specified by {@link org.wcardinal.controller.annotation.OnTime}
	 * @param parameters parameters to be passed to the specified methods
	 *
	 */
	@ThreadSafe
	void execute( final String name, final Object... parameters );

	/**
	 * Requests to execute the specified runnable.
	 *
	 * @param runnable runnable to be executed
	 * @return future
	 *
	 */
	@ThreadSafe
	Future<?> execute( final Runnable runnable );

	/**
	 * Requests to execute the specified callable.
	 *
	 * @param <T> return type of the specified callable
	 * @param callable callable to be executed
	 * @return future
	 *
	 */
	@ThreadSafe
	<T> Future<T> execute( final Callable<T> callable );

	/**
	 * Requests to call methods annotated with the {@link org.wcardinal.controller.annotation.OnTime} of the specified
	 * name after the specified delay and returns an ID of this request.
	 * Use the returned ID and {@link #cancel(long)} for canceling the request.
	 *
	 * @param name method name specified by {@link org.wcardinal.controller.annotation.OnTime}
	 * @param delay delay in milliseconds
	 * @param parameters parameters to be passed to the specified methods
	 * @return request ID
	 * @see #cancel()
	 * @see #cancel(long)
	 * @see #cancelAll()
	 */
	@ThreadSafe
	long timeout( final String name, final long delay, final Object... parameters );

	/**
	 * Requests to call the specified runnable after the specified delay and returns an ID of this request.
	 * Use the returned ID and {@link #cancel(long)} for canceling the request.
	 *
	 * @param runnable runnable to be executed
	 * @param delay delay in milliseconds
	 * @return request ID
	 * @see #cancel()
	 * @see #cancel(long)
	 * @see #cancelAll()
	 */
	@ThreadSafe
	long timeout( final Runnable runnable, final long delay );

	/**
	 * Requests to call the specified callable after the specified delay.
	 * Use the returned ID and {@link #cancel(long)} for canceling the request.
	 *
	 * @param <T> return type of the specified callable
	 * @param callable callable to be executed
	 * @param delay delay in milliseconds
	 * @return future
	 *
	 * @see #cancel()
	 * @see #cancel(long)
	 * @see #cancelAll()
	 */
	@ThreadSafe
	<T> TimeoutFuture<T> timeout( final Callable<T> callable, final long delay );

	/**
	 * Requests to call methods annotated with the {@link org.wcardinal.controller.annotation.OnTime} of the specified
	 * name at the specified interval and returns an ID of this request.
	 * Use the returned ID and {@link #cancel(long)}, or {@link #cancel()} for canceling the request.
	 * The first call is taking place after the milliseconds specified as the interval.
	 *
	 * @param name method name specified by {@link org.wcardinal.controller.annotation.OnTime}
	 * @param interval interval in milliseconds
	 * @return request ID
	 * @see #cancel()
	 * @see #cancel(long)
	 * @see #cancelAll()
	 */
	@ThreadSafe
	long interval( final String name, final long interval );

	/**
	 * Requests to call methods annotated with the {@link org.wcardinal.controller.annotation.OnTime} of the specified
	 * name at the specified interval after the 'startAfter' milliseconds and returns an ID of this request.
	 * Use the returned ID and {@link #cancel(long)}, or {@link #cancel()} for canceling the request.
	 * The first call is taking place after the 'startAfter' milliseconds.
	 *
	 * @param name method name specified by {@link org.wcardinal.controller.annotation.OnTime}
	 * @param startAfter delay of the first call in milliseconds
	 * @param interval interval in milliseconds
	 * @param parameters parameters to be passed to the specified methods
	 * @return request ID
	 * @see #cancel()
	 * @see #cancel(long)
	 * @see #cancelAll()
	 */
	@ThreadSafe
	long interval( final String name, final long startAfter, final long interval, final Object... parameters );

	/**
	 * Requests to call the specified runnable at the specified interval and returns an ID of this request.
	 * Use the returned ID and {@link #cancel(long)}, or {@link #cancel()} for canceling the request.
	 * The first call is taking place after the milliseconds specified as the interval.
	 *
	 * @param runnable runnable to be executed
	 * @param interval interval in milliseconds
	 * @return request ID
	 * @see #cancel()
	 * @see #cancel(long)
	 * @see #cancelAll()
	 */
	@ThreadSafe
	long interval( final Runnable runnable, final long interval );

	/**
	 * Requests to call the specified runnable at the specified interval after the 'startAfter' milliseconds and returns an ID of this request.
	 * Use the returned ID and {@link #cancel(long)}, or {@link #cancel()} for canceling the request.
	 * The first call is taking place after the 'startAfter' milliseconds.
	 *
	 * @param runnable runnable to be executed
	 * @param startAfter delay of the first call in milliseconds
	 * @param interval interval in milliseconds
	 * @return request ID
	 * @see #cancel()
	 * @see #cancel(long)
	 * @see #cancelAll()
	 */
	@ThreadSafe
	long interval( final Runnable runnable, final long startAfter, final long interval );

	/**
	 * Cancels the request of the specified ID issued by {@code timeout} or {@code interval} methods.
	 *
	 * @param id request ID
	 * @return true if succeeded
	 * @see #timeout(String, long, Object...)
	 * @see #timeout(Runnable, long)
	 * @see #timeout(Callable, long)
	 * @see #interval(String, long)
	 * @see #interval(String, long, long, Object...)
	 * @see #interval(Runnable, long)
	 * @see #interval(Runnable, long, long)
	 */
	@ThreadSafe
	boolean cancel( final long id );

	/**
	 * Cancels the current request issued by {@code timeout} or {@code interval} methods, or the current task.
	 * Under the hood, a thread local is used to distinguish requests/tasks.
	 *
	 * <pre><code> {@literal @}Controller
	 * class MyController extends AbstractController {
	 *   {@literal @}OnCreate
	 *   void onCreate(){
	 *     interval( "update", 1000 );
	 *   }
	 *
	 *   {@literal @}OnTime
	 *   void update(){
	 *     // Stops the "update" by itself
	 *     cancel();
	 *   }
	 * }
	 * </code></pre>
	 *
	 * @return true if succeeded
	 * @see #timeout(String, long, Object...)
	 * @see #timeout(Runnable, long)
	 * @see #timeout(Callable, long)
	 * @see #interval(String, long)
	 * @see #interval(String, long, long, Object...)
	 * @see #interval(Runnable, long)
	 * @see #interval(Runnable, long, long)
	 */
	@ThreadSafe
	boolean cancel();

	/**
	 * Cancels the current task.
	 * Under the hood, a thread local is used to distinguish tasks.
	 *
	 * @param reason a cancel reason
	 * @return true if succeeded
	 */
	@ThreadSafe
	boolean cancel( final String reason );

	/**
	 * Cancels all requests issued by {@code timeout} or {@code interval} methods.
	 *
	 * @see #timeout(String, long, Object...)
	 * @see #timeout(Runnable, long)
	 * @see #timeout(Callable, long)
	 * @see #interval(String, long)
	 * @see #interval(String, long, long, Object...)
	 * @see #interval(Runnable, long)
	 * @see #interval(Runnable, long, long)
	 */
	@ThreadSafe
	void cancelAll();

	/**
	 * Returns true if the current task is canceled or if the current request issued by {@code timeout} or {@code interval} methods is canceled/non-existing.
	 * Under the hood, a thread local is used to distinguish requests/tasks
	 *
	 * @return true if the current task is canceled or if the current request issued by {@code timeout} or {@code interval} methods is canceled/non-existing
	 */
	@ThreadSafe
	boolean isCanceled();

	/**
	 * Returns true if the thread calling this method is the forefront executor of methods annotated with {@link org.wcardinal.controller.annotation.Tracked}.
	 *
	 * @return true if the thread calling this method is the forefront executor of methods annotated with {@link org.wcardinal.controller.annotation.Tracked}
	 */
	@ThreadSafe
	boolean isHeadCall();

	/**
	 * Triggers the event of the given name at browsers.
	 * Optional arguments are passed to browsers as event data.
	 *
	 * <pre><code> // JavaScript
	 * myController.on( 'hello', function( e, name ){
	 *   console.log( name ); // prints 'John'
	 * });
	 *
	 * // Java
	 * {@literal @}Controller
	 * class MyController extends AbstractController {
	 *   {@literal @}OnCreate
	 *   void onCreate(){
	 *     timeout( "hello", 5000 );
	 *   }
	 *
	 *   {@literal @}OnTime
	 *   void hello(){
	 *     trigger( "hello", "John" );
	 *   }
	 * }
	 * </code></pre>
	 *
	 * @param name event name
	 * @param arguments event data
	 * @see #triggerAndWait(String, long, Object...)
	 * @see #triggerDirect(String, Object...)
	 */
	@ThreadSafe
	void trigger( final String name, final Object... arguments );

	/**
	 * Triggers the event of the given name at browsers directly.
	 * Only difference between {@link #trigger(String, Object...)} and this is this method sends trigger events directly
	 * by bypassing the most of the costly synchronization processes.
	 * Thus, in general, supposed to be faster than {@link #trigger(String, Object...)}.
	 * However, because of this, weak against network disconnections.
	 *
	 * @param name event name
	 * @param arguments event data
	 * @see #trigger(String, Object...)
	 * @see #triggerAndWait(String, long, Object...)
	 */
	@ThreadSafe
	void triggerDirect( final String name, final Object... arguments );

	/**
	 * Triggers the event of the specified name at browsers and waits for responses from browsers.
	 * Optional arguments are passed to browsers as event data.
	 * When a browser send back responses within a given period of time,
	 * the callback {@link org.jdeferred.DoneCallback} set to the {@link Promise#done(org.jdeferred.DoneCallback)} is called.
	 * Otherwise, the callback {@link org.jdeferred.FailCallback} set to the {@link Promise#fail(org.jdeferred.FailCallback)} is called.
	 *
	 * <pre><code> // JavaScript
	 * myController.on( 'hello', function( e, name ){
	 *   console.log( name ); // prints 'John'
	 *   return 'Hello, '+name;
	 * });
	 *
	 * // Java
	 * {@literal @}Controller
	 * class MyController extends AbstractController {
	 *   {@literal @}OnCreate
	 *   void onCreate(){
	 *     timeout( "hello", 5000 );
	 *   }
	 *
	 *   {@literal @}OnTime
	 *   void hello(){
	 *     trigger( "hello", 3000, "John" )
	 *     .done(new {@literal DoneCallback<List<JsonNode>>}(){
	 *       public void onDone({@literal List<JsonNode>} result) {
	 *         System.out.println( result.get( 0 ).asText() ); // prints "Hello, John"
	 *       }
	 *     });
	 *   }
	 * }
	 * </code></pre>
	 *
	 * @param name event name
	 * @param timeout timeout for this trigger in milliseconds
	 * @param arguments event data
	 * @return promise instance
	 * @see #trigger(String, Object...)
	 * @see #triggerDirect(String, Object...)
	 */
	@ThreadSafe
	TriggerResult triggerAndWait( final String name, final long timeout, final Object... arguments );

	/**
	 * Requests to call methods annotated with the {@link org.wcardinal.controller.annotation.OnNotice} of the specified name.
	 * Notifications propagate to parents. To catch a notification triggered by a child, use a name prefixed with a child name.
	 *
	 * <pre><code> {@literal @}Component
	 * class MyComponent extends AbstractComponent {
	 *   void notifyBar(){
	 *     notify( "bar" );
	 *   }
	 * }
	 *
	 * {@literal @}Controller
	 * class MyController {
	 *   {@literal @}Autowired
	 *   MyComponent foo;
	 *
	 *   {@literal @}OnNotice("foo.bar")
	 *   void onNotice(){
	 *     // Called when the child 'foo' sends a 'bar' notification
	 *   }
	 * }
	 * </code></pre>
	 *
	 * @param name notification name
	 * @param parameters notification parameters
	 * @see #notifyAsync(String, Object...)
	 */
	void notify( final String name, final Object... parameters );

	/**
	 * Requests to call methods annotated with the {@link org.wcardinal.controller.annotation.OnNotice} of the specified name asynchronously.
	 * Notifications propagate to parents. To catch a notification triggered by a child, use a name prefixed with a child name.
	 *
	 * <pre><code> {@literal @}Component
	 * class MyComponent extends AbstractComponent {
	 *   void notifyBar(){
	 *     notifyAsync( "bar" );
	 *   }
	 * }
	 *
	 * {@literal @}Controller
	 * class MyController {
	 *   {@literal @}Autowired
	 *   MyComponent foo;
	 *
	 *   {@literal @}OnNotice("foo.bar")
	 *   void onNotice(){
	 *     // Called when the child 'foo' sends a 'bar' notification
	 *   }
	 * }
	 * </code></pre>
	 *
	 * @param name notification name
	 * @param parameters notification parameters
	 * @see #notify(String, Object...)
	 */
	void notifyAsync( final String name, final Object... parameters );

	/**
	 * Returns the session ID.
	 *
	 * @return the session ID
	 */
	String getSessionId();

	/**
	 * Returns the sub session ID.
	 *
	 * @return the sub session ID
	 */
	String getSubSessionId();
}
