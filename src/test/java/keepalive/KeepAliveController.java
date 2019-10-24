/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package keepalive;

import org.wcardinal.controller.AbstractController;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.KeepAlive;

@Controller(keepAlive=@KeepAlive(interval=1000, ping=1000))
public class KeepAliveController extends AbstractController {
	@Callable
	public int multiply( int a, int b ){
		return a * b;
	}
}
