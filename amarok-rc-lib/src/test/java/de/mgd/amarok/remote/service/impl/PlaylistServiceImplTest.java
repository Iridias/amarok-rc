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
