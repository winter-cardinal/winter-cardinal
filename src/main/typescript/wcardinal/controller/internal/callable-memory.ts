/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { isNotEmptyArray } from "../../util/lang/is-empty";
import { isThenable } from "../../util/lang/is-thenable";
import { Thenable } from "../../util/thenable";
import { EVENT_CALL } from "../data/internal/event-name";
import { CallableParentMemory } from "./callable-parent";
import { CallableWrapper } from "./callable-wrapper";
import { Properties } from "./properties";
import { Property } from "./property";

export class CallableMemory<RESULT, ARGUMENTS extends unknown[]> {
	private readonly _wrapper: CallableWrapper<RESULT, ARGUMENTS>;

	private readonly _name: string;
	private readonly _parent: CallableParentMemory<RESULT>;
	private _timeout: number;
	private _properties: Properties;

	constructor(
		parent: CallableParentMemory<RESULT>, name: string, timeout: number, properties: Properties,
		makeWrapper: ( memory: CallableMemory<RESULT, ARGUMENTS> ) => CallableWrapper<RESULT, ARGUMENTS>
	) {
		this._wrapper = makeWrapper( this );

		this._name = name;
		this._parent = parent;
		this._timeout = timeout;
		this._properties = properties;
	}

	getWrapper_(): CallableWrapper<RESULT, ARGUMENTS> {
		return this._wrapper;
	}

	timeout_( timeout: number ): this {
		this._timeout = timeout;
		return this;
	}

	ajax_(): void {
		this._properties.add_( Property.AJAX );
	}

	unajax_(): void {
		this._properties.remove_( Property.AJAX );
	}

	call_( args: ARGUMENTS ): Promise<RESULT> {
		if( this._properties.isLocal_() ) {
			args.unshift( null );
			const wrapper = this._wrapper;
			const timeout = this._timeout;
			return new Thenable<RESULT>(( resolve, reject ) => {
				const results = wrapper.triggerDirect( EVENT_CALL, null, args, [] );
				if( isNotEmptyArray( results ) ) {
					const result = results[ 0 ];
					if( isThenable( result ) ) {
						(result as PromiseLike<RESULT>).then( resolve, reject );
					} else {
						resolve( result as RESULT );
					}
				} else {
					resolve();
				}

				self.setTimeout(() => {
					reject( "timeout" );
				}, timeout);
			});
		} else {
			return this._parent.call_( [this._name, args], this._timeout, this._properties.isAjax_() );
		}
	}
}
