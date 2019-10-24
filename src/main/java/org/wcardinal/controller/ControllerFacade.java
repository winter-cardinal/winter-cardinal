/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Provides additional functions of controllers for classes which can not extend {@link org.wcardinal.controller.AbstractController}.
 *
 * <pre><code> {@literal @}Controller
 * class MyController {
 *   {@literal @}Autowired
 *   ControllerFacade facade;
 *
 *   void foo(){
 *     System.out.println( facade.getRemoteAddress() );
 *   }
 * }
 * </code></pre>
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ControllerFacade extends AbstractController implements Facade {

}
