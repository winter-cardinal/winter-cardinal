/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller;

/**
 * Provides additional functions of pages.
 *
 * <pre><code> {@literal @}Page
 * class MyPage extends AbstractPage {
 *   void foo(){
 *     System.out.println( getRemoteAddress() );
 *   }
 * }
 * </code></pre>
 */
public abstract class AbstractPage extends AbstractVisibilityController implements PageContext {
}
