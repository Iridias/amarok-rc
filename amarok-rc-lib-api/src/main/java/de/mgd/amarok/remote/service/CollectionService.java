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
package de.mgd.amarok.remote.service;

import de.mgd.amarok.remote.model.Album;
import de.mgd.amarok.remote.model.Artist;
import de.mgd.amarok.remote.model.Track;

import java.util.List;

public interface CollectionService {


	List<Artist> findCollectionArtists();
	

	List<Album> findAlbumsByArtist(Artist artist);


	byte[] coverForAlbumByImageId(int imageId);
	

	List<Track> findTracksByAlbum(Album album);

	/**
	 *
	 * @param album
	 * @return the index of the first track of the album in the playlist
	 */
	int addAlbumToPlaylist(Album album);

	/**
	 *
	 * @param track
	 * @return the index of the track in the playlist
	 */
	int addTrackToPlaylist(Track track);
}
