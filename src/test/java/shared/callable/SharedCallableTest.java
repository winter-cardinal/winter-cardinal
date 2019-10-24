/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package shared.callable;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.PuppeteerTest;

public class SharedCallableTest {
	@Test
	public void index(){
		test("index.html");
	}

	public void test( final String path ){
		PuppeteerTest.test( "http://localhost:8080/shared/callable/"+path );
	}

	@BeforeClass
	public static void before() {
		PuppeteerTest.start(SharedCallableApplication.class);
	}

	@AfterClass
	public static void after() {
		PuppeteerTest.end();
	}
}
