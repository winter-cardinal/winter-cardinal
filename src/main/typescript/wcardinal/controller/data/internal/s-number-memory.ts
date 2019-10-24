/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { checkSupported } from "../../internal/check-supported";
import { SScalarMemory } from "./s-scalar-memory";

export abstract class SNumberMemory extends SScalarMemory<number> {
	addAndGet_( delta: number ): number | null {
		checkSupported( this );

		this.lock_();
		try {
			const value = this.get_();
			if( value != null ) {
				this.getAndSet_( value + delta, false, true );
			}
			return this.get_();
		} finally {
			this.unlock_();
		}
	}

	getAndAdd_( delta: number ): number | null {
		checkSupported( this );

		this.lock_();
		try {
			const value = this.get_();
			if( value != null ) {
				return this.getAndSet_( value + delta, false, true );
			}
			return value;
		} finally {
			this.unlock_();
		}
	}

	decrementAndGet_(): number | null {
		return this.addAndGet_( -1 );
	}

	getAndDecrement_(): number | null {
		return this.getAndAdd_( -1 );
	}

	getAndIncrement_(): number | null {
		return this.getAndAdd_( +1 );
	}

	incrementAndGet_(): number | null {
		return this.addAndGet_( +1 );
	}
}
