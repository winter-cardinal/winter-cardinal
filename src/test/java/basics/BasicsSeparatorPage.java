/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.DisplayName;
import org.wcardinal.controller.annotation.Page;

@Page
@DisplayName( "page" )
public class BasicsSeparatorPage {
	@Autowired
	BasicsSeparatorPageSub sub;
}
