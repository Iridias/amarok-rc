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
		assertEquals("5", result);
	}
	
	@Test
	public void testGetResponseAsLong() {
		long result = service.getResponseAsLong("localhost", 8484, "getServerVersion/");
		assertNotNull(result);
		assertEquals(5, result);
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
