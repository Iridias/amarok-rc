package de.mgd.amarok.remote.service.impl;

import de.mgd.amarok.remote.service.AmarokService;

public class AmarokServiceImpl extends AbstractRemoteService implements AmarokService {

	@Override
	public int serverVersion() {
		return remoteService.getResponseAsInt(host, port, "getServerVersion/");
	}

}
