/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Connectable } from "../../event/connectable";
import { UnsupportedOperationException } from "../../exception/unsupported-operation-exception";
import { Server } from "../../server/server";
import { Iteratee } from "../../util/lang/each";
import { isArray } from "../../util/lang/is-array";
import { isNotEmptyArray } from "../../util/lang/is-empty";
import { isString } from "../../util/lang/is-string";
import { PlainObject } from "../../util/lang/plain-object";
import { Thenable } from "../../util/thenable";
import { Callable } from "../callable";
import { Controller } from "../controller";
import { EventHandler, EventInfo, EventInfoDataMap, EventInfoMap, EventInfoSelf } from "../data/internal/event-info";
import {
	EVENT_CHANGE, EVENT_HIDE, EVENT_INIT,
	EVENT_PAGE, EVENT_SHOW, EVENT_VALUE
} from "../data/internal/event-name";
import { SClassMemory } from "../data/internal/s-class-memory";
import { SContainerMemory } from "../data/internal/s-container-memory";
import { SContainerParentMemory } from "../data/internal/s-container-parent-memory";
import { isSContainerId } from "../data/internal/s-containers";
import { SMapMemory } from "../data/internal/s-map-memory";
import { SScalarMemory } from "../data/internal/s-scalar-memory";
import { SStringMemory } from "../data/internal/s-string-memory";
import { STypeToClass } from "../data/internal/s-type-to-class";
import { WrapperConstructor } from "../data/internal/wrapper-constructor";
import { SClass } from "../data/s-class";
import { AddedMapItems, RemovedMapItems, SMap, UpdatedMapItems } from "../data/s-map";
import { SString } from "../data/s-string";
import { Lock } from "../lock";
import { Task } from "../task";
import { CallCallback } from "./call-callback";
import { CallableMemory } from "./callable-memory";
import { CallableWrapper } from "./callable-wrapper";
import { checkSupported } from "./check-supported";
import { ControllerType } from "./controller-type";
import { ControllerTypeToClass } from "./controller-type-to-class";
import { CallRequest } from "./info/call-request";
import { RejectionInfo } from "./info/rejection-info";
import { RejectionInfoDataMap } from "./info/rejection-info-data-map";
import { RejectionInfoMap } from "./info/rejection-info-map";
import { TriggerRequest } from "./info/trigger-request";
import { mergeHistoryTitle } from "./merge-history-title";
import { newMemory } from "./new-memory";
import { Properties } from "./properties";
import { Property } from "./property";
import { ArrayMap, ArrayMaps, CallableStaticData, DynamicInfo,
	DynamicInfoDataMap, DynamicInfoMap, isCallableStaticData,
	isTaskStaticData, StaticData, StaticInfo, StaticInstanceInfo, TaskStaticData } from "./settings";
import { TaskMemory } from "./task-memory";
import { TaskParentMemory } from "./task-parent-memory";
import { TaskWrapper } from "./task-wrapper";

const rejectOrResolveCallResult = (
	result: unknown, resolve: ( value?: unknown ) => void, reject: ( reason?: unknown ) => void
): void => {
	if( result == null ) {
		reject( "unknown" );
	} else if( isString( result ) ) {
		reject( result );
	} else if( isArray( result ) && isNotEmptyArray( result ) ) {
		resolve( result[ 0 ] );
	} else {
		resolve();
	}
};

const isInSenderIds = ( senderIds: number[], senderId: number ): boolean => {
	for( let i = 0, imax = senderIds.length; i < imax; ++i ) {
		if( senderIds[ i ] === senderId ) {
			return true;
		}
	}
	return false;
};

type HistoryHook = ( data: SScalarMemory | SContainerMemory, part: string ) => void;
type DynamicInfoHandler = ( names: PlainObject<null> ) => string;

export class ControllerMemory<W extends Controller = Controller>
	implements SContainerParentMemory, TaskParentMemory {
	private _wrapper: W;

	private _parent: ControllerMemory | null;
	private _name: string;
	protected _lock: Lock;

	protected _isChanged = false;
	protected _visibility = true;
	protected _isInitialized = false;
	protected _properties: Properties;

	private _type: ControllerType;

	private _data: PlainObject<SScalarMemory | SContainerMemory> = {};
	private _hdata: PlainObject<SScalarMemory | SContainerMemory> = {};
	private _hnames: string[] = [];
	private _hasHData = false;
	protected _ctrlrs: PlainObject<ControllerMemory> = {};

	private _isEventStopped = false;
	protected _events: EventInfo | null = null;

	private _dataHistoryHooks: PlainObject<HistoryHook> = {};

	private _dataEventHandlers: EventHandler[] = [];
	private _dataNameToHandlerName: PlainObject<string> = {};
	private _dynamicInfoHandlers: PlainObject<DynamicInfoHandler> = {};

	private _callId = 0;
	private _callCallbacks: PlainObject<CallCallback> = {};
	private _callRequests: SMapMemory<CallRequest>;
	private _callResults: SMapMemory<unknown>;

	private _triggerRequests: SMapMemory<TriggerRequest>;
	private _triggerResults: SMapMemory<unknown[]>;
	private _triggerDirects: SClassMemory<TriggerRequest[]>;

	private _pageActive: SStringMemory;

	private _constants: PlainObject = {};

	constructor(
		name: string, parent: ControllerMemory | null, staticInfo: StaticInfo,
		staticInstanceInfo: StaticInstanceInfo | null, lock: Lock,
		wrapperConstructor: WrapperConstructor<W>, type: ControllerType
	) {
		this._wrapper = this.initWrapper_( new wrapperConstructor( this ) );

		this._parent = parent;
		this._name = name;
		this._lock = lock;
		this._properties = new Properties( staticInfo[ 4 ] );
		this._type = type;

		// Create controller/data/constants
		this.createControllers_( staticInfo, staticInstanceInfo );

		// Callable
		const cp = this._properties.clone_().retain_( Property.LOCAL );
		this._callRequests = newMemory<SMapMemory<CallRequest>, this, SMap<CallRequest>>(
			SMapMemory, SMap, this, "$cq", cp, lock
		);
		this._callResults = newMemory<SMapMemory<unknown>, this, SMap<unknown>>(
			SMapMemory, SMap, this, "$cr", cp, lock
		);
		this._callResults.getWrapper_().on( EVENT_CHANGE,
			( e: {}, added: AddedMapItems<unknown>, removed: RemovedMapItems<unknown>, updated: UpdatedMapItems<unknown> ) => {
				this.onCallResultsChange_( added, removed, updated );
			}
		);

		// Show/hide
		this._pageActive = newMemory( SStringMemory, SString, this, "$pa", cp, lock );
		this._pageActive.getWrapper_().on( EVENT_CHANGE, ( e: {}, newValue: string, oldValue: string ) => {
			this.onPageActiveChange_( newValue, oldValue );
		});

		// Trigger
		this._triggerRequests = newMemory<SMapMemory<TriggerRequest>, this, SMap<TriggerRequest>>(
			SMapMemory, SMap, this, "$tq", cp, lock
		);
		this._triggerResults = newMemory<SMapMemory<unknown[]>, this, SMap<unknown[]>>(
			SMapMemory, SMap, this, "$tr", cp, lock
		);
		this._triggerRequests.getWrapper_().on(
			EVENT_CHANGE, ( e: {}, added: AddedMapItems<TriggerRequest>, removed: RemovedMapItems<TriggerRequest>
		) => {
			this.onTriggerRequestsChange_( added, removed );
		});

		this._triggerDirects = newMemory<SClassMemory<TriggerRequest[]>, this, SClass<TriggerRequest[]>>(
			SClassMemory, SClass, this, "$td", cp, lock
		);
		this._triggerDirects.unpack_ = ( origin, value, revision ) => {
			this.triggerDirectUnpack_( this._triggerDirects, origin, value as TriggerRequest[], revision );
		};
	}

	private initWrapper_( wrapper: W ): W {
		wrapper.onon( EVENT_SHOW, ( connection ) => {
			if( this._visibility ) {
				connection.triggerDirect( wrapper, null, [ null, this._name ] );
			}
		});
		wrapper.onon( EVENT_HIDE, ( connection ) => {
			if( this._visibility !== true ) {
				connection.triggerDirect( wrapper, null, [ null, this._name ] );
			}
		});
		wrapper.onon( EVENT_PAGE, ( connection ) => {
			if( this._pageActive.isInitialized_() ) {
				const pageName = this._pageActive.get_();
				connection.triggerDirect( wrapper, null, [ null, pageName, pageName ] );
			}
		});
		wrapper.onon( EVENT_INIT, ( connection ) => {
			if( this.isInitialized_() ) {
				connection.triggerDirect( wrapper, null, [ null, this ] );
			}
		});
		wrapper.onon( EVENT_VALUE, ( connection ) => {
			if( this.isInitialized_() ) {
				connection.triggerDirect( wrapper, null, [ null, this ] );
			}
		});

		return wrapper;
	}

	getWrapper_(): W {
		return this._wrapper;
	}

	getType_(): ControllerType {
		return this._type;
	}

	getParent_(): ControllerMemory | null {
		return this._parent;
	}

	getName_(): string {
		return this._name;
	}

	putData_( name: string, data: SScalarMemory | SContainerMemory ): void {
		this._data[ name ] = data;
	}

	putHistoricalData_( name: string, data: SScalarMemory | SContainerMemory ): void {
		this._hnames.push( name );
		this._hdata[ name ] = data;
	}

	putDataHistoryHook_( name: string, hook: HistoryHook ): void {
		this._dataHistoryHooks[ name ] = hook;
	}

	putDynamicInfoHandlerDataName_( dataName: string, handlerName: string ): void {
		this._dataNameToHandlerName[ dataName ] = handlerName;
	}

	putDynamicInfoHandler_( name: string, handler: DynamicInfoHandler ): void {
		this._dynamicInfoHandlers[ name ] = handler;
	}

	addDataEventHandler_( eventHandler: EventHandler ) {
		this._dataEventHandlers.push( eventHandler );
	}

	getEventInfo_(): EventInfo | null {
		if( this.isEventStopped_() ) {
			return null;
		}

		const parent = this._parent;
		if( parent != null ) {
			const parentEventInfo = parent.getEventInfo_();
			if( parentEventInfo == null ) {
				return null;
			}

			let eventInfoMap = parentEventInfo.ctrlr;
			if( eventInfoMap == null ) {
				eventInfoMap = {};
				parentEventInfo.ctrlr = eventInfoMap;
			}
			let eventInfo = eventInfoMap[ this._name ];
			if( eventInfo == null ) {
				eventInfo = {};
				eventInfoMap[ this._name ] = eventInfo;
			}
			return eventInfo;
		} else if( this._events != null ) {
			return this._events;
		} else {
			return this._events = {};
		}
	}

	startEvent_(): this {
		this._isEventStopped = false;
		return this;
	}

	isEventStopped_(): boolean {
		return this._isEventStopped;
	}

	stopEvent_(): this {
		this._isEventStopped = true;
		return this;
	}

	isReadOnly_(): boolean {
		return this._properties.isReadOnly_();
	}

	isNonNull_(): boolean {
		return this._properties.isNonNull_();
	}

	isHistorical_(): boolean {
		return this._properties.isHistorical_();
	}

	isLocal_(): boolean {
		return this._properties.isLocal_();
	}

	lock_(): void {
		this._lock.lock();
	}

	isLocked_(): boolean {
		return this._lock.isLocked();
	}

	unlock_(): void {
		// Propagate the change flag
		if( this._isChanged ) {
			this._isChanged = false;
			this.update_();
		}

		// Unlock
		const count = this._lock.getHoldCount();
		if( count <= 1 ) {
			// The last unlock
			this.onUnlock_();
		} else {
			// Otherwise
			this._lock.unlock();
		}
	}

	update_() {
		const parent = this._parent;
		if( parent != null ) {
			parent.update_();
		}
	}

	onUnlock_() {
		const parent = this._parent;
		if( parent != null ) {
			parent.onUnlock_();
		}
	}

	show_(): void {
		checkSupported( this );
	}

	hide_(): void {
		checkSupported( this );
	}

	showChild_( child: ControllerMemory ): void {
		this._pageActive.set_( child.getName_(), false );
	}

	hideChild_( child: ControllerMemory ): void {
		this._pageActive.compareAndSet_( child.getName_(), null, false );
	}

	each_( iteratee: Iteratee<string, any, any>, thisArg: unknown ): boolean {
		const wrapper = this._wrapper;
		return this.eachConstant_(( constant, name ) => {
			return iteratee.call( thisArg, constant, name, wrapper );
		}) && this.eachDataAndCtrlrWrapper_(( dataOrCtrlrWrapper, name ) => {
			return iteratee.call( thisArg, dataOrCtrlrWrapper, name, wrapper );
		});
	}

	eachDataAndCtrlrWrapper_( iteratee: Iteratee<string, any, any>, thisArg?: unknown ): boolean {
		const wrapper = this._wrapper;
		return this.eachData_(( data, name ) => {
			return iteratee.call( thisArg, data.getWrapper_(), name, wrapper );
		}) && this.eachCtrlr_(( ctrlr, name ) => {
			return iteratee.call( thisArg, ctrlr.getWrapper_(), name, wrapper );
		});
	}

	private eachConstant_( iteratee: Iteratee<string, any, this> ): boolean {
		const constants = this._constants;
		for( const name in constants ) {
			const constant = constants[ name ];
			if( iteratee( constant, name, this ) === false ) {
				return false;
			}
		}
		return true;
	}

	private eachData_( iteratee: Iteratee<string, SScalarMemory | SContainerMemory, this> ): boolean {
		const nameToData = this._data;
		for( const name in nameToData ) {
			if( name[ 0 ] !== "$" ) {
				const data = nameToData[ name ];
				if( data != null ) {
					if( iteratee( data, name, this ) === false ) {
						return false;
					}
				}
			}
		}
		return true;
	}

	protected eachCtrlr_( iteratee: Iteratee<string, ControllerMemory, this> ): boolean {
		const ctrlrs = this._ctrlrs;
		for( const name in ctrlrs ) {
			const ctrlr = ctrlrs[ name ];
			if( ctrlr != null ) {
				if( iteratee( ctrlr, name, this ) === false ) {
					return false;
				}
			}
		}
		return true;
	}

	private onShow_() {
		this._wrapper.triggerDirect( EVENT_SHOW, null, [ null, this._name ], null );
	}

	private onHide_() {
		this._wrapper.triggerDirect( EVENT_HIDE, null, [ null, this._name ], null );
	}

	isShown_(): boolean {
		const parent = this._parent;
		return ( parent != null ? parent.isShown_() : true );
	}

	isHidden_(): boolean {
		return ! this.isShown_();
	}

	protected checkVisibility_(): void {
		const newVisibility = this.isShown_();
		const oldVisibility = this._visibility;
		if( oldVisibility !== newVisibility ) {
			this._visibility = newVisibility;
			if( newVisibility ) {
				this.onShow_();
			} else {
				this.onHide_();
			}
		}

		// Controllers
		const ctrlrs = this._ctrlrs;
		for( const name in ctrlrs ) {
			ctrlrs[ name ].checkVisibility_();
		}
	}

	initialize_(): void {
		if( this._isInitialized !== true ) {
			this.eachData_(( data ) => {
				data.initialize_();
			});
			this.eachCtrlr_(( ctrlr ) => {
				ctrlr.initialize_();
			});
		}
	}

	isInitialized_(): boolean {
		if( this._isInitialized ) {
			return true;
		}
		return this._isInitialized = this.eachData_(( field ) => {
			return field.isInitialized_();
		}) && this.eachCtrlr_(( ctrlr ) => {
			return ctrlr.isInitialized_();
		});
	}

	uninitialize_(): void {
		this._isInitialized = false;
		if( this._parent != null ) {
			this._parent.uninitialize_();
		}
	}

	getCtrlr_( name: string, type: ControllerType ): ControllerMemory | null {
		const ctrlr = this._ctrlrs[ name ];
		return ( ctrlr != null && ctrlr.getType_() === type ? ctrlr : null );
	}

	getCtrlrWrapper_( name: string, type: ControllerType ): Controller | null {
		const ctrlr = this._ctrlrs[ name ];
		if( ctrlr != null && ctrlr.getType_() === type ) {
			return ctrlr.getWrapper_();
		} else {
			return null;
		}
	}

	getCtrlrWrappers_( type: ControllerType ): Controller[] {
		const result: Controller[] = [];
		const ctrlrs = this._ctrlrs;
		for( const name in ctrlrs ) {
			const ctrlr = ctrlrs[ name ];
			if( ctrlr.getType_() === type ) {
				result.push( ctrlr.getWrapper_() );
			}
		}
		return result;
	}

	getActivePage_(): ControllerMemory | null {
		const pageActive = this._pageActive.get_();
		if( pageActive != null ) {
			return this._ctrlrs[ pageActive ];
		} else {
			return null;
		}
	}

	getActivePageWrapper_(): Controller | null {
		const page = this.getActivePage_();
		return ( page != null ? page.getWrapper_() : null );
	}

	protected pushEvent_( name: string, type: string[] | null, args: unknown[] | null ): void {
		const eventInfo = this.getEventInfo_();
		if( eventInfo != null ) {
			const event: EventInfoSelf = [ name, type, args ];
			if( eventInfo.self != null ) {
				eventInfo.self.push( event );
			} else {
				eventInfo.self = [ event ];
			}
		}
	}

	protected handleEvents_( eventInfo: EventInfo ): void {
		if( eventInfo != null ) {
			// Data
			const nameToData = eventInfo.data;
			if( nameToData != null ) {
				const handlers = this._dataEventHandlers;
				for( let i = 0, imax = handlers.length; i < imax; ++i ) {
					handlers[ i ]( nameToData, eventInfo );
				}
			}

			// Ctrlr
			const nameToInfo = eventInfo.ctrlr;
			if( nameToInfo != null ) {
				for( const name in nameToInfo ) {
					const ctrlr = this._ctrlrs[ name ];
					if( ctrlr != null ) {
						ctrlr.handleEvents_( nameToInfo[ name ] );
					}
				}
			}
		}
	}

	private triggerEventsData_( nameToData: EventInfoDataMap | null | undefined ) {
		let result = false;
		if( nameToData != null ) {
			for( const name in nameToData ) {
				const data = nameToData[ name ];
				if( data != null ) {
					data[ 0 ].triggerEvents_( data[ 1 ], data[ 2 ], data[ 3 ] );
					result = true;
				}
			}
		}
		return result;
	}

	private triggerEventsCtrlr_( nameToInfo: EventInfoMap | null | undefined ) {
		let result = false;
		if( nameToInfo != null ) {
			const ctrlrs = this._ctrlrs;
			for( const name in nameToInfo ) {
				const ctrlr = ctrlrs[ name ];
				if( ctrlr != null && ctrlr.triggerEvents_( nameToInfo[ name ] ) ) {
					result = true;
				}
			}
		}
		return result;
	}

	private triggerEventsSelf_( events: EventInfoSelf[] ): void {
		const wrapper = this.getWrapper_();
		for( let i = 0, imax = events.length; i < imax; ++i ) {
			const event = events[ i ];
			wrapper.triggerDirect( event[ 0 ], event[ 1 ], event[ 2 ], null );
		}
	}

	protected triggerEvents_( eventInfo: EventInfo ): boolean {
		if( eventInfo == null ) {
			return false;
		}

		const hasDataChanges = this.triggerEventsData_( eventInfo.data );
		const hasCtrlrChanges = this.triggerEventsCtrlr_( eventInfo.ctrlr );
		const hasChanges = hasDataChanges || hasCtrlrChanges;

		if( eventInfo.self != null ) {
			this.triggerEventsSelf_( eventInfo.self );
		}

		if( this.isHistoryChanged_( eventInfo ) ) {
			this.pushHistory_();
		}

		if( hasChanges && this.isInitialized_() ) {
			const wrapper = this.getWrapper_();
			const args = [ null, wrapper ];
			wrapper.triggerDirect( EVENT_CHANGE, null, args, null );
			wrapper.triggerDirect( EVENT_VALUE, null, args, null );
		}

		return hasChanges;
	}

	protected isHistoryChanged_( eventInfo: EventInfo ): boolean {
		if( eventInfo.data != null ) {
			for( const name in eventInfo.data ) {
				if( name in this._hdata ) {
					return true;
				}
			}
		}
		return false;
	}

	private destroyController_(): void {
		const ctrlrs = this._ctrlrs;
		for( const name in ctrlrs ) {
			const ctrlr = ctrlrs[ name ];
			ctrlr.destroy_();
		}
	}

	destroy_(): void {
		this.lock_();
		try {
			this.destroyController_();
		} finally {
			this.unlock_();
		}
	}

	// HISTORY
	pushHistory_(): void {
		const parent = this._parent;
		if( parent != null ) {
			parent.pushHistory_();
		}
	}

	// HISTORY TITLE
	private getHistoryTitleCtrlr_( separator: string ): string {
		let result = "";
		const ctrlrs = this._ctrlrs;
		for( const name in ctrlrs ) {
			const ctrlr = ctrlrs[ name ];
			const type = ctrlr.getType_();
			if( type !== ControllerType.PAGE && type !== ControllerType.POPUP ) {
				result = mergeHistoryTitle( result, ctrlr.getHistoryTitle_( separator ), separator );
			}
		}
		return result;
	}

	private getHistoryTitlePage_( separator: string ): string {
		let result = "";
		const page = this.getActivePage_();
		if( page != null ) {
			result = mergeHistoryTitle( result, page.getHistoryTitle_( separator ), separator );
		}
		return result;
	}

	private getHistoryTitlePopup_( separator: string ): string {
		let result = "";
		const ctrlrs = this._ctrlrs;
		for( const name in ctrlrs ) {
			const ctrlr = ctrlrs[ name ];
			if( ctrlr.getType_() === ControllerType.POPUP ) {
				result = mergeHistoryTitle( result, ctrlr.getHistoryTitle_( separator ), separator );
			}
		}
		return result;
	}

	protected getHistoryTitle_( separator: string ): string {
		const pageAndPopupTitle = mergeHistoryTitle(
			this.getHistoryTitlePage_( separator ),
			this.getHistoryTitlePopup_( separator ),
			separator
		);
		return mergeHistoryTitle(
			pageAndPopupTitle,
			this.getHistoryTitleCtrlr_( separator ),
			separator
		);
	}

	// HISTORY STATE
	private setHistoricalData__( index: number, parts: string[] ): number {
		const hnames = this._hnames;
		const hdata = this._hdata;
		const imax = Math.min( hnames.length, parts.length - index );
		for( let i = 0; i < imax; ++i ) {
			const name = hnames[ i ];
			const data = hdata[ name ];
			const hook = this._dataHistoryHooks[ name ];
			if( hook != null ) {
				hook( data, parts[ index + i ] );
			} else {
				data.fromString_( decodeURIComponent( parts[ index + i ] ) );
			}
		}
		return index + imax;
	}

	private setHistoryStateData_( index: number, parts: string[] ): number {
		if( parts.length <= index || this._hasHData !== true ) {
			return index;
		}

		try {
			return this.setHistoricalData__( index, parts );
		} catch( e ) {
			return -1;
		}
	}

	private setHistoryStateController_( index: number, parts: string[] ): number {
		if( parts.length <= index ) {
			return index;
		}

		const ctrlrs = this._ctrlrs;
		for( const imax = parts.length; index < imax; ) {
			const ctrlr = ctrlrs[ parts[ index ] ];
			if( ctrlr == null || ctrlr.getType_() === ControllerType.PAGE || ctrlr.getType_() === ControllerType.POPUP ) {
				return index;
			}

			index = ctrlr.setHistoryState_( index + 1, parts );
			if( index < 0 ) {
				return -1;
			}
		}

		return index;
	}

	private setHistoryStatePage_( index: number, parts: string[] ): number {
		if( parts.length <= index ) {
			const activePage = this.getActivePage_();
			if( activePage != null ) {
				activePage.hide_();
			}
		} else {
			const ctrlr = this._ctrlrs[ parts[ index ] ];
			if( ctrlr == null || ctrlr.getType_() !== ControllerType.PAGE ) {
				const activePage = this.getActivePage_();
				if( activePage != null ) {
					activePage.hide_();
				}
			} else {
				index = ctrlr.setHistoryState_( index + 1, parts );
				if( 0 <= index ) {
					ctrlr.show_();
				}
			}
		}

		return index;
	}

	private setHistoryStatePopup_( index: number, parts: string[] ): number {
		const ctrlrs = this._ctrlrs;
		if( parts.length <= index ) {
			for( const name in ctrlrs ) {
				const ctrlr = ctrlrs[ name ];
				if( ctrlr.getType_() === ControllerType.POPUP ) {
					ctrlr.hide_();
				}
			}
		} else {
			const visibles: PlainObject<null> = {};
			for( const imax = parts.length; index < imax; ) {
				const name = parts[ index ];
				const ctrlr = ctrlrs[ name ];
				if( ctrlr == null || ctrlr.getType_() !== ControllerType.POPUP ) {
					return index;
				}

				index = ctrlr.setHistoryState_( index + 1, parts );
				if( index < 0 ) {
					return -1;
				}
				ctrlr.show_();
				visibles[ name ] = null;
			}

			for( const name in ctrlrs ) {
				if( !(name in visibles) ) {
					const ctrlr = ctrlrs[ name ];
					if( ctrlr.getType_() === ControllerType.POPUP ) {
						ctrlr.hide_();
					}
				}
			}
		}

		return index;
	}

	protected setHistoryState_( index: number, parts: string[] ): number {
		index = this.setHistoryStateData_( index, parts );
		if( index < 0 ) {
			return -1;
		}
		index = this.setHistoryStatePage_( index, parts );
		if( index < 0 ) {
			return -1;
		}
		index = this.setHistoryStatePopup_( index, parts );
		if( index < 0 ) {
			return -1;
		}
		return this.setHistoryStateController_( index, parts );
	}

	private getHistoryStateData_(): string {
		let result = "";
		const hnames = this._hnames;
		const hdata = this._hdata;
		for( let i = 0, imax = hnames.length; i < imax; ++i ) {
			result += `/${encodeURIComponent( hdata[ hnames[ i ] ].toString_() )}`;
		}
		return result;
	}

	private getHistoryStateController_(): string {
		let result = "";
		const ctrlrs = this._ctrlrs;
		for( const name in ctrlrs ) {
			const ctrlr = ctrlrs[ name ];
			if( ctrlr.getType_() !== ControllerType.PAGE && ctrlr.getType_() !== ControllerType.POPUP ) {
				const state = ctrlr.getHistoryState_();
				if( isNotEmptyArray( state ) ) {
					result += `/${ctrlr._name}${state}`;
				}
			}
		}

		return result;
	}

	private getHistoryStatePage_(): string {
		const page = this.getActivePage_();
		if( page == null ) {
			return "";
		}
		const state = page.getHistoryState_();
		if( isNotEmptyArray( state ) ) {
			return `/${page._name}${state}`;
		} else {
			return `/${page._name}`;
		}
	}

	private getHistoryStatePopup_(): string {
		let result = "";
		const ctrlrs = this._ctrlrs;
		for( const name in ctrlrs ) {
			const ctrlr = ctrlrs[ name ];
			if( ctrlr.getType_() === ControllerType.POPUP ) {
				if( ctrlr.isShown_() ) {
					const state = ctrlr.getHistoryState_();
					if( isNotEmptyArray( state ) ) {
						result += `/${ctrlr._name}${state}`;
					} else {
						result += `/${ctrlr._name}`;
					}
				}
			}
		}

		return result;
	}

	protected getHistoryState_(): string {
		return this.getHistoryStateData_()
			+ this.getHistoryStatePage_()
			+ this.getHistoryStatePopup_()
			+ this.getHistoryStateController_();
	}

	toJson_(): unknown {
		const result: PlainObject = {};
		this.eachConstant_(( constant: unknown, name: string ): void => {
			result[ name ] = constant;
		});
		this.eachData_(( data, name: string ): void => {
			result[ name ] = data.toJson_();
		});
		this.eachCtrlr_(( ctrlr, name: string ): void => {
			result[ name ] = ctrlr.toJson_();
		});
		return result;
	}

	toString_(): string {
		const parts: string[] = [];
		this.eachConstant_(( constant, name ): void => {
			parts.push( `"${name}":${JSON.stringify( constant )}` );
		});
		this.eachData_(( data, name ): void => {
			parts.push( `"${name}":${data.toString_()}` );
		});
		this.eachCtrlr_(( ctrlr, name ): void => {
			parts.push( `"${name}":${ctrlr.toString_()}` );
		});
		return `{${parts.join(",")}}`;
	}

	private makeCallableWrapper_<RESULT, ARGUMENTS extends unknown[]>(
		memory: CallableMemory<RESULT, ARGUMENTS> ): CallableWrapper<RESULT, ARGUMENTS> {
		const callable = new Callable<RESULT, ARGUMENTS>( memory );

		const result = (( ...args: ARGUMENTS ) => {
			return callable.invoke( args );
		}) as CallableWrapper<RESULT, ARGUMENTS>;

		Connectable.extend(result);

		result.call = ( ...args: ARGUMENTS ) => {
			return callable.invoke( args );
		};
		result.ajax = () => {
			return callable.ajax();
		};
		result.unajax = () => {
			return callable.unajax();
		};
		result.safe = () => {
			return callable.safe();
		};
		result.unsafe = () => {
			return callable.unsafe();
		};
		result.timeout = ( timeout: number ) => {
			return callable.timeout( timeout );
		};

		return result;
	}

	private createCallable_(
		name: string, callableStaticData: CallableStaticData
	): CallableWrapper<unknown, unknown[]> {
		const timeout = callableStaticData[ 1 ];
		const properties = new Properties( callableStaticData[ 2 ] );
		return new CallableMemory<unknown, unknown[]>(
			this, name, timeout, properties, this.makeCallableWrapper_ ).getWrapper_();
	}

	private makeTaskWrapper_<RESULT, ARGUMENTS extends unknown[]>(
		memory: TaskMemory<RESULT, ARGUMENTS>
	): TaskWrapper<RESULT, ARGUMENTS, this> {
		const task = new Task<RESULT, ARGUMENTS>( memory );

		const wrapper: TaskWrapper<RESULT, ARGUMENTS, this> = function( this: any, ...args: ARGUMENTS ) {
			task.create( args );
			return this;
		} as TaskWrapper<RESULT, ARGUMENTS, this>;

		Connectable.extend( wrapper );

		wrapper.create = function( args: ARGUMENTS ) {
			task.create( args );
			return this;
		};
		wrapper.getArguments = () => {
			return task.getArguments();
		};
		wrapper.getArgument = ( index ) => {
			return task.getArgument( index );
		};
		wrapper.getResult = () => {
			return task.getResult();
		};
		wrapper.getReason = () => {
			return task.getReason();
		};
		wrapper.isCanceled = () => {
			return task.isCanceled();
		};
		wrapper.isSucceeded = () => {
			return task.isSucceeded();
		};
		wrapper.isFailed = () => {
			return task.isFailed();
		};
		wrapper.isDone = () => {
			return task.isDone();
		};
		wrapper.isReadOnly = () => {
			return task.isReadOnly();
		};
		wrapper.isNonNull = () => {
			return task.isNonNull();
		};
		wrapper.cancel = function() {
			task.cancel(); return this;
		};
		wrapper.lock = function() {
			task.lock(); return this;
		};
		wrapper.isLocked = () => {
			return task.isLocked();
		};
		wrapper.unlock = function() {
			task.unlock();
			return this;
		};
		wrapper.toJson = () => {
			return task.toJson();
		};
		wrapper.toString = () => {
			return task.toString();
		};

		return wrapper;
	}

	private createTask_( name: string, taskStaticData: TaskStaticData ): Connectable {
		return new TaskMemory<unknown, unknown[]>(
			this, name, taskStaticData, this._lock, this.makeTaskWrapper_ ).getWrapper_();
	}

	private createConstants_( constants: ArrayMap<unknown> | null | undefined ): void {
		if( constants != null ) {
			const names = constants[ 0 ];
			const values = constants[ 1 ];
			const wrapper = this.getWrapper_() as unknown as PlainObject;
			for( let i = 0, imax = names.length; i < imax; ++i ) {
				const name = names[ i ];
				wrapper[ name ] = this._constants[ name ] = values[ i ];
			}
		}
	}

	private createCallablesAndVariables_( nameToData: ArrayMap<StaticData> ): void {
		if( nameToData != null ) {
			const names = nameToData[ 0 ];
			const staticDatas = nameToData[ 1 ];
			const wrapper = this.getWrapper_() as unknown as PlainObject;
			for( let i = 0, imax = names.length; i < imax; ++i ) {
				const name = names[ i ];
				const staticData = staticDatas[ i ];
				if( isCallableStaticData( staticData ) ) {
					wrapper[ name ] = this.createCallable_( name, staticData );
				} else if( isTaskStaticData( staticData ) ) {
					wrapper[ name ] = this.createTask_( name, staticData );
				} else {
					const factory = STypeToClass.getFactory_( staticData[ 1 ] );
					if( factory != null ) {
						const properties = new Properties( staticData[ 2 ] );
						const variable = factory.create_( this, name, properties, this._lock );
						if( variable != null ) {
							this.putData_( name, variable );
							wrapper[ name ] = variable.getWrapper_();
							if( properties.isHistorical_() ) {
								this.putHistoricalData_( name, variable );
							}
						}
					}
				}
			}
			this._hnames = this._hnames.sort();
			this._hasHData = isNotEmptyArray( this._hnames );
		}
	}

	private createController_(
		nameToInfo: ArrayMap<StaticInfo> | null | undefined,
		instanceNameToInfo: ArrayMap<StaticInstanceInfo> | null | undefined
	): void {
		if( nameToInfo != null ) {
			const lock = this._lock;
			const wrapper = this.getWrapper_() as unknown as PlainObject;
			const ctrlrs = this._ctrlrs;

			const names = nameToInfo[ 0 ];
			const staticInfos = nameToInfo[ 1 ];
			const staticInstanceInfos = (instanceNameToInfo != null ? instanceNameToInfo[ 1 ] : null);
			for( let i = 0, imax = names.length; i < imax; ++i ) {
				const name = names[ i ];
				const staticInfo = staticInfos[ i ];
				const factory = ControllerTypeToClass.getFactory_( staticInfo[ 3 ] );
				if( factory != null ) {
					const staticInstanceInfo = (staticInstanceInfos != null ? staticInstanceInfos[ i ] : null);
					const controller = factory.create_( this, name, staticInfo, staticInstanceInfo, lock );
					ctrlrs[ name ] = controller;
					wrapper[ name ] = controller.getWrapper_();
				}
			}
		}
	}

	private createControllers_( staticInfo: StaticInfo, staticInstanceInfo: StaticInstanceInfo | null ): void {
		if( staticInfo != null ) {
			this.createConstants_( staticInfo[ 2 ] );
			if( staticInstanceInfo != null ) {
				this.createConstants_( staticInstanceInfo[ 2 ] );
			}
			this.createCallablesAndVariables_( staticInfo[ 0 ] );
			this.createController_( staticInfo[ 1 ], (staticInstanceInfo != null ? staticInstanceInfo[ 1 ] : null) );
		}
	}

	private getCallPath_() {
		const result = [];
		let memory: ControllerMemory = this;
		for(;;) {
			const parent = memory._parent;
			if( parent == null ) {
				break;
			}
			result.unshift( memory._name );
			memory = parent;
		}
		return result;
	}

	getServer_(): Server {
		const parent = this._parent;
		if( parent != null ) {
			return parent.getServer_();
		}
		throw UnsupportedOperationException.create();
	}

	call_( data: CallRequest, timeout: number, headers: PlainObject<string> | null, isAjaxMode: boolean ): Promise<unknown> {
		return new Thenable<unknown>(( resolve, reject ) => {
			if( isAjaxMode ) {
				const ajaxData = JSON.stringify({
					path: this.getCallPath_(),
					request: data
				});
				this.getServer_().ajax( "call", ajaxData, timeout, headers, ( result ) => {
					rejectOrResolveCallResult( result, resolve, reject );
				}, ( xhr, reason ) => {
					reject( reason );
				});
			} else {
				this._callId += 1;
				const cid = String(this._callId);
				this._callCallbacks[ cid ] = { resolve, reject };
				this._callRequests.put_( cid, data );
				self.setTimeout(() => {
					delete this._callCallbacks[ cid ];
					if( this._callRequests.remove_( cid ) != null ) {
						reject( "timeout" );
					}
				}, timeout);
			}
		});
	}

	private onCallResultChange_( key: string, result: unknown ): void {
		const data = this._callRequests.remove_( key );
		const callback = this._callCallbacks[ key ];
		delete this._callCallbacks[ key ];
		if( data != null && callback != null ) {
			rejectOrResolveCallResult( result, callback.resolve, callback.reject );
		}
	}

	private onCallResultsChange_(
		added: AddedMapItems<unknown>, removed: RemovedMapItems<unknown>, updated: UpdatedMapItems<unknown>
	): void {
		for( const key in added ) {
			this.onCallResultChange_( key, added[ key ] );
		}

		for( const key in updated ) {
			this.onCallResultChange_( key, updated[ key ].newValue );
		}
	}

	private getDynamicInfoData_( senderId: number ): DynamicInfoDataMap | null {
		let result: DynamicInfoDataMap | null = null;
		for( const name in this._data ) {
			const data = this._data[ name ];
			if( data instanceof SScalarMemory ) {
				if(
					! data.isReadOnly_() && ! data.isLocal_() && data.isInitialized_() &&
					data.getAuthorizedRevision_() < data.getRevision_()
				) {
					result = data.pack_( result, senderId );
				}
			}
		}

		return result;
	}

	private getDynamicInfoController_( senderId: number ): DynamicInfoMap | null {
		let result: DynamicInfoMap | null = null;
		const ctrlrs = this._ctrlrs;
		for( const name in ctrlrs ) {
			const ctrlr = ctrlrs[ name ];
			if( ! ctrlr.isLocal_() ) {
				const dynamicInfo = ctrlr.getDynamicInfo_( senderId );
				if( dynamicInfo != null ) {
					if( result == null ) {
						result = [ [ name ], [ dynamicInfo ] ];
					} else {
						result[ 0 ].push( name );
						result[ 1 ].push( dynamicInfo );
					}
				}
			}
		}
		return result;
	}

	protected getDynamicInfo_( senderId: number ): DynamicInfo | null {
		const nameToData = this.getDynamicInfoData_( senderId );
		const nameToInfo = this.getDynamicInfoController_( senderId );
		if( nameToData != null || nameToInfo != null ) {
			return [ nameToData, nameToInfo ];
		} else {
			return null;
		}
	}

	private resetAuthorizedRevisionData_(): void {
		for( const name in this._data ) {
			const data = this._data[ name ];
			data.resetAuthorizedRevision_();
		}
	}

	private resetAuthorizedRevisionController_(): void {
		const ctrlrs = this._ctrlrs;
		for( const name in ctrlrs ) {
			const ctrlr = ctrlrs[ name ];
			ctrlr.resetAuthorizedRevision_();
		}
	}

	protected resetAuthorizedRevision_(): void {
		this.resetAuthorizedRevisionData_();
		this.resetAuthorizedRevisionController_();
	}

	private setAuthorizedRevisionData_( senderIds: number[] ): void {
		for( const name in this._data ) {
			const data = this._data[ name ];
			if( data instanceof SScalarMemory ) {
				if( 0 <= data.getSentSenderId_() && isInSenderIds( senderIds, data.getSentSenderId_() ) ) {
					data.resetSentSenderId_();
				}
			}
		}
	}

	private setAuthorizedRevisionController_( senderIds: number[] ): void {
		const ctrlrs = this._ctrlrs;
		for( const name in ctrlrs ) {
			ctrlrs[ name ].setAuthorizedRevision_( senderIds );
		}
	}

	protected setAuthorizedRevision_( senderIds: number[] ): void {
		if( senderIds != null ) {
			this.setAuthorizedRevisionData_( senderIds );
			this.setAuthorizedRevisionController_( senderIds );
		}
	}

	private setDynamicInfoData_( nameToData: DynamicInfoDataMap | null | undefined ): void {
		if( nameToData != null ) {
			const names = nameToData[ 0 ];
			const dynamicDatas = nameToData[ 1 ];
			for( let i = 0, imax = names.length; i < imax; ++i ) {
				const name = names[ i ];
				const dynamicData = dynamicDatas[ i ];
				if( dynamicData != null ) {
					const data = this._data[ name ];
					if( data instanceof SScalarMemory ) {
						const revision = dynamicData[ 0 ];
						const type = dynamicData[ 1 ];

						if( data.getType_() === type && data.getRevision_() <= revision ) {
							data.unpack_( this._wrapper, dynamicData[ 2 ], revision );
						}
					}
				}
			}
		}
	}

	private setDynamicInfoController_( nameToInfo: DynamicInfoMap | null | undefined ): void {
		if( nameToInfo != null ) {
			const names = nameToInfo[ 0 ];
			const dynamicInfos = nameToInfo[ 1 ];
			const ctrlrs = this._ctrlrs;
			for( let i = 0, imax = names.length; i < imax; ++i ) {
				const name = names[ i ];
				const ctrlr = ctrlrs[ name ];
				if( ctrlr != null ) {
					ctrlr.setDynamicInfo_( dynamicInfos[ i ] );
				}
			}
		}
	}

	protected setDynamicInfo_( dynamicInfo: DynamicInfo ): void {
		if( dynamicInfo != null ) {
			this.lock_();
			try {
				this.setDynamicInfoData_( dynamicInfo[ 0 ] );
				this.setDynamicInfoController_( dynamicInfo[ 1 ] );
			} finally {
				this.unlock_();
			}
		}
	}

	private setSContainerDynamicInfoData_(
		nameToData: DynamicInfoDataMap | null | undefined, hasNonSContainer: boolean[]
	): RejectionInfoDataMap | null {
		let result: RejectionInfoDataMap | null = null;

		let handlerNameToData: PlainObject<PlainObject<null>> | null = null;
		if( nameToData != null ) {
			const names = nameToData[ 0 ];
			const dynamicDatas = nameToData[ 1 ];
			for( let i = 0, imax = names.length; i < imax; ++i ) {
				const name = names[ i ];
				if( isSContainerId( name ) ) {
					const dynamicData = dynamicDatas[ i ];
					if( dynamicData != null ) {
						const data = this._data[ name ];
						if( data instanceof SScalarMemory ) {
							const revision = dynamicData[ 0 ];
							const type = dynamicData[ 1 ];

							if( data.getType_() === type && data.getRevision_() <= revision ) {
								dynamicDatas[ i ] = null;
								data.unpack_( this._wrapper, dynamicData[ 2 ], revision );
							}

							// Handler
							const handlerName = this._dataNameToHandlerName[ name ];
							if( handlerName != null ) {
								if( handlerNameToData == null ) {
									handlerNameToData = {};
								}
								let handlerData = handlerNameToData[ handlerName ];
								if( handlerData == null ) {
									handlerData = {};
									handlerNameToData[ handlerName ] = handlerData;
								}
								handlerData[ name ] = null;
							}
						}
					}
				} else {
					hasNonSContainer[ 0 ] = true;
				}
			}

			// CALL HANDLERS
			if( handlerNameToData != null ) {
				for( const handlerName in handlerNameToData ) {
					const rejectedDataName = this._dynamicInfoHandlers[ handlerName ]( handlerNameToData[ handlerName ] );
					if( rejectedDataName != null ) {
						result = ArrayMaps.put_( result, rejectedDataName, true );
					}
				}
			}
		}
		return result;
	}

	private setSContainerDynamicInfoController_(
		nameToInfo: DynamicInfoMap | null, hasNonSContainer: boolean[]
	): RejectionInfoMap | null {
		let result: RejectionInfoMap | null = null;
		if( nameToInfo != null ) {
			const names = nameToInfo[ 0 ];
			const dynamicInfos = nameToInfo[ 1 ];
			const ctrlrs = this._ctrlrs;
			for( let i = 0, imax = names.length; i < imax; ++i ) {
				const name = names[ i ];
				const ctrlr = ctrlrs[ name ];
				if( ctrlr != null ) {
					const rejectionInfo = ctrlr.setSContainerDynamicInfo_( dynamicInfos[ i ], hasNonSContainer );
					if( rejectionInfo != null ) {
						result = ArrayMaps.put_( result, name, rejectionInfo );
					}
				}
			}
		}
		return result;
	}

	protected setSContainerDynamicInfo_( dynamicInfo: DynamicInfo, hasNonSContainer: boolean[] ): RejectionInfo | null {
		if( dynamicInfo != null ) {
			this.lock_();
			try {
				return ArrayMaps.merge_(
					this.setSContainerDynamicInfoData_( dynamicInfo[ 0 ], hasNonSContainer ),
					this.setSContainerDynamicInfoController_( dynamicInfo[ 1 ], hasNonSContainer )
				);
			} finally {
				this.unlock_();
			}
		}
		return null;
	}

	setPartialDynamicInfo_( senderId: number, dynamicInfo: DynamicInfo ): void {
		const parent = this._parent;
		if( parent != null ) {
			parent.setPartialDynamicInfo_( senderId, [ null, [ [ this._name ], [ dynamicInfo ] ] ] );
		}
	}

	private onTriggerRequestChange_( request: TriggerRequest | null ) {
		const results: unknown[] = [];
		if( request != null ) {
			const types = request[ 0 ];
			for( let i = 0, imax = types.length; i < imax; ++i ) {
				const type = types[ i ];
				this._wrapper.triggerDirect( type[ 0 ], type, request[ 1 ], results );
				request[ 1 ][ 0 ] = null;
			}
		}
		return results;
	}

	private onTriggerRequestsChange_( added: AddedMapItems<TriggerRequest>, removed: RemovedMapItems<TriggerRequest> ) {
		for( const key in removed ) {
			this._triggerResults.remove_( key );
		}

		for( const key in added ) {
			const request = added[ key ];
			this._triggerResults.put_( key, this.onTriggerRequestChange_( request ) );
		}
	}

	private triggerDirectUnpack_(
		sclassMemory: SClassMemory<TriggerRequest[]>, origin: unknown, value: TriggerRequest[], revision: number
	) {
		sclassMemory.forceRevision_( revision );
		sclassMemory.forceInitilized_();

		if( value != null ) {
			for( let i = 0, imax = value.length; i < imax; ++i ) {
				const request = value[ i ];
				const types = request[ 0 ];
				for( let j = 0, jmax = types.length; j < jmax; ++j ) {
					const type = types[ j ];
					this.pushEvent_( type[ 0 ], type, request[ 1 ] );
				}
			}
		}
	}

	private onPageActiveChange_( newValue: string, oldValue: string ) {
		const ctrlrs = this._ctrlrs;
		const oldPage = ( oldValue != null ? ctrlrs[ oldValue ] : null );
		const newPage = ( newValue != null ? ctrlrs[ newValue ] : null );

		this._wrapper.triggerDirect( EVENT_PAGE, null, [ null, newValue, oldValue ], null );

		if( oldPage != null ) {
			oldPage.checkVisibility_();
		}
		if( newPage != null ) {
			newPage.checkVisibility_();
		}

		// History
		this.pushHistory_();
	}

	protected isApplicable_( rejectionInfo: RejectionInfo ) {
		// DATA
		const nameToData = rejectionInfo[ 0 ];
		if( nameToData != null ) {
			const names = nameToData[ 0 ];
			for( let i = 0, imax = names.length; i < imax; ++i ) {
				const data = this._data[ names[ i ] ];
				if( data instanceof SScalarMemory && data.get_() !== 0 ) {
					return false;
				}
			}
		}

		// INFO
		const nameToInfo = rejectionInfo[ 1 ];
		if( nameToInfo != null ) {
			const names = nameToInfo[ 0 ];
			const rejectionInfos = nameToInfo[ 1 ];
			for( let i = 0, imax = names.length; i < imax; ++i ) {
				const ctrlr = this._ctrlrs[ names[ i ] ];
				if( ctrlr.isApplicable_( rejectionInfos[ i ] ) !== true ) {
					return false;
				}
			}
		}

		return true;
	}
}
