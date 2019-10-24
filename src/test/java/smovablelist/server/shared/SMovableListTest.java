/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package smovablelist.server.shared;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.PuppeteerTest;

public class SMovableListTest {
	@Test
	public void serverShared(){
		test("server-shared.html");
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
