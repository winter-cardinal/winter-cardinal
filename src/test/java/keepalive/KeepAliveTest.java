/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package keepalive;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.PuppeteerTest;

public class KeepAliveTest {
	@Test
	public void keepalive(){
		test("index.html");
	}

	public void test( final String path ){
		PuppeteerTest.test( "http://localhost:8080/keepalive/"+path );
	}

	@BeforeClass
	public static void before() {
		PuppeteerTest.start(KeepAliveApplication.class);
	}

	@AfterClass
	public static void after() {
		PuppeteerTest.end();
	}

}
