/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package manual.history;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.PageFactory;
import org.wcardinal.controller.PopupFactory;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.DisplayName;
import org.wcardinal.controller.annotation.NoPrimaryPage;

@Controller(separatorMessages={"title.separator.1","title.separator.2"})
@NoPrimaryPage
public class HistoryController {
	@Autowired
	@DisplayName( "第１項" )
	FirstPage first;

	@Autowired
	SecondPage second;

	@Autowired
	ComponentA component;

	@Autowired
	PageFactory<PageA> page_factory;

	@Autowired
	PopupFactory<PopupC> popup_factory;
}
