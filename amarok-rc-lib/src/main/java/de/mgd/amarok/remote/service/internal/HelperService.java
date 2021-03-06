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
package de.mgd.amarok.remote.service.internal;

import org.json.JSONObject;

import de.mgd.amarok.remote.model.Album;
import de.mgd.amarok.remote.model.Artist;
import de.mgd.amarok.remote.model.Track;

public interface HelperService {


	Track buildTrackFromJSONObject(final JSONObject track);

	Artist buildArtistFromJSONObject(final JSONObject artist);

	Album buildAlbumFromJSONObject(final JSONObject album);
	
}
