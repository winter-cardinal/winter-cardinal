/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

import org.wcardinal.controller.internal.info.SetDynamicInfo;
import org.wcardinal.controller.data.SInteger;
import org.wcardinal.controller.data.internal.SBase;
import org.wcardinal.controller.data.internal.SData;
import org.wcardinal.controller.internal.info.AckInfo;
import org.wcardinal.controller.internal.info.DynamicInfo;
import org.wcardinal.controller.internal.info.SenderIdAndDynamicInfo;
import org.wcardinal.controller.internal.info.ReceivedDynamicInfo;
import org.wcardinal.controller.internal.info.ReceivedSenderIdAndDynamicInfo;
import org.wcardinal.controller.internal.info.RejectedSetDynamicInfo;
import org.wcardinal.controller.internal.info.RejectionInfo;
import org.wcardinal.controller.internal.info.SChangeInfo;
import org.wcardinal.io.message.ReceivedRequestMessage;
import org.wcardinal.io.message.RequestMessageAck;
import org.wcardinal.io.message.RequestMessageConnect;
import org.wcardinal.io.message.RequestMessageUpdate;
import org.wcardinal.util.Reference;
import org.wcardinal.util.json.Json;
import org.wcardinal.util.thread.AutoCloseableReentrantLock;
import org.wcardinal.util.thread.Unlocker;

public class RootController extends Controller implements Runnable {
	static final Logger _logger = LoggerFactory.getLogger(RootController.class);

	final long SYNC_CONNECT_TIMEOUT;
	final long SYNC_UPDATE_TIMEOUT;
	final long SYNC_UPDATE_INTERVAL;
	final long SYNC_CLIENT_UPDATE_INTERVAL;

	final AtomicLong lastOnUpdateTime = new AtomicLong(0);
	final AtomicLong senderId = new AtomicLong(-1);
	ControllerState state = null;

	final RootControllerOnUpdateRunner onUpdateRunner = new RootControllerOnUpdateRunner( this );
	final RootControllerArgument argument = new RootControllerArgument( this );

	final RejectedSetDynamicInfos rejectedInfos = new RejectedSetDynamicInfos();

	public RootController( final String name, final ControllerFactory factory, final Object instance,
			final ControllerBaggage baggage ){
		super( name, null, factory, instance, baggage, null, new AutoCloseableReentrantLock(), new TaskInternalQueue() );

		SYNC_CONNECT_TIMEOUT = baggage.configuration.getSyncConnectTimeout();
		SYNC_UPDATE_TIMEOUT = baggage.configuration.getSyncUpdateTimeout();
		SYNC_UPDATE_INTERVAL = baggage.configuration.getSyncUpdateInterval();
		SYNC_CLIENT_UPDATE_INTERVAL = baggage.configuration.getSyncClientUpdateInterval();

		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			state = ControllerState.INITIALIZING;
		}
	}

	boolean setState( final ControllerState current, final ControllerState next ){
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			boolean result = false;
			if( state == current ){
				state = next;
				result = true;
			}
			return result;
		}
	}

	boolean checkState( final ControllerState current ){
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			return ( state == current );
		}
	}

	@Override
	public void unlock() {
		int count = lock.getHoldCount();
		if( count <= 1 ){
			// The last unlock
			onUnlock();
		} else {
			// Otherwise
			lock.unlock();
		}
	}

	@Override
	public void unlock( final Controller origin ) {
		unlock();
	}

	@Override
	public void onUnlock(){
		if( isChanged ){
			isChanged = false;
			lock.unlock();
			if( onUpdateRunner != null ) {
				baggage.scheduler.execute( onUpdateRunner );
			}
		} else {
			lock.unlock();
		}
	}

	public void connect(){
		if( setState( ControllerState.INITIALIZED, ControllerState.CONNECTING ) != true ) return;
		_connect();
	}

	public void reconnect(){
		if( checkState( ControllerState.CONNECTING ) != true ) return;
		_connect();
	}

	void _connect(){
		baggage.messageSender.send( new RequestMessageConnect( argument ) );
		baggage.scheduler.schedule( new Runnable(){
			@Override
			public void run() {
				reconnect();
			}

		}, SYNC_CONNECT_TIMEOUT );
	}

	void toConnected(){
		if( setState( ControllerState.CONNECTING, ControllerState.CONNECTED ) != true ) return;
		baggage.scheduler.execute(RootController.this);
	}

	@Override
	public void update(){
		isChanged = true;
	}

	void onUpdate(){
		if( state != null ) {
			lastOnUpdateTime.set( System.currentTimeMillis() );
			if( hasDynamicInfo() ) {
				baggage.messageSender.send( new RequestMessageUpdate( argument ) );
				lastOnUpdateTime.set( System.currentTimeMillis() );
			}
		}
	}

	public void handleAuthorizeMessage( final ReceivedRequestMessage message, final boolean allowCompaction ){
		final JsonNode arguments = message.getArguments();
		if( 0 < arguments.size() ) {
			setAuthorizedRevision( Json.convert( arguments.get( 0 ), AckInfo.class ), allowCompaction );
		}
	}

	public void handleConnectAcceptMessage( final ReceivedRequestMessage message, final boolean allowCompaction ){
		handleAuthorizeMessage( message, allowCompaction );
		toConnected();
	}

	public void handleUpdateMessage( final ReceivedRequestMessage message, final boolean allowCompaction ){
		final JsonNode arguments = message.getArguments();
		if( 0 < arguments.size() ) {
			final ReceivedSenderIdAndDynamicInfo info = Json.convert( arguments.get( 0 ), ReceivedSenderIdAndDynamicInfo.class );
			baggage.messageSender.send( new RequestMessageAck( new AckInfo( info.senderId ) ) );
			setDynamicInfo( info.dynamicInfo, allowCompaction );
		}
	}

	public void setAuthorizedRevision( final AckInfo info, final boolean allowCompaction ){
		super.setAuthorizedRevision( null, info, allowCompaction );
	}

	static boolean isApplicable( final Controller origin, final Controller target, final RejectionInfo info ) {
		// DATA
		if( info.nameToData != null ) {
			final Map<String, SData> nameToSData = target.originToSData.get( origin );
			if( nameToSData != null ) {
				for( final String name: info.nameToData.keySet() ) {
					final SData sdata = nameToSData.get( name );
					if( sdata == null ) continue;

					final SBase<?> sbase = sdata.get();
					if( sbase instanceof SInteger ) {
						final SInteger sinteger = (SInteger) sbase;
						if( sinteger.get() != 0 ) return false;
					}
				}
			}
		}

		// INFO
		if( info.nameToInfo != null ) {
			for( final Map.Entry<String, RejectionInfo> entry: info.nameToInfo.entrySet() ) {
				final Controller controller = target.controllerNameToController.get( entry.getKey() );
				if( isApplicable( target, controller, entry.getValue() ) != true ) {
					return false;
				}
			}
		}

		return true;
	}

	public void setDynamicInfo( final ReceivedDynamicInfo receivedInfo, final boolean allowCompaction ){
		try( final Unlocker unlocker = lock() ) {
			boolean isRejected = false;
			final Reference<Boolean> hasNonSContainer = new Reference<Boolean>( false );
			final SetDynamicInfo scontainerInfo = setSContainerDynamicInfo( null, receivedInfo, allowCompaction, hasNonSContainer );
			if( scontainerInfo != null && scontainerInfo.rejection != null && hasNonSContainer.get() ) {
				rejectedInfos.add( new RejectedSetDynamicInfo( scontainerInfo, receivedInfo ) );
				isRejected = true;
			}

			if( isRejected != true ) {
				List<SChangeInfo> schanges = null;
				if( rejectedInfos.isEmpty() != true ) {
					schanges = new ArrayList<>();
					final Iterator<RejectedSetDynamicInfo> iterator = rejectedInfos.iterator();
					while( iterator.hasNext() ) {
						final RejectedSetDynamicInfo rejectedInfo = iterator.next();
						if( isApplicable( null, this, rejectedInfo.info.rejection ) ) {
							final SetDynamicInfo info = setDynamicInfo( null, rejectedInfo.receivedInfo, allowCompaction );
							if( info != null ) {
								schanges.add( info.schange );
							}
							iterator.remove();
						}
					}
				}

				final SetDynamicInfo info = setDynamicInfo( null, receivedInfo, allowCompaction );
				if( schanges != null ) {
					for( final SChangeInfo schange: schanges ) {
						triggerOnChange( null, schange );
					}
				}
				if( scontainerInfo != null ) {
					triggerOnChange( null, scontainerInfo.schange );
				}
				if( info != null ) {
					triggerOnChange( null, info.schange );
				}
			}
		}
	}

	@Override
	public SenderIdAndDynamicInfo getPartialDynamicInfo( final Controller origin, final Controller target ) {
		final long senderId = this.senderId.incrementAndGet();
		final long now = System.currentTimeMillis();
		final DynamicInfo info = target.lockDynamicInfo( origin, senderId, now, SYNC_UPDATE_TIMEOUT );
		if( info != null ) {
			target.unlockDynamicInfo( origin, senderId, now );
			return new SenderIdAndDynamicInfo( senderId, info );
		} else {
			return null;
		}
	}

	public RootControllerLockResult lockDynamicInfo() {
		final long senderId = this.senderId.incrementAndGet();
		final DynamicInfo info = super.lockDynamicInfo( (Controller) null, senderId, System.currentTimeMillis(), SYNC_UPDATE_TIMEOUT );
		if( info != null ) {
			return new RootControllerLockResult(
				new RootControllerLockImpl( senderId, this ),
				new SenderIdAndDynamicInfo( senderId, info )
			);
		} else {
			return null;
		}
	}

	public void unlockDynamicInfo( final long senderId ) {
		super.unlockDynamicInfo( (Controller) null, senderId, System.currentTimeMillis() );
	}

	public boolean hasDynamicInfo() {
		return super.hasDynamicInfo( (Controller) null, System.currentTimeMillis(), SYNC_UPDATE_TIMEOUT );
	}

	public void destroy(){
		try( final Unlocker unlocker = lock() ) {
			if( state != ControllerState.DESTROYING && state != ControllerState.DESTROYED ) {
				state = ControllerState.DESTROYING;
				super.onDestroy();
				super.destroy( null );
				state = ControllerState.DESTROYED;
			}
		}
	}

	@Override
	public void create( final Object... parameters ){
		try( final Unlocker unlocker = lock() ) {
			if( state == ControllerState.INITIALIZING ) {
				super.create( parameters );
				super.checkVisibility();
				state = ControllerState.INITIALIZED;
			}
		}
	}

	@Override
	public void postCreate( final Object... parameters ){
		baggage.scheduler.execute(new Runnable() {
			@Override
			public void run() {
				try( final Unlocker unlocker = lock() ) {
					RootController.super.postCreate( parameters );
				}
			}
		});
	}

	@Override
	public void run() {
		if( checkState( ControllerState.CONNECTED ) != true ) return;

		// Cleanup rejections
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			rejectedInfos.cleanup( SYNC_CLIENT_UPDATE_INTERVAL );
		}

		// Send update requests
		final long elapsedTime = System.currentTimeMillis() - lastOnUpdateTime.get();
		if( SYNC_UPDATE_INTERVAL <= elapsedTime ) {
			onUpdate();
		}
		final long delay = Math.max(0, lastOnUpdateTime.get() + SYNC_UPDATE_INTERVAL - System.currentTimeMillis());
		baggage.scheduler.schedule( this, delay );
	}
}
