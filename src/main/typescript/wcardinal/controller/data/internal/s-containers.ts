/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

const SCONTAINER_ID_PREFIX = "$$";
const SCONTAINER_ID_CHAR = "$";

export const toSContainerId = ( name: string, type: string ): string => {
	return `${SCONTAINER_ID_PREFIX}${name}@${type}`;
};

export const isSContainerId = ( name: string ): boolean => {
	return name[ 0 ] === SCONTAINER_ID_CHAR && name[ 1 ] === SCONTAINER_ID_CHAR;
};

export const isNotSContainerId = ( name: string ): boolean => {
	return ! isSContainerId( name );
};
