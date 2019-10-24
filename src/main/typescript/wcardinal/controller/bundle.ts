/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import * as dataModule from "./data/bundle";
import * as internalModule from "./internal/bundle";

import * as CallableModule from "./callable";
import * as CallableCallModule from "./callable-call";
import * as ControllersModule from "./controllers";
import * as ComponentFactoryImplModule from "./internal/component-factory-impl";
import * as ComponentImplModule from "./internal/component-impl";
import * as ControllerImplModule from "./internal/controller-impl";
import * as FactoryImplModule from "./internal/factory-impl";
import * as PageFactoryImplModule from "./internal/page-factory-impl";
import * as PageImplModule from "./internal/page-impl";
import * as PopupFactoryImplModule from "./internal/popup-factory-impl";
import * as PopupImplModule from "./internal/popup-impl";
import * as RootControllerImplModule from "./internal/root-controller-impl";
import * as VisibilityControllerImplModule from "./internal/visibility-controller-impl";
import * as TaskModule from "./task";
import * as TaskCallModule from "./task-call";

export namespace controller {
	export import data = dataModule.data;
	export import internal = internalModule.internal;

	export import Callable = CallableModule.Callable;
	export import CallableCall = CallableCallModule.CallableCall;
	export import ComponentFactory = ComponentFactoryImplModule.ComponentFactoryImpl;
	export import Component = ComponentImplModule.ComponentImpl;
	export import Controller = ControllerImplModule.ControllerImpl;
	export import Controllers = ControllersModule.Controllers;
	export import Factory = FactoryImplModule.FactoryImpl;
	export import PageFactory = PageFactoryImplModule.PageFactoryImpl;
	export import Page = PageImplModule.PageImpl;
	export import PopupFactory = PopupFactoryImplModule.PopupFactoryImpl;
	export import Popup = PopupImplModule.PopupImpl;
	export import RootController = RootControllerImplModule.RootControllerImpl;
	export import Task = TaskModule.Task;
	export import TaskCall = TaskCallModule.TaskCall;
	export import VisibilityController = VisibilityControllerImplModule.VisibilityControllerImpl;
}
