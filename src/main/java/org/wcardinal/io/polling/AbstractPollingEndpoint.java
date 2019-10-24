/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io.polling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.AbstractController;

import org.wcardinal.configuration.WCardinalConfiguration;

public abstract class AbstractPollingEndpoint extends AbstractController {
	protected final static Logger logger = LoggerFactory.getLogger(AbstractPollingEndpoint.class);
	protected final WCardinalConfiguration configuration;

	public AbstractPollingEndpoint( final WCardinalConfiguration configuration ){
		this.configuration = configuration;
	}
}
