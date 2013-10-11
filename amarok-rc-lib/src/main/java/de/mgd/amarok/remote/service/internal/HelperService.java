package de.mgd.amarok.remote.service.internal;

import org.json.JSONObject;

import de.mgd.amarok.remote.model.Album;
import de.mgd.amarok.remote.model.Artist;
import de.mgd.amarok.remote.model.Track;

public interface HelperService {

	/**
	 * 
	 * @param track
	 * @return
	 */
	Track buildTrackFromJSONObject(final JSONObject track);
	
	/**
	 * 
	 * @param artist
	 * @return
	 */
	Artist buildArtistFromJSONObject(final JSONObject artist);

	/**
	 * 
	 * @param album
	 * @return
	 */
	Album buildAlbumFromJSONObject(final JSONObject album);
	
}
