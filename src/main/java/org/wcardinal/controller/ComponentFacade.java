/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Provides additional functions of components for classes which can not extend {@link org.wcardinal.controller.AbstractComponent}.
 *
 * <pre><code> {@literal @}Component
 * class MyComponent {
 *   {@literal @}Autowired
 *   ComponentFacade facade;
 *
 *   void foo(){
 *     System.out.println( facade.getRemoteAddress() );
 *   }
 * }
 * </code></pre>
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ComponentFacade extends AbstractComponent implements Facade {

}
