/*
    This file is part of Amarok RC.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package de.mgd.amarok.remote.core.factory;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;

import de.mgd.amarok.remote.service.AmarokService;
import de.mgd.amarok.remote.service.CollectionService;
import de.mgd.amarok.remote.service.PlayerService;
import de.mgd.amarok.remote.service.PlaylistService;
import de.mgd.amarok.remote.service.impl.AmarokServiceImpl;
import de.mgd.amarok.remote.service.impl.CollectionServiceImpl;
import de.mgd.amarok.remote.service.impl.PlayerServiceImpl;
import de.mgd.amarok.remote.service.impl.PlaylistServiceImpl;
import de.mgd.amarok.remote.service.internal.HelperService;
import de.mgd.amarok.remote.service.internal.impl.HelperServiceImpl;
import de.mgd.amarok.remote.service.internal.impl.RemoteInvokationServiceImpl;
import de.mgd.amarok.remote.core.AppEngine;

public class ServiceFactory {

	private static HelperService helperService;
	private static RemoteInvokationServiceImpl remoteService;
	private static ClientConnectionManager ccm;
	
	private static PlayerServiceImpl playerService;
	private static PlaylistServiceImpl playlistService;
	private static CollectionServiceImpl collectionService;
	private static AmarokServiceImpl amarokService;

	public static void invalidate() {
		playerService = null;
		playlistService = null;
		collectionService = null;
		amarokService = null;
	}

	public static void init() {
		if(remoteService != null) {
			return; // already initialized
		}
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		
		helperService = new HelperServiceImpl();
		ccm = new ThreadSafeClientConnManager(new BasicHttpParams(), schemeRegistry);
		
		remoteService = new RemoteInvokationServiceImpl();
		remoteService.setClientConnectionManager(ccm);
	}

	public static AmarokService getAmarokService() {
		if(amarokService != null) {
			return amarokService;
		}

		amarokService = new AmarokServiceImpl();
		amarokService.setHelperService(helperService);
		amarokService.setRemoteService(remoteService);
		amarokService.setHost(AppEngine.remoteHost());
		amarokService.setPort(AppEngine.remotePort());

		return amarokService;
	}

	public static PlayerService getPlayerService() {
		if(playerService != null) {
			return playerService;
		}
		
		playerService = new PlayerServiceImpl();
		playerService.setHelperService(helperService);
		playerService.setRemoteService(remoteService);
		playerService.setHost(AppEngine.remoteHost());
		playerService.setPort(AppEngine.remotePort());
		
		return playerService;
	}
	
	public static PlaylistService getPlaylistService() {
		if(playlistService != null) {
			return playlistService;
		}
		
		playlistService = new PlaylistServiceImpl();
		playlistService.setHelperService(helperService);
		playlistService.setRemoteService(remoteService);
		playlistService.setHost(AppEngine.remoteHost());
		playlistService.setPort(AppEngine.remotePort());
		
		return playlistService;
	}
	
	public static CollectionService getCollectionService() {
		if(collectionService != null) {
			return collectionService;
		}
		
		collectionService = new CollectionServiceImpl();
		collectionService.setHelperService(helperService);
		collectionService.setRemoteService(remoteService);
		collectionService.setHost(AppEngine.remoteHost());
		collectionService.setPort(AppEngine.remotePort());
		
		return collectionService;
	}
	
}
