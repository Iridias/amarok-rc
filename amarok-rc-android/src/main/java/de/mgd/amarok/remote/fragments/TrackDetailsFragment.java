package de.mgd.amarok.remote.fragments;

import de.mgd.amarok.remote.R;
import de.mgd.amarok.remote.core.AppEngine;
import de.mgd.amarok.remote.core.util.HelperUtil;
import de.mgd.amarok.remote.model.Track;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class TrackDetailsFragment extends AbstractBaseFragment implements BaseFragment {

	private Track previousTrack = null;
	
	public TrackDetailsFragment() {
		super();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.track_details_fragment, container, false);
		return view;
	}
	
	public void updateStatus() {
		if(view == null || isUpdateNecessary() == false) {
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
