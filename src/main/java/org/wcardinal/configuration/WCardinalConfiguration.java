/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.configuration;

/**
 * Provides methods for configuring Winter Cardinal.
 */
public interface WCardinalConfiguration {
	/**
	 * Returns the maximum size of binary messages in bytes.
	 * The default value is 500000.
	 * The default value can be changed by setting a property
	 * `wcardinal.message.binary.size.max` in properties files,
	 * command-line arguments, etc.
	 *
	 * @return the maximum size of binary messages in bytes.
	 * @see #setMaximumBinaryMessageSize(int)
	 */
	int getMaximumBinaryMessageSize();

	/**
	 * Sets the maximum size of binary messages.
	 *
	 * @param limit the maximum size of binary messages in bytes
	 * @return this
	 * @see #getMaximumBinaryMessageSize()
	 */
	WCardinalConfiguration setMaximumBinaryMessageSize(int limit);

	/**
	 * Returns the maximum size of text messages.
	 * The default value is 62500.
	 * The default value can be changed by setting a property
	 * `wcardinal.message.text.size.max` in properties files,
	 * command-line arguments, etc.
	 *
	 * @return the maximum size of text messages
	 * @see #setMaximumTextMessageSize(int)
	 */
	int getMaximumTextMessageSize();

	/**
	 * Sets the maximum size of text messages.
	 *
	 * @param limit the maximum size of text messages in bytes
	 * @return this
	 * @see #getMaximumTextMessageSize()
	 */
	WCardinalConfiguration setMaximumTextMessageSize(int limit);

	/**
	 * Returns the maximum idle time in milliseconds.
	 * The default value is 30000.
	 * The default value can be changed by setting a property
	 * `wcardinal.idle.max` in properties files,
	 * command-line arguments, etc.
	 *
	 * @return the maximum idle time in milliseconds
	 * @see #setMaximumIdleTime(long)
	 */
	long getMaximumIdleTime();

	/**
	 * Sets the maximum idle time in milliseconds.
	 * A browser who have not sent any messages for this time span
	 * is considered inactive and disconnected.
	 *
	 * @param idleTime the maximum idle time in milliseconds
	 * @return this
	 * @see #getMaximumIdleTime()
	 */
	WCardinalConfiguration setMaximumIdleTime(long idleTime);

	/**
	 * Returns the maximum disconnection time in milliseconds.
	 * If it is negative, the maximum disconnection time is not checked.
	 * The default value is 4000.
	 * The default value can be changed by setting a property
	 * `wcardinal.disconnection.max` in properties files,
	 * command-line arguments, etc.
	 *
	 * @return the maximum disconnection time in milliseconds
	 * @see #setMaximumDisconnectionTime(long)
	 */
	long getMaximumDisconnectionTime();

	/**
	 * Sets the maximum disconnection time in milliseconds.
	 * A browser who have been disconnected for this time span
	 * is considered inactive.
	 *
	 * @param disconnectionTime the maximum disconnection time in milliseconds
	 * @return this
	 * @see #getMaximumDisconnectionTime()
	 */
	WCardinalConfiguration setMaximumDisconnectionTime(long disconnectionTime);

	/**
	 * Returns the URL of the WebSocket endpoint.
	 * The default value is "**\/wcardinal-websocket".
	 * The default value can be changed by setting a property
	 * `wcardinal.websocket.path` in properties files,
	 * command-line arguments, etc.
	 *
	 * @return the URL of the WebSocket endpoint
	 * @see #setWebSocketPath(String)
	 */
	String getWebSocketPath();

	/**
	 * Sets the URL of the WebSocket endpoint.
	 *
	 * @param path the URL of the WebSocket endpoint
	 * @return this
	 * @see #getWebSocketPath()
	 */
	WCardinalConfiguration setWebSocketPath(String path);

	/**
	 * Returns the allowed origins.
	 * The default value is an empty string array.
	 * The default value can be changed by setting a property
	 * `wcardinal.allowed-origins` in properties files,
	 * command-line arguments, etc.
	 *
	 * @return the allowed origins
	 * @see #setAllowedOrigins(String...)
	 */
	String[] getAllowedOrigins();

	/**
	 * Sets the allowed origins.
	 *
	 * @param origins allowed origin
	 * @return this
	 * @see #getAllowedOrigins()
	 */
	WCardinalConfiguration setAllowedOrigins(String... origins);

	/**
	 * Returns true if HTTP requests of controllers area allowed.
	 * The default value is true.
	 * The default value can be changed by setting a property
	 * `wcardinal.controller.http` in properties files,
	 * command-line arguments, etc.
	 *
	 * @return true if HTTP requests of controllers area allowed
	 * @see #setControllerHttpEnabled(boolean)
	 */
	boolean isControllerHttpEnabled();

	/**
	 * Sets whether HTTP requests of controllers are allowed.
	 *
	 * @param enabled true to allow, false to disallow the controller requests
	 * @return this
	 * @see #isControllerHttpEnabled()
	 */
	WCardinalConfiguration setControllerHttpEnabled(boolean enabled);

	/**
	 * Returns true if controller variables are embeddable.
	 * The default value is true.
	 * The default value can be changed by setting a property
	 * `wcardinal.controller.variable.embedding` in properties files,
	 * command-line arguments, etc.
	 *
	 * @return true if controller variables are embeddable
	 * @see #setControllerVariableEmbeddable(boolean)
	 */
	boolean isControllerVariableEmbeddable();

	/**
	 * Sets whether controller variables are embeddable.
	 *
	 * @param embeddable true to make controller variables embeddable
	 * @return this
	 * @see #isControllerVariableEmbeddable()
	 */
	WCardinalConfiguration setControllerVariableEmbeddable(boolean embeddable);

	/**
	 * Sets to the specified encoding of embedded controller variables.
	 * The default value is "REPLACE".
	 * The default value can be changed by setting a property
	 * `wcardinal.controller.variable.embedding.encoding` in properties files,
	 * command-line arguments, etc.
	 *
	 * @param encoding encoding
	 * @return this
	 * @see #setEmbeddedControllerVariableEncoding(ControllerVariableEncoding)
	 */
	WCardinalConfiguration setEmbeddedControllerVariableEncoding(ControllerVariableEncoding encoding);

	/**
	 * Returns the encoding of embedded controller variables.
	 *
	 * @return the encoding of embedded controller variables
	 * @see #getEmbeddedControllerVariableEncoding()
	 */
	ControllerVariableEncoding getEmbeddedControllerVariableEncoding();

	/**
	 * Returns the URL of the polling endpoint.
	 * The default value is "**\/wcardinal-polling".
	 * The default value can be changed by setting a property
	 * `wcardinal.polling.path` in properties files,
	 * command-line arguments, etc.
	 *
	 * @return the URL of the polling endpoint
	 * @see #setPollingPath(String)
	 */
	String getPollingPath();

	/**
	 * Sets the URL of the polling endpoint.
	 *
	 * @param path the URL of the polling endpoint
	 * @return this
	 * @see #getPollingPath()
	 */
	WCardinalConfiguration setPollingPath(String path);

	/**
	 * Returns the polling timeout in seconds.
	 * The default value is 10000.
	 * The default value can be changed by setting a property
	 * `wcardinal.polling.timeout` in properties files,
	 * command-line arguments, etc.
	 *
	 * @return the polling timeout in seconds
	 * @see #setPollingTimeout(int)
	 */
	int getPollingTimeout();

	/**
	 * Sets the the polling timeout in seconds.
	 *
	 * @param timeout the polling timeout in seconds
	 * @return this
	 * @see #getPollingTimeout()
	 */
	WCardinalConfiguration setPollingTimeout( int timeout );

	/**
	 * Returns the default thread pool size.
	 * The default value is 30.
	 * The default value can be changed by setting a property
	 * `wcardinal.thread.pool.size` in properties files,
	 * command-line arguments, etc.
	 *
	 * @return the default thread pool size
	 * @see #setThreadPoolSize(int)
	 */
	int getThreadPoolSize();

	/**
	 * Sets the default thread pool size.
	 *
	 * @param poolSize the default thread pool size
	 * @return this
	 * @see #getThreadPoolSize()
	 */
	WCardinalConfiguration setThreadPoolSize( int poolSize );

	/**
	 * Returns the message pool size.
	 * The default value is 30.
	 * The default value can be changed by setting a property
	 * `wcardinal.message.pool.size` in properties files,
	 * command-line arguments, etc.
	 *
	 * @return the message pool size
	 * @see #setMessagePoolSize(int)
	 */
	int getMessagePoolSize();

	/**
	 * Sets the message pool size.
	 *
	 * @param poolSize the message pool size
	 * @return this
	 * @see #getMessagePoolSize()
	 */
	WCardinalConfiguration setMessagePoolSize( int poolSize );

	/**
	 * Returns true if partial messages are enabled.
	 * The default value is true.
	 * The default value can be changed by setting a property
	 * `wcardinal.message.partial` in properties files,
	 * command-line arguments, etc.
	 *
	 * @return true if partial messages are enabled
	 * @see #setPartialMessageEnabled(boolean)
	 */
	boolean isPartialMessageEnabled();

	/**
	 * Sets whether partial messages are enabled.
	 *
	 * @param enabled true to enable partial messages
	 * @return itselt
	 * @see #isPartialMessageEnabled()
	 */
	WCardinalConfiguration setPartialMessageEnabled( boolean enabled );

	/**
	 * Returns the partial message size.
	 * The default value is 10000.
	 * The default value can be changed by setting a property
	 * `wcardinal.message.partial.size` in properties files,
	 * command-line arguments, etc.
	 *
	 * @return partial message size
	 * @see #setPartialMessageSize(int)
	 */
	int getPartialMessageSize();

	/**
	 * Sets the partial message size.
	 *
	 * @param size partial message size
	 * @return this
	 * @see #getPartialMessageSize()
	 */
	WCardinalConfiguration setPartialMessageSize( int size );

	/**
	 * Returns true if shared connections are enabled.
	 * The default value is false.
	 * The default value can be changed by setting a property
	 * `wcardinal.io.shared` in properties files,
	 * command-line arguments, etc.
	 *
	 * @return true if shared connections are enabled
	 * @see #setSharedConnectionEnabled(boolean)
	 */
	boolean isSharedConnectionEnabled();

	/**
	 * Sets whether shared connections are enabled.
	 *
	 * @param enabled true to enable shared connections
	 * @return this
	 * @see #isSharedConnectionEnabled()
	 */
	WCardinalConfiguration setSharedConnectionEnabled( boolean enabled );

	/**
	 * Returns the default communication protocols.
	 * The default value is "websocket,polling-100".
	 * The default value can be changed by setting a property
	 * `wcardinal.io.protocol.defaults` in properties files,
	 * command-line arguments, etc.
	 *
	 * @return default communication protocols
	 * @see #setDefaultProtocols(String...)
	 */
	String[] getDefaultProtocols();

	/**
	 * Sets the default communication protocols.
	 *
	 * @param protocols default communication protocols
	 * @return this
	 * @see #getDefaultProtocols()
	 */
	WCardinalConfiguration setDefaultProtocols( String... protocols );

	/**
	 * Returns the connection request timeout of the synchronization process.
	 * The default value is 5000.
	 * The default value can be changed by setting a property
	 * `wcardinal.sync.connect.timeout` in properties files,
	 * command-line arguments, etc.
	 *
	 * @return the connection request timeout of the synchronization process
	 * @see #setSyncConnectTimeout(long)
	 */
	long getSyncConnectTimeout();

	/**
	 * Sets the connection request timeout in the synchronization process.
	 *
	 * @param timeout connection request timeout in the synchronization process
	 * @return this
	 * @see #getSyncConnectTimeout()
	 */
	WCardinalConfiguration setSyncConnectTimeout( long timeout );

	/**
	 * Returns the update request timeout in the synchronization process.
	 * The default value is 5000.
	 * The default value can be changed by setting a property
	 * `wcardinal.sync.update.timeout` in properties files,
	 * command-line arguments, etc.
	 *
	 * @return the update request timeout in the synchronization process
	 * @see #setSyncUpdateTimeout(long)
	 */
	long getSyncUpdateTimeout();

	/**
	 * Sets the update request timeout in the synchronization process.
	 *
	 * @param timeout update request timeout in the synchronization process
	 * @return this
	 * @see #setSyncUpdateTimeout(long)
	 */
	WCardinalConfiguration setSyncUpdateTimeout( long timeout );

	/**
	 * Returns the update request interval in the synchronization process.
	 * The default value is 10000.
	 * The default value can be changed by setting a property
	 * `wcardinal.sync.update.interval` in properties files,
	 * command-line arguments, etc.
	 *
	 * @return the update request interval in the synchronization process
	 * @see #setSyncUpdateInterval(long)
	 */
	long getSyncUpdateInterval();

	/**
	 * Sets the update request interval in the synchronization process.
	 *
	 * @param interval update request interval in the synchronization process
	 * @return this
	 * @see #getSyncUpdateInterval()
	 */
	WCardinalConfiguration setSyncUpdateInterval( long interval );

	/**
	 * Returns the client-side connection request timeout of the synchronization process.
	 * The default value is 5000.
	 * The default value can be changed by setting a property
	 * `wcardinal.sync.client.connect.timeout` in properties files,
	 * command-line arguments, etc.
	 *
	 * @return the client-side connection request timeout of the synchronization process
	 * @see #setSyncClientConnectTimeout(long)
	 */
	long getSyncClientConnectTimeout();

	/**
	 * Sets the client-side connection request timeout in the synchronization process.
	 *
	 * @param timeout client-side connection request timeout in the synchronization process
	 * @return this
	 * @see #getSyncClientConnectTimeout()
	 */
	WCardinalConfiguration setSyncClientConnectTimeout( long timeout );

	/**
	 * Returns the client-side update request timeout in the synchronization process.
	 * The default value is 5000.
	 * The default value can be changed by setting a property
	 * `wcardinal.sync.client.update.timeout` in properties files,
	 * command-line arguments, etc.
	 *
	 * @return the client-side update request timeout in the synchronization process
	 * @see #setSyncClientUpdateTimeout(long)
	 */
	long getSyncClientUpdateTimeout();

	/**
	 * Sets the client-side update request timeout in the synchronization process.
	 *
	 * @param timeout client-side update request timeout in the synchronization process
	 * @return this
	 * @see #getSyncClientUpdateTimeout()
	 */
	WCardinalConfiguration setSyncClientUpdateTimeout( long timeout );

	/**
	 * Returns the client-side update request interval in the synchronization process.
	 * The default value is 10000.
	 * The default value can be changed by setting a property
	 * `wcardinal.sync.client.update.interval` in properties files,
	 * command-line arguments, etc.
	 *
	 * @return the client-side update request interval in the synchronization process
	 * @see #setSyncClientUpdateInterval(long)
	 */
	long getSyncClientUpdateInterval();

	/**
	 * Sets the client-side update request interval in the synchronization process.
	 *
	 * @param interval client-side update request interval in the synchronization process
	 * @return this
	 * @see #getSyncClientUpdateInterval()
	 */
	WCardinalConfiguration setSyncClientUpdateInterval( long interval );

	/**
	 * Returns the client-side sync process interval in milliseconds.
	 * If the interval is 0 or less, sync messages are processed immediately.
	 * The default value is 0.
	 * The default value can be changed by setting a property
	 * `wcardinal.sync.client.process.interval` in properties files,
	 * command-line arguments, etc.
	 *
	 * @return the client-side sync process interval in milliseconds.
	 * @see #setSyncClientProcessInterval(long)
	 */
	long getSyncClientProcessInterval();

	/**
	 * Sets the client-side sync process interval in milliseconds.
	 *
	 * @param interval the client-side sync process interval in milliseconds.
	 * @return this
	 * @see #getSyncClientProcessInterval()
	 */
	WCardinalConfiguration setSyncClientProcessInterval( long interval );

	/**
	 * Returns the maximum number of queued trigger requests per controllers.
	 * This is to avoid consuming large memory.
	 * If it is 0 or less, imposes no restrictions.
	 * The default value is 100.
	 * The default value can be changed by setting a property
	 * `wcardinal.trigger.queue.size.max` in properties files,
	 * command-line arguments, etc.
	 *
	 * @return the maximum number of queued trigger messages.
	 * @see #setMaximumTriggerQueueSize(int)
	 */
	int getMaximumTriggerQueueSize();

	/**
	 * Sets the maximum number of queued trigger requests.
	 *
	 * @param size maximum number of queued trigger requests.
	 * @return this
	 * @see #getMaximumTriggerQueueSize()
	 */
	WCardinalConfiguration setMaximumTriggerQueueSize( int size );
}
