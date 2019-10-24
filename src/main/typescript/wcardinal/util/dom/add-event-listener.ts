/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

export const addEventListener = (
	element: Window|Element, type: string, handler: EventListenerOrEventListenerObject
): void => {
	element.addEventListener( type, handler, false );
};
