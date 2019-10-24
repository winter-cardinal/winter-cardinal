/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller;

/**
 * Provides additional functions of popups.
 *
 * <pre><code> {@literal @}Popup
 * class MyPopup extends AbstractPopup {
 *   void foo(){
 *     System.out.println( getRemoteAddress() );
 *   }
 * }
 * </code></pre>
 */
public abstract class AbstractPopup extends AbstractVisibilityController implements PopupContext {

}
