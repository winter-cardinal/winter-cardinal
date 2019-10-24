/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.field.shared.fail4;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.PageFactory;
import org.wcardinal.controller.annotation.SharedComponent;

@SharedComponent
public class SyntaxCheckSharedComponent {
	@Autowired
	PageFactory<SyntaxCheckPopup> popups;
}
