package de.mgd.amarok.remote.fragments;

import java.util.List;

import de.mgd.amarok.remote.R;
import de.mgd.amarok.remote.adapter.PlaylistEntryAdapter;
import de.mgd.amarok.remote.core.AppEngine;
import de.mgd.amarok.remote.core.factory.ServiceFactory;
import de.mgd.amarok.remote.core.util.HelperUtil;
import de.mgd.amarok.remote.model.Track;
import de.mgd.amarok.remote.service.PlaylistService;
import android.app.ListFragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ListView;

public class PlaylistFragment extends ListFragment {

	private PlaylistEntryAdapter playlistEntryAdapter;
	private PlaylistService playlistService = ServiceFactory.getPlaylistService();
	private Handler handler = new Handler();
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		playlistEntryAdapter = new PlaylistEntryAdapter(getActivity(), 0);
		
		//getListView().setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		HelperUtil.runInBackground(new Runnable() {
			public void run() {
				List<Track> tracks = playlistService.listAllTracks();
				playlistEntryAdapter.setData(tracks);
				
				handler.post(new Runnable() {
					public void run() {
						setListAdapter(playlistEntryAdapter);
						int index = (AppEngine.getCurrentTrack() == null ? 0 :AppEngine.getCurrentTrack().getIndexInPlaylist());
						getListView().setSelection(index);
					}
				});
			}
		});
	}
	
	
	@Override
	public void onListItemClick(final ListView l, final View v, final int position, final long id) {
		Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.buttonpress);
		anim.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) { }
			public void onAnimationRepeat(Animation animation) { }
			
			public void onAnimationEnd(Animation animation) {
				HelperUtil.runInBackground(new Runnable() {
					public void run() {
						playlistService.playTrackAtIndex(position);
					}
				});
			}
		});
		
		v.startAnimation(anim);
	}
	
	
	
}
