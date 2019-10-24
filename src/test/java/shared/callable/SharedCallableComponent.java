/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package shared.callable;

import org.wcardinal.controller.AbstractComponent;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.SharedComponent;

@SharedComponent
public class SharedCallableComponent extends AbstractComponent {
	@Callable
	void callable() {}
}
