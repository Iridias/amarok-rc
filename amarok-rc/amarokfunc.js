/*
 *    Copyright (C) 2013 by Iridias <iridias@tiphares.de>
 *    Copyright (C) 2010 by Holger Reichert <mail@h0lger.de>    
 *    Copyright (C) 2009 by Johannes Wolter <jw@inutil.org>    
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

Importer.loadQtBinding("qt.gui");
Importer.include("httpserver.js");
Importer.include("util.js");
Importer.include("const.js");

/* General commands */

getServerVersion = function(path){
	response = new HandlerResponse();
	sv = SERVERVERSION
	response.append(sv.toString());
	return response;
}


/* Player commands */

getState = function(path){
	response = new HandlerResponse();
	response.append(Amarok.Engine.engineState().toString());
	return response;
}

/*
 * @Deprecated use playerCurrentTrack instead
 */
getArtist = function(path){
	response = new HandlerResponse();
	response.append(shorten(Amarok.Engine.currentTrack().artist, SHORTEN1));
	return response;
}

/*
 * @Deprecated use playerCurrentTrack instead
 */
getTitle = function(path){
	response = new HandlerResponse();
	response.append(shorten(Amarok.Engine.currentTrack().title, SHORTEN1));
	return response;
}

/*
 * @Deprecated use playerCurrentTrack instead
 */
getAlbum = function(path){
	response = new HandlerResponse();
	response.append(shorten(Amarok.Engine.currentTrack().album, SHORTEN1));
	return response;
}

getLength = function(path){
	response = new HandlerResponse();
	length = Amarok.Engine.currentTrack().length
	if (Amarok.Info.version() == "2.2.0") length = length * 1000
	response.append(length.toString());
	return response;
}

getPosition = function(path){
	response = new HandlerResponse();
	response.append(Amarok.Engine.trackPositionMs().toString());
	return response;
}

getVolume = function(path) {
	response = new HandlerResponse();
	response.append(Amarok.Engine.volume.toString());
	return response;
}

getCurrentCover = function(path){
    response = new HandlerResponse();
    response.setMimeType("image/png");
    engineState = Amarok.Engine.engineState();
    if(engineState == ENGINE_STATE_PAUSE || engineState == ENGINE_STATE_PLAY){
        response.append(pixmapToPNG(Amarok.Engine.currentTrack().imagePixmap(), 200));
    }
    return response;
}


playerCurrentTrack = function(path) {
	response = new HandlerResponse();
	var track = Amarok.Engine.currentTrack();
	var playlistIndex = Amarok.Playlist.activeIndex();
	
	response.append(JSON.stringify({
		title: track.title,
		artist: track.artist,
		album: track.album,
		length: track.length,
		number: track.trackNumber,
		index: playlistIndex
	}));
	
	return response;
}

playlistTotalTrackCount = function(path){
	response = new HandlerResponse();
	response.append(Amarok.Playlist.totalTrackCount().toString());
	return response;
}

playlistAllTracks = function(path) {
	response = new HandlerResponse();
	var totalTrackCount = Amarok.Playlist.totalTrackCount();
	var tracks = new Array();
	
	for(i=0; i<totalTrackCount; i++) {
		var track = Amarok.Playlist.trackAt(i);
		tracks[i] = {
				title: track.title,
				artist: track.artist,
				album: track.album,
				length: track.length,
				number: track.trackNumber,
				index: i
		};
	}
	
	response.append(JSON.stringify(tracks));
	
	return response;
}

playlistCoverAt = function(path){
	index = parseInt(path.substring(path.lastIndexOf("/")+1));
    response = new HandlerResponse();
    response.setMimeType("image/png");

    response.append(pixmapToPNG(Amarok.Playlist.trackAt(index).imagePixmap(), 200));
    
    return response;
}

/*
 * @Deprecated use playlistAllTracks instead
 */
getPlaylist = function(path){
    response = new HandlerResponse();
    tracks = "";
    for(trackidx=0; trackidx<Amarok.Playlist.totalTrackCount(); trackidx=trackidx+1){
        t = Amarok.Playlist.trackAt(trackidx);
	if(t.artist=="") tmpArtist = "---";
	else tmpArtist = t.artist;
	
	if(t.title=="") tmpTitle = "---";
	else tmpTitle = t.title;
	
	if(t.album=="") tmpAlbum = " ";
	else tmpAlbum = t.album;
	
	tracks += trackidx.toString() + '\r';
	tracks += shorten(tmpArtist, SHORTEN2) + '\r';
	tracks += shorten(tmpTitle, SHORTEN2) + '\r';
	tracks += shorten(tmpAlbum, SHORTEN2) + '\r';
	tracks += '\n';	
    }
    response.append(tracks);
    return response;
}

cmdPlaylistClear = function(path){
    Amarok.Playlist.clearPlaylist();
    response = new HandlerResponse();
    return response
}

cmdNext = function(path){
    Amarok.Engine.Next();
    return new HandlerResponse();  
}

cmdPrev = function(path){
    Amarok.Engine.Prev();
    return new HandlerResponse();
}

cmdPlay = function(path){
    Amarok.Engine.Play();
    return new HandlerResponse();
}

cmdPause = function(path){
    Amarok.Engine.Pause();
    return new HandlerResponse();
}

cmdPlayPause = function(path){
    if(Amarok.Engine.engineState() == 0)
        Amarok.Engine.Pause();
    else
        Amarok.Engine.Play();
    return new HandlerResponse();
}

cmdStop = function(path){
    Amarok.Engine.Stop(false);
    return new HandlerResponse();
}

cmdVolumeUp = function(path){
    step = parseInt(path.substring(path.lastIndexOf("/")+1));
    Amarok.Engine.IncreaseVolume(step);
    return new HandlerResponse();
}

cmdVolumeDown = function(path){
    step = parseInt(path.substring(path.lastIndexOf("/")+1));
    Amarok.Engine.DecreaseVolume(step);
    return new HandlerResponse();
}

cmdMute = function(path){
    Amarok.Engine.Mute()
    return new HandlerResponse();
}

cmdSetVolume = function(path) {
	step = parseInt(path.substring(path.lastIndexOf("/")+1));
	Amarok.Engine.volume = step;
	return new HandlerResponse();
}

cmdPlayByIndex = function(path){
    index = parseInt(path.substring(path.lastIndexOf("/")+1));
    Amarok.Playlist.playByIndex(index);
    return new HandlerResponse();
}

cmdSetPosition = function(path){
    position = parseInt(path.substring(path.lastIndexOf("/")+1));
    Amarok.Engine.Seek(position);
    return new HandlerResponse();
}


/* Collection commands */

collectionsArtists = function(path) {
	response = new HandlerResponse();
	artistResult = Amarok.Collection.query("SELECT name, id FROM artists ORDER BY name;");
	var artistList = new Array();
	
	var index = 0;
	for(artistidx=0; artistidx<artistResult.length; artistidx++){	
		var artist = artistResult[artistidx++];
		var artistId = artistResult[artistidx];
		
		if (artist.length>0){
			artistList[index] = {
				id: artistId,
				name: artist
			};
			index++;
		}
	}
	
	response.append(JSON.stringify(artistList));
	
	return response;
}

/*
 * @Deprecated: use collectionsArtists instead
 */
getCollectionAllArtists = function(path){
    response = new HandlerResponse();
    artists = "";
    artistsQuery = Amarok.Collection.query("SELECT name, id FROM artists ORDER BY name;");
    for(artistidx=0; artistidx<artistsQuery.length; artistidx++){
		artist = artistsQuery[artistidx++];
		artistId = artistsQuery[artistidx];
		if (artist.length>0){
			artists += artistId + '\n' + shorten(artist, 25) + '\n';			
		}        
    }
    response.append(artists);
    return response;
}


collectionAlbumsByArtistId = function(path) {
	artistId = parseInt(path.substring(path.lastIndexOf("/")+1));
	//dbVersion = Amarok.Collection.query('select version from admin where component = "DB_VERSION"');
	
	var query = "select distinct case when a.name is null or a.name = '' then 'Unknown Album' else a.name end, " +
			" a.id , " +
			"case when a.image is null or a.image = '' then -1 else a.image end, " +
			"count(*) " +
			"from tracks t left join albums a on a.id = t.album " +
			"where t.artist = "+artistId+" group by 1,2,3 order by a.name";
	
	/*
	if(dbVersion >= 14) { // TODO: what about the junction-tables??
		query = "";
	}
	*/
	var albumList = new Array();
	var index = 0;
	var result = Amarok.Collection.query(query);
	for(cursor=0; cursor<result.length; cursor++) {
		var albumName = result[cursor++];
		var albumId = result[cursor++];
		var imageId = result[cursor++];
		var trackCount = result[cursor];
		
		if(albumName != null && albumName !== undefined) {
			albumList[index] = {
				id: albumId,
				name: albumName,
				image: imageId,
				tracks: trackCount
			};
			index++;
		}
	}
	
	response = new HandlerResponse();
	response.append(JSON.stringify(albumList));
	
	return response;
}


albumCoverForImageId = function(path){
	imageId = parseInt(path.substring(path.lastIndexOf("/")+1));
    response = new HandlerResponse();
    response.setMimeType("image/png");
    
    var filepaths = Amarok.Collection.query("select path from images where id = "+imageId);
    var pixmap = loadPixmapFromFile(filepaths[0]);
    
    response.append(pixmapToPNG(pixmap, 100));
    
    return response;
}

collectionTracksByAlbumId = function(path) {
	albumId = parseInt(path.substring(path.lastIndexOf("/")+1));
	//dbVersion = Amarok.Collection.query('select version from admin where component = "DB_VERSION"');
	
	var query = "select t.id, t.title, a.id, a.name, t.tracknumber, t.length " +
			"from tracks t left join artists a on a.id = t.artist " +
			"where t.album = "+albumId+" order by t.discnumber, t.tracknumber, t.title";
	
	/*
	if(dbVersion >= 14) { // TODO: what about the junction-tables??
		query = "";
	}
	*/
	return collectionsTracksInternal(query);
}

collectionsTracksInternal = function(query) {
	var trackList = new Array();
	var index = 0;
	var result = Amarok.Collection.query(query);
	for(cursor=0; cursor<result.length; cursor++) {
		var trackId = result[cursor++];
		var trackTitle = result[cursor++];
		var artistId = result[cursor++];
		var artistName = result[cursor++];
		var trackNumber = result[cursor++];
		var trackLength = result[cursor];
		
		if(trackTitle != null && trackTitle !== undefined) {
			trackList[index] = {
					id: trackId,
					title: trackTitle,
					artist: artistName,
					album: "",
					length: trackLength,
					number: trackNumber,
					index: 0
			};
			index++;
		}
	}
	
	response = new HandlerResponse();
	response.append(JSON.stringify(trackList));
	
	return response;
}


cmdCollectionAddAlbum = function(path) {
	albumId = parseInt(path.substring(path.lastIndexOf("/")+1));
	
	var currentLastEntry = Amarok.Playlist.totalTrackCount();
	
	var query = "select u.uniqueid from urls u, tracks t, albums a " +
			"where t.album = a.id and u.id = t.url and a.id = " + albumId + " " +
			"order by t.discnumber, t.tracknumber, t.title";
	
	var result = Amarok.Collection.query(query);
	for(i = 0; i < result.length; i++) {
		Amarok.Playlist.addMedia(new QUrl(result[i]));
	}
	
	response = new HandlerResponse();
    response.append(currentLastEntry.toString());
    return response
}

cmdCollectionAddTrack = function(path) {
	trackId = parseInt(path.substring(path.lastIndexOf("/")+1));
	
	var currentLastEntry = Amarok.Playlist.totalTrackCount();
	
	var query = "select u.uniqueid from urls u, tracks t " +
			"where u.id = t.url and t.id = " + trackId + " " +
			"order by t.discnumber, t.tracknumber, t.title";
	
	var result = Amarok.Collection.query(query);
	for(i = 0; i < result.length; i++) {
		Amarok.Playlist.addMedia(new QUrl(result[i]));
	}
	
	response = new HandlerResponse();
    response.append(currentLastEntry.toString());
    return response
}


getCollectionTracksByArtistId = function(path){
    artistId = parseInt(path.substring(path.lastIndexOf("/")+1));
    trackQuery = Amarok.Collection.query('SELECT id, title, album FROM tracks WHERE artist = '+artistId+';');
    artistName = Amarok.Collection.query('SELECT name FROM artists WHERE id = '+artistId+';');
    trackCount = trackQuery.length;
    result = "";
    for(trackidx = 0; trackidx < trackCount; trackidx+=3){
	albumName = Amarok.Collection.query('SELECT name FROM albums WHERE id = '+trackQuery[trackidx+2]+';');
	trackId = trackQuery[trackidx].toString();
	trackTitle = trackQuery[trackidx+1];
	
	if(albumName=="") albumName = " ";
	if(trackTitle=="") trackTitle = " ";

	result += trackId + '\r';
	result += artistName + '\r';
	result += shorten(trackTitle, SHORTEN2) + '\r';
	result += shorten(albumName, SHORTEN2) + '\r';
	result += '\n';	
    }
    response = new HandlerResponse();
    response.append(result);
    return response
}

cmdCollectionPlayByTrackId = function(path){
    trackId = parseInt(path.substring(path.lastIndexOf("/")+1));
    trackURL = Amarok.Collection.query('SELECT rpath FROM urls LEFT JOIN tracks ON urls.id = tracks.url WHERE tracks.id = '+trackId+';');
    Amarok.Playlist.addMedia(new QUrl('file:///' + trackURL[0]));
    response = new HandlerResponse();
    Amarok.Playlist.playByIndex(Amarok.Playlist.totalTrackCount()-1)
    return response
}

cmdCollectionEnqueue = function(path) {
    req_len = path.split("/").length;
    req_splitted = path.split("/");
    for(i = 2; i <= req_len; i++) {
	trackId = req_splitted[i];
	trackURL = Amarok.Collection.query('SELECT rpath FROM urls LEFT JOIN tracks ON urls.id = tracks.url WHERE tracks.id = '+trackId+';');
	Amarok.Playlist.addMedia(new QUrl('file:///' + trackURL[0]));
    }
    response = new HandlerResponse();
    return response;
}

getCollectionSearchAll = function(path){
    response = new HandlerResponse();
    result = "";
    querystring = decodeURIComponent(path.substring(path.lastIndexOf("/")+1));
    AllQuery = Amarok.Collection.query('SELECT tracks.id, artists.name, tracks.title, albums.name FROM (artists LEFT JOIN tracks ON artists.id = tracks.artist) LEFT JOIN albums ON tracks.album = albums.id WHERE UPPER(artists.name) LIKE UPPER("%' + querystring + '%") OR UPPER(tracks.title) LIKE UPPER("%' + querystring + '%") OR UPPER(albums.name) LIKE UPPER("%' + querystring + '%")');    /* search for artists */
    for(i = 0; i < AllQuery.length; i+=4){
	trackId = AllQuery[i].toString();
	artistName = AllQuery[i+1]
	trackTitle = AllQuery[i+2];
	albumName = AllQuery[i+3];	
	
	if(albumName=="") albumName = " ";
	if(trackTitle=="") trackTitle = " ";
	if(artistName=="") artistName = " ";

	result += trackId + '\r';
	result += artistName + '\r';
	result += shorten(trackTitle, SHORTEN2) + '\r';
	result += shorten(albumName, SHORTEN2) + '\r';
	result += '\n';	
    }
    response.append(result);
    return response
}
