package de.mgd.amarok.remote.service;

import java.util.List;

import de.mgd.amarok.remote.model.Track;

public interface PlaylistService {

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
	
}
