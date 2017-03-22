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
package de.mgd.amarok.remote.model;

public class Track {

	private Track(Builder b) {
		id = b.id;
		artist = b.artist;
		title = b.title;
		album = b.album;
		length = b.length;
		number = b.number;
		indexInPlaylist = b.indexInPlaylist;
	}
	
	private final int id;
	private final int number;
	private final String artist;
	private final String title;
	private final String album;
	private final long length;
	private final int indexInPlaylist;
	
	public static class Builder {
		private int id;
		private int number;
		private String artist;
		private String title;
		private String album;
		private long length;
		private int indexInPlaylist;
		
		public Builder setArtist(String artist) {
			this.artist = artist;
			return this;
		}
		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}
		public Builder setAlbum(String album) {
			this.album = album;
			return this;
		}
		public Builder setId(final int id) {
			this.id = id;
			return this;
		}
		public Builder setLength(final long length) {
			this.length = length;
			return this;
		}
		public Builder setIndexInPlaylist(final int index) {
			this.indexInPlaylist = index;
			return this;
		}
		public Builder setNumber(final int number) {
			this.number = number;
			return this;
		}
		
		public Track build() {
			return new Track(this);
		}
	}

	public String getArtist() {
		return artist;
	}
	public String getTitle() {
		return title;
	}
	public String getAlbum() {
		return album;
	}
	public long getLength() {
		return length;
	}
	public int getIndexInPlaylist() {
		return indexInPlaylist;
	}
	public int getNumber() {
		return number;
	}
	public int getId() {
		return id;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((album == null) ? 0 : album.hashCode());
		result = prime * result + ((artist == null) ? 0 : artist.hashCode());
		result = prime * result + (int) (length ^ (length >>> 32));
		result = prime * result + number;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Track other = (Track) obj;
		if (album == null) {
			if (other.album != null)
				return false;
		} else if (!album.equals(other.album))
			return false;
		if (artist == null) {
			if (other.artist != null)
				return false;
		} else if (!artist.equals(other.artist))
			return false;
		if (length != other.length)
			return false;
		if (number != other.number)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Track [artist=" + artist + ", title=" + title + ", album=" + album + ", length=" + length + "]";
	}
	
}
