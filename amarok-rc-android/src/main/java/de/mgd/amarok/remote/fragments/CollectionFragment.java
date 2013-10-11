package de.mgd.amarok.remote.fragments;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mgd.amarok.remote.R;
import de.mgd.amarok.remote.adapter.CollectionAlbumEntryAdapter;
import de.mgd.amarok.remote.adapter.CollectionArtistEntryAdapter;
import de.mgd.amarok.remote.adapter.CollectionTrackEntryAdapter;
import de.mgd.amarok.remote.core.factory.ServiceFactory;
import de.mgd.amarok.remote.core.util.HelperUtil;
import de.mgd.amarok.remote.model.Album;
import de.mgd.amarok.remote.model.Artist;
import de.mgd.amarok.remote.model.Track;
import de.mgd.amarok.remote.service.CollectionService;
import android.app.ListFragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

public class CollectionFragment extends ListFragment {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private CollectionArtistEntryAdapter adapter;
	private CollectionService collectionService = ServiceFactory.getCollectionService();
	private Handler handler = new Handler();
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		adapter = new CollectionArtistEntryAdapter(getActivity(), 0);
		
		HelperUtil.runInBackground(new Runnable() {
			public void run() {
				List<Artist> artists = collectionService.findCollectionArtists();
				adapter.setData(artists);
				
				handler.post(new Runnable() {
					public void run() {
						setListAdapter(adapter);
					}
				});
			}
		});
	}
	
	@Override
	public void onListItemClick(final ListView l, final View v, final int position, final long id) {
		final CollectionAlbumEntryAdapter albumAdapter = new CollectionAlbumEntryAdapter(getActivity(), 0);
		final LinearLayout albumListView = (LinearLayout) v.findViewById(R.id.artistAlbumList);
		final Artist artist = adapter.getItem(position);
		
		log.info("Artist clicked");
		if(albumListView.getVisibility() == View.VISIBLE) {
			albumListView.setVisibility(View.GONE);
			return;
		}
		
		if(albumListView.getChildCount() > 0 && StringUtils.equals(artist.getName(), (String) albumListView.getTag(R.id.TAGKEY_COLL_ARTIST))) {
			albumListView.setVisibility(View.VISIBLE);
			return;
		}
		
		log.info("filling albums");
		HelperUtil.runInBackground(new Runnable() {
			public void run() {
				final List<Album> albums = collectionService.findAlbumsByArtist(artist);
				albumAdapter.setData(albums);
				
				log.info("albums fetched. adding to view...");
				handler.post(new Runnable() {
					public void run() {
						addAlbumEntries(albums, albumAdapter, albumListView, artist);
					}
				});
			}
		});
	}
	
	private void addAlbumEntries(final List<Album> albums, final CollectionAlbumEntryAdapter albumAdapter, 
			final LinearLayout albumListView, final Artist artist) {
		
		albumListView.removeAllViews();
		
		log.info("adding {} albums", albums.size());
		for(int index = 0; index < albums.size(); index++) {
			final Album album = albums.get(index);
			final View entry = albumAdapter.getView(index, (View)null, (ViewGroup) null);
			entry.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					addTrackEntries(album, entry);
				}
			});
			
			albumListView.addView(entry);
		}
		albumListView.setVisibility(View.VISIBLE);
		albumListView.setTag(R.id.TAGKEY_COLL_ARTIST, artist.getName());
	}
	
	private void addTrackEntries(final Album album, final View v) {
		final CollectionTrackEntryAdapter trackEntryAdapter = new CollectionTrackEntryAdapter(getActivity(), 0);
		final LinearLayout trackListView = (LinearLayout) v.findViewById(R.id.albumTrackList);
		
		if(trackListView.getVisibility() == View.VISIBLE) {
			trackListView.setVisibility(View.GONE);
			return;
		}
		
		if(trackListView.getChildCount() > 0 ) {
			trackListView.setVisibility(View.VISIBLE);
			return;
		}
		
		HelperUtil.runInBackground(new Runnable() { // TODO: show loading
			public void run() {
				final List<Track> tracks = collectionService.findTracksByAlbum(album);
				trackEntryAdapter.setData(tracks);
				
				handler.post(new Runnable() {
					public void run() {
						addTrackEntriesInternal(tracks, trackEntryAdapter, trackListView);
					}
				});
			}
		});
	}
	
	private void addTrackEntriesInternal(final List<Track> tracks, final CollectionTrackEntryAdapter trackEntryAdapter,
			final LinearLayout trackListView) {
		
		for(int index = 0; index < tracks.size(); index++) {
			View entry = trackEntryAdapter.getView(index, (View)null, (ViewGroup) null);
			trackListView.addView(entry);
		}
		
		trackListView.setVisibility(View.VISIBLE);
	}
}
