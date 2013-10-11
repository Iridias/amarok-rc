package de.mgd.amarok.remote.service;

import de.mgd.amarok.remote.model.Album;
import de.mgd.amarok.remote.model.Artist;
import de.mgd.amarok.remote.model.Track;

import java.util.List;

public interface CollectionService {

	/**
	 * 
	 * @return
	 */
	List<Artist> findCollectionArtists();
	
	/**
	 * 
	 * @param artist
	 * @return
	 */
	List<Album> findAlbumsByArtist(Artist artist);

	/**
	 * 
	 * @param imageId
	 * @return
	 */
	byte[] coverForAlbumByImageId(int imageId);
	
	
	/**
	 * 
	 * @param album
	 * @return
	 */
	List<Track> findTracksByAlbum(Album album);
	
}
