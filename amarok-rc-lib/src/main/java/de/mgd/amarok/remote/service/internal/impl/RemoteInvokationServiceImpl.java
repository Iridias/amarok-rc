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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mgd.amarok.remote.service.internal.RemoteInvokationService;

public class RemoteInvokationServiceImpl implements RemoteInvokationService {

	private static final Logger log = LoggerFactory.getLogger(RemoteInvokationServiceImpl.class);

	private static final String DEFAULT_PROTOCOL = "http";

	private ContentFetcher<String> stringContentFetcher = new StringContentFetcher();
	private ContentFetcher<byte[]> byteArrayContentFetcher = new ByteArrayContentFetcher();

	@Override
	public String getResponseAsString(final String host, final int port, final String path) {
		return getResponseInternal(host, port, path, stringContentFetcher);
	}

	@Override
	public int getResponseAsInt(final String host, final int port, final String path) {
		final String result = getResponseInternal(host, port, path, stringContentFetcher);
		
		return NumberUtils.toInt(result, -1);
	}
	
	@Override
	public long getResponseAsLong(String host, int port, String path) {
		final String result = getResponseInternal(host, port, path, stringContentFetcher);
		
		return NumberUtils.toLong(result, -1);
	}

	@Override
	public byte[] getResponseAsByteArray(final String host, final int port, final String path) {
		return getResponseInternal(host, port, path, byteArrayContentFetcher);
	}
	
	private <T> T getResponseInternal(final String host, final int port, final String path, final ContentFetcher<T> contentFetcher) {
		InputStream result = null;
		URLConnection urlConnection = null;
		try {
			urlConnection = openURLConnection(host, port, path);
			result = urlConnection.getInputStream();
			return contentFetcher.fetchContent(result);
		} catch (Exception e) {
			log.error("Unable to get response!", e);
		} finally {
			IOUtils.closeQuietly(result);
			closeURLConnection(urlConnection);
		}
		
		return null;
	}


	private interface ContentFetcher<T> {
		T fetchContent(InputStream stream) throws IOException;
	}

	private class StringContentFetcher implements ContentFetcher<String> {
		@Override
		public String fetchContent(final InputStream stream) throws IOException {
			return IOUtils.toString(stream);
		}
	}

	private class ByteArrayContentFetcher implements ContentFetcher<byte[]> {
		@Override
		public byte[] fetchContent(final InputStream stream) throws IOException {
			return IOUtils.toByteArray(stream);
		}
	}

	private void closeURLConnection(final URLConnection urlConnection) {
		if(urlConnection instanceof HttpURLConnection) {
			((HttpURLConnection)urlConnection).disconnect();
		}
	}

	private URLConnection openURLConnection(final String host, final int port, final String path) throws IOException {
		URL url = createURL(host, port, path);
		return url.openConnection();
	}

	private URL createURL(final String host, final int port, final String path) throws MalformedURLException {
		String protocol = determineProtocol(host);
		final String fixedPath = (StringUtils.startsWith(path, "/") ? path : "/"+path);

		return new URL(protocol, host, port, fixedPath);
	}

	private String determineProtocol(final String host) {
		if(StringUtils.contains(host, "://")) {
			return StringUtils.substringBefore(host, "://");
		}
		return DEFAULT_PROTOCOL;
	}

}
