/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller;

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

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Promise interface to observe a trigger result.
 *
 * @see org.wcardinal.controller.ControllerContext#triggerAndWait(String, long, Object...)
*/
public class TriggerResult implements Promise<List<JsonNode>, TriggerErrors, Integer> {
	final Promise<List<JsonNode>, TriggerErrors, Integer> promise;

	public TriggerResult( final Promise<List<JsonNode>, TriggerErrors, Integer> promise ){
		this.promise = promise;
	}

	@Override
	public Promise.State state() {
		return promise.state();
	}

	@Override
	public boolean isPending() {
		return promise.isPending();
	}

	@Override
	public boolean isResolved() {
		return promise.isResolved();
	}

	@Override
	public boolean isRejected() {
		return promise.isRejected();
	}

	@Override
	public Promise<List<JsonNode>, TriggerErrors, Integer> then(
			DoneCallback<List<JsonNode>> doneCallback) {
		return promise.then(doneCallback);
	}

	@Override
	public Promise<List<JsonNode>, TriggerErrors, Integer> then(
			DoneCallback<List<JsonNode>> doneCallback,
			FailCallback<TriggerErrors> failCallback) {
		return promise.then(doneCallback, failCallback);
	}

	@Override
	public Promise<List<JsonNode>, TriggerErrors, Integer> then(
			DoneCallback<List<JsonNode>> doneCallback,
			FailCallback<TriggerErrors> failCallback,
			ProgressCallback<Integer> progressCallback) {
		return promise.then(doneCallback, failCallback, progressCallback);
	}

	@Override
	public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(
			DoneFilter<List<JsonNode>, D_OUT> doneFilter) {
		return promise.then(doneFilter);
	}

	@Override
	public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(
			DoneFilter<List<JsonNode>, D_OUT> doneFilter,
			FailFilter<TriggerErrors, F_OUT> failFilter) {
		return promise.then(doneFilter, failFilter);
	}

	@Override
	public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(
			DoneFilter<List<JsonNode>, D_OUT> doneFilter,
			FailFilter<TriggerErrors, F_OUT> failFilter,
			ProgressFilter<Integer, P_OUT> progressFilter) {
		return promise.then(doneFilter, failFilter, progressFilter);
	}

	@Override
	public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(
			DonePipe<List<JsonNode>, D_OUT, F_OUT, P_OUT> donePipe) {
		return promise.then(donePipe);
	}

	@Override
	public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(
			DonePipe<List<JsonNode>, D_OUT, F_OUT, P_OUT> donePipe,
			FailPipe<TriggerErrors, D_OUT, F_OUT, P_OUT> failPipe) {
		return promise.then(donePipe, failPipe);
	}

	@Override
	public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(
			DonePipe<List<JsonNode>, D_OUT, F_OUT, P_OUT> donePipe,
			FailPipe<TriggerErrors, D_OUT, F_OUT, P_OUT> failPipe,
			ProgressPipe<Integer, D_OUT, F_OUT, P_OUT> progressPipe) {
		return promise.then(donePipe, failPipe, progressPipe);
	}

	@Override
	public Promise<List<JsonNode>, TriggerErrors, Integer> done(
			DoneCallback<List<JsonNode>> callback) {
		return promise.done(callback);
	}

	@Override
	public Promise<List<JsonNode>, TriggerErrors, Integer> fail(
			FailCallback<TriggerErrors> callback) {
		return promise.fail(callback);
	}

	@Override
	public Promise<List<JsonNode>, TriggerErrors, Integer> always(
			AlwaysCallback<List<JsonNode>, TriggerErrors> callback) {
		return promise.always(callback);
	}

	@Override
	public Promise<List<JsonNode>, TriggerErrors, Integer> progress(
			ProgressCallback<Integer> callback) {
		return promise.progress(callback);
	}

	@Override
	public void waitSafely() throws InterruptedException {
		promise.waitSafely();
	}

	@Override
	public void waitSafely(long timeout) throws InterruptedException {
		promise.waitSafely(timeout);
	}

}
