/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package manual.generics;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.Component;
import org.wcardinal.controller.annotation.OnChange;
import org.wcardinal.controller.data.SClass;

@Component
public class GenericsComponent<T, V> {
	@Autowired
	SClass<T> field_class;

	@OnChange( "field_class" )
	void onChange1( final T newValue, final T oldValue ){

	}

	@OnChange( "field_class" )
	void onChange2( final V newValue, final T oldValue ){

	}

	@OnChange( "field_class" )
	void onChange3( final T newValue, final V oldValue ){

	}

	@OnChange( "field_class" )
	void onChange4( final V newValue, final V oldValue ){

	}
}
