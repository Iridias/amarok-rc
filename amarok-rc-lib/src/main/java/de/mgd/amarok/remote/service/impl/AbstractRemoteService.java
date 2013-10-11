package de.mgd.amarok.remote.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mgd.amarok.remote.service.internal.HelperService;
import de.mgd.amarok.remote.service.internal.RemoteInvokationService;

public abstract class AbstractRemoteService {

	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	protected HelperService helperService;
	protected RemoteInvokationService remoteService;
	protected String host;
	protected int port;
	
	
	public void setRemoteService(RemoteInvokationService remoteService) {
		this.remoteService = remoteService;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public void setHelperService(HelperService helperService) {
		this.helperService = helperService;
	}
	
}
