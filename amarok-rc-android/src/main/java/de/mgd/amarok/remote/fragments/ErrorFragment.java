package de.mgd.amarok.remote.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.mgd.amarok.remote.R;

/**
 * Created by iridias on 18.10.13.
 */
public class ErrorFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.error, container, false);
	}
}
