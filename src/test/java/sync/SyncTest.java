/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package sync;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.PuppeteerTest;

public class SyncTest {
	@Test
	public void disconnect(){
		test("index.html");
	}

	public void test( final String path ){
		PuppeteerTest.test( "http://localhost:8080/sync/"+path );
	}

	@BeforeClass
	public static void before() {
		PuppeteerTest.start(SyncApplication.class);
	}

	@AfterClass
	public static void after() {
		PuppeteerTest.end();
	}

}
