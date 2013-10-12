package de.mgd.amarok.remote.adapter;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mgd.amarok.remote.R;
import de.mgd.amarok.remote.core.AppEngine;
import de.mgd.amarok.remote.core.factory.ServiceFactory;
import de.mgd.amarok.remote.core.util.HelperUtil;
import de.mgd.amarok.remote.model.Album;
import de.mgd.amarok.remote.service.CollectionService;
import de.mgd.amarok.remote.service.PlaylistService;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CollectionAlbumEntryAdapter extends ArrayAdapter<Album> {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private Context mContext;
	private List<Album> data = new ArrayList<Album>();
	private CollectionService collectionService = ServiceFactory.getCollectionService();
	private PlaylistService playlistService = ServiceFactory.getPlaylistService();
	private Handler handler = new Handler();
	
	public CollectionAlbumEntryAdapter(Context context, int resource) {
		super(context, resource);
		mContext = context;
	}

	@Override
	public int getCount() {
		return (data != null ? data.size() : 0);
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = new View(mContext);
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.collection_album_entry, null);
		} else {
			v = convertView;
		}
		
		final Album album = data.get(position);
		TextView albumName = (TextView) v.findViewById(R.id.albumTitle);
		LinearLayout tracks = (LinearLayout) v.findViewById(R.id.albumTrackList);
		final ImageView cover = (ImageView) v.findViewById(R.id.albumCover);
		final ImageView appendAlbumButton = (ImageView) v.findViewById(R.id.addToPlaylist);

		appendAlbumButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				appendAlbumToPlaylist(album);
			}
		});

		albumName.setText(album.getName());
		if(tracks.getChildCount() <= 0) {
			tracks.setVisibility(View.GONE); 
		}
		
		applyCoverImage(cover, album);
		
		return v;
	}

	private void appendAlbumToPlaylist(final Album album) {
		HelperUtil.runInBackground(new Runnable() {
			@Override
			public void run() {
				int index = collectionService.addAlbumToPlaylist(album);
				//playlistService.playTrackAtIndex(index); // TODO
			}
		});
	}

	private void applyCoverImage(final ImageView cover, final Album album) {
		if(album.getImageId() < 0) {
			cover.setImageDrawable(AppEngine.getNoCover());
			return;
		}
		
		HelperUtil.runInBackground(new Runnable() { // FIXME: use caching!!
			public void run() {
				byte[] coverBytes = collectionService.coverForAlbumByImageId(album.getImageId());
				if(coverBytes == null || coverBytes.length < 10) {
					log.warn("No Coverdata received!");
					return;
				}
				ByteArrayInputStream byis = new ByteArrayInputStream(coverBytes);
				final Drawable coverImage = Drawable.createFromStream(byis, "cover"+album.getImageId());
				handler.post(new Runnable() {
					public void run() {
						cover.setImageDrawable(coverImage);
					}
				});
			}
		});
	}
	
	@Override
	public Album getItem(final int position) {
		if(data == null || data.isEmpty()) {
			return null;
		}
		
		return data.get(position);
	}
	
	public void setData(final List<Album> albums) {
		this.data = albums;
	}
}
