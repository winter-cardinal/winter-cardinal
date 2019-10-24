/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Connectable } from "../../../event/connectable";
import { ViewTarget } from "./view-target";
import { WrapperConstructor } from "./wrapper-constructor";

export class ViewMemory<T extends ViewTarget, W extends Connectable> {
	readonly _target: T;
	readonly _wrapper: W;

	constructor( target: T, wrapperConstructor: WrapperConstructor<W> ) {
		this._target = target;
		this._wrapper = new wrapperConstructor( this );
	}

	getTarget_(): T {
		return this._target;
	}

	getWrapper_(): W {
		return this._wrapper;
	}

	isReadOnly_() {
		return this._target.isReadOnly_();
	}

	isNonNull_() {
		return this._target.isNonNull_();
	}

	isInitialized_() {
		return this._target.isInitialized_();
	}

	lock_() {
		return this._target.lock_();
	}

	isLocked_() {
		return this._target.isLocked_();
	}

	unlock_() {
		return this._target.unlock_();
	}

}
