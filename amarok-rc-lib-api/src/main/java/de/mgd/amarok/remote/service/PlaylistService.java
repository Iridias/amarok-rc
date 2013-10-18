package de.mgd.amarok.remote.service;

import java.util.List;

import de.mgd.amarok.remote.model.Track;

public interface PlaylistService {

	public static enum PlaylistMode { NORMAL, REPEAT_PLAYLIST, REPEAT_TRACK, RANDOM };

	/**
	 * 
	 */
	void clearPlaylist();
	
	/**
	 * 
	 * @return
	 */
	List<Track> listAllTracks();
	
	/**
	 * 
	 * @param index
	 */
	void playTrackAtIndex(final int index);

	/**
	 * 
	 * @param index
	 * @return
	 */
	byte[] coverAtIndex(int index);

	/**
	 *
	 * @return
	 */
	PlaylistMode determinePlaylistMode();

	/**
	 *
	 * @param mode
	 */
	void changePlaylistMode(PlaylistMode mode);

}
