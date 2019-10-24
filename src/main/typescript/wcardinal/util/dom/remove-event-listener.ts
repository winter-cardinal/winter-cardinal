/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

export const removeEventListener = (
	element: Window | Element, type: string, handler: EventListenerOrEventListenerObject
): void => {
	element.removeEventListener( type, handler, false );
};
