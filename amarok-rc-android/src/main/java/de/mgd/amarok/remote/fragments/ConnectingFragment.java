package de.mgd.amarok.remote.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.mgd.amarok.remote.R;
import de.mgd.amarok.remote.core.AppEngine;

/**
 * Created by iridias on 18.10.13.
 */
public class ConnectingFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.connecting, container, false);

		TextView serverAddress = (TextView) v.findViewById(R.id.infoTextServer);
		serverAddress.setText(AppEngine.remoteHost() + ":" + AppEngine.remotePort());

		return v;
	}

}
