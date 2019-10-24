/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package shared.trigger;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;

import org.wcardinal.controller.AbstractComponent;
import org.wcardinal.controller.TriggerErrors;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnTime;
import org.wcardinal.controller.annotation.SharedComponent;
import org.wcardinal.controller.data.SLong;
import org.wcardinal.controller.data.annotation.NonNull;

@SharedComponent
public class SharedTriggerComponent extends AbstractComponent {
	final Logger logger = LoggerFactory.getLogger(SharedTriggerComponent.class);

	@OnCreate
	void init() {
		interval( "trigger", 0, 500 );
	}

	@Autowired @NonNull
	SLong done;

	@Autowired @NonNull
	SLong fail;

	@Autowired @NonNull
	SLong checkFail;

	boolean check( final List<JsonNode> result ) {
		if( result.size() == 0 ) {
			return true;
		} else if( result.size() == 1 ) {
			final JsonNode firstNode = result.get( 0 );
			if( firstNode.isTextual() ) {
				final String firstString = firstNode.textValue();
				return (firstString.equals("A") || firstString.equals("B"));
			}
		} else if( result.size() == 2 ) {
			final JsonNode firstNode = result.get( 0 );
			final JsonNode secondNode = result.get( 1 );
			if( firstNode.isTextual() && secondNode.isTextual() ) {
				final String firstString = firstNode.textValue();
				final String secondString = secondNode.textValue();
				return (firstString.equals("A") && secondString.equals("B")) || (firstString.equals("B") && secondString.equals("A"));
			}
		}
		return false;
	}

	final AtomicInteger counter = new AtomicInteger( 0 );

	@OnTime
	void trigger() {
		final int count = counter.getAndIncrement();
		this.triggerAndWait( "event", 2000, "data", count )
		.then(new DoneCallback<List<JsonNode>>() {
			@Override
			public void onDone( List<JsonNode> result ) {
				if( check( result ) ) {
					done.incrementAndGet();
					logger.info("done {} {}", count, result);
					if( 20 <= done.get() ) {
						cancelAll();
					}
				} else {
					fail.incrementAndGet();
					logger.info("check-fail {} {}", count, result);
				}
			}
		}, new FailCallback<TriggerErrors>() {
			@Override
			public void onFail( TriggerErrors result ) {
				fail.incrementAndGet();
				logger.info("fail {} {}", count, result);
			}
		});
	}
}
