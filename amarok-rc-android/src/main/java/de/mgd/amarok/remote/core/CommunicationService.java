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
		while(true) { // TODO: need better condition
			final PlayerState state = playerService.state();
			if(state == null) { // assume missing connectivity
				log.warn("Received playerState null - wait 5 seconds for retry...");
				sleepFor(5000);
				continue;
			}
			
			//log.info("updating short-term data...");
			final long currentTrackPositionInMs = playerService.trackPositionMs();
			//log.info("updating short-term data: fetched currentTrackPositionInMs");
			AppEngine.setCurrentTrackPositionInMs(currentTrackPositionInMs);
			//log.info("updating short-term data: apply state");
			AppEngine.setPlayerState(state);
			//log.info("updating short-term data: fetching currentVolume");
			AppEngine.setCurrentVolume(playerService.currentVolume());
			
			//log.info("updating: check for update trackDetails");
			if(isUpdateTrackDetails(currentTrackPositionInMs)) {
				//log.info("updating long-term data...");
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
			
			//log.info("update done - sleeping");
			
			sleep();
		}
		
		//running = false;
	}
	
	public boolean isRunning() {
		return running;
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
