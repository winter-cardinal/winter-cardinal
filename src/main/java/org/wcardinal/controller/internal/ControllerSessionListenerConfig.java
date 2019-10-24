/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

@Component
public class ControllerSessionListenerConfig implements BeanDefinitionRegistryPostProcessor {
	final Logger logger = LoggerFactory.getLogger(ControllerSessionListenerConfig.class);

	static final boolean SPRING_SESSION_EXISTS
		= ClassUtils.isPresent("org.springframework.session.events.SessionDestroyedEvent", ControllerSessionListenerConfig.class.getClassLoader());

	static final boolean SPRING_SESSION_HAS_LISTENER_ADAPTER
		= ClassUtils.isPresent("org.springframework.session.web.http.SessionEventHttpSessionListenerAdapter", ControllerSessionListenerConfig.class.getClassLoader());

	static final boolean SPRING_SESSION_HAS_CREATED_EVENT
		= ClassUtils.isPresent("org.springframework.session.events.SessionCreatedEvent", ControllerSessionListenerConfig.class.getClassLoader());

	@Override
	public void postProcessBeanDefinitionRegistry( final BeanDefinitionRegistry registry ) throws BeansException {
		if( SPRING_SESSION_EXISTS ) {
			if( SPRING_SESSION_HAS_LISTENER_ADAPTER ){
				registerControllerSessionListener( registry );
			} else {
				if( SPRING_SESSION_HAS_CREATED_EVENT ) {
					registerControllerSessionListenerForSpringSession10xCreated( registry );
				}

				registerControllerSessionListenerForSpringSession10xDestroyed( registry );
			}
		} else {
			registerControllerSessionListener( registry );
		}
	}

	void registerControllerSessionListener( final BeanDefinitionRegistry registry ){
		logger.debug( "Use ControllerSessionListener as session listener" );
		final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ControllerSessionListener.class);
		registry.registerBeanDefinition("controllerSessionListener", builder.getBeanDefinition());
	}

	void registerControllerSessionListenerForSpringSession10xCreated( final BeanDefinitionRegistry registry ){
		logger.debug( "Use controllerSessionListenerForSpringSession10xCreated as session listener" );
		final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ControllerSessionListenerForSpringSession10xCreated.class);
		registry.registerBeanDefinition("controllerSessionListenerForSpringSession10xCreated", builder.getBeanDefinition());
	}

	void registerControllerSessionListenerForSpringSession10xDestroyed( final BeanDefinitionRegistry registry ){
		logger.debug( "Use controllerSessionListenerForSpringSession10xDestroyed as session listener" );
		final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ControllerSessionListenerForSpringSession10xDestroyed.class);
		registry.registerBeanDefinition("controllerSessionListenerForSpringSession10xDestroyed", builder.getBeanDefinition());
	}

	@Override
	public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
	}
}
