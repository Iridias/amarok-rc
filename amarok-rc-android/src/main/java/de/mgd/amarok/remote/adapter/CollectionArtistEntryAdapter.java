package de.mgd.amarok.remote.adapter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.mgd.amarok.remote.R;
import de.mgd.amarok.remote.core.util.HelperUtil;
import de.mgd.amarok.remote.model.Artist;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CollectionArtistEntryAdapter extends ArrayAdapter<Artist> {

	private Context mContext;
	private List<Artist> data = new ArrayList<Artist>();
	
	public CollectionArtistEntryAdapter(Context context, int resource) {
		super(context, resource);
		mContext = context;
	}
	
	@Override
	public int getCount() {
		return (data != null ? data.size() : 0);
	}
	
	@Override
	public Artist getItem(final int position) {
		if(data == null || data.isEmpty()) {
			return null;
		}
		
		return data.get(position);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = new View(mContext);
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.collection_artist_entry, null);
		} else {
			v = convertView;
		}
		
		final Artist artist = data.get(position);
		HelperUtil.applyBackgroundColor(mContext, position, v);
		
		TextView firstLetter = (TextView) v.findViewById(R.id.firstLetter);
		TextView artistName = (TextView) v.findViewById(R.id.artistName);
		LinearLayout albums = (LinearLayout) v.findViewById(R.id.artistAlbumList);
		
		applyAlphabeticalLetters(firstLetter, artist, position);
		
		artistName.setText(artist.getName());
		if(albums.getChildCount() <= 0 || !StringUtils.equals(artist.getName(), (String) albums.getTag(R.id.TAGKEY_COLL_ARTIST))) {
			albums.setVisibility(View.GONE);
		}
		
		return v;
	}
	
	private void applyAlphabeticalLetters(final TextView firstLetter, final Artist artist, final int position) {
		String firstChar = StringUtils.substring(artist.getName(), 0, 1);
		if(!isApplyAlphabeticalLetter(artist, position, firstChar)) {
			firstLetter.setVisibility(View.GONE);
			return;
		}
		
		firstLetter.setText(StringUtils.upperCase(firstChar));
		firstLetter.setVisibility(View.VISIBLE);
	}
	
	private boolean isApplyAlphabeticalLetter(final Artist artist, final int position, final String firstChar) {
		if(!StringUtils.isAlpha(firstChar)) {
			return false;
		}
		if(position == 0) {
			return true;
		}
		
		Artist previous = data.get(position-1);
		if(StringUtils.startsWithIgnoreCase(previous.getName(), firstChar)) {
			return false;
		}
		
		return true;
	}
	
	public void setData(final List<Artist> artists) {
		this.data = artists;
	}
	
}
