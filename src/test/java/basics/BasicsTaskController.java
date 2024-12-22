/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.util.concurrent.Uninterruptibles;

import org.wcardinal.controller.ControllerFacade;
import org.wcardinal.controller.TaskAbortException;
import org.wcardinal.controller.TaskResult;
import org.wcardinal.controller.TaskResults;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Locked;
import org.wcardinal.controller.annotation.ReadOnly;
import org.wcardinal.controller.annotation.Task;
import org.wcardinal.controller.annotation.Unlocked;
import org.wcardinal.controller.data.SBoolean;
import org.wcardinal.controller.data.SInteger;
import org.wcardinal.controller.data.annotation.Historical;
import org.wcardinal.controller.data.annotation.NonNull;

@Controller
public class BasicsTaskController {
	@Autowired
	ControllerFacade facade;

	// TASK
	@Task
	static int task( final String value ){
		return value.length();
	}

	// LONG TASK
	@Task
	TaskResult<Integer> long_task( final String value ){
		Uninterruptibles.sleepUninterruptibly(1000, TimeUnit.MILLISECONDS);
		return TaskResults.success( value.length() );
	}

	// LONG TASK 2
	@Task
	int long_task_2( final String value ){
		Uninterruptibles.sleepUninterruptibly(1000, TimeUnit.MILLISECONDS);
		return value.length();
	}

	// LONG TASK 3
	@Autowired
	SInteger field_long_task_3_result;

	@Autowired
	@NonNull
	SInteger field_long_task_3_count;

	@Task
	TaskResult<Integer> long_task_3( final String value ){
		Uninterruptibles.sleepUninterruptibly(1000, TimeUnit.MILLISECONDS);
		final int result = value.length();
		return TaskResults.success(result, new Runnable(){
			@Override
			public void run() {
				field_long_task_3_result.set( result );
				field_long_task_3_count.incrementAndGet();
			}
		});
	}

	// NON NULL
	@Task
	@NonNull
	Integer non_null_task( final String value ){
		return null;
	}

	@Task
	@NonNull
	TaskResult<Integer> non_null_task_with_runnable( final String value ){
		return TaskResults.success();
	}

	// EXCEPTION
	@Task
	void exception_task(){
		throw new RuntimeException("exception for test");
	}

	// NO SUCH TASK
	@Task
	void no_such_task(){

	}

	@Task
	@Locked
	void no_such_task_locked(){

	}

	// FAIL
	@Task
	TaskResult<Void> fail_task(){
		return TaskResults.fail();
	}

	@Task
	TaskResult<Void> fail_task_with_reason(){
		return TaskResults.fail( "Cardinal" );
	}

	// ABORT
	@Task
	String abort_task(){
		throw new TaskAbortException( "a" );
	}

	// READ ONLY
	@Task
	@ReadOnly
	void readonly_task(){

	}

	// HISTORICAL
	@Task
	@Historical
	void historical_task( final String value ){

	}

	// LOCKED
	@Autowired
	SBoolean lock_task_result;

	@Autowired
	SBoolean lock_task_result_done;

	@Task
	TaskResult<Void> lock_task(){
		lock_task_result.set( facade.isLockedByCurrentThread() != true );

		return TaskResults.success( new Runnable(){
			@Override
			public void run() {
				lock_task_result_done.set( facade.isLockedByCurrentThread() );
			}
		});
	}

	@Autowired
	SBoolean lock_task_unlocked_result;

	@Autowired
	SBoolean lock_task_unlocked_result_done;

	@Task
	@Unlocked
	TaskResult<Void> unlocked_task(){
		lock_task_unlocked_result.set( facade.isLockedByCurrentThread() != true );

		return TaskResults.success( new Runnable(){
			@Override
			public void run() {
				lock_task_unlocked_result_done.set( facade.isLockedByCurrentThread() );
			}
		});
	}

	@Autowired
	SBoolean lock_task_locked_result;

	@Autowired
	SBoolean lock_task_locked_result_done;

	@Task
	@Locked
	TaskResult<Void> locked_task(){
		lock_task_locked_result.set( facade.isLockedByCurrentThread() );

		return TaskResults.success( new Runnable(){
			@Override
			public void run() {
				lock_task_locked_result_done.set( facade.isLockedByCurrentThread() );
			}
		});
	}

	// TWO TASK ORDER
	@Autowired
	SInteger field_two_order;

	@Task
	TaskResult<Integer> two_task_a( final String name ){
		Uninterruptibles.sleepUninterruptibly(1000, TimeUnit.MILLISECONDS);
		return TaskResults.success( 1, new Runnable(){
			@Override
			public void run() {
				field_two_order.set( 1 );
			}
		});
	}

	@Task
	TaskResult<Integer> two_task_b( final String name ){
		return TaskResults.success( 2, new Runnable(){
			@Override
			public void run() {
				field_two_order.set( 2 );
			}
		});
	}

	// EVENT
	@Task
	int event_task( final String name ){
		return name.length();
	}

	@Task
	int event_task_fail( final String name ){
		throw new RuntimeException( "exception for test" );
	}

	// CANCEL
	@Autowired
	SBoolean cancel_task_before;

	@Autowired
	SBoolean cancel_task_after;

	@Task
	void cancel_task(){
		cancel_task_before.set( facade.isCanceled() == false );
		Uninterruptibles.sleepUninterruptibly(1000, TimeUnit.SECONDS);
		cancel_task_after.set( facade.isCanceled() == true );
	}

	@Autowired
	SBoolean cancel_task_2_1;

	@Autowired
	SBoolean cancel_task_2_2;

	@Autowired
	SBoolean cancel_task_2_3;

	@Task
	void cancel_task_2(){
		cancel_task_2_1.set( facade.cancel() == true );
		cancel_task_2_2.set( facade.cancel( "reason" ) != true );
		cancel_task_2_3.set( facade.isCanceled() == true );
	}

	@Callable
	boolean cancel_task_from_outside(){
		return facade.cancel() == false && facade.cancel( "reason" ) == false && facade.isCanceled() == false;
	}
}
