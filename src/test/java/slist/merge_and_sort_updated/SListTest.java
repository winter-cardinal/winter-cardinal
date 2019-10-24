/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package slist.merge_and_sort_updated;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.PuppeteerTest;

public class SListTest {
	@Test
	public void mergeAndSortUpdated(){
		test("merge-and-sort-updated.html");
	}

	public void test( final String path ){
		PuppeteerTest.test( "http://localhost:8080/slist/"+path );
	}

	@BeforeClass
	public static void before() {
		PuppeteerTest.start(SListApplication.class);
	}

	@AfterClass
	public static void after() {
		PuppeteerTest.end();
	}

}
