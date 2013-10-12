package de.mgd.amarok.remote.core;

import java.io.ByteArrayInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.graphics.drawable.Drawable;

import de.mgd.amarok.remote.core.factory.ServiceFactory;
import de.mgd.amarok.remote.model.Track;
import de.mgd.amarok.remote.service.PlayerService;
import de.mgd.amarok.remote.service.PlayerService.PlayerState;

public class CommunicationService extends Thread {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private boolean running = false;
	private PlayerService playerService;
	private long lastTrackPositionInMs = Long.MAX_VALUE;
	
	public CommunicationService() {
		playerService = ServiceFactory.getPlayerService();
	}
	
	@Override
	public void run() {
		log.info("Started CommunicationService");
		running = true;
		while(running) {
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
