/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.wcardinal.controller.annotation.Ajax;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Component;

@Component
public class BasicsCallableAjaxComponent {
	@Callable
	@Ajax
	int callable_ajax( final int value ){
		return value * 3;
	}
}
