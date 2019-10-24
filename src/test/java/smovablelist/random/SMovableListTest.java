/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package smovablelist.random;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.PuppeteerTest;

public class SMovableListTest {
	@Test
	public void random(){
		test("random.html");
	}

	public void test( final String path ){
		PuppeteerTest.test( "http://localhost:8080/smovablelist/"+path );
	}

	@BeforeClass
	public static void before() {
		PuppeteerTest.start(SMovableListApplication.class);
	}

	@AfterClass
	public static void after() {
		PuppeteerTest.end();
	}

}
