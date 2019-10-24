/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package slist.shared.reload;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.PuppeteerTest;

public class SListSharedReloadTest {
	@Test
	public void reload(){
		test("shared-reload.html");
		test("shared-reload.html");
		test("shared-reload.html");
	}

	public void test( final String path ){
		PuppeteerTest.test( "http://localhost:8080/slist/"+path );
	}

	@BeforeClass
	public static void before() {
		PuppeteerTest.start(SListSharedReloadApplication.class);
	}

	@AfterClass
	public static void after() {
		PuppeteerTest.end();
	}
}
