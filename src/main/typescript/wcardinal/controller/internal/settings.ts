/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { IoSettings } from "../../io/io-settings";
import { PlainObject } from "../../util/lang/plain-object";
import { SType } from "../data/internal/s-type";
import { ControllerType } from "./controller-type";
import { Property } from "./property";

export type EnumSet<V> = number;
export type ArrayMap<T> = [ string[], T[] ];
export const ArrayMaps = {
	put_<T>( arrayMap: ArrayMap<T> | null, key: string, value: T ): ArrayMap<T> {
		if( arrayMap == null ) {
			arrayMap = [ [ key ], [ value ] ];
		} else {
			arrayMap[ 0 ].push( key );
			arrayMap[ 1 ].push( value );
		}
		return arrayMap;
	},

	merge_<T, U>( data: ArrayMap<T> | null, info: ArrayMap<U> | null ): [ ArrayMap<T> | null, ArrayMap<U> | null ] | null {
		return ( (data != null || info != null) ? [ data, info ] : null );
	}
};
export type DynamicInfoData = [ number, SType, unknown ];
export type DynamicInfoDataMap = ArrayMap<DynamicInfoData | null>;
export type DynamicInfoMap = ArrayMap<DynamicInfo>;
export interface DynamicInfo {
	[ 0 ]: DynamicInfoDataMap | null;
	[ 1 ]: DynamicInfoMap | null;
}

export interface CallableStaticData {
	[ 0 ]: 0;
	[ 1 ]: number;
	[ 2 ]: EnumSet<Property>;
}
export interface TaskStaticData {
	[ 0 ]: 1;
	[ 1 ]: EnumSet<Property>;
}
export interface VariableStaticData {
	[ 0 ]: 2;
	[ 1 ]: SType;
	[ 2 ]: EnumSet<Property>;
}
export type StaticData = CallableStaticData | TaskStaticData | VariableStaticData;
export const isCallableStaticData = ( staticData: StaticData ): staticData is CallableStaticData => {
	return ( staticData[ 0 ] === 0 );
};
export const isTaskStaticData = ( staticData: StaticData ): staticData is TaskStaticData => {
	return ( staticData[ 0 ] === 1 );
};
export const isVariableStaticData = ( staticData: StaticData ): staticData is VariableStaticData => {
	return ( staticData[ 0 ] === 2 );
};

export interface StaticInfo {
	[ 0 ]: ArrayMap<StaticData>;
	[ 1 ]: ArrayMap<StaticInfo>;
	[ 2 ]: ArrayMap<unknown>;
	[ 3 ]: ControllerType;
	[ 4 ]: EnumSet<Property>;
}

export interface StaticInstanceInfo {
	[ 0 ]?: ArrayMap<null>;
	[ 1 ]?: ArrayMap<StaticInstanceInfo>;
	[ 2 ]?: ArrayMap<unknown>;
}

export interface PathSettings {
	content: string;
	query: string;
}

export interface KeepAliveSettings {
	timeout: number;
	interval: number;
	ping: number;
}

export interface RetrySettings {
	timeout: number;
	delay: number;
	interval: number;
}

export interface SyncSettings {
	connect: {
		timeout: number
	};
	update: {
		timeout: number,
		interval: number
	};
	process: {
		interval: number
	};
}

export interface TitleSettings {
	separators: [ string, string ];
}

export interface InfoSettings {
	static: StaticInfo;
	instance: StaticInstanceInfo;
	dynamic?: DynamicInfo;
	senderId: number;
	historical: boolean;
}

export interface Settings {
	ssid: string;
	path: PathSettings;
	keep_alive: KeepAliveSettings;
	retry: RetrySettings;
	protocols: PlainObject<IoSettings>;
	title: TitleSettings;
	sync: SyncSettings;
	info: InfoSettings;
	bfcached?: boolean;
}

export interface LocalInfoSettings {
	static: StaticInfo;
	instance: StaticInstanceInfo;
	dynamic?: DynamicInfo;
}

export interface LocalSettings {
	info: LocalInfoSettings;
}
