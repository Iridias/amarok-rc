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
package de.mgd.amarok.remote.core;

import java.io.ByteArrayInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.graphics.drawable.Drawable;

import de.mgd.amarok.remote.core.factory.ServiceFactory;
import de.mgd.amarok.remote.model.Track;
import de.mgd.amarok.remote.service.AmarokService;
import de.mgd.amarok.remote.service.PlayerService;
import de.mgd.amarok.remote.service.PlayerService.PlayerState;
import de.mgd.amarok.remote.service.PlaylistService;

public class CommunicationService extends Thread {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private boolean running = false;
	private PlayerService playerService;
	private PlaylistService playlistService;
	private AmarokService amarokService;
	private long lastTrackPositionInMs = Long.MAX_VALUE;
	
	public CommunicationService() {
		playerService = ServiceFactory.getPlayerService();
		playlistService = ServiceFactory.getPlaylistService();
		amarokService = ServiceFactory.getAmarokService();
	}



	@Override
	public void run() {
		log.info("Started CommunicationService");
		running = true;
		while(running) {
			if(AppEngine.getBackendVersion() <= 0) {
				AppEngine.setBackendVersion(amarokService.serverVersion());
			}

			final PlayerState state = playerService.state();
			if(state == null) { // assume missing connectivity
				log.warn("Received playerState null - wait 5 seconds for retry...");
				sleepFor(5000);
				continue;
			}
			
			final long currentTrackPositionInMs = playerService.trackPositionMs();
			AppEngine.setCurrentTrackPositionInMs(currentTrackPositionInMs);
			AppEngine.setPlayerState(state);
			AppEngine.setCurrentVolume(playerService.currentVolume());

			if(isUpdateTrackDetails(currentTrackPositionInMs)) {
				Track currentTrack = playerService.currentTrack();
				AppEngine.setCurrentTrack(currentTrack);
				AppEngine.setPlaylistMode(playlistService.determinePlaylistMode());

				byte[] coverBytes = playerService.currentCover();
				if(coverBytes != null && coverBytes.length > 10) {
					ByteArrayInputStream byis = new ByteArrayInputStream(coverBytes);
					AppEngine.setCurrentCover(Drawable.createFromStream(byis, "cover"));
				} else {
					AppEngine.setCurrentCover(AppEngine.getNoCover());
				}
			}

			sleep();
		}

	}

	public void setPlayerService(PlayerService playerService) {
		this.playerService = playerService;
	}

	public void setAmarokService(AmarokService amarokService) {
		this.amarokService = amarokService;
	}

	public void setPlaylistService(PlaylistService playlistService) {
		this.playlistService = playlistService;
	}

	public boolean isRunning() {
		return running;
	}

	public void requestStop() {
		running = false;
	}

	private void sleep() {
		sleepFor(1000);
	}
	
	private void sleepFor(final long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			log.info("Cannot sleep", e);
			/* empty by design */
		}
	}
	
	private boolean isUpdateTrackDetails(final long currentTrackPositionInMs) {
		if(AppEngine.getCurrentTrack() == null) {
			return true;
		}
		if(lastTrackPositionInMs > currentTrackPositionInMs) {
			return true;
		}
		
		return false;
	}
	
}
