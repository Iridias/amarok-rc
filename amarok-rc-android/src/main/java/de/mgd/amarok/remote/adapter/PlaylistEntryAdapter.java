package de.mgd.amarok.remote.adapter;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.mgd.amarok.remote.R;
import de.mgd.amarok.remote.core.factory.ServiceFactory;
import de.mgd.amarok.remote.core.util.HelperUtil;
import de.mgd.amarok.remote.model.Track;
import de.mgd.amarok.remote.service.PlaylistService;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class PlaylistEntryAdapter extends ArrayAdapter<Track> {

	private Context mContext;
	private List<Track> data = new ArrayList<Track>();
	private Handler handler = new Handler();
	private PlaylistService playlistService;
	
	public PlaylistEntryAdapter(Context context, int resource) {
		super(context, resource);
		mContext = context;
		playlistService = ServiceFactory.getPlaylistService();
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
            v = vi.inflate(R.layout.playlist_albumentry, null);
		} else {
			v = convertView;
		}
		
		final Track track = data.get(position);
		final String albumArtist = findAlbumArtist(position, track);
		
		HelperUtil.applyBackgroundColor(mContext, position, v);
		applyOrHideTrackNumber(track, v);
		
		TextView trackLength = (TextView) v.findViewById(R.id.trackEntryLength);
		TextView trackTitle = (TextView) v.findViewById(R.id.trackTitle);
		trackTitle.setText(track.getTitle());
		trackLength.setText(HelperUtil.convertToTimeString(track.getLength()));
		
		applyOrHideTrackArtist(track, v, albumArtist);
		applyAlbumDetails(position, v, track, albumArtist);
		
		return v;
	}
	
	private String findAlbumArtist(final int position, final Track track) {
		String trackArtist = track.getArtist();
		// check backwards
		if(position > 0) {
			for(int i=position; i>=0; i--) {
				Track previous = data.get(i);
				if(!compareAlbum(track, previous)) {
					break;
				}
				if(!compareArtist(track, previous)) {
					return null;
				}
			}
		}
		
		// check remaining
		for(int i=position; i<data.size(); i++) {
			Track previous = data.get(i);
			if(!compareAlbum(track, previous)) {
				break;
			}
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
	
	private boolean compareAlbum(final Track track, final Track previousTrack) {
		if(track == null || previousTrack == null) {
			return false;
		}
		
		return StringUtils.equals(track.getAlbum(), previousTrack.getAlbum());
	}
	
	private void applyAlbumDetails(final int position, final View v, final Track track, final String albumArtist) {
		FrameLayout albumDetails = (FrameLayout) v.findViewById(R.id.albumEntryRoot);
		if(!isCombinedEntry(position)) {
			albumDetails.setVisibility(View.GONE);
			return;
		}
		
		albumDetails.setVisibility(View.VISIBLE);
		TextView albumTitle = (TextView) v.findViewById(R.id.albumTitle);
		TextView albumArtistTV = (TextView) v.findViewById(R.id.albumArtist);
		final ImageView cover = (ImageView) v.findViewById(R.id.albumCover);
		
		albumTitle.setText(
				track.getAlbum());
		albumArtistTV.setText((albumArtist == null ? "" : albumArtist));
		
		HelperUtil.runInBackground(new Runnable() { // FIXME: use caching!!
			public void run() {
				byte[] coverBytes = playlistService.coverAtIndex(position);
				if(coverBytes == null || coverBytes.length < 10) {
					return;
				}
				ByteArrayInputStream byis = new ByteArrayInputStream(coverBytes);
				final Drawable coverImage = Drawable.createFromStream(byis, "cover");
				handler.post(new Runnable() {
					public void run() {
						cover.setImageDrawable(coverImage);
					}
				});
			}
		});
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
	
	
	private boolean isCombinedEntry(final int position) {
		if(position == 0) {
			return true;
		}
		
		Track selected = data.get(position);
		Track previous = data.get(position-1);
		
		if(selected == null || previous == null) {
			return false;
		}
		
		return !StringUtils.equals(selected.getAlbum(), previous.getAlbum());
	}

	public void setData(List<Track> data) {
		this.data = data;
	}

}
