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
package de.mgd.amarok.remote.service.internal.impl;

import static org.junit.Assert.*;

import org.apache.commons.lang.StringUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class RemoteInvokationServiceImplTest {

	private RemoteInvokationServiceImpl service;
	private ClientConnectionManager ccm;
	
	@Before
	public void setUp() {
		//ccm = new PoolingClientConnectionManager();
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		
		ccm = new ThreadSafeClientConnManager(new BasicHttpParams(), schemeRegistry);
		service = new RemoteInvokationServiceImpl();
		service.setClientConnectionManager(ccm);
	}
	
	@Test
	public void testGetResponseAsString() {
		String result = service.getResponseAsString("localhost", 8484, "getServerVersion/");
		assertNotNull(result);
		assertEquals("6", result);
	}
	
	@Test
	public void testGetResponseAsLong() {
		long result = service.getResponseAsLong("localhost", 8484, "getServerVersion/");
		assertNotNull(result);
		assertEquals(6, result);
	}
	
	@Test
	@Ignore
	public void testGetResponseAsStringForNoResponse() {
		String result = service.getResponseAsString("localhost", 8484, "cmdPlayPause/");
		assertNotNull(result);
		assertTrue(StringUtils.isEmpty(result));
	}
	
	// FIXME: setup webunit test later
}
