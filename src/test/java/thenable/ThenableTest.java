/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package thenable;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.PuppeteerTest;

public class ThenableTest {
	@Test
	public void promise(){
		test("promise.html");
	}

	@Test
	public void thenable(){
		test("thenable.html");
	}

	public void test( final String path ){
		PuppeteerTest.test( "http://localhost:8080/thenable/"+path );
	}

	@BeforeClass
	public static void before() {
		PuppeteerTest.start(ThenableApplication.class);
	}

	@AfterClass
	public static void after() {
		PuppeteerTest.end();
	}
}
