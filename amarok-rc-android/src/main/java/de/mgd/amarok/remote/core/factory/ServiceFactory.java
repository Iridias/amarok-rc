package de.mgd.amarok.remote.core.factory;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;

import de.mgd.amarok.remote.service.CollectionService;
import de.mgd.amarok.remote.service.PlayerService;
import de.mgd.amarok.remote.service.PlaylistService;
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

	public static void invalidate() {
		playerService = null;
		playlistService = null;
		collectionService = null;
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
