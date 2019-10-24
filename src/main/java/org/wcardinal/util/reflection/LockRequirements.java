/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

import java.util.EnumSet;

public class LockRequirements {
	public static EnumSet<LockRequirement> REQUIRED_OR_UNSPECIFIED = EnumSet.of(LockRequirement.REQUIRED, LockRequirement.UNSPECIFIED);
	public static EnumSet<LockRequirement> NOT_REQUIRED_OR_UNSPECIFIED = EnumSet.of(LockRequirement.NOT_REQUIRED, LockRequirement.UNSPECIFIED);
	public static EnumSet<LockRequirement> NOT_REQUIRED = EnumSet.of(LockRequirement.NOT_REQUIRED);
	public static EnumSet<LockRequirement> REQUIRED = EnumSet.of(LockRequirement.REQUIRED);
	public static EnumSet<LockRequirement> ANY = EnumSet.of(LockRequirement.REQUIRED, LockRequirement.NOT_REQUIRED, LockRequirement.UNSPECIFIED);
}
