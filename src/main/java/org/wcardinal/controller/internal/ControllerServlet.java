/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.wcardinal.configuration.ControllerVariableEncoding;
import org.wcardinal.configuration.WCardinalConfiguration;
import org.wcardinal.controller.internal.info.SenderIdAndDynamicInfo;
import org.wcardinal.io.Endpoint;
import org.wcardinal.io.Session;
import org.wcardinal.io.SessionResult;
import org.wcardinal.io.SubSession;
import org.wcardinal.util.json.Json;
import org.wcardinal.util.thread.Scheduler;

public class ControllerServlet extends AbstractController {
	final boolean IS_CONTROLLER_VARIABLE_EMBEDDABLE;
	final ControllerVariableEncoding EMBEDDED_CONTROLLER_VARIBLE_ENCODING;

	final Logger logger = LoggerFactory.getLogger(ControllerServlet.class);

	final SortedMap<Integer, Collection<ControllerFactoryAndRoles>> countToFactoryAndRoles;
	final ApplicationContext context;
	final WCardinalConfiguration configuration;
	final Scheduler scheduler;
	final String spath;

	public ControllerServlet(
			final ApplicationContext context,
			final SortedMap<Integer, Collection<ControllerFactoryAndRoles>> countToFactoryAndRoles,
			final WCardinalConfiguration configuration,
			final Scheduler scheduler,
			final String spath
	){
		this.context = context;
		this.countToFactoryAndRoles = countToFactoryAndRoles;
		this.configuration = configuration;
		this.scheduler = scheduler;
		this.spath = spath;

		IS_CONTROLLER_VARIABLE_EMBEDDABLE = configuration.isControllerVariableEmbeddable();
		EMBEDDED_CONTROLLER_VARIBLE_ENCODING = configuration.getEmbeddedControllerVariableEncoding();
	}

	@Override
	protected ModelAndView handleRequestInternal(
			final HttpServletRequest req,
			final HttpServletResponse resp
	) throws Exception {
		try{
			handle( req, resp );
		} catch ( final JsonProcessingException e ) {
			logger.error( "Failed to serialize a controller to JSON", e );

			try {
				resp.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
			} catch ( final Exception eb ) {

			}
		} catch ( final BeanCreationException e ) {
			logger.error( "Failed to create a controller", e );

			try {
				resp.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
			} catch ( final Exception eb ) {

			}
		} catch ( final IOException e ) {
			logger.error( "Failed to send a controller response", e );

			try {
				resp.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
			} catch ( final Exception eb ) {

			}
		} catch ( final Exception ea ){
			logger.error( "Failed to handle a controller request", ea );

			try {
				resp.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
			} catch ( final Exception eb ) {

			}
		}

		return null;
	}

	private boolean isUserInRoles( final HttpServletRequest req, final String[] roles ){
		for( final String role: roles ){
			if( req.isUserInRole(role) != true ) return false;
		}
		return true;
	}

	private ControllerFactoryAndRoles getFactoryAndRoles( final HttpServletRequest req ){
		for( final Collection<ControllerFactoryAndRoles> factoryAndRolesCollection: countToFactoryAndRoles.values() ){
			for( ControllerFactoryAndRoles factoryAndRoles: factoryAndRolesCollection ){
				if( isUserInRoles(req, factoryAndRoles.roles) ) {
					if( factoryAndRoles.factory.onCheck( req ) ) {
						return factoryAndRoles;
					}
				}
			}
		}
		return null;
	}

	protected List<Locale> getLocales( final HttpServletRequest req ){
		final List<Locale> result = new ArrayList<Locale>();
		final LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver( req );
		if ( localeResolver != null ) {
			result.add( localeResolver.resolveLocale( req ) );
		} else {
			final Enumeration<Locale> localeEnumeration = req.getLocales();
			while( localeEnumeration.hasMoreElements() ) {
				result.add( localeEnumeration.nextElement() );
			}
		}

		return result;
	}

	String toMode( final HttpServletRequest req ){
		final String mode = req.getHeader("X-WCardinal-Mode"); // create, retry, keep-alive
		if( mode == null || mode.equals("create") ){
			if( configuration.isControllerHttpEnabled() ) {
				return "create";
			} else {
				return null;
			}
		}
		return mode;
	}

	String toSSID( final HttpServletRequest req ){
		return req.getHeader("X-WCardinal-SSID");
	}

	String toQueryString( final String queryString ){
		if( queryString == null || queryString.length() <= 0 ) return "";
		return "?"+queryString;
	}

	void writeScript( final Writer writer, final String name, final Map<String, Object> data, final SenderIdAndDynamicInfo senderIdAndDynamicInfo, final boolean isEmbedded ) throws IOException {
		writer.write("(function(){");
		writer.write("var M = wcardinal.controller.internal.ControllerMaker;");
		writer.write("var name = '");
		writer.write(name);
		writer.write("';");
		writer.write("var settings = ");
		Json.non_closing_writer.writeValue( writer, data );
		writer.write(";");
		if( senderIdAndDynamicInfo != null ) {
			if( isEmbedded ) {
				switch( EMBEDDED_CONTROLLER_VARIBLE_ENCODING ) {
				case NONE:
					writeDynamic(writer, senderIdAndDynamicInfo);
					break;
				case REPLACE:
					writeDynamicReplace(writer, senderIdAndDynamicInfo);
					break;
				case BASE64:
					writeDynamicBase64(writer, senderIdAndDynamicInfo);
					break;
				}
			} else {
				writeDynamic(writer, senderIdAndDynamicInfo);
			}
		}
		writer.write("var ");
		writer.write(name);
		writer.write(" = function( m ) { M.init( this, m ); };");
		writer.write("new M(");
		writer.write(name);
		writer.write(", name, settings);");
		writer.write("}());");
	}

	<T> void writeAsJson( final Writer writer, final T data ) throws JsonGenerationException, JsonMappingException, IOException{
		Json.non_closing_writer.writeValue(writer, data);
	}

	void writeDynamic( final Writer writer, final SenderIdAndDynamicInfo senderIdAndDynamicInfo ) throws JsonGenerationException, JsonMappingException, IOException{
		writer.write("settings.info.dynamic = ");
		Json.non_closing_writer.writeValue(writer, senderIdAndDynamicInfo.dynamicInfo);
		writer.write(";");
		writer.write("settings.info.senderId = " + senderIdAndDynamicInfo.senderId + ";");
	}

	void writeDynamicReplace( final Writer writer, final SenderIdAndDynamicInfo senderIdAndDynamicInfo ) throws JsonGenerationException, JsonMappingException, IOException{
		final String json = Json.mapper.writeValueAsString( senderIdAndDynamicInfo.dynamicInfo );
		int index = 0;
		final List<String> list = new ArrayList<>();
		while( true ){
			final int next = json.indexOf( "<", index );
			if( 0 <= next ) {
				list.add( json.substring(index, next) );
				index = next + 1;
			} else {
				list.add( json.substring(index) );
				break;
			}
		}

		writer.write("settings.info.dynamic = JSON.parse(");
		Json.non_closing_writer.writeValue( writer, list );
		writer.write(".join('<'));");
		writer.write("settings.info.senderId = " + senderIdAndDynamicInfo.senderId + ";");
	}

	void writeDynamicBase64( final Writer writer, final SenderIdAndDynamicInfo senderIdAndDynamicInfo ) throws JsonGenerationException, JsonMappingException, IOException{
		writer.write("settings.info.dynamic = JSON.parse(wcardinal.util.util.atob(");
		Json.non_closing_writer.writeValue(writer, Json.mapper.writeValueAsBytes( senderIdAndDynamicInfo.dynamicInfo ) );
		writer.write("));");
		writer.write("settings.info.senderId = " + senderIdAndDynamicInfo.senderId + ";");
	}

	@SuppressWarnings("deprecation")
	String toScript( final String name, final Map<String, Object> data, final SenderIdAndDynamicInfo senderIdAndDynamicInfo ) throws JsonGenerationException {
		final StringWriter writer = new StringWriter();
		try {
			writeScript( writer, name, data, senderIdAndDynamicInfo, true );
		} catch (final IOException e) {
			throw new JsonGenerationException( "Failed to create a controller script", e );
		}

		return writer.toString();
	}

	CallPathAndRequest toCallPathAndRequest( final HttpServletRequest req ) {
		try {
			return Json.mapper.readValue( req.getReader(), CallPathAndRequest.class );
		} catch( final Exception e ) {
			return null;
		}
	}

	void handleCallMode( final HttpServletRequest req, final HttpServletResponse resp ) throws IOException {
		final HttpSession httpSession = req.getSession(false);
		if( httpSession != null ) {
			final Session session = Session.get(httpSession);
			if( session != null ) {
				final String ssid = toSSID( req );
				if( ssid != null ) {
					final SubSession subSession = session.getSubSession( ssid );
					if( subSession != null ){
						final RootController rootController = subSession.getRootController();
						if( rootController != null ) {
							final CallPathAndRequest callPathAndRequest = toCallPathAndRequest( req );
							if( callPathAndRequest != null ) {
								final Controller controller = rootController.getControllerAt( callPathAndRequest.path );
								if( controller != null ) {
									controller.call( new CallSMapsPutImpl( req ), null, callPathAndRequest.request );
								} else {
									new CallSMapsPutImpl( req ).putResultIfExists( null, CallResultType.NO_SUCH_METHOD );
								}
								return;
							}
						}
					}
				}
			}
		}

		resp.sendError( HttpServletResponse.SC_NOT_FOUND );
	}

	void handleTouchMode( final HttpServletRequest req, final HttpServletResponse resp ) throws IOException {
		final HttpSession httpSession = req.getSession(false);
		if( httpSession != null ) {
			final Session session = Session.get(httpSession);
			if( session != null ) {
				final String ssid = toSSID( req );
				if( ssid != null ) {
					final SubSession subSession = session.getSubSession( ssid );
					if( subSession != null ){
						final Endpoint endpoint = subSession.getEndpoint();
						if( endpoint != null ){
							endpoint.touch();

							resp.setStatus(HttpServletResponse.SC_OK);
							resp.setHeader("Content-Type", "application/json");
							final OutputStream oStream = resp.getOutputStream();
							try {
								final OutputStreamWriter osWriter = new OutputStreamWriter( oStream, "UTF-8" );
								try {
									osWriter.write("{}");
								} finally {
									IOUtils.closeQuietly(osWriter);
								}
							} finally {
								IOUtils.closeQuietly(oStream);
							}
							return;
						}
					}
				}
			}
		}

		resp.sendError( HttpServletResponse.SC_NOT_FOUND );
	}

	public void handle( final HttpServletRequest req, final HttpServletResponse resp )
			throws ServletException, IOException, InstantiationException, IllegalAccessException {
		// Query string
		final String mode = toMode( req );
		if( mode == null ){
			resp.sendError( HttpServletResponse.SC_FORBIDDEN );
			return;
		}

		// Response header
		resp.setCharacterEncoding("UTF-8");
		resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
		resp.setHeader("Pragma","no-cache");
		resp.setHeader("Expires", "Mon, 26 Jul 1997 05:00:00 GMT");

		// Find a factory
		final ControllerFactoryAndRoles factoryAndRoles = getFactoryAndRoles( req );
		if( factoryAndRoles == null ){
			resp.sendError( HttpServletResponse.SC_NOT_FOUND );
			return;
		}

		final ControllerFactory factory = factoryAndRoles.factory;
		if( factory == null ){
			resp.sendError( HttpServletResponse.SC_NOT_FOUND );
			return;
		}

		// Session data
		final SessionResult result;
		List<Locale> locales = Collections.emptyList();
		if( mode.equals("retry") ){
			final HttpSession httpSession = req.getSession(true);
			final String ssid = toSSID( req );
			locales = getLocales( req );
			result = Session.get(httpSession, context, configuration, scheduler, true )
				.getSubSession( factory, ssid, req.getRemoteAddr(), true, req.getUserPrincipal(), req.getParameterMap(), locales, req );
		} else if( mode.equals("create") ) {
			final HttpSession httpSession = req.getSession(true);
			locales = getLocales( req );
			result = Session.get(httpSession, context, configuration, scheduler, true )
				.createSubSession( factory, req.getRemoteAddr(), req.getUserPrincipal(), req.getParameterMap(), locales, req );
		} else if( mode.equals("call") ) {
			handleCallMode( req, resp );
			return;
		} else {
			handleTouchMode( req, resp );
			return;
		}
		if( result == null ){
			resp.sendError( HttpServletResponse.SC_NOT_FOUND );
			return;
		}

		// Controller settings
		if( result.isNew() ){
			// Paths
			final String queryString = toQueryString(req.getQueryString());

			// SRoot
			final RootController rootController = result.getSubSession().getRootController();

			// Settings
			final Map<String, Object> info = new HashMap<>();
			info.put("static", rootController.getStaticInfo());
			info.put("instance", rootController.getStaticInstanceInfo());
			info.put("historical", rootController.hasHistorical());

			final Map<String, Object> path = new HashMap<>();
			path.put("content", spath);
			path.put("query", queryString);

			final Map<String, Object> title = new HashMap<>();
			title.put("separators", factoryAndRoles.title.getSeparators( locales ));

			final Map<String, Object> connect = new HashMap<>();
			connect.put("timeout", configuration.getSyncClientConnectTimeout());

			final Map<String, Object> update = new HashMap<>();
			update.put("timeout", configuration.getSyncClientUpdateTimeout());
			update.put("interval", configuration.getSyncClientUpdateInterval());

			final Map<String, Object> process = new HashMap<>();
			process.put("interval", configuration.getSyncClientProcessInterval());

			final Map<String, Object> sync = new HashMap<>();
			sync.put("connect", connect);
			sync.put("update", update);
			sync.put("process", process);

			final Map<String, Object> settings = new HashMap<>();
			settings.put("info", info);
			settings.put("ssid", result.getSubSessionId());
			settings.put("path", path);
			settings.put("keep_alive", factoryAndRoles.keepAlive);
			settings.put("retry", factoryAndRoles.retry);
			settings.put("protocols", factoryAndRoles.protocols);
			settings.put("title", title);
			settings.put("sync", sync);

			// Controller name
			final String name = rootController.getName();

			// Connect
			rootController.connect();

			// Response
			if( mode.equals("create") ){
				final RootControllerLockResult lockResult = (IS_CONTROLLER_VARIABLE_EMBEDDABLE ? rootController.lockDynamicInfo() : null);
				final SenderIdAndDynamicInfo senderIdAndDynamicInfo = (lockResult != null ? lockResult.senderIdAndDynamicInfo : null );
				try {
					resp.setStatus(HttpServletResponse.SC_OK);
					resp.setHeader("Content-Type", "text/javascript");
					final OutputStream oStream = resp.getOutputStream();
					try {
						final OutputStreamWriter osWriter = new OutputStreamWriter( oStream, "UTF-8" );
						try {
							final Writer writer = new BufferedWriter(osWriter);
							try {
								writeScript( writer, name, settings, senderIdAndDynamicInfo, false );
							} finally {
								IOUtils.closeQuietly(writer);
							}
						} finally {
							IOUtils.closeQuietly(osWriter);
						}
					} finally {
						IOUtils.closeQuietly(oStream);
					}
				} finally {
					if( lockResult != null ) {
						lockResult.lock.unlock();
					}
				}
			} else {
				resp.setStatus(HttpServletResponse.SC_OK);
				resp.setHeader("Content-Type", "application/json");
				final OutputStream oStream = resp.getOutputStream();
				try {
					final OutputStreamWriter osWriter = new OutputStreamWriter( oStream, "UTF-8" );
					try {
						final Writer writer = new BufferedWriter(osWriter);
						try {
							writeAsJson(writer, settings);
						} finally {
							IOUtils.closeQuietly(writer);
						}
					} finally {
						IOUtils.closeQuietly(osWriter);
					}
				} finally {
					IOUtils.closeQuietly(oStream);
				}
			}

			// Call @OnPostCreate methods
			rootController.postCreate();
		} else {
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.setHeader("Content-Type", "application/json");
			final OutputStream oStream = resp.getOutputStream();
			try {
				final OutputStreamWriter osWriter = new OutputStreamWriter( oStream, "UTF-8" );
				try {
					final Writer writer = new BufferedWriter( osWriter );
					try {
						writer.write("{\"ssid\":");
						writeAsJson(writer, result.getSubSessionId());
						writer.write("}");
					} finally {
						IOUtils.closeQuietly(writer);
					}
				} finally{
					IOUtils.closeQuietly(osWriter);
				}
			} finally {
				IOUtils.closeQuietly(oStream);
			}
		}
	}

	public ControllerServletHandleResult handle( final HttpServletRequest req ) throws JsonProcessingException {
		return handle( req, spath, null, Collections.<String, String[]>emptyMap() );
	}

	public ControllerServletHandleResult handle( final HttpServletRequest req, final String contentPath, final String queryString, final Map<String, String[]> parameterMap ) throws JsonProcessingException {
		// Find a factory
		final ControllerFactoryAndRoles factoryAndRoles = getFactoryAndRoles( req );
		if( factoryAndRoles == null ) return null;
		final ControllerFactory factory = factoryAndRoles.factory;
		if( factory == null ) return null;

		// Session data
		final HttpSession httpSession = req.getSession(true);
		final Session session = Session.get(httpSession, context, configuration, scheduler, true );
		final List<Locale> locales = getLocales( req );
		final SessionResult result = session.createSubSession( factory, req.getRemoteAddr(), req.getUserPrincipal(), parameterMap, locales, req );
		if( result == null ) return null;

		// Controller settings

		// SRoot
		final RootController rootController = result.getSubSession().getRootController();

		// Settings
		final Map<String, Object> info = new HashMap<>();
		info.put("static", rootController.getStaticInfo());
		info.put("instance", rootController.getStaticInstanceInfo());
		info.put("historical", rootController.hasHistorical());

		final SenderIdAndDynamicInfo senderIdAndDynamicInfo;
		if( IS_CONTROLLER_VARIABLE_EMBEDDABLE ) {
			final RootControllerLockResult lockResult = rootController.lockDynamicInfo();
			senderIdAndDynamicInfo = lockResult.senderIdAndDynamicInfo;
			lockResult.lock.unlock();
		} else {
			senderIdAndDynamicInfo = null;
		}

		final Map<String, Object> path = new HashMap<>();
		path.put("content", contentPath);
		path.put("query", toQueryString(queryString));

		final Map<String, Object> title = new HashMap<>();
		title.put("separators", factoryAndRoles.title.getSeparators( locales ));

		final Map<String, Object> connect = new HashMap<>();
		connect.put("timeout", configuration.getSyncClientConnectTimeout());

		final Map<String, Object> update = new HashMap<>();
		update.put("timeout", configuration.getSyncClientUpdateTimeout());
		update.put("interval", configuration.getSyncClientUpdateInterval());

		final Map<String, Object> process = new HashMap<>();
		process.put("interval", configuration.getSyncClientProcessInterval());

		final Map<String, Object> sync = new HashMap<>();
		sync.put("connect", connect);
		sync.put("update", update);
		sync.put("process", process);

		final Map<String, Object> settings = new HashMap<>();
		settings.put("info", info);
		settings.put("ssid", result.getSubSessionId());
		settings.put("path", path);
		settings.put("keep_alive", factoryAndRoles.keepAlive);
		settings.put("retry", factoryAndRoles.retry);
		settings.put("protocols", factoryAndRoles.protocols);
		settings.put("title", title);
		settings.put("sync", sync);

		// Controller name
		final String name = rootController.getName();

		// Connect
		rootController.connect();

		//
		final String script = toScript( name, settings, senderIdAndDynamicInfo );

		// Call @OnPostCreate methods
		rootController.postCreate();

		return new ControllerServletHandleResult( script, result.getSubSession() );
	}
}
