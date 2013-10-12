package de.mgd.amarok.remote.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.mgd.amarok.remote.model.Album;
import de.mgd.amarok.remote.model.Artist;
import de.mgd.amarok.remote.model.Track;
import de.mgd.amarok.remote.service.CollectionService;

public class CollectionServiceImpl extends AbstractRemoteService implements CollectionService {

	@Override
	public List<Artist> findCollectionArtists() {
		final String allArtists = remoteService.getResponseAsString(host, port, "collectionsArtists/");
		List<Artist> artists = new ArrayList<Artist>();
		if(allArtists == null) {
			return Collections.emptyList();
		}
		
		try {
			JSONArray artistList = new JSONArray(allArtists);
			for(int i=0; i<artistList.length(); i++) {
				JSONObject artist = artistList.getJSONObject(i);
				Artist a = helperService.buildArtistFromJSONObject(artist);
				if(a != null) {
					artists.add(a);
				}
			}
		} catch (JSONException e) {
			log.error("Unable to deserialize playlist", e);
			return Collections.emptyList();
		}
		
		return artists;
	}
	
	@Override
	public List<Album> findAlbumsByArtist(final Artist artist) {
		final String allAlbums = remoteService.getResponseAsString(host, port, "collectionAlbumsByArtistId/"+artist.getId());
		List<Album> albums = new ArrayList<Album>();
		if(allAlbums == null) {
			return Collections.emptyList();
		}
		
		try {
			JSONArray artistList = new JSONArray(allAlbums);
			for(int i=0; i<artistList.length(); i++) {
				JSONObject album = artistList.getJSONObject(i);
				Album a = helperService.buildAlbumFromJSONObject(album);
				if(a != null) {
					albums.add(a);
				}
			}
		} catch (JSONException e) {
			log.error("Unable to deserialize playlist", e);
			return Collections.emptyList();
		}
		
		return albums;
	}
	
	@Override
	public List<Track> findTracksByAlbum(final Album album) {
		final String allTracks = remoteService.getResponseAsString(host, port, "collectionTracksByAlbumId/"+album.getId());
		List<Track> tracks = new ArrayList<Track>();
		if(allTracks == null) {
			return Collections.emptyList();
		}
		
		try { // TODO: extract into separate method to avoid duplicate code with PlaylistService.listAllTracks
			JSONArray trackList = new JSONArray(allTracks);
			for(int i=0; i<trackList.length(); i++) {
				JSONObject track = trackList.getJSONObject(i);
				Track t = helperService.buildTrackFromJSONObject(track);
				if(t != null) {
					tracks.add(t);
				}
			}
		} catch (JSONException e) {
			log.error("Unable to deserialize playlist", e);
			return Collections.emptyList();
		}
		
		return tracks;
	}
	
	@Override
	public byte[] coverForAlbumByImageId(final int imageId) {
		return remoteService.getResponseAsByteArray(host, port, "albumCoverForImageId/"+imageId);
	}

	@Override
	public int addAlbumToPlaylist(final Album album) {
		return remoteService.getResponseAsInt(host, port, "cmdCollectionAddAlbum/"+album.getId());
	}

	@Override
	public int addTrackToPlaylist(final Track track) {
		return remoteService.getResponseAsInt(host, port, "cmdCollectionAddTrack/"+track.getId());
	}

}
