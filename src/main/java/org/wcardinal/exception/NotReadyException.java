/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.exception;

/**
 * Thrown to indicate methods which are not ready have been invoked.
 *
 * <p>Invoking methods of {@link org.wcardinal.controller.AbstractController AbstractController} or
 * fields including {@link org.wcardinal.controller.data.SClass SClass} from constructors or
 * {@link javax.annotation.PostConstruct @PostConstruct} methods is, typically, the
 * root cause of this exception. If this is the case, please consider using
 * {@link org.wcardinal.controller.annotation.OnCreate @OnCreate}.
 *
 * <blockquote><pre>
 * import org.wcardinal.controller.annotation.Callable;
 * import org.wcardinal.controller.annotation.Controller;
 *
 * &#64;Controller
 * class MyController {
 *   public SClass<String> field;
 *
 *   MyController(SClass<String> field) {
 *     this.field = field;
 *     field.set("Cardinal"); // This throws NotReadyException since SClass<String> is not ready to be used.
 *   }
 *
 *   &#64;OnCreate
 *   void init() {
 *     field.set("Cardinal"); // Instead please initialize here.
 *   }
 * }</pre></blockquote>
 *
 * @see javax.annotation.PostConstruct
 * @see org.wcardinal.controller.annotation.OnCreate
 */
public class NotReadyException extends RuntimeException {
	private static final long serialVersionUID = 4205139096116180187L;

	public NotReadyException(){
		super("Not ready to be used. Please consider using @OnCreate.");
	}

	public NotReadyException( final String message ){
		super( message );
	}
}
