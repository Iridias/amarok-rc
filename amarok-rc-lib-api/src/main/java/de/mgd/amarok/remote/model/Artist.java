package de.mgd.amarok.remote.model;

public class Artist {

	private Artist(Builder b) {
		id = b.id;
		name = b.name;
	}
	
	private final int id;
	private final String name;
	
	public static class Builder {
		private int id;
		private String name;
		
		public Builder setId(final int id) {
			this.id = id;
			return this;
		}
		
		public Builder setName(final String name) {
			this.name = name;
			return this;
		}
		
		public Artist build() {
			return new Artist(this);
		}
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Artist other = (Artist) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Artist [id=" + id + ", name=" + name + "]";
	}
	
}
