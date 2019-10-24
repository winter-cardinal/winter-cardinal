/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Connectable } from "../../event/connectable";
import { Iteratee } from "../../util/lang/each";
import { Component } from "../component";
import { ComponentFactory } from "../component-factory";
import { Controller } from "../controller";
import { Page } from "../page";
import { PageFactory } from "../page-factory";
import { Popup } from "../popup";
import { PopupFactory } from "../popup-factory";
import { ControllerMemory } from "./controller-memory";
import { ControllerType } from "./controller-type";

export class ControllerImpl<
	C extends Controller = Controller,
	M extends ControllerMemory<C> = ControllerMemory<C>
> extends Connectable
	implements Controller {
	constructor( protected readonly __mem__: M ) {
		super();
	}

	isReadOnly(): boolean {
		return this.__mem__.isReadOnly_();
	}

	isNonNull(): boolean {
		return this.__mem__.isNonNull_();
	}

	isHistorical(): boolean {
		return this.__mem__.isHistorical_();
	}

	getPages(): Page[] {
		return this.__mem__.getCtrlrWrappers_( ControllerType.PAGE ) as Page[];
	}

	getPage( name: string ): Page | null {
		return this.__mem__.getCtrlrWrapper_( name, ControllerType.PAGE ) as Page;
	}

	getActivePage(): Page | null {
		return this.__mem__.getActivePageWrapper_() as Page;
	}

	getPopups(): Popup[] {
		return this.__mem__.getCtrlrWrappers_( ControllerType.POPUP ) as Popup[];
	}

	getPopup( name: string ): Popup | null {
		return this.__mem__.getCtrlrWrapper_( name, ControllerType.POPUP ) as Popup;
	}

	getComponents(): Component[] {
		return this.__mem__.getCtrlrWrappers_( ControllerType.COMPONENT ) as Component[];
	}

	getComponent( name: string ): Component | null {
		return this.__mem__.getCtrlrWrapper_( name, ControllerType.COMPONENT ) as Component;
	}

	getPageFactories(): PageFactory[] {
		return this.__mem__.getCtrlrWrappers_( ControllerType.PAGE_FACTORY ) as PageFactory[];
	}

	getPageFactory( name: string ): PageFactory | null {
		return this.__mem__.getCtrlrWrapper_( name, ControllerType.PAGE_FACTORY ) as PageFactory;
	}

	getPopupFactories(): PopupFactory[] {
		return this.__mem__.getCtrlrWrappers_( ControllerType.POPUP_FACTORY ) as PopupFactory[];
	}

	getPopupFactory( name: string ): PopupFactory | null {
		return this.__mem__.getCtrlrWrapper_( name, ControllerType.POPUP_FACTORY ) as PopupFactory;
	}

	getComponentFactories(): ComponentFactory[] {
		return this.__mem__.getCtrlrWrappers_( ControllerType.COMPONENT_FACTORY ) as ComponentFactory[];
	}

	getComponentFactory( name: string ): ComponentFactory | null {
		return this.__mem__.getCtrlrWrapper_( name, ControllerType.COMPONENT_FACTORY ) as ComponentFactory;
	}

	getName(): string {
		return this.__mem__.getName_();
	}

	getParent(): Controller | null {
		const parent = this.__mem__.getParent_();
		if( parent != null ) {
			return parent.getWrapper_();
		}
		return null;
	}

	show(): this {
		this.__mem__.show_();
		return this;
	}

	hide(): this {
		this.__mem__.hide_();
		return this;
	}

	isShown(): boolean {
		return this.__mem__.isShown_();
	}

	isHidden(): boolean {
		return ! this.isShown();
	}

	uninitialize(): this {
		this.__mem__.uninitialize_();
		return this;
	}

	isInitialized(): boolean {
		return this.__mem__.isInitialized_();
	}

	initialize(): this {
		this.__mem__.initialize_();
		return this;
	}

	lock(): this {
		this.__mem__.lock_();
		return this;
	}

	isLocked(): boolean {
		return this.__mem__.isLocked_();
	}

	unlock(): this {
		this.__mem__.unlock_();
		return this;
	}

	each( iteratee: Iteratee<string, any, this>, thisArg: unknown= this ): this {
		this.__mem__.each_( iteratee, thisArg );
		return this;
	}

	toJson(): unknown {
		return this.__mem__.toJson_();
	}

	toString(): string {
		return this.__mem__.toString_();
	}
}
