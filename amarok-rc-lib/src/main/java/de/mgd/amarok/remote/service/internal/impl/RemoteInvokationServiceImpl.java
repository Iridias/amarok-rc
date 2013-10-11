package de.mgd.amarok.remote.service.internal.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mgd.amarok.remote.service.internal.RemoteInvokationService;

public class RemoteInvokationServiceImpl implements RemoteInvokationService {

	private final static Logger log = LoggerFactory.getLogger(RemoteInvokationServiceImpl.class);
	
	private ClientConnectionManager ccm;
	
	@Override
	public String getResponseAsString(final String host, final int port, final String path) {
		return getResponseAsStringInternal(host, port, path);
	}

	@Override
	public int getResponseAsInt(final String host, final int port, final String path) {
		final String result = getResponseAsStringInternal(host, port, path);
		
		return NumberUtils.toInt(result, -1);
	}
	
	@Override
	public long getResponseAsLong(String host, int port, String path) {
		final String result = getResponseAsStringInternal(host, port, path);
		
		return NumberUtils.toLong(result, -1);
	}

	@Override
	public byte[] getResponseAsByteArray(final String host, final int port, final String path) {
		InputStream result = null;
		try {
			result = getResponseInternal(host, port, path);
			return IOUtils.toByteArray(result);
		} catch (IllegalStateException e) {
			log.error("Unable to get response", e);
		} catch (URISyntaxException e) {
			log.error("Unable to get response!", e);
		} catch (IOException e) {
			log.error("Unable to get response!!", e);
		} finally {
			IOUtils.closeQuietly(result);
		}
		
		return null;
	}
	
	private String getResponseAsStringInternal(final String host, final int port, final String path) {
		InputStream result = null;
		try {
			result = getResponseInternal(host, port, path);
			return IOUtils.toString(result);
		} catch (IllegalStateException e) {
			log.error("Unable to get response", e);
		} catch (URISyntaxException e) {
			log.error("Unable to get response!", e);
		} catch (IOException e) {
			log.error("Unable to get response!!", e);
		} finally {
			IOUtils.closeQuietly(result);
		}
		
		return null;
	}
	
	private InputStream getResponseInternal(final String host, final int port, final String path) throws URISyntaxException, IllegalStateException, IOException {
		final String fixedPath = (StringUtils.startsWith(path, "/") ? path : "/"+path);
		
		
		HttpClient httpclient = new DefaultHttpClient(ccm, new BasicHttpParams());
		/*
		URIBuilder builder = new URIBuilder();
		builder.setScheme("http").setHost(host).setPort(port).setPath(fixedPath);
		URI uri = builder.build();
		*/
		URI uri = new URI("http", null, host, port, fixedPath, null, null);
		HttpGet httpget = new HttpGet(uri);
		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();
		if(entity == null) { // TODO
			return null;
		}
		
		return entity.getContent();
	}

	public void setClientConnectionManager(ClientConnectionManager ccm) {
		this.ccm = ccm;
	}
	
}
