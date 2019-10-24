/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package test;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;

import test.annotation.Test;

/**
 * Testing utility for the puppeteer.
 * This class assumes the node.js and the puppeteer is installed before testing.
 */
public class PuppeteerTest {
	private final static Log logger = LogFactory.getLog(PuppeteerTest.class);
	private static ConfigurableApplicationContext context = null;
	private final static String NG_WORD = "7vhu64qkpun";

	/**
	 * Prepares for tests.
	 *
	 * @param targetClass the Spring boot's application class
	 */
	public static void start( final Class<?> targetClass ) {
		start( targetClass, new String[]{} );
	}

	public static void prepare(){}

	/**
	 * Prepares for tests.
	 *
	 * @param targetClass the Spring boot's application class
	 * @param args the runtime arguments of the Spring boot application.
	 */
	public static void start( final Class<?> targetClass, final String[] args ) {
		prepare();
		context = SpringApplication.run( targetClass, args );
	}

	/**
	 * Shuts down tests.
	 */
	public static void end(){
		if( context != null ) {
			SpringApplication.exit(context);
			context = null;
		}
	}

	/**
	 * Tests the specified URL at the sever with the Puppeteer.
	 *
	 * @param url URL to be tested
	 */
	public static void test( final String url ) {
		// Create a process
		final ProcessBuilder builder = new ProcessBuilder(
			"node", "./src/test/resources/static/test/puppeteer-runner.js", url
		);
		builder.redirectErrorStream(true);
		final Process process;
		try {
			process = builder.start();
		} catch (final IOException e) {
			logger.info( "Failed to create a Puppeteer process", e );
			return;
		}

		// Read lines
		final BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
		try {
			while( true ) {
				final String line = br.readLine();
				if (line == null) break;
				if( line.startsWith( NG_WORD ) ) {
					throw new AssertionError( line.substring( NG_WORD.length() ) );
				} else {
					logger.info( line );
				}
			}
		} catch (final IOException e) {
			logger.error("Failed to read lines", e);
		} finally {
			try {
				br.close();
			} catch (final IOException e) {
				logger.error("Failed to close a stream", e);
			}
		}

		// Wait
		while( true ) {
			try {
				process.waitFor();
				return;
			} catch (final InterruptedException e) {
				// DO NOTHING
			}
		}
	}

	/**
	 * Finds resources matched with the specified patterns.
	 *
	 * <blockquote><pre>
	 * findResources( "classpath*:static/*.html" )
	 * </pre></blockquote>
	 *
	 * @param patterns the patterns of resource paths
	 * @see org.springframework.core.io.support.PathMatchingResourcePatternResolver
	 * @return found resources
	 */
	public static List<Resource> findResources( final String... patterns ){
		final List<Resource> result = new ArrayList<>();
		try {
			final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(PuppeteerTest.class.getClassLoader());
			for( final String pattern: patterns ) {
				result.addAll( Arrays.asList(resolver.getResources( pattern )) );
			}
		} catch ( final Exception e ){
			// DO NOTHING
		}
		return result;
	}

	/**
	 * Finds all paths matched with the specified pattern under the specified directory in the classpath.
	 * For instance, findSubPath( "static/", "basics/*.html" ) returns all matched paths relative to the directory "static/"
	 * like "basics/index.html", "basics/foo.html", and so on.
	 *
	 * @param directory the directory to be searched in the classpath
	 * @param pattern the pattern to be tested
	 * @return found paths matched with the specified pattern
	 */
	public static Set<String> findPaths( final String directory, final String pattern ){
		final Set<String> result = new HashSet<>();
		final List<Resource> resources = findResources( "classpath*:"+directory+pattern );
		try {
			for( final Resource resource: resources ){
				final String path = resource.getURL().toExternalForm();
				final int index = path.indexOf( directory );
				if( 0 <= index ) {
					final String subpath = path.substring(index+directory.length());
					result.add( subpath );
				}
			}
		} catch (final Exception e) {
			// DO NOTHING
		}
		return result;
	}

	public static List<String> findTestMethods( final Class<?> clazz ){
		final List<String> result = new ArrayList<>();
		ReflectionUtils.doWithMethods(clazz, new MethodCallback(){
			@Override
			public void doWith(Method method) throws IllegalArgumentException {
				if( AnnotationUtils.findAnnotation(method, Test.class) != null ) {
					result.add( method.getName() );
				}
			}
		});
		return result;
	}
}
