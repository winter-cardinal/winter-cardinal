/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { IllegalArgumentException } from "../../exception/illegal-argument-exception";
import { NullPointerException } from "../../exception/null-pointer-exception";
import { UnsupportedOperationException } from "../../exception/unsupported-operation-exception";
import { hasOwn } from "../../util/lang/has-own";
import { isArray } from "../../util/lang/is-array";
import { isBoolean } from "../../util/lang/is-boolean";
import { isEmptyArray } from "../../util/lang/is-empty";
import { isFunction } from "../../util/lang/is-function";
import { isNumber } from "../../util/lang/is-number";
import { isPlainObject } from "../../util/lang/is-plain-object";
import { isString } from "../../util/lang/is-string";
import { PlainObject } from "../../util/lang/plain-object";
import { Callable } from "../callable";
import { Controller } from "../controller";
import { toSContainerId } from "../data/internal/s-containers";
import { SType } from "../data/internal/s-type";
import { STypeToClass } from "../data/internal/s-type-to-class";
import { Task } from "../task";
import { ControllerImpl } from "./controller-impl";
import { ControllerInfo } from "./controller-info";
import { ControllerType } from "./controller-type";
import { ControllerTypeToClass } from "./controller-type-to-class";
import { StaticAndDynamicInfo } from "./info/static-and-dynamic-info";
import { LocalRootControllerMemory } from "./local-root-controller-memory";
import { Properties } from "./properties";
import { Property } from "./property";
import {
	ArrayMaps, DynamicInfo, DynamicInfoDataMap, DynamicInfoMap,
	EnumSet, LocalSettings, StaticInfo, StaticInstanceInfo
} from "./settings";
import { CallableType, STypeOrTaskOrCallable, TaskType, TypeInfo } from "./type-info";
import { TypesAndValue } from "./types-and-value";

const toValue = ( name: string, colonIndex: number ): unknown => {
	const valueString = name.substring( colonIndex + 1, name.length );
	try {
		return JSON.parse( valueString );
	} catch( e ) {
		throw IllegalArgumentException.create( `Malformed default value '${valueString}' for field '${name}'` );
	}
};

const toTypes = ( typesString: string ): string[] => {
	return typesString.trim().toLowerCase().split( /\s+/ );
};

const toTypesAndValue = ( name: string ): TypesAndValue => {
	const colonIndex = name.indexOf(":");
	if( 0 <= colonIndex ) {
		return {
			types: toTypes( name.substring( 0, colonIndex ) ),
			value: toValue( name, colonIndex )
		};
	} else {
		return {
			types: toTypes( name )
		};
	}
};

const toType = ( typeName: string ): STypeOrTaskOrCallable | null => {
	switch( typeName ) {
	case "sarraynode":
		return SType.ARRAY_NODE;
	case "sboolean":
		return SType.BOOLEAN;
	case "sdouble":
		return SType.DOUBLE;
	case "sjsonnode":
		return SType.JSON_NODE;
	case "sfloat":
		return SType.FLOAT;
	case "sinteger":
		return SType.INTEGER;
	case "slong":
		return SType.LONG;
	case "sobjectnode":
		return SType.OBJECT_NODE;
	case "sstring":
		return SType.STRING;
	case "slist":
		return SType.LIST;
	case "smap":
		return SType.MAP;
	case "sclass":
		return SType.CLASS;
	case "squeue":
		return SType.QUEUE;
	case "sroqueue":
		return SType.RO_QUEUE;
	case "snavigablemap":
	case "sascendingmap":
		return SType.ASCENDING_MAP;
	case "sdescendingmap":
		return SType.DESCENDING_MAP;
	case "smovablelist":
		return SType.MOVABLE_LIST;
	case "@callable":
		return CallableType;
	case "@task":
		return TaskType;
	}
	return null;
};

const findTypeByName = ( name: string ): TypeInfo | null => {
	const typesAndValue = toTypesAndValue( name );
	const types = typesAndValue.types;
	const type = toType( types[ types.length - 1 ] );
	if( type != null ) {
		const properties
			= (0 <= types.indexOf( "@nonnull" ) ? Property.NON_NULL : Property.NONE )
			| (0 <= types.indexOf( "@uninitialized" ) ? Property.UNINITIALIZED : Property.NONE )
			| Property.LOCAL;
		if( "value" in typesAndValue ) {
			return {
				type,
				value: typesAndValue.value,
				properties
			};
		} else {
			return {
				type,
				properties
			};
		}
	} else {
		return null;
	}
};

const findTypeByConstructor = ( constructor: Function ): TypeInfo | null => {
	let type: STypeOrTaskOrCallable | null = STypeToClass.getType_( constructor );
	if( type != null ) {
		if( type === SType.DESCENDING_MAP ) {
			type = SType.ASCENDING_MAP;
		}
	} else if( Callable === constructor ) {
		type = CallableType;
	} else if( Task === constructor ) {
		type = TaskType;
	}
	if( type != null ) {
		return {
			type,
			properties: Property.LOCAL
		};
	} else {
		return null;
	}
};

const findType = ( nameOrConstructor: unknown ): TypeInfo | null => {
	if( isString( nameOrConstructor ) ) {
		return findTypeByName( nameOrConstructor );
	} else if( isFunction( nameOrConstructor ) ) {
		return findTypeByConstructor( nameOrConstructor );
	} else {
		return null;
	}
};

const findControllerByName = ( name: string ): ControllerInfo | null => {
	let type: ControllerType | null = null;
	const types = name.trim().toLowerCase().split( /\s+/ );
	switch( types[ types.length - 1 ] ) {
	case "page":
		type = ControllerType.PAGE;
		break;
	case "popup":
		type = ControllerType.POPUP;
		break;
	case "component":
		type = ControllerType.COMPONENT;
		break;
	case "pagefactory":
		type = ControllerType.PAGE_FACTORY;
		break;
	case "popupfactory":
		type = ControllerType.POPUP_FACTORY;
		break;
	case "componentfactory":
		type = ControllerType.COMPONENT_FACTORY;
		break;
	}

	if( type != null ) {
		const properties
			= (0 <= types.indexOf( "@nonnull" ) ? Property.NON_NULL : Property.NONE )
			| (0 <= types.indexOf( "@uninitialized" ) ? Property.UNINITIALIZED : Property.NONE )
			| Property.LOCAL;
		return {
			type,
			properties
		};
	} else {
		return null;
	}
};

const findControllerByConstructor = ( constructor: Function ): ControllerInfo | null => {
	const type = ControllerTypeToClass.getType_( constructor );
	if( type != null ) {
		return {
			type,
			properties: Property.LOCAL
		};
	} else {
		return null;
	}
};

const findController = ( nameOrConstructor: unknown ): ControllerInfo | null => {
	if( isString( nameOrConstructor ) ) {
		return findControllerByName( nameOrConstructor );
	} else if( isFunction( nameOrConstructor ) ) {
		return findControllerByConstructor( nameOrConstructor );
	} else {
		return null;
	}
};

const addSContainerData = ( dynamicInfoDataMap: DynamicInfoDataMap, name: string, $aValue: unknown ): void => {
	ArrayMaps.put_( dynamicInfoDataMap, toSContainerId( name, "ar" ), [ 1, SType.LONG, 0 ] );
	ArrayMaps.put_( dynamicInfoDataMap, toSContainerId( name, "af" ), [ 1, SType.INTEGER, 0 ] );
	ArrayMaps.put_( dynamicInfoDataMap, toSContainerId( name, "a"  ), [ 1, SType.CLASS, $aValue ] );
	ArrayMaps.put_( dynamicInfoDataMap, toSContainerId( name, "br" ), [ 1, SType.LONG, 0 ] );
	ArrayMaps.put_( dynamicInfoDataMap, toSContainerId( name, "bf" ), [ 1, SType.INTEGER, 0 ] );
	ArrayMaps.put_( dynamicInfoDataMap, toSContainerId( name, "b"  ), [ 1, SType.CLASS, null ]) ;
};

const getDynamicInfoDataMap = ( dynamicInfo: DynamicInfo ): DynamicInfoDataMap => {
	const dynamicInfoDataMap = dynamicInfo[ 0 ];
	if( dynamicInfoDataMap != null ) {
		return dynamicInfoDataMap;
	}

	const newDynamicInfoDataMap: DynamicInfoDataMap = [[], []];
	dynamicInfo[ 0 ] = newDynamicInfoDataMap;
	return newDynamicInfoDataMap;
};

const getDynamicInfoMap = ( dynamicInfo: DynamicInfo ): DynamicInfoMap => {
	const dynamicInfoMap = dynamicInfo[ 1 ];
	if( dynamicInfoMap != null ) {
		return dynamicInfoMap;
	}

	const newDynamicInfoMap: DynamicInfoMap = [[], []];
	dynamicInfo[ 1 ] = newDynamicInfoMap;
	return newDynamicInfoMap;
};

const toStaticInfoVariable = (
	type: STypeOrTaskOrCallable, name: string, staticInfo: StaticInfo,
	dynamicInfo: DynamicInfo, properties: Property, variableType: TypeInfo
): void => {
	const staticInfoDataMap = staticInfo[ 0 ];
	const dynamicInfoDataMap = getDynamicInfoDataMap( dynamicInfo );

	if( type === CallableType ) {
		ArrayMaps.put_( staticInfoDataMap, name, [ 0, 5000, properties ] );
	} else if( type === TaskType ) {
		ArrayMaps.put_( staticInfoDataMap, name, [ 1, properties ] );
		ArrayMaps.put_( dynamicInfoDataMap, `$id@${name}`, [ 1, SType.LONG, 0 ] );
		ArrayMaps.put_( dynamicInfoDataMap, `$pid@${name}`, [ 1, SType.LONG, 0 ] );
	} else {
		ArrayMaps.put_( staticInfoDataMap, name, [ 2, type, properties ] );
	}

	const isNonNull = Properties.contains_( properties, Property.NON_NULL );
	const isUninitialized = Properties.contains_( properties, Property.UNINITIALIZED );

	if( "value" in variableType ) {
		const value = variableType.value;
		if( isNonNull && value == null ) {
			throw NullPointerException.create( `'${value}' for non-null field '${name}'` );
		}

		switch( type ) {
		case SType.ARRAY_NODE:
			if( value == null || isArray(value) ) {
				ArrayMaps.put_( dynamicInfoDataMap, name, [ 1, type, value ] );
				return;
			}
			break;
		case SType.LIST:
		case SType.MOVABLE_LIST:
			if( isArray( value ) ) {
				addSContainerData( dynamicInfoDataMap, name, [0, 1, 0, value] );
				return;
			}
			break;
		case SType.MAP:
		case SType.ASCENDING_MAP:
		case SType.DESCENDING_MAP:
			if( isPlainObject( value ) ) {
				addSContainerData( dynamicInfoDataMap, name, [0, 1, 0, value] );
				return;
			}
			break;
		case SType.QUEUE:
		case SType.RO_QUEUE:
			if( isArray( value ) ) {
				addSContainerData( dynamicInfoDataMap, name, [0, 1, 0, value, -1] );
				return;
			}
			break;
		case SType.BOOLEAN:
			if( value == null || isBoolean(value) ) {
				ArrayMaps.put_( dynamicInfoDataMap, name, [ 1, type, value ] );
				return;
			}
			break;
		case SType.DOUBLE:
		case SType.FLOAT:
		case SType.INTEGER:
		case SType.LONG:
			if( value == null || isNumber(value) ) {
				ArrayMaps.put_( dynamicInfoDataMap, name, [ 1, type, value ] );
				return;
			}
			break;
		case SType.CLASS:
		case SType.JSON_NODE:
			if( value == null || isNumber(value) || isString(value) || isPlainObject(value) || isArray(value) ) {
				ArrayMaps.put_( dynamicInfoDataMap, name, [ 1, type, value ] );
			}
			return;
		case SType.STRING:
			if( value == null || isString( value ) ) {
				ArrayMaps.put_( dynamicInfoDataMap, name, [ 1, type, value ] );
				return;
			}
			break;
		case SType.OBJECT_NODE:
			if( value == null || isPlainObject( value ) ) {
				ArrayMaps.put_( dynamicInfoDataMap, name, [ 1, type, value ] );
				return;
			}
			break;
		}

		throw IllegalArgumentException.create( `Illegal value '${value}' for field '${name}'` );
	} else if( ! isUninitialized ) {
		switch( type ) {
		case SType.ARRAY_NODE:
			ArrayMaps.put_( dynamicInfoDataMap, name, [ 1, type, isNonNull ? [] : null ] );
			break;
		case SType.BOOLEAN:
			ArrayMaps.put_( dynamicInfoDataMap, name, [ 1, type, isNonNull ? false : null ] );
			break;
		case SType.DOUBLE:
		case SType.FLOAT:
			ArrayMaps.put_( dynamicInfoDataMap, name, [ 1, type, isNonNull ? 0.0 : null ] );
			break;
		case SType.CLASS:
		case SType.JSON_NODE:
			if( ! isNonNull ) {
				ArrayMaps.put_( dynamicInfoDataMap, name, [ 1, type, null ] );
			}
			break;
		case SType.INTEGER:
		case SType.LONG:
			ArrayMaps.put_( dynamicInfoDataMap, name, [ 1, type, isNonNull ? 0 : null ] );
			break;
		case SType.OBJECT_NODE:
			ArrayMaps.put_( dynamicInfoDataMap, name, [ 1, type, isNonNull ? {} : null ] );
			break;
		case SType.STRING:
			ArrayMaps.put_( dynamicInfoDataMap, name, [ 1, type, isNonNull ? "" : null ] );
			break;
		case SType.LIST:
		case SType.MOVABLE_LIST:
			addSContainerData( dynamicInfoDataMap, name, [0, 1, 0, []] );
			break;
		case SType.QUEUE:
		case SType.RO_QUEUE:
			addSContainerData( dynamicInfoDataMap, name, [0, 1, 0, [], -1] );
			break;
		case SType.MAP:
		case SType.ASCENDING_MAP:
		case SType.DESCENDING_MAP:
			addSContainerData( dynamicInfoDataMap, name, [0, 1, 0, {}] );
			break;
		}
	}
};

const getReserved = ( from: PlainObject, name: string ): unknown => {
	const escapedName = `@${name}`;
	if( hasOwn( from, escapedName ) ) {
		return from[ escapedName ];
	}

	if( hasOwn( from, name ) ) {
		return from[ name ];
	}

	return null;
};

const toProperties = ( value: PlainObject ): EnumSet<Property> => {
	return Property.LOCAL
		| (getReserved( value, "nonnull" ) === true ? Property.NON_NULL : Property.NONE )
		| (getReserved( value, "uninitialized" ) === true ? Property.UNINITIALIZED : Property.NONE );
};

const isNotReserved = ( name: string, object: unknown ): boolean => {
	switch( name ) {
	case "type":
		return hasOwn( object, `@${name}` );
	case "nonnull":
		return hasOwn( object, `@${name}` );
	case "uninitialized":
		return hasOwn( object, `@${name}` );
	default:
		return ( name == null || isEmptyArray( name ) || name[ 0 ] !== "@" );
	}
};

const isFactoryType = (
	type: ControllerType
): type is ControllerType.PAGE_FACTORY | ControllerType.POPUP_FACTORY | ControllerType.COMPONENT_FACTORY => {
	return (
		type === ControllerType.PAGE_FACTORY ||
		type === ControllerType.POPUP_FACTORY ||
		type === ControllerType.COMPONENT_FACTORY
	);
};

const toStaticAndDynamicInfo = (
	type: ControllerType, structure: PlainObject, properties: EnumSet<Property>, ignoreReserved: boolean
): StaticAndDynamicInfo => {
	const staticInfo: StaticInfo = [
		[[], []], // data
		[[], []], // Controller
		[[], []], // Constants
		type,
		properties
	];
	const dynamicInfo: DynamicInfo = [
		[[], []],
		[[], []]
	];

	for( const name in structure ) {
		if( hasOwn( structure, name ) && (!ignoreReserved || isNotReserved( name, structure )) ) {
			const substructure = structure[ name ];
			if( isPlainObject( substructure ) ) {
				const substructureType = getReserved( substructure, "type" );
				if( substructureType != null ) {
					const variableType = findType( substructureType );
					if( variableType == null ) {
						const controllerType = findController( substructureType );
						if( controllerType == null ) {
							throw UnsupportedOperationException.create( `Unsupported type '${substructureType}'` );
						} else {
							const staticAndDynamicInfo = toStaticAndDynamicInfo(
								controllerType.type, substructure, controllerType.properties | toProperties( substructure ) | properties, true
							);
							if( isFactoryType( controllerType.type ) ) {
								const staticInfoMap = staticInfo[ 1 ];
								ArrayMaps.put_( staticInfoMap, name, [
									[[], []], // Data
									[[], []], // Controller
									[[], []], // Constants
									controllerType.type,
									staticAndDynamicInfo[ 0 ][ 4 ]
								]);

								const dynamicInfoMap = getDynamicInfoMap( dynamicInfo );
								ArrayMaps.put_( dynamicInfoMap, name, [
									[[ "$si" ], [[ 1, SType.CLASS, staticAndDynamicInfo[ 0 ] ]]],
									[[], []]
								]);
							} else {
								const staticInfoMap = staticInfo[ 1 ];
								ArrayMaps.put_( staticInfoMap, name, staticAndDynamicInfo[ 0 ] );

								const dynamicInfoMap = getDynamicInfoMap( dynamicInfo );
								ArrayMaps.put_( dynamicInfoMap, name, staticAndDynamicInfo[ 1 ] );
							}
						}
					} else {
						if( "value" in substructure ) {
							variableType.value = substructure.value;
						}
						toStaticInfoVariable(
							variableType.type, name, staticInfo, dynamicInfo,
							variableType.properties | toProperties( substructure ) | properties, variableType
						);
					}
				} else {
					const staticAndDynamicInfo = toStaticAndDynamicInfo(
						ControllerType.COMPONENT, substructure, toProperties( substructure ) | properties, true
					);

					const staticInfoMap = staticInfo[ 1 ];
					ArrayMaps.put_( staticInfoMap, name, staticAndDynamicInfo[ 0 ] );

					const dynamicInfoMap = getDynamicInfoMap( dynamicInfo );
					ArrayMaps.put_( dynamicInfoMap, name, staticAndDynamicInfo[ 1 ] );
				}
			} else {
				const variableType = findType( substructure );
				if( variableType == null ) {
					const controllerType = findController( substructure );
					if( controllerType == null ) {
						const staticNameToConstants = staticInfo[ 2 ];
						ArrayMaps.put_( staticNameToConstants, name, substructure );
					} else {
						const staticAndDynamicInfo = toStaticAndDynamicInfo(
							controllerType.type, {}, controllerType.properties | properties, true
						);
						if( isFactoryType( controllerType.type ) ) {
							const staticInfoMap = staticInfo[ 1 ];
							ArrayMaps.put_( staticInfoMap, name, [
								[[], []], // Data
								[[], []], // Controller
								[[], []], // Constants
								controllerType.type,
								staticAndDynamicInfo[ 0 ][ 4 ]
							]);

							const dynamicInfoMap = getDynamicInfoMap( dynamicInfo );
							ArrayMaps.put_( dynamicInfoMap, name, [
								[[ "$si" ], [[ 1, SType.CLASS, staticAndDynamicInfo[ 0 ] ]]],
								[[], []]
							]);
						} else {
							const staticInfoMap = staticInfo[ 1 ];
							ArrayMaps.put_( staticInfoMap, name, staticAndDynamicInfo[ 0 ] );

							const dynamicInfoMap = getDynamicInfoMap( dynamicInfo );
							ArrayMaps.put_( dynamicInfoMap, name, staticAndDynamicInfo[ 1 ] );
						}
					}
				} else {
					toStaticInfoVariable(
						variableType.type, name, staticInfo, dynamicInfo, variableType.properties | properties, variableType
					);
				}
			}
		}
	}

	return [ staticInfo, dynamicInfo ];
};

export class ControllersImpl {
	static create( structure: PlainObject ): Controller {
		const staticAndDynamicInfo = toStaticAndDynamicInfo(
			ControllerType.CONTROLLER, structure, Property.LOCAL, false
		);
		const settings: LocalSettings = {
			info: {
				static: staticAndDynamicInfo[ 0 ],
				instance: [] as StaticInstanceInfo,
				dynamic: staticAndDynamicInfo[ 1 ]
			}
		};
		return new LocalRootControllerMemory<Controller>( "", settings, ControllerImpl )
			.init_( settings ).getWrapper_();
	}
}
