/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.wcardinal.controller.annotation.ControllerScopeService;

@ControllerScopeService
public class BasicsControllerScopeBean {
	Logger logger = LoggerFactory.getLogger(BasicsControllerScopeBean.class);

	int data = 0;

	public synchronized int get(){
		return data;
	}

	public synchronized int set( int data ){
		int result = this.data;
		this.data = data;
		return result;
	}

	@PostConstruct
	void init(){
		logger.info("init");
	}

	@PreDestroy
	void destroy(){
		logger.info("destroy");
	}
}
