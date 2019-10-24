/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Iteratee } from "../../util/lang/each";
import { now } from "../../util/lang/now";
import { Component } from "../component";
import { EventInfo, EventInfoDataMap, EventInfoSelf } from "../data/internal/event-info";
import { EVENT_CHANGE_VALUE, EVENT_CREATE,
	EVENT_DESTROY, EVENT_VALUE } from "../data/internal/event-name";
import { SClassMemory } from "../data/internal/s-class-memory";
import { SListMemory } from "../data/internal/s-list-memory";
import { WrapperConstructor } from "../data/internal/wrapper-constructor";
import { SClass } from "../data/s-class";
import { AddedListItems, RemovedListItems, SList, UpdatedListItems } from "../data/s-list";
import { Lock } from "../lock";
import { checkSupported } from "./check-supported";
import { ComponentMemory } from "./component-memory";
import { ControllerMemory } from "./controller-memory";
import { ControllerType } from "./controller-type";
import { FactoryData, ReceivedFactoryData } from "./info/factory-data";
import { newMemory } from "./new-memory";
import { Property } from "./property";
import { StaticInfo, StaticInstanceInfo } from "./settings";

const STATIC_INFO_NAME = "$si";
const LIST_NAME = "$d";

const pushEvent_ = ( eventInfo: EventInfo, name: string, type: string[] | null, args: unknown[] | null ): void => {
	const event: EventInfoSelf = [ name, type, args ];
	if( eventInfo.self != null ) {
		eventInfo.self.push( event );
	} else {
		eventInfo.self = [ event ];
	}
};

export type NewCtrlr<W extends Component, WC extends Component> = (
	name: string, parent: FactoryMemory<W, WC>, staticInfo: StaticInfo,
	staticInstanceInfo: StaticInstanceInfo | null, lock: Lock
) => ControllerMemory<WC>;

export abstract class FactoryMemory<W extends Component, WC extends Component> extends ComponentMemory<W> {
	private readonly _staticInfo: SClassMemory<StaticInfo>;
	private readonly _list: SListMemory<FactoryData>;
	private readonly _newCtrlr: NewCtrlr<W, WC>;

	constructor(
		name: string, parent: ControllerMemory, staticInfo: StaticInfo,
		staticInstanceInfo: StaticInstanceInfo | null, lock: Lock, wrapperConstructor: WrapperConstructor<W>,
		newCtrlr: NewCtrlr<W, WC>, type: ControllerType
	) {
		super( name, parent, staticInfo, staticInstanceInfo, lock, wrapperConstructor, type );

		this._newCtrlr = newCtrlr;

		const cp = this._properties.clone_().retain_( Property.LOCAL );
		const cpr = cp.clone_().add_( Property.READ_ONLY );
		this._staticInfo = newMemory<SClassMemory<StaticInfo>, this, SClass<StaticInfo>>(
			SClassMemory, SClass, this, STATIC_INFO_NAME, cpr, lock
		);
		this._list = newMemory<SListMemory<FactoryData>, this, SList<FactoryData>>(
			SListMemory, SList, this, LIST_NAME, cp, lock
		);

		this.addDataEventHandler_( ( nameToEvents, typeToEvents ) => {
			this.dataEventHandler_( nameToEvents, typeToEvents );
		});
	}

	create_( parameters: unknown[] ): WC {
		checkSupported( this );
		this.lock_();
		try {
			const data: FactoryData = {
				name: this.nextName_(),
				args: parameters
			};

			// We must suppress events of the list `$d` to prevent the unnecessary creation of controller instances.
			// However, suppressing the events of the list also stops `change`/`value` events on parent controllers
			// of this factory. This is why an dummy event is pushed as shown below.
			this.stopEvent_();
			this._list.add_( this._list.size_(), data );
			this.startEvent_();
			this._list.pushEvent_( "dummy", [] );

			// Create and register a controller instance.
			const ctrlr = this._newCtrlr( data.name, this, this._staticInfo.get_() as StaticInfo, null, this._lock );
			this._ctrlrs[ data.name ] = ctrlr;
			this.uninitialize_();
			const wrapper = ctrlr.getWrapper_();
			this.pushEvent_( EVENT_CREATE, null, [ null, wrapper, data.args ] );
			return wrapper;
		} finally {
			this.unlock_();
		}
	}

	eachWrapper_( iteratee: Iteratee<number, WC, any>, thisArg: unknown ): void {
		const ctrlrs = this._ctrlrs;
		const wrapper = this.getWrapper_();
		this._list.each_(( data, index ) => {
			if( data != null ) {
				const ctrlr = ctrlrs[ data.name ];
				return iteratee.call( thisArg, ctrlr.getWrapper_() as WC, index, wrapper );
			}
		}, null, false );
	}

	toWrapperArray_(): WC[] {
		const result: WC[] = [];
		const ctrlrs = this._ctrlrs;
		this._list.each_(( data ) => {
			if( data != null ) {
				result.push( ctrlrs[ data.name ].getWrapper_() as WC );
			}
		}, null, false );
		return result;
	}

	size_(): number {
		return this._list.size_();
	}

	getCtrlrAt_( index: number ): ControllerMemory | null {
		const data = this._list.get_( index ) as FactoryData;
		if( data == null ) {
			return null;
		}
		return this._ctrlrs[ data.name ];
	}

	getWrapperAt_( index: number ): WC | null {
		const ctrlr = this.getCtrlrAt_( index );
		return ( ctrlr != null ? ctrlr.getWrapper_() as WC : null );
	}

	isEmpty_(): boolean {
		return this._list.isEmpty_();
	}

	indexOf_( wrapper: WC ): number {
		let result = -1;
		const ctrlrs = this._ctrlrs;
		this._list.each_(( data, index ) => {
			if( data != null && ctrlrs[ data.name ].getWrapper_() === wrapper ) {
				result = index;
				return false;
			}
			return true;
		}, null, false );
		return result;
	}

	containsOf_( wrapper: WC ): boolean {
		const ctrlrs = this._ctrlrs;
		for( const name in ctrlrs ) {
			if( ctrlrs[ name ].getWrapper_() === wrapper ) {
				return true;
			}
		}
		return false;
	}

	removeCtrlrAt_( index: number ): ControllerMemory | null {
		checkSupported( this );
		const ctrlr = this.getCtrlrAt_( index );
		if( ctrlr != null ) {
			if( this.destroyCtrlr_( ctrlr ) ) {
				return ctrlr;
			}
		}
		return null;
	}

	removeWrapperAt_( index: number ): WC | null {
		const ctrlr = this.removeCtrlrAt_( index );
		if( ctrlr != null ) {
			return ctrlr.getWrapper_() as WC;
		}
		return null;
	}

	filter_( iteratee: Iteratee<number, WC, any>, thisArg: unknown ): void {
		checkSupported( this );
		this.lock_();
		try {
			const ctrlrs = this._ctrlrs;
			const wrapper = this.getWrapper_();
			const nameAndCtrlrs: Array<[ string, ControllerMemory ]> = [];
			this._list.filter_(( data, index ) => {
				if( data != null ) {
					const ctrlr = ctrlrs[ data.name ];
					if( iteratee.call( thisArg, ctrlr.getWrapper_() as WC, index, wrapper ) === true ) {
						return true;
					} else {
						nameAndCtrlrs.push([ data.name, ctrlr ]);
						return false;
					}
				} else {
					return true;
				}
			}, null );

			for( let i = 0, imax = nameAndCtrlrs.length; i < imax; ++i ) {
				const nameAndCtrlr = nameAndCtrlrs[ i ];
				const name = nameAndCtrlr[ 0 ];
				const ctrlr = nameAndCtrlr[ 1 ];

				delete ctrlrs[ name ];
				ctrlr.hide_();
				this.pushEvent_( EVENT_DESTROY, null, [ null, ctrlr.getWrapper_() ] );
				ctrlr.destroy_();
			}
		} finally {
			this.unlock_();
		}
	}

	destroyWrapper_( wrapper: WC ): boolean {
		const ctrlr = this.findCtrlrOf_( wrapper );
		if( ctrlr != null ) {
			return this.destroyCtrlr_( ctrlr );
		}
		return false;
	}

	destroyCtrlr_( ctrlr: ControllerMemory ): boolean {
		checkSupported( this );
		this.lock_();
		try {
			if( ctrlr != null ) {
				const name = ctrlr.getName_();
				delete this._ctrlrs[ name ];
				this._list.filter_(( data ) => {
					return data == null || name !== data.name;
				}, null );

				ctrlr.hide_();
				this.pushEvent_( EVENT_DESTROY, null, [ null, ctrlr.getWrapper_() ] );
				ctrlr.destroy_();
				return true;
			}
		} finally {
			this.unlock_();
		}
		return false;
	}

	findCtrlrOf_( wrapper: WC ): ControllerMemory | null {
		const ctrlrs = this._ctrlrs;
		for( const name in ctrlrs ) {
			const ctrlr = ctrlrs[ name ];
			if( ctrlr.getWrapper_() === wrapper ) {
				return ctrlr;
			}
		}
		return null;
	}

	clearCtrlrs_(): void {
		checkSupported( this );
		this.lock_();
		try {
			const ctrlrs = this._ctrlrs;
			this._ctrlrs = {};
			this._list.clear_();
			for( const name in ctrlrs ) {
				const ctrlr = ctrlrs[ name ];
				ctrlr.hide_();
				this.pushEvent_( EVENT_DESTROY, null, [ null, ctrlr.getWrapper_() ] );
				ctrlr.destroy_();
			}
		} finally {
			this.unlock_();
		}
	}

	toJson_(): unknown {
		const result: unknown[] = [];

		const ctrlrs = this._ctrlrs;
		this._list.each_(( data ) => {
			if( data != null ) {
				result.push( ctrlrs[ data.name ].toJson_() );
			}
		}, null, false );

		return result;
	}

	toString_(): string {
		const parts: string[] = [];

		const ctrlrs = this._ctrlrs;
		this._list.each_(( data ) => {
			if( data != null ) {
				parts.push( ctrlrs[ data.name ].toString_() );
			}
		}, null, false );

		return `[${parts.join(",")}]`;
	}

	dataEventHandler_( nameToEvents: EventInfoDataMap, typeToEvents: EventInfo ): void {
		if( nameToEvents != null && LIST_NAME in nameToEvents ) {
			const nameToArgs = nameToEvents[ LIST_NAME ][ 1 ];
			const args = (nameToArgs[ EVENT_VALUE ] || nameToArgs[ EVENT_CHANGE_VALUE ]) as
				[ unknown, AddedListItems<FactoryData>, RemovedListItems<FactoryData>, UpdatedListItems<FactoryData> ];
			if( args != null ) {
				this.dataEventHandlerList_( typeToEvents, args[ 1 ], args[ 2 ], args[ 3 ] );
			}
			nameToArgs[ LIST_NAME ] = null;
		}
	}

	dataEventHandlerList_(
		typeToEvents: EventInfo, added: AddedListItems<FactoryData>,
		removed: RemovedListItems<FactoryData>, updated: UpdatedListItems<FactoryData>
	) {
		added = added || [];
		removed = removed || [];
		updated = updated || [];

		// Removed
		for( let i = 0, imax = removed.length; i < imax; ++i ) {
			const data = removed[ i ].value;
			this.destroyByData_( typeToEvents, data as FactoryData );
		}

		// Added
		for( let i = 0, imax = added.length; i < imax; ++i ) {
			const data = added[ i ].value;
			this.createByData_( typeToEvents, data as ReceivedFactoryData );
		}

		// Updated
		for( let i = 0, imax = updated.length; i < imax; ++i ) {
			const update = updated[ i ];
			this.destroyByData_( typeToEvents, update.oldValue as ReceivedFactoryData );
			this.createByData_( typeToEvents, update.newValue as ReceivedFactoryData );
		}
	}

	createByData_( typeToEvents: EventInfo, data: ReceivedFactoryData ): ControllerMemory | null {
		const staticInfo = this._staticInfo.get_();
		if( staticInfo == null ) {
			return null;
		}

		const ctrlr = this._newCtrlr( data.name, this, staticInfo, null, this._lock );
		this._ctrlrs[ data.name ] = ctrlr;
		this.uninitialize_();

		if( data.dynamic != null ) {
			ctrlr.setPartialDynamicInfo_( data.senderId, data.dynamic );

			// Without this, browsers send back dynamic data to servers.
			data.dynamic = null;
		}

		pushEvent_( typeToEvents, EVENT_CREATE, null, [ null, ctrlr.getWrapper_(), data.args ] );
		return ctrlr;
	}

	destroyByData_( typeToEvents: EventInfo, data: FactoryData ): ControllerMemory | null {
		this.lock_();
		try {
			const ctrlr = this._ctrlrs[ data.name ];
			if( ctrlr != null ) {
				delete this._ctrlrs[ data.name ];

				ctrlr.hide_();
				pushEvent_( typeToEvents, EVENT_DESTROY, null, [ null, ctrlr.getWrapper_() ] );
				ctrlr.destroy_();
				return ctrlr;
			}
			return null;
		} finally {
			this.unlock_();
		}
	}

	getFactoryData_( name: string ) {
		let result: string | null = null;
		this._list.each_(( data ) => {
			if( data != null && name === data.name ) {
				result = name;
				return false;
			}
			return true;
		}, null, false );
		return result;
	}

	nextName_() {
		let result = null;
		do {
			result = `b${now().toString(32)}${Math.round(Math.random() * 9007199254740991).toString(32)}`;
		} while( this.getFactoryData_( result ) != null );
		return result;
	}
}
