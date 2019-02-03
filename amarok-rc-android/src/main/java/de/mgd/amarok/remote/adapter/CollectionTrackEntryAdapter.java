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
package de.mgd.amarok.remote.adapter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mgd.amarok.R;
import de.mgd.amarok.remote.core.factory.ServiceFactory;
import de.mgd.amarok.remote.core.util.HelperUtil;
import de.mgd.amarok.remote.model.Track;
import de.mgd.amarok.remote.service.CollectionService;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CollectionTrackEntryAdapter extends ArrayAdapter<Track> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private Context mContext;
	private List<Track> data = new ArrayList<Track>();
	private CollectionService collectionService = ServiceFactory.getCollectionService();
	private Handler handler = new Handler();
	
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

		final ImageView appendTrackButton = (ImageView) v.findViewById(R.id.addToPlaylist);
		TextView trackTitle = (TextView) v.findViewById(R.id.trackTitle);
		trackTitle.setText(track.getTitle());
		
		applyOrHideTrackArtist(track, v, albumArtist);

		appendTrackButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				appendTrackToPlaylist(track, v);
			}
		});

		return v;
	}

	private void appendTrackToPlaylist(final Track track, final View v) {
		HelperUtil.runInBackground(new Runnable() {
			@Override
			public void run() {
				int index = collectionService.addTrackToPlaylist(track);
				if(index < 0) {
					log.warn("returned index for the appended track is {} - it seems the call to Amarok has failed!", index);
					return;
				}
				handler.post(new Runnable() {
					@Override
					public void run() {
						hideAppendButton(v);
					}
				});
				//playlistService.playTrackAtIndex(index); // TODO
			}
		});
	}

	private void hideAppendButton(final View v) {
		final ImageView appendTrackButton = (ImageView) v.findViewById(R.id.addToPlaylist);
		if(appendTrackButton != null) {
			appendTrackButton.setVisibility(View.INVISIBLE);
		}
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
