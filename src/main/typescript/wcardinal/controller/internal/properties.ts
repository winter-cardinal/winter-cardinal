/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Property } from "./property";
import { EnumSet } from "./settings";

export class Properties {
	private _properties: EnumSet<Property>;

	constructor( properties: EnumSet<Property> ) {
		this._properties = properties;
	}

	isReadOnly_(): boolean {
		return this.contains_( Property.READ_ONLY );
	}

	isNonNull_(): boolean {
		return this.contains_( Property.NON_NULL );
	}

	isSoft_(): boolean {
		return this.contains_( Property.SOFT );
	}

	isLocal_(): boolean {
		return this.contains_( Property.LOCAL );
	}

	isHistorical_(): boolean {
		return this.contains_( Property.HISTORICAL );
	}

	isAjax_(): boolean {
		return this.contains_( Property.AJAX );
	}

	add_( properties: EnumSet<Property> ): this {
		this._properties |= properties;
		return this;
	}

	remove_( properties: EnumSet<Property> ): this {
		this._properties &= ~properties;
		return this;
	}

	retain_( properties: EnumSet<Property> ): this {
		this._properties &= properties;
		return this;
	}

	toggle_( isOn: boolean, property: Property ): this {
		return ( isOn ? this.add_( property ) : this.remove_( property ) );
	}

	contains_( property: Property ) {
		return Properties.contains_( this._properties, property );
	}

	clone_() {
		return new Properties( this._properties );
	}

	static nonOf_(): Properties {
		return new Properties( Property.NONE );
	}

	static of_( a: Property ): Properties;
	static of_( a: Property, b: Property ): Properties;
	static of_( a: Property, b: Property, c: Property ): Properties;
	static of_( a: Property ) {
		const args = arguments;
		let properties: EnumSet<Property> = a;
		for( let i = 1, imax = args.length; i < imax; ++i ) {
			properties |= args[ i ];
		}
		return new Properties( properties );
	}

	static contains_( properties: EnumSet<Property>, property: Property ) {
		return (properties & property) !== 0;
	}
}
