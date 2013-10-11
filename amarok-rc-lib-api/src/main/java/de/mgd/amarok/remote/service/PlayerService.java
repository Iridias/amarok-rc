package de.mgd.amarok.remote.service;

import de.mgd.amarok.remote.model.Track;

public interface PlayerService {

	public static enum PlayerState { PLAYING, STOPPED, PAUSED };
	
	/**
	 * Wrapper for Amarok.Engine.engineState()
	 * 
	 * @return
	 */
	PlayerState state();
	
	/**
	 * 
	 * @return
	 */
	Track currentTrack();
	
	/**
	 * 
	 * @return
	 */
	long trackPositionMs();
	
	/**
	 * 
	 */
	void play();
	
	void pause();
	
	void stop();
	
	void forceStop();
	
	void togglePlayPause();
	
	void next();
	
	void previous();
	
	void mute();
	
	/**
	 * Same as {@link #volumeUp(int)} but defaults to 5 ticks.
	 * 
	 */
	void volumeUp();
	
	/**
	 * Increases the volume by the specified amount of ticks.
	 * 
	 * @param ticks must be between 1 and 99
	 */
	void volumeUp(final int ticks);
	
	/**
	 * Same as {@link #volumeDown(int)} but defaults to 5 ticks.
	 * 
	 */
	void volumeDown();
	
	/**
	 * Decreases the volume by the specified amount of ticks.
	 * 
	 * @param ticks must be between 1 and 99
	 */
	void volumeDown(final int ticks);
	
	/**
	 * 
	 * @param positionInMs
	 */
	void jumpToPositionInTrack(final int positionInMs);

	byte[] currentCover();

	int currentVolume();

	void setVolume(int volume);
	
}
