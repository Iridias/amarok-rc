package de.mgd.amarok.remote.fragments;

import de.mgd.amarok.remote.R;
import de.mgd.amarok.remote.model.Track;
import de.mgd.amarok.remote.service.PlayerService;
import de.mgd.amarok.remote.service.PlayerService.PlayerState;
import de.mgd.amarok.remote.core.AppEngine;
import de.mgd.amarok.remote.core.factory.ServiceFactory;
import de.mgd.amarok.remote.core.util.HelperUtil;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class PlayerFragment extends AbstractBaseFragment implements BaseFragment {

	private PlayerState previousState = PlayerState.STOPPED;
	private Track previousTrack = null;
	private PlayerService playerService;
	private Activity activity;
	
	public PlayerFragment() {
		super();
		playerService = ServiceFactory.getPlayerService();
	}

	@Override
	public void onAttach(Activity a) {
		super.onAttach(a);
		activity = a;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(activity == null) {
			view = inflater.inflate(R.layout.player_fragment, container, false);
		} else if(activity.findViewById(R.id.fragment_container1) == null && activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			view = inflater.inflate(R.layout.player_fragment_portrait, container, false);
		} else {
			view = inflater.inflate(R.layout.player_fragment, container, false);
		}

		registerListeners(view);
		return view;
	}
	
	public void onResume() {
		super.onResume();
		previousTrack = null;
		lastUpdate = 0;
	}
	
	private void registerListeners(final View v) {
		ImageView playPause = (ImageView) v.findViewById(R.id.playPause);
		ImageView stop = (ImageView) v.findViewById(R.id.stop);
		ImageView prev = (ImageView) v.findViewById(R.id.prev);
		ImageView next = (ImageView) v.findViewById(R.id.next);
		final ImageView speaker = (ImageView) v.findViewById(R.id.volume);
		SeekBar volumeSeekBar = (SeekBar) getView().findViewById(R.id.volumeBar);
		SeekBar trackPositionSeekBar = (SeekBar) getView().findViewById(R.id.trackPositionBar);
		
		playPause.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				runInBackgroundAfterAnimation(v, new Runnable() {
					public void run() {
						if(AppEngine.getPlayerState() == PlayerState.PLAYING) {
							playerService.pause();
						} else {
							playerService.play();
						}
					}
				});
			}
		});
		
		speaker.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				runInBackgroundAfterAnimation(v, new Runnable() {
					public void run() {
						playerService.mute();
					}
				});
				AppEngine.setMuted(!AppEngine.isMuted());
				if(AppEngine.isMuted()) {
					speaker.setImageResource(R.drawable.speaker_muted);
				} else {
					speaker.setImageResource(R.drawable.speaker);
				}
			}
		});
		
		stop.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				runInBackgroundAfterAnimation(v, new Runnable() {
					public void run() {
						playerService.stop();
					}
				});
			}
		});
		
		prev.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				runInBackgroundAfterAnimation(v, new Runnable() {
					public void run() {
						playerService.previous();
					}
				});
			}
		});
		
		next.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				runInBackgroundAfterAnimation(v, new Runnable() {
					public void run() {
						playerService.next();
					}
				});
			}
		});
		
		volumeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) { }
			
			public void onStartTrackingTouch(SeekBar seekBar) { }
			
			public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
				if(!fromUser) {
					return;
				}
				HelperUtil.runInBackground(new Runnable() {
					public void run() {
						playerService.setVolume(progress);
						AppEngine.setCurrentVolume(progress);
					}
				});
			}
		});
		
		trackPositionSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) { }
			
			public void onStartTrackingTouch(SeekBar seekBar) { }
			
			public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
				if(!fromUser) {
					return;
				}
				HelperUtil.runInBackground(new Runnable() {
					public void run() {
						playerService.jumpToPositionInTrack(progress);
						AppEngine.setCurrentTrackPositionInMs(progress);
					}
				});
			}
		});
	}

	public void updateStatus() {
		if(view == null || AppEngine.getCurrentTrack() == null || isUpdateNecessary() == false) {
			return;
		}
		
		TextView trackPositionTime = (TextView) getView().findViewById(R.id.trackPositionTime);
		trackPositionTime.setText(HelperUtil.convertToTimeString(AppEngine.getCurrentTrackPositionInMs()));
		
		SeekBar volumeSeekBar = (SeekBar) getView().findViewById(R.id.volumeBar);
		volumeSeekBar.setProgress(AppEngine.getCurrentVolume());
		
		updateTrackStatus();
		updateTrackPositionBar();
		togglePlayButtonDependingOnStatus();
		
		lastUpdate = System.currentTimeMillis();
	}
	
	public void updateTrackStatus() {
		if(view == null || isUpdateTrackNecessary() == false) {
			return;
		}
		
		TextView trackTitle = (TextView) getView().findViewById(R.id.trackTitle);
		TextView trackArtist = (TextView) getView().findViewById(R.id.trackArtist);
		TextView trackAlbum = (TextView) getView().findViewById(R.id.trackAlbum);
		TextView trackLength = (TextView) getView().findViewById(R.id.trackLength);
		ImageView trackCover = (ImageView) getView().findViewById(R.id.cover);
		
		trackTitle.setText(AppEngine.getCurrentTrack().getTitle());
		trackArtist.setText(AppEngine.getCurrentTrack().getArtist());
		trackAlbum.setText(AppEngine.getCurrentTrack().getAlbum());
		trackLength.setText(HelperUtil.convertToTimeString(AppEngine.getCurrentTrack().getLength()));
		trackCover.setImageDrawable(AppEngine.getCurrentCover());
		
		//previousTrack = AppEngine.getCurrentTrack();
	}
	
	private void updateTrackPositionBar() {
		if(AppEngine.getCurrentTrack() == null) {
			return;
		}
		
		SeekBar trackPositionSeekBar = (SeekBar) getView().findViewById(R.id.trackPositionBar);
		if(!AppEngine.getCurrentTrack().equals(previousTrack)) {
			trackPositionSeekBar.setMax((int) AppEngine.getCurrentTrack().getLength());
			previousTrack = AppEngine.getCurrentTrack();
		}
		
		trackPositionSeekBar.setProgress((int) AppEngine.getCurrentTrackPositionInMs());
	}
	
	private void togglePlayButtonDependingOnStatus() {
		if(previousState == AppEngine.getPlayerState()) {
			return;
		}
		ImageView button = (ImageView) getView().findViewById(R.id.playPause);

		if(AppEngine.getPlayerState() == PlayerState.PLAYING) {
			button.setImageResource(R.drawable.media_pause);
		} else {
			button.setImageResource(R.drawable.media_start);
		}
		
		previousState = AppEngine.getPlayerState();
	}
	

	protected boolean isUpdateTrackNecessary() {
		if(AppEngine.getCurrentTrack() == null || !isUpdateNecessary()) {
			return false;
		}
		
		return (false == AppEngine.getCurrentTrack().equals(previousTrack));
	}
	
}
