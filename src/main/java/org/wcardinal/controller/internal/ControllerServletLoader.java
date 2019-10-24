/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ResolvableType;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import com.google.common.base.CaseFormat;

import org.wcardinal.configuration.WCardinalConfiguration;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.KeepAlive;
import org.wcardinal.controller.annotation.Retry;
import org.wcardinal.util.thread.Scheduler;

@Configuration
public class ControllerServletLoader {
	final static int DEFAULT_POLLING_INTERVAL = 100;
	final static Map<String, ControllerServlet> mappings = new ConcurrentHashMap<String, ControllerServlet>();

	final Logger logger = LoggerFactory.getLogger(ControllerServletLoader.class);

	@Autowired
	ApplicationContext context;

	@Autowired
	WCardinalConfiguration configuration;

	public ControllerServletLoader(){}

	public static ControllerServlet getServlet( final String path ){
		return mappings.get( path );
	}

	@Bean
	public HandlerMapping controllerServletMapping(){
		final Scheduler scheduler = new Scheduler( configuration.getThreadPoolSize() );
		final SimpleUrlHandlerMapping handler = new SimpleUrlHandlerMapping();
		final Map<String, SortedMap<Integer, Collection<ControllerFactoryAndRoles>>> mappings = initMapping();
		final Map<String, ControllerServlet> urlMap = new HashMap<>();
		for( final String path: mappings.keySet() ){
			final String npath = toNormalizedPath(path);
			final String spath = npath.substring(2);
			final SortedMap<Integer, Collection<ControllerFactoryAndRoles>> factoryAndRolesList = mappings.get(path);
			final ControllerServlet bean = new ControllerServlet( context, factoryAndRolesList, configuration, scheduler, spath );
			ControllerServletLoader.mappings.put(spath, bean);
			urlMap.put(npath, bean);
		}
		handler.setOrder(Integer.MAX_VALUE - 2);
		handler.setUrlMap( urlMap );

		return handler;
	}

	protected String toNormalizedPath( final String path ){
		if( path == null || path.length()<=0 ) return "**/";
		if( path.startsWith("**/") ) return path;
		if( path.startsWith("/") ) return "**"+path;
		return "**/" + path;
	}

	protected String toNormalizedName( final String name ){
		if( name == null || name.length()<=0 ) return name;
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	protected void push( final Map<String, SortedMap<Integer, Collection<ControllerFactoryAndRoles>>> container, final String path, final ControllerFactoryAndRoles factoryAndRoles, final String typeName ){
		SortedMap<Integer, Collection<ControllerFactoryAndRoles>> map = container.get(path);
		if( map == null ){
			map = new TreeMap<Integer, Collection<ControllerFactoryAndRoles>>( Collections.reverseOrder() );
			container.put(path, map);
		}

		Collection<ControllerFactoryAndRoles> collection = map.get(factoryAndRoles.roles.length);
		if( collection == null ){
			collection = new ArrayList<ControllerFactoryAndRoles>();
			map.put(factoryAndRoles.roles.length, collection);
		}

		collection.add(factoryAndRoles);

		logger.info("Mapped a controller '"+typeName+"' to ["+path+"] with roles "+Arrays.toString(factoryAndRoles.roles)+" as '"+factoryAndRoles.factory.getName()+"'");
	}

	protected Map<String, SortedMap<Integer, Collection<ControllerFactoryAndRoles>>> initMapping(){
		final Map<String, SortedMap<Integer, Collection<ControllerFactoryAndRoles>>> result
			= new HashMap<String, SortedMap<Integer, Collection<ControllerFactoryAndRoles>>>();
		final String[] beanNames = context.getBeanNamesForType(Object.class);
		for( final String beanName: beanNames ){
			final Class<?> type = context.getType(beanName);
			final Controller controller = type.getAnnotation( Controller.class );
			if( controller != null ){
				final String typeName = type.getName();
				final String controllerName = toNormalizedName(controller.name());
				final String[] roles = controller.roles();
				final Map<String, Object> keepAlive = toKeepAlive( controller );
				final Map<String, Object> retry = toRetry( controller );
				final Map<String, Object> protocols = toProtocols( controller );
				final ControllerTitle title = toTitle( controller );
				final EnumSet<Property> properties = Properties.create( type );
				final ControllerFactory factory = new ControllerFactory( ControllerType.CONTROLLER, context, ResolvableType.forClass( type ), properties, controllerName, null, null );
				final ControllerFactoryAndRoles factoryAndRoles = new ControllerFactoryAndRoles( factory, roles, keepAlive, retry, protocols, title );

				final String[] value = controller.value();
				for( final String path: value ){
					push( result, path, factoryAndRoles, typeName );
				}

				final String[] urls = controller.urls();
				for( final String path: urls ){
					push( result, path, factoryAndRoles, typeName );
				}

				if( value.length <= 0 && urls.length <= 0 ){
					final String url = "/" + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, factory.getName());
					push( result, url, factoryAndRoles, typeName );
				}
			}
		}
		return result;
	}

	protected Map<String, Object> toKeepAlive( final Controller controller ){
		final KeepAlive keepAlive = controller.keepAlive();

		final Map<String, Object> result = new HashMap<String, Object>();
		result.put("timeout", keepAlive.timeout());
		result.put("interval", toKeepAliveInterval( keepAlive ));
		result.put("ping", toKeepAlivePing( keepAlive ));

		return result;
	}

	protected long toKeepAliveInterval( final KeepAlive keepAlive ){
		final long interval = keepAlive.interval();
		if( 0 <= interval ) return interval;
		return configuration.getMaximumIdleTime() >> 1;
	}

	protected long toKeepAlivePing( final KeepAlive keepAlive ){
		final long ping = keepAlive.ping();
		if( 0 <= ping ) return ping;
		return configuration.getMaximumIdleTime() >> 1;
	}

	protected Map<String, Object> toRetry( final Controller controller ){
		final Retry retry = controller.retry();

		final Map<String, Object> result = new HashMap<String, Object>();
		result.put("timeout", retry.timeout());
		result.put("delay", retry.delay());
		result.put("interval", retry.interval());

		return result;
	}

	protected ControllerTitle toTitle( final Controller controller ){
		return new ControllerTitle( context, controller.separators(), controller.separatorMessages() );
	}

	protected Map<String, Object> toProtocols( final Controller controller ){
		final String[] specified = controller.protocols();
		final String[] defaults = configuration.getDefaultProtocols();
		final String[] protocols = ( 0 < specified.length ? specified : defaults );
		final String prefix = configuration.isSharedConnectionEnabled() ? "shared-" : "";

		final Map<String, Object> result = new HashMap<String, Object>();
		for( final String protocol: protocols ) {
			if( protocol == null ) continue;

			final String formatted = protocol.trim().toLowerCase();
			if( formatted.equals("websocket") || formatted.equals("web-socket") ) {
				result.put( prefix + "websocket", toWebSocketParameters() );
			} else if( formatted.equals("comet") ) {
				result.put( prefix + "polling", toPollingParameters( DEFAULT_POLLING_INTERVAL ) );
			} else if( formatted.startsWith("polling") ){
				result.put( prefix + "polling", toPollingParameters( toPollingInterval( formatted ) ) );
			} else {
				logger.error( "Unknown protocol: {}", protocol );
			}
		}

		return result;
	}

	protected Map<String, Object> toWebSocketParameters(){
		return Collections.emptyMap();
	}

	protected Map<String, Object> toPollingParameters( final int interval ){
		final Map<String, Object> result = new HashMap<>();
		final int timeout = configuration.getPollingTimeout();
		result.put( "interval", interval );
		result.put( "timeout", timeout + (timeout >> 1) );
		return result;
	}

	protected int toPollingInterval( final String polling ){
		if( 8 < polling.length() ) {
			try {
				return Integer.valueOf(polling.substring( 8 ));
			} catch( final Exception e ) {

			}
		}

		return DEFAULT_POLLING_INTERVAL;
	}
}
