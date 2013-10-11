package de.mgd.amarok.remote.service.impl;

import org.json.JSONException;
import org.json.JSONObject;

import de.mgd.amarok.remote.model.Track;
import de.mgd.amarok.remote.service.PlayerService;

public class PlayerServiceImpl extends AbstractRemoteService implements PlayerService {

	@Override
	public PlayerState state() {
		final int result = remoteService.getResponseAsInt(host, port, "getState/");
		PlayerState state = null;
		
		if(result == 0) {
			state = PlayerState.PLAYING;
		} else if(result == 1) {
			state = PlayerState.PAUSED;
		} else if(result == 2) {
			state = PlayerState.STOPPED;
		}
		
		return state;
	}

	@Override
	public Track currentTrack() {
		// FIXME: cover is currently not part of Track!!!
		final String trackDetails = remoteService.getResponseAsString(host, port, "playerCurrentTrack/");
		log.debug("Result for current track was: {}", trackDetails);
		
		if(trackDetails == null) {
			return null;
		}
		try {
			JSONObject track = new JSONObject(trackDetails);
			return helperService.buildTrackFromJSONObject(track);
		} catch (JSONException e) {
			log.error("Unable to deserialize track-details", e);
		}
		
		return null;
	}

	@Override
	public int currentVolume() {
		return remoteService.getResponseAsInt(host, port, "getVolume/");
	}
	
	@Override
	public byte[] currentCover() {
		return remoteService.getResponseAsByteArray(host, port, "getCurrentCover/");
	}
	
	@Override
	public long trackPositionMs() {
		return remoteService.getResponseAsInt(host, port, "getPosition/");
	}

	@Override
	public void play() {
		remoteService.getResponseAsInt(host, port, "cmdPlay/");
	}

	@Override
	public void pause() {
		remoteService.getResponseAsInt(host, port, "cmdPause/");
	}

	@Override
	public void stop() {
		remoteService.getResponseAsInt(host, port, "cmdStop/");
	}

	@Override
	public void forceStop() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void togglePlayPause() {
		remoteService.getResponseAsInt(host, port, "cmdPlayPause/");
	}

	@Override
	public void next() {
		remoteService.getResponseAsInt(host, port, "cmdNext/");
	}

	@Override
	public void previous() {
		remoteService.getResponseAsInt(host, port, "cmdPrev/");
	}

	@Override
	public void mute() {
		remoteService.getResponseAsInt(host, port, "cmdMute/");
	}

	@Override
	public void setVolume(final int volume) {
		if(volume < 0 || volume > 100) {
			throw new IllegalArgumentException("Value for volume must be >= 0 and <= 100");
		}
		
		remoteService.getResponseAsInt(host, port, "cmdSetVolume/"+volume);
	}
	
	@Override
	public void volumeUp() {
		volumeUp(5);
	}

	@Override
	public void volumeUp(final int ticks) {
		if(ticks < 0 || ticks > 100) {
			throw new IllegalArgumentException("Steps for volume modification must be >= 0 and <= 100");
		}
		remoteService.getResponseAsInt(host, port, "cmdVolumeUp/"+ticks);
	}

	@Override
	public void volumeDown() {
		volumeDown(5);
	}

	@Override
	public void volumeDown(final int ticks) {
		if(ticks < 0 || ticks > 100) {
			throw new IllegalArgumentException("Steps for volume modification must be >= 0 and <= 100");
		}
		remoteService.getResponseAsInt(host, port, "cmdVolumeDown/"+ticks);
	}

	@Override
	public void jumpToPositionInTrack(final int positionInMs) {
		remoteService.getResponseAsInt(host, port, "cmdSetPosition/"+positionInMs);
	}

}
