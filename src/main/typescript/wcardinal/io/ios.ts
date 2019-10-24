/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { IoConstructor } from "./io-constructor";

/**
 * IO factory class
 */
export class Ios {
	private static INSTANCE: Ios | null = null;
	private _nameToIoConstructor: { [name: string]: IoConstructor };

	constructor() {
		this._nameToIoConstructor = {};
	}

	/**
	 * Add the specified protocol and the specified IO class to this factory.
	 *
	 * @param protocol Protocol name.
	 * @param io IO class of the specified protocol.
	 * @returns this
	 */
	add( protocol: string, ioConstructor: IoConstructor ): Ios {
		this._nameToIoConstructor[ protocol ] = ioConstructor;
		return this;
	}

	/**
	 * Returns the IO class of the specified protocol.
	 *
	 * @param protocol Protocol name.
	 * @returns IO class providing communication methods of the specified protocol.
	 */
	get( protocol: string ): IoConstructor {
		return this._nameToIoConstructor[ protocol ];
	}

	static getInstance(): Ios {
		if( this.INSTANCE == null ) {
			this.INSTANCE = new Ios();
		}
		return this.INSTANCE;
	}
}
