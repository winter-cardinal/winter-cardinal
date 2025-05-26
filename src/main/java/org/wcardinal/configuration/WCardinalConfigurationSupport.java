/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ApplicationObjectSupport;

@Configuration
@ComponentScan("org.wcardinal")
@ConditionalOnWebApplication
public class WCardinalConfigurationSupport extends ApplicationObjectSupport implements WCardinalConfiguration {
	final Logger logger = LoggerFactory.getLogger(WCardinalConfigurationSupport.class);

	@Value("${wcardinal.message.binary.size.max:500000}")
	int maximumBinaryMessageSize;

	@Value("${wcardinal.message.text.size.max:62500}")
	int maximumTextMessageSize;

	@Value("${wcardinal.idle.max:30000}")
	long maximumIdleTime;

	@Value("${wcardinal.disconnection.max:4000}")
	long maximumDisconnectionTime;

	@Value("${wcardinal.websocket.path:**/wcardinal-websocket}")
	String websocketPath;

	@Value("${wcardinal.allowed-origins:}")
	String[] allowedOrigins;

	@Value("${wcardinal.controller.http:true}")
	boolean isControllerHttpEnabled;

	@Value("${wcardinal.controller.variable.embedding:true}")
	boolean isControllerVariableEmbeddable;

	@Value("${wcardinal.controller.variable.embedding.encoding:REPLACE}")
	ControllerVariableEncoding embeddedControllerVariableEncoding;

	@Value("${wcardinal.polling.path:**/wcardinal-polling}")
	String pollingPath;

	@Value("${wcardinal.polling.timeout:10000}")
	int pollingTimeout;

	@Value("${wcardinal.thread.pool.size:30}")
	int threadPoolSize;

	@Value("${wcardinal.message.pool.size:30}")
	int messagePoolSize;

	@Value("${wcardinal.message.partial:true}")
	boolean isMessagePartialEnabled;

	@Value("${wcardinal.message.partial.size:10000}")
	int partialMessageSize;

	@Value("${wcardinal.message.partial.timeout:30000}")
	long partialMessageTimeout;

	@Value("${wcardinal.io.shared:false}")
	boolean isSharedConnectionEnabled;

	@Value("${wcardinal.io.protocol.defaults:websocket,polling-100}")
	String[] defaultProtocols;

	@Value("${wcardinal.sync.connect.timeout:5000}")
	long syncConnectTimeout;

	@Value("${wcardinal.sync.update.timeout:5000}")
	long syncUpdateTimeout;

	@Value("${wcardinal.sync.update.interval:10000}")
	long syncUpdateInterval;

	@Value("${wcardinal.sync.client.connect.timeout:5000}")
	long syncClientConnectTimeout;

	@Value("${wcardinal.sync.client.update.timeout:5000}")
	long syncClientUpdateTimeout;

	@Value("${wcardinal.sync.client.update.interval:10000}")
	long syncClientUpdateInterval;

	@Value("${wcardinal.sync.client.process.interval:16}")
	long syncClientProcessInterval;

	@Value("${wcardinal.trigger.queue.size.max:100}")
	int maximumTriggerQueueSize;

	@Override
	public void initApplicationContext( final ApplicationContext context ){
		// Run configurers
		for( final WCardinalConfigurer bean: context.getBeansOfType(WCardinalConfigurer.class).values() ){
			bean.configure(this);
		}
	}

	@Override
	public synchronized int getMaximumBinaryMessageSize(){
		return maximumBinaryMessageSize;
	}

	@Override
	public synchronized int getMaximumTextMessageSize(){
		return maximumTextMessageSize;
	}

	@Override
	public synchronized long getMaximumIdleTime(){
		return maximumIdleTime;
	}

	@Override
	public synchronized String getWebSocketPath(){
		return websocketPath;
	}

	@Override
	public synchronized String[] getAllowedOrigins(){
		return allowedOrigins;
	}

	@Override
	public synchronized WCardinalConfigurationSupport setMaximumBinaryMessageSize( final int limit ) {
		maximumBinaryMessageSize = limit;
		return this;
	}

	@Override
	public synchronized WCardinalConfigurationSupport setMaximumTextMessageSize( final int limit ) {
		maximumTextMessageSize = limit;
		return this;
	}

	@Override
	public synchronized WCardinalConfigurationSupport setMaximumIdleTime( final long idleTime ) {
		maximumIdleTime = idleTime;
		return this;
	}

	@Override
	public WCardinalConfiguration setAllowedOrigins( final String... origins ) {
		allowedOrigins = origins;
		return this;
	}

	@Override
	public synchronized WCardinalConfigurationSupport setWebSocketPath( final String path ) {
		websocketPath = path;
		return this;
	}

	@Override
	public synchronized WCardinalConfiguration setControllerHttpEnabled( final boolean enabled ) {
		isControllerHttpEnabled = enabled;
		return this;
	}

	@Override
	public synchronized boolean isControllerHttpEnabled() {
		return isControllerHttpEnabled;
	}

	@Override
	public synchronized int getPollingTimeout() {
		return pollingTimeout;
	}

	@Override
	public synchronized WCardinalConfiguration setPollingTimeout( final int timeout ) {
		pollingTimeout = timeout;
		return this;
	}

	@Override
	public synchronized int getThreadPoolSize() {
		return threadPoolSize;
	}

	@Override
	public synchronized WCardinalConfiguration setThreadPoolSize( final int poolSize ) {
		threadPoolSize = poolSize;
		return this;
	}

	@Override
	public synchronized String getPollingPath() {
		return pollingPath;
	}

	@Override
	public synchronized WCardinalConfiguration setPollingPath( final String path ) {
		pollingPath = path;
		return this;
	}

	@Override
	public int getMessagePoolSize() {
		return messagePoolSize;
	}

	@Override
	public WCardinalConfiguration setMessagePoolSize( final int poolSize ) {
		messagePoolSize = poolSize;
		return this;
	}

	@Override
	public boolean isPartialMessageEnabled() {
		return isMessagePartialEnabled;
	}

	@Override
	public WCardinalConfiguration setPartialMessageEnabled( final boolean enabled ) {
		isMessagePartialEnabled = enabled;
		return this;
	}

	@Override
	public boolean isSharedConnectionEnabled() {
		return isSharedConnectionEnabled;
	}

	@Override
	public WCardinalConfiguration setSharedConnectionEnabled( final boolean enabled ) {
		isSharedConnectionEnabled = enabled;
		return this;
	}

	@Override
	public String[] getDefaultProtocols() {
		return defaultProtocols;
	}

	@Override
	public WCardinalConfiguration setDefaultProtocols( final String... protocols ) {
		defaultProtocols = protocols;
		return this;
	}

	@Override
	public WCardinalConfiguration setControllerVariableEmbeddable( final boolean embeddable ) {
		isControllerVariableEmbeddable = embeddable;
		return this;
	}

	@Override
	public boolean isControllerVariableEmbeddable() {
		return isControllerVariableEmbeddable;
	}

	@Override
	public WCardinalConfiguration setEmbeddedControllerVariableEncoding( final ControllerVariableEncoding encoding ) {
		embeddedControllerVariableEncoding = encoding;
		return this;
	}

	@Override
	public ControllerVariableEncoding getEmbeddedControllerVariableEncoding() {
		return embeddedControllerVariableEncoding;
	}

	@Override
	public long getSyncConnectTimeout() {
		return syncConnectTimeout;
	}

	@Override
	public WCardinalConfiguration setSyncConnectTimeout( final long timeout ) {
		syncConnectTimeout = timeout;
		return this;
	}

	@Override
	public long getSyncUpdateTimeout() {
		return syncUpdateTimeout;
	}

	@Override
	public WCardinalConfiguration setSyncUpdateTimeout( final long timeout ) {
		syncUpdateTimeout = timeout;
		return this;
	}

	@Override
	public long getSyncUpdateInterval() {
		return syncUpdateInterval;
	}

	@Override
	public WCardinalConfiguration setSyncUpdateInterval( final long interval ) {
		syncUpdateInterval = interval;
		return this;
	}

	@Override
	public long getSyncClientConnectTimeout() {
		return syncClientConnectTimeout;
	}

	@Override
	public WCardinalConfiguration setSyncClientConnectTimeout( final long timeout ) {
		syncClientConnectTimeout = timeout;
		return this;
	}

	@Override
	public long getSyncClientUpdateTimeout() {
		return syncClientUpdateTimeout;
	}

	@Override
	public WCardinalConfiguration setSyncClientUpdateTimeout( final long timeout ) {
		syncClientUpdateTimeout = timeout;
		return this;
	}

	@Override
	public long getSyncClientUpdateInterval() {
		return syncClientUpdateInterval;
	}

	@Override
	public WCardinalConfiguration setSyncClientUpdateInterval( final long interval ) {
		syncClientUpdateInterval = interval;
		return this;
	}

	@Override
	public long getSyncClientProcessInterval() {
		return syncClientProcessInterval;
	}

	@Override
	public WCardinalConfiguration setSyncClientProcessInterval( final long interval ) {
		syncClientProcessInterval = interval;
		return this;
	}

	@Override
	public WCardinalConfiguration setPartialMessageSize( final int size ) {
		partialMessageSize = size;
		return this;
	}

	@Override
	public int getPartialMessageSize() {
		return partialMessageSize;
	}

	@Override
	public int getMaximumTriggerQueueSize() {
		return maximumTriggerQueueSize;
	}

	@Override
	public WCardinalConfiguration setMaximumTriggerQueueSize( final int size ) {
		maximumTriggerQueueSize = size;
		return this;
	}

	@Override
	public long getMaximumDisconnectionTime() {
		return maximumDisconnectionTime;
	}

	@Override
	public WCardinalConfiguration setMaximumDisconnectionTime( final long disconnectionTime ) {
		maximumDisconnectionTime = disconnectionTime;
		return this;
	}
}
