/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.PuppeteerTest;

public class BasicsTest {
	@Test
	public void softList() {
		test( "soft-list.html" );
	}

	@Test
	public void softMap() {
		test( "soft-map.html" );
	}

	@Test
	public void softMovableList() {
		test( "soft-movable-list.html" );
	}

	@Test
	public void softNavigableMap() {
		test( "soft-navigable-map.html" );
	}

	@Test
	public void softQueue() {
		test( "soft-queue.html" );
	}

	@Test
	public void softScalar() {
		test( "soft-scalar.html" );
	}

	@Test
	public void binary() {
		test( "binary.html" );
	}

	@Test
	public void callable() {
		test( "callable.html" );
	}

	@Test
	public void callableTiming() {
		test( "callable-timing.html" );
	}

	@Test
	public void callableException() {
		test( "callable-exception.html" );
	}

	@Test
	public void callableFactory() {
		test( "callable-factory.html" );
	}

	@Test
	public void callableAjax() {
		test( "callable-ajax.html" );
	}

	@Test
	public void task() {
		test( "task.html" );
	}

	@Test
	public void taskException() {
		test( "task-exception.html" );
	}

	@Test
	public void constant() {
		test( "constant.html" );
	}

	@Test
	public void controllerChecker() {
		test( "check-a.html" );
		test( "check-b.html" );
		test( "check-c.html" );
		test( "check-a.html" );
		test( "check-b.html" );
		test( "check-c.html" );
	}

	@Test
	public void controllerParameter() {
		test( "controller-parameter.html" );
		test( "controller-parameter.html" );
		test( "controller-separator-0.html" );
		test( "controller-separator-1.html" );
		test( "controller-separator-2.html" );
		test( "controller-separator-3.html" );
		test( "controller-urls-1.html" );
		test( "controller-urls-2.html" );
	}

	@Test
	public void controller() {
		test( "controller.html" );
		test( "controller.html" );
	}

	@Test
	public void debounced() {
		test( "debounced.html" );
	}

	@Test
	public void tracked() {
		test( "tracked.html" );
	}

	@Test
	public void event() {
		test( "event.html" );
	}

	@Test
	public void facade() {
		test( "facade.html" );
	}

	@Test
	public void factoryParameter() {
		test( "factory-parameter.html" );
	}

	@Test
	public void factory() {
		test( "factory.html" );
	}

	@Test
	public void fieldScalar() {
		test( "field-scalar.html" );
	}

	@Test
	public void fieldNumber() {
		test( "field-number.html" );
	}

	@Test
	public void fieldList() {
		test( "field-list.html" );
	}

	@Test
	public void fieldMovableList() {
		test( "field-movable-list.html" );
	}

	@Test
	public void fieldMap() {
		test( "field-map.html" );
	}

	@Test
	public void fieldNavigableMap() {
		test( "field-navigable-map.html" );
	}

	@Test
	public void fieldQueue() {
		test( "field-queue.html" );
	}

	@Test
	public void interval() {
		test( "interval.html" );
	}

	@Test
	public void lock() {
		test( "lock.html" );
		test( "lock.html" );
	}

	@Test
	public void networkPolling() {
		test( "network-polling.html" );
	}

	@Test
	public void network() {
		test( "network.html" );
	}

	@Test
	public void notification() {
		test( "notification.html" );
	}

	@Test
	public void page() {
		test( "page.html" );
		test( "page.html" );
	}

	@Test
	public void popup() {
		test( "popup.html" );
		test( "popup.html" );
	}

	@Test
	public void request() {
		test( "request.html" );
		test( "request.html" );
	}

	@Test
	public void scope() {
		test( "scope.html" );
		test( "scope.html" );
	}

	@Test
	public void shared() {
		test( "shared.html" );
	}

	@Test
	public void throttled() {
		test( "throttled.html" );
	}

	@Test
	public void timeout() {
		test( "timeout.html" );
	}

	@Test
	public void execute() {
		test( "execute.html" );
	}

	@Test
	public void trigger() {
		test( "trigger.html" );
	}

	@Test
	public void triggerDirect() {
		test( "trigger-direct.html" );
	}

	@Test
	public void reducedEvent() {
		test( "reduced-event.html" );
	}

	@Test
	public void initialize() {
		test( "initialize.html" );
	}

	@Test
	public void local() {
		test( "local.html" );
	}

	@Test
	public void exception() {
		test( "exception-1.html" );
		test( "exception-2.html" );
		test( "exception-3.html" );
		test( "exception-4.html" );
		test( "exception-5.html" );
		test( "exception-6.html" );
		test( "exception-7.html" );
		test( "exception-8.html" );
	}

	public void test( final String path ){
		PuppeteerTest.test( "http://localhost:8080/basics/"+path );
	}

	@BeforeClass
	public static void before() {
		PuppeteerTest.start(BasicsApplication.class);
	}

	@AfterClass
	public static void after() {
		PuppeteerTest.end();
	}
}
