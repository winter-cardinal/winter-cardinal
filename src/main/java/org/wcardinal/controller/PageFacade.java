/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Provides additional functions of pages for classes which can not extend {@link org.wcardinal.controller.AbstractPage}.
 *
 * <pre><code> {@literal @}Page
 * class MyPage {
 *   {@literal @}Autowired
 *   PageFacade facade;
 *
 *   void foo(){
 *     System.out.println( facade.getRemoteAddress() );
 *   }
 * }
 * </code></pre>
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PageFacade extends AbstractPage implements Facade {

}
