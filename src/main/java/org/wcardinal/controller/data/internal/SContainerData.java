/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Map;

import org.springframework.core.ResolvableType;

import org.wcardinal.controller.internal.info.DynamicDataObject;
import org.wcardinal.controller.internal.info.SetDynamicDataMap;
import org.wcardinal.util.thread.AutoCloseableReentrantLock;

public abstract class SContainerData<V, P extends SPatch, S extends SPatches<V, P>> {
	final SContainer<V, SPatchesPacked<V, P>> scontainer;
	long authorizedRevision;
	final S patches;

	final SLongImpl $ar;
	final SIntegerImpl $af;
	final SClassImpl<Object> $a;
	final SLongImpl $br;
	final SIntegerImpl $bf;
	final SClassImpl<SPatchesPacked<V, P>> $b;

	final String $arn;
	final String $afn;
	final String $an;
	final String $brn;
	final String $bfn;
	final String $bn;

	public SContainerData( final SContainer<V, SPatchesPacked<V, P>> scontainer, final Object origin, final ResolvableType $bType ) {
		this.scontainer = scontainer;
		this.authorizedRevision = (0 < scontainer.getRevision() ? scontainer.getRevision() : -1);
		this.patches = newPatches();

		final String name = scontainer.getName();
		$arn = SContainers.toId( name, "ar" );
		$afn = SContainers.toId( name, "af" );
		$an  = SContainers.toId( name, "a"  );
		$brn = SContainers.toId( name, "br" );
		$bfn = SContainers.toId( name, "bf" );
		$bn  = SContainers.toId( name, "b"  );

		final boolean isInitialized = scontainer.isInitialized();
		final SContainerParent parent = scontainer.getParent();
		final AutoCloseableReentrantLock lock = scontainer.getLock();

		$ar = new SLongImpl();
		$ar.setParent( parent );
		$ar.setLock( lock );
		$ar.setNonNull( true );
		$ar.setReadOnly( true );
		parent.put( origin, $arn, $ar );
		if( isInitialized != true ) {
			$ar.uninitialize();
		}

		$af = new SIntegerImpl();
		$af.setParent( parent );
		$af.setLock( lock );
		$af.setNonNull( true );
		$af.setReadOnly( true );
		parent.put( origin, $afn, $af );
		if( isInitialized != true ) {
			$af.uninitialize();
		}

		$a = new SClassImpl<Object>();
		$a.setParent( parent );
		$a.setLock( lock );
		$a.setNonNull( false );
		$a.setReadOnly( true );
		$a.setGenericType( ResolvableType.forClass( Object.class ) );
		$a.setSoft( true );
		parent.put( origin, $an, $a );
		if( isInitialized != true ) {
			$a.uninitialize();
		}

		$br = new SLongImpl();
		$br.setParent( parent );
		$br.setLock( lock );
		$br.setNonNull( true );
		$br.setReadOnly( false );
		parent.put( origin, $brn, $br );
		if( isInitialized != true ) {
			$br.uninitialize();
		}

		$bf = new SIntegerImpl();
		$bf.setParent( parent );
		$bf.setLock( lock );
		$bf.setNonNull( true );
		$bf.setReadOnly( false );
		parent.put( origin, $bfn, $bf );
		if( isInitialized != true ) {
			$bf.uninitialize();
		}

		$b = new SClassImpl<SPatchesPacked<V, P>>();
		$b.setParent( parent );
		$b.setLock( lock );
		$b.setNonNull( false );
		$b.setReadOnly( false );
		$b.setLoose( true );
		$b.setGenericType( $bType );
		parent.put( origin, $bn, $b );
		if( isInitialized != true ) {
			$b.uninitialize();
		}
	}

	abstract S newPatches();

	public void handle( final SetDynamicDataMap map ) {
		if( map.nameToSChange != null ) {
			if( map.nameToSChange.remove( $brn ) != null ) {
				handleBr( map );
			}

			if( map.nameToSChange.remove( $bfn ) != null ) {
				handleBf( map );
			}

			if( map.nameToSChange.remove( $bn ) != null ) {
				handleB( map );
			}
		}
	}

	void handleBr( final SetDynamicDataMap map ) {
		final long brevision = $br.get();
		if( brevision <= scontainer.getRevision() ) {
			patches.compact( brevision );
			authorizedRevision = Math.max(authorizedRevision, brevision);
			if( brevision < scontainer.getRevision() ) {
				$a.set( $a );
			} else if( patches.isEmpty() ) {
				scontainer.compact();
			}
		}
	}

	void handleBf( final SetDynamicDataMap map ) {
		final int bflag = $bf.get();
		if( bflag != 0 ) {
			$a.set( $a );
		}
	}

	void handleB( final SetDynamicDataMap map ) {
		final SPatchesPacked<V, P> bpatches = $b.get();
		if( bpatches == null || bpatches.isEmpty() ) return;

		if( bpatches.isApplicable( scontainer.getRevision(), patches.isEmpty(), scontainer.isSoft() ) ) {
			if( scontainer.getRevision() < bpatches.getEndRevision() ) {
				if( bpatches.isEmpty() ) {
					patches.clear();
					scontainer.setRevision( bpatches.getEndRevision() );
					authorizedRevision = Math.max(authorizedRevision, bpatches.getEndRevision());
					scontainer.initialize( this );
					scontainer.compact();
					$ar.set( bpatches.getEndRevision() );
				} else {
					final SChange schange = bpatches.apply( scontainer.getRevision(), scontainer.getValue() );
					scontainer.onPatches( this, bpatches );
					patches.clear();
					scontainer.setRevision( bpatches.getEndRevision() );
					authorizedRevision = Math.max(authorizedRevision, bpatches.getEndRevision());
					scontainer.initialize( this );
					scontainer.compact();

					$ar.set( bpatches.getEndRevision() );

					map.put( scontainer.getName(), schange );
				}
			}
			$af.compareAndSet( 1, 0 );
		} else {
			if( scontainer.getRevision() < bpatches.getEndRevision() ) {
				$af.compareAndSet( 0, 1 );
				map.put( $afn, true );
			}
		}
	}

	public void handle( final Object origin, final Map<String, SData> nameToSData, final Map<String, DynamicDataObject> nameToData, final long senderId ) {
		final DynamicDataObject dataA = nameToData.get( $an );
		if( dataA != null ) {
			// $bf
			final boolean forceReset = ($bf.get() != 0);
			if( forceReset ) {
				// Need to set to zero because, without this, a server sends multiple 'reset' requests,
				// which are not necessary. If data in a container are large in their size, sending
				// many 'reset' requests increases a network traffic and slows down servers and browsers.
				// Even if browsers lost `reset` requests, they will eventually set the $bf to 1 again
				// because they can not accept 'map' patches. Please note that $bf is not locked by
				// a sender who is identified by the `senderId`. This is to tell browsers that the $bf
				// is set to 0 at the server side as soon as possible. This increases a traffic a little
				// bit. But enables browsers to detect the changes of the $bf quickly.
				$bf.value = 0;
				$bf.revision += 1;
				final SData $bfSData = nameToSData.get( $bfn );
				nameToData.put( $bfn, new DynamicDataObject( $bf.getRevision(), $bf.getType(), $bf.pack( $bfSData ) ) );
				$bfSData.lock(senderId, $bf.revision);
			}

			// $a
			final SPatchesPacked<V, P> packed = patches.pack( authorizedRevision, forceReset, scontainer.getRevision(), scontainer.getValue(), scontainer.isSoft() );
			nameToData.put( $an, new DynamicDataObject( dataA.revision, dataA.type, packed ) );
		}
	}

	void onClear() {
		patches.clear( scontainer.getRevision() );
		$a.set( $a );
	}

	void onInitialize() {
		$a.toDirty();
	}

	void onPatches( final SPatchesPacked<V, P> bpatches ) {
		patches.apply( scontainer.getRevision(), bpatches );
		$a.set( $a );
	}
}
