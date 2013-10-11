package de.mgd.amarok.remote;

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
    		getFragmentManager().beginTransaction().replace(R.id.contentRoot, new SettingsFragment()).addToBackStack(null).commit();
    	} else if(item.getItemId() == R.id.action_playlist) {
    		getFragmentManager().beginTransaction().replace(R.id.contentRoot, new PlaylistFragment()).addToBackStack(null).commit();
    	} else if(item.getItemId() == R.id.action_collection) {
    		getFragmentManager().beginTransaction().replace(R.id.contentRoot, new CollectionFragment()).addToBackStack(null).commit();
    	}
    	
    	return super.onOptionsItemSelected(item);
    }
    
}

