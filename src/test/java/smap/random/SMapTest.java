/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package smap.random;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.PuppeteerTest;

public class SMapTest {
	@Test
	public void random(){
		test("random.html");
	}

	public void test( final String path ){
		PuppeteerTest.test( "http://localhost:8080/smap/"+path );
	}

	@BeforeClass
	public static void before() {
		PuppeteerTest.start(SMapApplication.class);
	}

	@AfterClass
	public static void after() {
		PuppeteerTest.end();
	}

}
