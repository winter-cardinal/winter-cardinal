/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Connectable } from "../../event/connectable";
import { WrapperConstructor } from "../data/internal/wrapper-constructor";
import { Lock } from "../lock";
import { Properties } from "./properties";

type MemoryConstructor<M, P, W> = new (
	parent: P, name: string, properties: Properties,
	lock: Lock, newWrapper: WrapperConstructor<W>
) => M;

export const newMemory = <M, P, S extends Connectable>(
	memoryCtor: MemoryConstructor<M, P, S>, ctor: new (memory: M) => S,
	parent: P & { putData_( name: string, data: M ): void; }, name: string,
	properties: Properties, lock: Lock
): M => {
	const result = new memoryCtor( parent, name, properties, lock, ctor );
	parent.putData_( name, result );
	return result;
};
