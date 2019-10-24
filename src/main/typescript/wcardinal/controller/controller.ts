/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Connectable } from "../event/connectable";
import { Event } from "../event/event";
import { Iteratee } from "../util/lang/each";
import { PlainObject } from "../util/lang/plain-object";
import { Component } from "./component";
import { ComponentFactory } from "./component-factory";
import { Page } from "./page";
import { PageFactory } from "./page-factory";
import { Popup } from "./popup";
import { PopupFactory } from "./popup-factory";

/**
 * A base class of all controllers.
 */
export interface Controller extends Connectable {
	/**
	 * Returns true if this is read-only.
	 *
	 * @returns true if this is read-only
	 */
	isReadOnly(): boolean;

	/**
	 * Returns true if this is non-null.
	 *
	 * @returns true if this is non-null
	 */
	isNonNull(): boolean;

	/**
	 * Returns true if this is historical.
	 *
	 * @returns true if this is historical
	 */
	isHistorical(): boolean;

	/**
	 * Returns the mapping from the page name to the wcardinal.controller.Page.
	 * The returned mapping is a read-only.
	 *
	 * @returns the mapping from the pagen name to the wcardinal.controller.Page.
	 */
	getPages(): Page[];

	/**
	 * Returns the page of the specified name.
	 * If the page of the specified name does not exist. returns null.
	 *
	 * @param name a page name
	 * @returns the page of the specified name
	 */
	getPage( name: string ): Page | null;

	/**
	 * Returns the active page.
	 * If the active page does not exist, returns null.
	 *
	 * @returns the active page
	 */
	getActivePage(): Page | null;

	/**
	 * Returns the mapping from the popup name to the wcardinal.controller.Popup.
	 * The returned mapping is a read-only.
	 *
	 * @returns the mapping from the popup name to the wcardinal.controller.Popup
	 */
	getPopups(): Popup[];

	/**
	 * Returns the popup of the specified name.
	 * If the popup of the specified name does not exist, returns null.
	 *
	 * @param name a popup name
	 * @returns the popup of the specified name
	 */
	getPopup( name: string ): Popup | null;

	/**
	 * Returns the mapping from the component name to the wcardinal.controller.Component.
	 * The returned mapping is a read-only.
	 *
	 * @returns the mapping from the component name to the wcardinal.controller.Component
	 */
	getComponents(): Component[];

	/**
	 * Returns the component of the specified name.
	 * If the component of the specified name does not exist, returns null.
	 *
	 * @param name a component name
	 * @returns the component of the specified name
	 */
	getComponent( name: string ): Component | null;

	/**
	 * Returns the mapping from the page factory name to the wcardinal.controller.PageFactory.
	 * The returned mapping is a read-only.
	 *
	 * @returns the mapping from the page name to the wcardinal.controller.PageFactory
	 */
	getPageFactories(): PageFactory[];

	/**
	 * Returns the page factory of the specified name.
	 * If the page factory of the specified name does not exist, returns null.
	 *
	 * @param name a page factory name
	 * @returns the page factory of the specified name
	 */
	getPageFactory( name: string ): PageFactory | null;

	/**
	 * Returns the mapping from the popup factory name to the wcardinal.controller.PopupFactory.
	 * The returned mapping is a read-only.
	 *
	 * @returns the mapping from the popup name to the wcardinal.controller.PopupFactory
	 */
	getPopupFactories(): PopupFactory[];

	/**
	 * Returns the popup factory of the specified name.
	 * If the popup factory of the specified name does not exist, returns null.
	 *
	 * @param name a popup factory name
	 * @returns the popup factory of the specified name
	 */
	getPopupFactory( name: string ): PopupFactory | null;

	/**
	 * Returns the mapping from the component factory name to the wcardinal.controller.ComponentFactory.
	 * The returned mapping is a read-only.
	 *
	 * @returns the mapping from the component name to the wcardinal.controller.ComponentFactory
	 */
	getComponentFactories(): ComponentFactory[];

	/**
	 * Returns the component factory of the specified name.
	 * If the component factory of the specified name does not exist, returns null.
	 *
	 * @param name a component factory name
	 * @returns the component factory of the specified name
	 */
	getComponentFactory( name: string ): ComponentFactory | null;

	/**
	 * Returns the name.
	 *
	 * @returns the name
	 */
	getName(): string;

	/**
	 * Returns the parent.
	 *
	 * @returns {*} the parent
	 */
	getParent(): Controller | null;

	/**
	 * Makes this controller visible.
	 *
	 * @throws Error if this is read-only
	 * @returns this
	 */
	show(): this;

	/**
	 * Makes this controller invisible.
	 *
	 * @throws Error if this is read-only
	 * @returns this
	 */
	hide(): this;

	/**
	 * Returns true if this controller is visible.
	 *
	 * @returns true if this controller is visible
	 */
	isShown(): boolean;

	/**
	 * Returns true if this controller is invisible.
	 *
	 * @returns true if this controller is invisible
	 */
	isHidden(): boolean;

	uninitialize(): this;

	isInitialized(): boolean;

	initialize(): this;

	/**
	 * Locks this controller.
	 *
	 * @returns this
	 */
	lock(): this;

	/**
	 * Returns true if this controller is locked.
	 *
	 * @returns true if this controller is locked
	 */
	isLocked(): boolean;

	/**
	 * Unlock this controller.
	 *
	 * @returns this
	 */
	unlock(): this;

	/**
	 * Iterates over fields of this controller, calling the iteratee for each field.
	 * The iteratee is bound to the thisArg and invoked with three arguments: field, name and this controller.
	 * The iteratee may exit iteration early by explicitly returning false.
	 *
	 * @param iteratee the function called per iteration
	 * @param thisArg the this binding of the iteratee
	 * @returns this
	 */
	each( iteratee: Iteratee<string, any, this>, thisArg: unknown ): this;

	/**
	 * Returns the JSON object representing this controller.
	 *
	 * @returns the JSON object representing this controller
	 */
	toJson(): unknown;

	/**
	 * Returns the string representing this controller.
	 *
	 * @returns the string representing this controller
	 */
	toString(): string;

	/**
	 * Triggered when a controller is initialized, or when a controller is changed and its fields are initialized.
	 * If a controller is initialized when event handlers are set,
	 * event handlers are invoked immediately.
	 *
	 *     on( "value", ( event, controller ) => {} )
	 *
	 * @event value
	 * @param event an event object
	 * @param controller a controller itself
	 * @internal
	 */
	onvalue?( event: Event, controller: this ): void;

	/**
	 * Triggered when a controller is changed.
	 *
	 *     on( "change", ( event, controller ) => {} )
	 *
	 * @event change
	 * @param event an event object
	 * @param controller a controller itself
	 * @internal
	 */
	onchange?( event: Event, controller: this ): void;

	/**
	 * Triggered when an active page is changed.
	 * If an active page exists when event handlers are set,
	 * event handlers are invoked immediately.
	 *
	 *     on( "page", ( event, newPageName, oldPageName ) => {} )
	 *
	 * @event page
	 * @param event an event object
	 * @param newPageName a new active page name or a null
	 * @param oldPageName an old active page name or a null
	 * @internal
	 */
	onpage?( event: Event, newPageName: string, oldPageName: string ): void;

	/**
	 * Triggered when a controller gets shown.
	 * If a controller is visible when event handlers are set,
	 * event handlers are invoked immediately.
	 *
	 *     on( "show", ( event, name ) => {} )
	 *
	 * @event show
	 * @param event an event object
	 * @param name a controller name
	 * @internal
	 */
	onshow?( event: Event, name: string ): void;

	/**
	 * Triggered when a controller gets hidden.
	 * If a controller is not visible when event handlers are set,
	 * event handlers are invoked immediately.
	 *
	 *     on( "hide", ( event, name ) => {} )
	 *
	 * @event hide
	 * @param event an event object
	 * @param name a controller name
	 * @internal
	 */
	onhide?( event: Event, name: string ): void;
}
