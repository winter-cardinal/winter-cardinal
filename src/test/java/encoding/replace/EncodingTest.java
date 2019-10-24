/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package encoding.replace;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.PuppeteerTest;

public class EncodingTest {
	@Test
	public void encoding(){
		test("encoding");
	}

	public void test( final String path ){
		PuppeteerTest.test( "http://localhost:8080/"+path );
	}

	@BeforeClass
	public static void before() {
		PuppeteerTest.start(EncodingApplication.class);
	}

	@AfterClass
	public static void after() {
		PuppeteerTest.end();
	}

}
