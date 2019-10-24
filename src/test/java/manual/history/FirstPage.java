/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package manual.history;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.DisplayName;
import org.wcardinal.controller.annotation.Page;
import org.wcardinal.controller.data.SString;
import org.wcardinal.controller.data.annotation.Historical;
import org.wcardinal.controller.data.annotation.NonNull;

@Page
@DisplayName( "ファーストページ" )
public class FirstPage {
	@Autowired
	@Historical
	@NonNull
	SString selected;

	@Autowired
	@Historical
	@NonNull
	SString search;
}
