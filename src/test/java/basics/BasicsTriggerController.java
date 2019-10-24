/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import java.util.List;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.DoneFilter;
import org.jdeferred.DonePipe;
import org.jdeferred.FailCallback;
import org.jdeferred.FailFilter;
import org.jdeferred.FailPipe;
import org.jdeferred.ProgressCallback;
import org.jdeferred.ProgressFilter;
import org.jdeferred.ProgressPipe;
import org.jdeferred.Promise;
import org.jdeferred.Promise.State;
import org.jdeferred.impl.DeferredObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;

import org.wcardinal.controller.AbstractController;
import org.wcardinal.controller.TriggerErrors;
import org.wcardinal.controller.TriggerResult;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Unlocked;
import org.wcardinal.controller.data.SLong;

@Controller
public class BasicsTriggerController extends AbstractController {
	@Callable
	void trigger_request( final String eventName, final int value ){
		this.trigger(eventName, value);
	}

	@Callable
	void trigger_request_and( final String eventName, final long timeout, final int value ){
		this.triggerAndWait(eventName, timeout, value)
		.then(new DoneCallback<List<JsonNode>>(){
			@Override
			public void onDone(final List<JsonNode> result) {
				trigger(eventName+"_done", result.get(0).asInt() + 1);
			}
		}, new FailCallback<TriggerErrors>(){
			@Override
			public void onFail(TriggerErrors result) {
				trigger(eventName+"_fail", -1);
			}
		});
	}

	@Autowired
	SLong doneCount;
	@Autowired
	SLong failCount;
	@Autowired
	SLong progressCount;
	@Autowired
	SLong alwaysCount;

	@Callable
	@Unlocked
	boolean trigger_result(){
		doneCount.set( 0L );
		failCount.set( 0L );
		progressCount.set( 0L );
		alwaysCount.set( 0L );

		final TriggerResult result = this.triggerAndWait("trigger_result_event", 3000, 1);

		final DoneCallback<List<JsonNode>> doneCallback = new DoneCallback<List<JsonNode>>(){
			@Override
			public void onDone(List<JsonNode> result) {
				if( result.size() == 1 && result.get(0).asInt() == 1 ) {
					doneCount.incrementAndGet();
				}
			}
		};

		final FailCallback<TriggerErrors> failCallback = new FailCallback<TriggerErrors>(){
			@Override
			public void onFail(TriggerErrors result) {
				failCount.incrementAndGet();
			}
		};

		final ProgressCallback<Integer> progressCallback = new ProgressCallback<Integer>(){
			@Override
			public void onProgress(Integer progress) {
				progressCount.incrementAndGet();
			}
		};

		final AlwaysCallback<List<JsonNode>, TriggerErrors> alwaysCallback = new AlwaysCallback<List<JsonNode>, TriggerErrors>(){
			@Override
			public void onAlways( State state, List<JsonNode> result, TriggerErrors rejected ) {
				if( state == State.RESOLVED && result.size() == 1 && result.get(0).asInt() == 1 && rejected == null ) {
					alwaysCount.incrementAndGet();
				}
			}
		};

		final DoneFilter<List<JsonNode>, String> doneFilter = new DoneFilter<List<JsonNode>, String>(){
			@Override
			public String filterDone(List<JsonNode> result) {
				return "done-filter";
			}
		};

		final FailFilter<TriggerErrors, String> failFilter = new FailFilter<TriggerErrors, String>(){
			@Override
			public String filterFail(TriggerErrors result) {
				return "fail-filter";
			}
		};

		final ProgressFilter<Integer, String> progressFilter = new ProgressFilter<Integer, String>(){
			@Override
			public String filterProgress(Integer progress) {
				return "progress-filter";
			}
		};

		final DoneCallback<String> doneCallbackPiped = new DoneCallback<String>(){
			@Override
			public void onDone(String result) {
				if( result.equals("done-filter") || result.equals("done-pipe") ) {
					doneCount.incrementAndGet();
				}
			}
		};

		final DonePipe<List<JsonNode>, String, String, String> donePipe = new DonePipe<List<JsonNode>, String, String, String>(){
			@Override
			public Promise<String, String, String> pipeDone(List<JsonNode> result) {
				final DeferredObject<String, String, String> deferred = new DeferredObject<String, String, String>();
				deferred.resolve("done-pipe");
				return deferred.promise();
			}
		};

		final FailPipe<TriggerErrors, String, String, String> failPipe = new FailPipe<TriggerErrors, String, String, String>(){
			@Override
			public Promise<String, String, String> pipeFail(TriggerErrors result) {
				final DeferredObject<String, String, String> deferred = new DeferredObject<String, String, String>();
				deferred.reject("fail-pipe");
				return deferred.promise();
			}
		};

		final ProgressPipe<Integer, String, String, String> progressPipe = new ProgressPipe<Integer, String, String, String>(){
			@Override
			public Promise<String, String, String> pipeProgress(Integer progress) {
				final DeferredObject<String, String, String> deferred = new DeferredObject<String, String, String>();
				deferred.reject("progress-pipe");
				return deferred.promise();
			}
		};

		result.then(doneCallback);
		result.then(doneCallback, failCallback);
		result.then(doneCallback, failCallback, progressCallback);
		result.then(doneFilter).done(doneCallbackPiped);
		result.then(doneFilter, failFilter).done(doneCallbackPiped);
		result.then(doneFilter, failFilter, progressFilter).done(doneCallbackPiped);
		result.done(doneCallback);
		result.fail(failCallback);
		result.progress(progressCallback);
		result.always(alwaysCallback);
		result.then(donePipe).done(doneCallbackPiped);
		result.then(donePipe, failPipe).done(doneCallbackPiped);
		result.then(donePipe, failPipe, progressPipe).done(doneCallbackPiped);

		try {
			result.waitSafely();
		} catch (final InterruptedException e) {
			return false;
		}

		try {
			result.waitSafely( 1000 );
		} catch (final InterruptedException e) {
			return false;
		}

		return (
			result.isPending() == false &&
			result.isRejected() == false &&
			result.isResolved() == true &&
			result.state() == State.RESOLVED
		);
	}
}
