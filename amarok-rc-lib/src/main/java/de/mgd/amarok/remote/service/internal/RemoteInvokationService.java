package de.mgd.amarok.remote.service.internal;

public interface RemoteInvokationService {

	
	String getResponseAsString(String host, int port, String path);
	
	int getResponseAsInt(String host, int port, String path);
	
	long getResponseAsLong(String host, int port, String path);

	byte[] getResponseAsByteArray(String host, int port, String path);
	
}
