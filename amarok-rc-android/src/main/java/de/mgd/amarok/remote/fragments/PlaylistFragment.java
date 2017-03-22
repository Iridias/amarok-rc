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
package de.mgd.amarok.remote.fragments;

import java.util.List;

import de.mgd.amarok.remote.R;
import de.mgd.amarok.remote.adapter.PlaylistEntryAdapter;
import de.mgd.amarok.remote.core.AppEngine;
import de.mgd.amarok.remote.core.factory.ServiceFactory;
import de.mgd.amarok.remote.core.util.HelperUtil;
import de.mgd.amarok.remote.model.Track;
import de.mgd.amarok.remote.service.PlaylistService;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

public class PlaylistFragment extends AbstractBaseFragment {

	private Track previousTrack = null;
	private PlaylistEntryAdapter playlistEntryAdapter;
	private PlaylistService playlistService = ServiceFactory.getPlaylistService();
	private Handler handler = new Handler();
	private Activity activity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(activity == null) {
			view = inflater.inflate(R.layout.playlist_fragment, container, false);
		} else if(activity.findViewById(R.id.fragment_container1) == null && activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			view = inflater.inflate(R.layout.playlist_fragment_landscape, container, false);
		} else {
			view = inflater.inflate(R.layout.playlist_fragment, container, false);
		}

		togglePlaylistModeButtonBackground(AppEngine.getPlaylistMode());
		registerListeners(view);

		return view;
	}

	@Override
	public void onAttach(Activity a) {
		super.onAttach(a);
		activity = a;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		playlistEntryAdapter = new PlaylistEntryAdapter(getActivity(), 0);
		final ListView playlistEntryList = (ListView) getView().findViewById(R.id.playlistEntryList);
		
		refreshPlaylist(playlistEntryList);
	}

	private void refreshPlaylist(final ListView playlistEntryList) {
		HelperUtil.runInBackground(new Runnable() {
			public void run() {
				List<Track> tracks = playlistService.listAllTracks();
				playlistEntryAdapter.setData(tracks);

				handler.post(new Runnable() {
					public void run() {
						playlistEntryList.setAdapter(playlistEntryAdapter);
						int index = (AppEngine.getCurrentTrack() == null ? 0 :AppEngine.getCurrentTrack().getIndexInPlaylist());
						playlistEntryList.setSelection(index);
						playlistEntryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								onListItemClick(parent, view, position, id);
							}
						});
					}
				});
			}
		});
	}

	public void onListItemClick(final AdapterView<?> parent, final View v, final int position, final long id) {
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

	private void registerListeners(final View v) {
		ImageView clearPlaylist = (ImageView) v.findViewById(R.id.clearPlaylist);
		ImageView repeatPlaylist = (ImageView) v.findViewById(R.id.repeatPlaylist);
		ImageView repeatTrack = (ImageView) v.findViewById(R.id.repeatTrack);
		ImageView randomMode = (ImageView) v.findViewById(R.id.randomMode);

		clearPlaylist.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				runInBackgroundAfterAnimation(v, new Runnable() {
					public void run() {
						playlistService.clearPlaylist();
						previousTrack = null; // ensure updateStatus
						lastUpdate = 0;
					}
				});
			}
		});

		repeatPlaylist.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				runInBackgroundAfterAnimation(v, acquirePlaylistModeChanger(PlaylistService.PlaylistMode.REPEAT_PLAYLIST));
			}
		});

		repeatTrack.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				runInBackgroundAfterAnimation(v, acquirePlaylistModeChanger(PlaylistService.PlaylistMode.REPEAT_TRACK));
			}
		});

		randomMode.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				runInBackgroundAfterAnimation(v, acquirePlaylistModeChanger(PlaylistService.PlaylistMode.RANDOM));
			}
		});

	}

	private Runnable acquirePlaylistModeChanger(final PlaylistService.PlaylistMode targetMode) {
		return new Runnable() {
			public void run() {
				if(AppEngine.getPlaylistMode() == targetMode) {
					playlistService.changePlaylistMode(PlaylistService.PlaylistMode.NORMAL);
					togglePlaylistModeButtonBackgroundInUiThread(PlaylistService.PlaylistMode.NORMAL);
				} else {
					playlistService.changePlaylistMode(targetMode);
					togglePlaylistModeButtonBackgroundInUiThread(targetMode);
				}
			}
		};
	}

	private void togglePlaylistModeButtonBackgroundInUiThread(final PlaylistService.PlaylistMode mode) {
		handler.post(new Runnable() {
			public void run() {
				togglePlaylistModeButtonBackground(mode);
			}
		});
	}

	private void togglePlaylistModeButtonBackground(final PlaylistService.PlaylistMode mode) {
		View v = getView();
		ImageView repeatPlaylist = (ImageView) v.findViewById(R.id.repeatPlaylist);
		ImageView repeatTrack = (ImageView) v.findViewById(R.id.repeatTrack);
		ImageView randomMode = (ImageView) v.findViewById(R.id.randomMode);

		repeatPlaylist.setBackgroundColor(getResources().getColor(R.color.backgroundBlue));
		repeatTrack.setBackgroundColor(getResources().getColor(R.color.backgroundBlue));
		randomMode.setBackgroundColor(getResources().getColor(R.color.backgroundBlue));

		if(mode == PlaylistService.PlaylistMode.REPEAT_PLAYLIST) {
			repeatPlaylist.setBackgroundColor(getResources().getColor(R.color.darkGray));
		} else if(mode == PlaylistService.PlaylistMode.REPEAT_TRACK) {
			repeatTrack.setBackgroundColor(getResources().getColor(R.color.darkGray));
		} else if(mode == PlaylistService.PlaylistMode.RANDOM) {
			randomMode.setBackgroundColor(getResources().getColor(R.color.darkGray));
		}
	}

	public void updateStatus() {
		if(view == null || isUpdateNecessary() == false) {
			return;
		}

		log.info("Update Playlist");

		final ListView playlistEntryList = (ListView) getView().findViewById(R.id.playlistEntryList);
		refreshPlaylist(playlistEntryList);

		lastUpdate = System.currentTimeMillis();
		previousTrack = AppEngine.getCurrentTrack();
	}

	@Override
	protected boolean isUpdateNecessary() {
		if(AppEngine.getCurrentTrack() == null || !super.isUpdateNecessary()) {
			return false;
		}

		return (false == AppEngine.getCurrentTrack().equals(previousTrack));
	}
	
}
