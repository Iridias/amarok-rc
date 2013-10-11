package de.mgd.amarok.remote.adapter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.mgd.amarok.remote.R;
import de.mgd.amarok.remote.core.util.HelperUtil;
import de.mgd.amarok.remote.model.Track;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CollectionTrackEntryAdapter extends ArrayAdapter<Track> {

	private Context mContext;
	private List<Track> data = new ArrayList<Track>();
	
	public CollectionTrackEntryAdapter(Context context, int resource) {
		super(context, resource);
		mContext = context;
	}
	
	@Override
	public int getCount() {
		return (data != null ? data.size() : 0);
	}
	
	@Override
	public Track getItem(final int position) {
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
            v = vi.inflate(R.layout.collection_track_entry, null);
		} else {
			v = convertView;
		}
		
		final Track track = data.get(position);
		final String albumArtist = findAlbumArtist(track);
		
		HelperUtil.applyBackgroundColor(mContext, position, v);
		applyOrHideTrackNumber(track, v);
		
		TextView trackTitle = (TextView) v.findViewById(R.id.trackTitle);
		trackTitle.setText(track.getTitle());
		
		applyOrHideTrackArtist(track, v, albumArtist);
		
		return v;
	}
	
	private String findAlbumArtist(final Track track) {
		String trackArtist = track.getArtist();

		for(int i=0; i<data.size(); i++) {
			Track previous = data.get(i);
			if(!compareArtist(track, previous)) {
				return null;
			}
		}
		
		return trackArtist;
	}
	
	private boolean compareArtist(final Track track, final Track previousTrack) {
		if(track == null || previousTrack == null) {
			return false;
		}
		
		return StringUtils.equals(track.getArtist(), previousTrack.getArtist());
	}
	
	private void applyOrHideTrackArtist(final Track track, final View v, final String albumArtist) {
		TextView trackArtist = (TextView) v.findViewById(R.id.trackArtist);
		TextView separator2 = (TextView) v.findViewById(R.id.separator2);
		
		if(albumArtist == null) {
			trackArtist.setText(track.getArtist());
			separator2.setText(" - ");
		} else {
			trackArtist.setText("");
			separator2.setText("");
		}
	}
	
	private void applyOrHideTrackNumber(final Track track, final View v) {
		TextView trackNumber = (TextView) v.findViewById(R.id.trackNumber);
		TextView separator1 = (TextView) v.findViewById(R.id.separator1);
		
		if(track.getNumber() > 0) {
			trackNumber.setText(String.valueOf(track.getNumber()));
			separator1.setText(" - ");
		} else {
			trackNumber.setText("");
			separator1.setText("");
		}
	}
	
	public void setData(final List<Track> tracks) {
		this.data = tracks;
	}

}
