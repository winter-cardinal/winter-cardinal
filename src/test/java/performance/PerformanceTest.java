/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package performance;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.PuppeteerTest;

public class PerformanceTest {
	@Test
	public void worker(){
		test("worker.html");
	}

	@Test
	public void noWorker(){
		test("no-worker.html");
	}

	public void test( final String path ){
		PuppeteerTest.test( "http://localhost:8080/performance/"+path );
	}

	@BeforeClass
	public static void before() {
		PuppeteerTest.start(PerformanceApplication.class);
	}

	@AfterClass
	public static void after() {
		PuppeteerTest.end();
	}
}
