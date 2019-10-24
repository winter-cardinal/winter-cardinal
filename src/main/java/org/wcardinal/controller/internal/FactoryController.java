/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.core.ResolvableType;

import com.fasterxml.jackson.databind.node.ArrayNode;

import org.wcardinal.controller.data.SList.Update;
import org.wcardinal.controller.data.internal.SChange;
import org.wcardinal.controller.data.internal.SClassImpl;
import org.wcardinal.controller.data.internal.SListImpl;
import org.wcardinal.controller.internal.info.SetDynamicDataMap;
import org.wcardinal.controller.internal.info.DynamicDataJsonNode;
import org.wcardinal.controller.internal.info.StaticInfo;
import org.wcardinal.util.Reference;
import org.wcardinal.util.thread.AutoCloseableReentrantLock;

public abstract class FactoryController<T extends Controller> extends ComponentController implements ControllerCreator<T> {
	final ControllerFactory childFactory;

	final static String LIST_ID = "$d";
	final static String STATIC_INFO_ID = "$si";
	SListImpl<FactoryData> list;
	SClassImpl<StaticInfo> staticInfo;
	final ControllerCreatorAware<T> aware;
	final AtomicBoolean isPostCreated;

	@SuppressWarnings("unchecked")
	public FactoryController(
			final String name, final Controller parent,
			final ControllerFactory factory, final Object instance,
			final ControllerBaggage baggage,
			final AutoCloseableReentrantLock lock,
			final TaskInternalQueue tasks,
			final ControllerFactory childFactory ) {
		super(name, parent, factory, instance, baggage, null, lock, tasks);

		this.childFactory = childFactory;
		staticInfo.set(childFactory.staticInfo);

		aware = (ControllerCreatorAware<T>) instance;
		aware.setControllerCreator( this );

		isPostCreated = new AtomicBoolean( false );
	}

	@Override
	void createAdditionals() {
		list = new SListImpl<FactoryData>();
		list.init( LIST_ID, this, lock, ResolvableType.forClass(FactoryData.class), Properties.empty() );
		scontainers.add(list);

		staticInfo = new SClassImpl<StaticInfo>();
		staticInfo.setParent(this);
		staticInfo.setLock(lock);
		staticInfo.setReadOnly( true );
		staticInfo.setGenericType(ResolvableType.forClass(StaticInfo.class));
		put( STATIC_INFO_ID, staticInfo );

		super.createAdditionals();
	}

	@Override
	public List<FactoryData> getFactoryData(){
		return list;
	}

	@Override
	SetDynamicDataMap setSContainerDynamicInfoData( final Controller origin, final Map<String, DynamicDataJsonNode> nameToData, final boolean allowCompaction, final Reference<Boolean> hasNonSContainer ) {
		final SetDynamicDataMap result = super.setSContainerDynamicInfoData( origin, nameToData, allowCompaction, hasNonSContainer );

		if( result != null && result.nameToSChange != null ) {
			final SChange schange = result.nameToSChange.remove( LIST_ID );
			if( schange != null ) {
				final Object[] parameters = schange.toParameters();

				@SuppressWarnings("unchecked")
				final Map<String, FactoryData> added = (Map<String, FactoryData>) parameters[ 0 ];

				@SuppressWarnings("unchecked")
				final Map<String, FactoryData> removed = (Map<String, FactoryData>) parameters[ 1 ];

				@SuppressWarnings("unchecked")
				final Map<String, Update<FactoryData>> updated = (Map<String, Update<FactoryData>>) parameters[ 2 ];

				for( final FactoryData factoryData: removed.values() ) {
					aware.destroyByData( factoryData );
				}

				for( final FactoryData factoryData: added.values() ) {
					aware.createAsRequested( factoryData );
				}

				for( final Update<FactoryData> update: updated.values() ) {
					aware.destroyByData( update.getOldValue() );
					aware.createAsRequested( update.getNewValue() );
				}
			}
		}

		return result;
	}

	@Override
	public void postCreate( final Object... parameters ) {
		isPostCreated.set( true );

		for( final FactoryData factoryData: list ) {
			final Controller controller = controllerNameToController.get( factoryData.name );
			if( controller != null ) {
				if( factoryData.arguments != null ) {
					final Object[] arguments = factoryData.arguments;
					factoryData.arguments = null;
					controller.postCreate( arguments );
				} else {
					controller.postCreate();
				}
			}
		}

		onPostCreate( parameters );
	}

	@Override
	public boolean isPostCreated() {
		return isPostCreated.get();
	}

	@Override
	public void initDynamic( final String name, final Controller controller, final ArrayNode args, final Object[] arguments ){
		list.add( new FactoryData( name, getPartialDynamicInfo( this, controller ), args, arguments ) );
	}
}
