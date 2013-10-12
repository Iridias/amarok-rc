package de.mgd.amarok.remote;

import de.mgd.amarok.remote.core.AppEngine;
import de.mgd.amarok.remote.fragments.CollectionFragment;
import de.mgd.amarok.remote.fragments.PlayerFragment;
import de.mgd.amarok.remote.fragments.PlaylistFragment;
import de.mgd.amarok.remote.fragments.SettingsFragment;
import de.mgd.amarok.remote.fragments.TrackDetailsFragment;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class BaseActivity extends Activity {

	private enum Fragments { PLAYER, PLAYLIST, COLLECTION, SETTINGS };
	private Fragments currentFragment = Fragments.PLAYER;

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after 
     * previously being shut down then this Bundle contains the data it most 
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (savedInstanceState != null) {
            return;
        }
        
        // FIXME: check if wifi is turned on

        AppEngine.startBackgroundJobs();
        PlayerFragment playerFragment = new PlayerFragment();
        TrackDetailsFragment trackDetailsFragment = new TrackDetailsFragment();

        getFragmentManager().beginTransaction().add(R.id.contentRoot, playerFragment).commit();
        //getFragmentManager().beginTransaction().add(R.id.contentRoot, trackDetailsFragment).commit();
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(de.mgd.amarok.remote.R.menu.main, menu);
		return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if(item.getItemId() == R.id.action_settings) {
			currentFragment = Fragments.SETTINGS;
    		getFragmentManager().beginTransaction().replace(R.id.contentRoot, new SettingsFragment()).commit();
    	} else if(item.getItemId() == R.id.action_playlist) {
			currentFragment = Fragments.PLAYLIST;
    		getFragmentManager().beginTransaction().replace(R.id.contentRoot, new PlaylistFragment()).commit();
    	} else if(item.getItemId() == R.id.action_collection) {
			currentFragment = Fragments.COLLECTION;
    		getFragmentManager().beginTransaction().replace(R.id.contentRoot, new CollectionFragment()).commit();
    	}
    	
    	return super.onOptionsItemSelected(item);
    }

	@Override
	public void onBackPressed() {
		if(currentFragment != Fragments.PLAYER) {
			currentFragment = Fragments.PLAYER;
			getFragmentManager().beginTransaction().replace(R.id.contentRoot, new PlayerFragment()).commit();
			return;
		}

		AppEngine.stopBackgroundJobs();
		finish();
	}
    
}

