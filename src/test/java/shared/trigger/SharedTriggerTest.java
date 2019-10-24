/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package shared.trigger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.PuppeteerTest;

public class SharedTriggerTest {
	@Test
	public void index(){
		test("index.html");
	}

	public void test( final String path ){
		PuppeteerTest.test( "http://localhost:8080/shared/trigger/"+path );
	}

	@BeforeClass
	public static void before() {
		PuppeteerTest.start(SharedTriggerApplication.class);
	}

	@AfterClass
	public static void after() {
		PuppeteerTest.end();
	}
}
