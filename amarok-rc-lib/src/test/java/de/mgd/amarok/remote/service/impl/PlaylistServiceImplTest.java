package de.mgd.amarok.remote.service.impl;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mgd.amarok.remote.model.Track;
import de.mgd.amarok.remote.service.internal.HelperService;
import de.mgd.amarok.remote.service.internal.impl.HelperServiceImpl;
import de.mgd.amarok.remote.service.internal.impl.RemoteInvokationServiceImpl;

public class PlaylistServiceImplTest {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private PlaylistServiceImpl service;
	
	private HelperService helperService;
	private RemoteInvokationServiceImpl remoteService;
	private ClientConnectionManager ccm;
	
	private String host = "localhost";
	private int port = 8484;
	
	@Before
	public void setUp() {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		
		ccm = new ThreadSafeClientConnManager(new BasicHttpParams(), schemeRegistry);
		remoteService = new RemoteInvokationServiceImpl();
		remoteService.setClientConnectionManager(ccm);
		helperService = new HelperServiceImpl();
		
		service = new PlaylistServiceImpl();
		service.setRemoteService(remoteService);
		service.setHelperService(helperService);
		service.setHost(host);
		service.setPort(port);
	}
	
	@Test
	public void testListAllTracks() {
		List<Track> result = service.listAllTracks();
		
		assertNotNull(result);
		assertFalse(result.isEmpty());
		
		for(Track t : result) {
			assertNotNull(t);
			log.info("Found Track {}", t);
		}
	}
	
}
