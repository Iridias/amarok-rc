package de.mgd.amarok.remote;

import de.mgd.amarok.remote.core.AppEngine;
import de.mgd.amarok.remote.core.util.HelperUtil;
import de.mgd.amarok.remote.fragments.CollectionFragment;
import de.mgd.amarok.remote.fragments.ConnectingFragment;
import de.mgd.amarok.remote.fragments.ErrorFragment;
import de.mgd.amarok.remote.fragments.PlayerFragment;
import de.mgd.amarok.remote.fragments.PlaylistFragment;
import de.mgd.amarok.remote.fragments.SettingsFragment;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

public class BaseActivity extends Activity {

	private enum Fragments { LOADING, ERROR, PLAYER, PLAYLIST, COLLECTION, SETTINGS };
	private static Fragments currentFragment = Fragments.LOADING;
	private static Fragments previousFragment = Fragments.LOADING;
	private static final int REQUIRED_BACKEND_VERSION = 6;
	private static boolean connectivity = false;
	private static boolean validBackend = false;
	private Handler handler = new Handler();

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after 
     * previously being shut down then this Bundle contains the data it most 
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			setContentView(R.layout.activity_main_portrait);
		} else {
			setContentView(R.layout.activity_main);
		}

		if(connectivity) {
			updateActivity(savedInstanceState);
			return;
		}

		if(findViewById(R.id.fragment_container1) != null) {
			getFragmentManager().beginTransaction().add(R.id.fragment_container1, new ConnectingFragment()).commit();
		} else {
			getFragmentManager().beginTransaction().add(R.id.contentRoot, new ConnectingFragment()).commit();
		}

		AppEngine.startBackgroundJobs();
		loadActivityAsync(savedInstanceState);
    }


	private void loadActivityAsync(final Bundle savedInstanceState) {
		HelperUtil.runInBackground(new Runnable() {
			@Override
			public void run() {
				while(AppEngine.getBackendVersion() <= 0) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						/* empty by design */
					}
				}

				connectivity = true;
				handler.post(new Runnable() {
					@Override
					public void run() {
						updateActivity(savedInstanceState);
					}
				});
			}
		});
	}

	private void updateActivity(final Bundle savedInstanceState) {
		if(REQUIRED_BACKEND_VERSION > AppEngine.getBackendVersion()) {
			validBackend = false;
			currentFragment = Fragments.ERROR;
			if(findViewById(R.id.fragment_container1) != null) {
				getFragmentManager().beginTransaction().replace(R.id.fragment_container1, new ErrorFragment()).commit();
			} else {
				getFragmentManager().beginTransaction().replace(R.id.contentRoot, new ErrorFragment()).commit();
			}
			return;
		}

		validBackend = true;
		if (savedInstanceState != null) {
			return;
		}

		previousFragment = currentFragment;
		currentFragment = Fragments.PLAYER;

		PlayerFragment playerFragment = new PlayerFragment();

		if(findViewById(R.id.fragment_container1) != null) {
			getFragmentManager().beginTransaction().replace(R.id.fragment_container1, playerFragment).commit();
			getFragmentManager().beginTransaction().add(R.id.fragment_container2, new PlaylistFragment()).commit();
		} else {
			getFragmentManager().beginTransaction().replace(R.id.contentRoot, playerFragment).commit();
		}

		invalidateOptionsMenu();
	}

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
		if(connectivity && validBackend) {
			menu.clear();
			getMenuInflater().inflate(de.mgd.amarok.remote.R.menu.main, menu);
		} else {
			getMenuInflater().inflate(R.menu.loading_menu, menu);
		}
		return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		int contentRoot = R.id.contentRoot;
		if(findViewById(R.id.fragment_container1) != null) {
			contentRoot = R.id.fragment_container2;
		}

    	if(item.getItemId() == R.id.action_settings) {
			previousFragment = currentFragment;
			currentFragment = Fragments.SETTINGS;
    		getFragmentManager().beginTransaction().replace(contentRoot, new SettingsFragment()).commit();
    	} else if(item.getItemId() == R.id.action_playlist) {
			previousFragment = currentFragment;
			currentFragment = Fragments.PLAYLIST;
    		getFragmentManager().beginTransaction().replace(contentRoot, new PlaylistFragment()).commit();
    	} else if(item.getItemId() == R.id.action_collection) {
			previousFragment = currentFragment;
			currentFragment = Fragments.COLLECTION;
    		getFragmentManager().beginTransaction().replace(contentRoot, new CollectionFragment()).commit();
    	}
    	
    	return super.onOptionsItemSelected(item);
    }

	@Override
	public void onBackPressed() {
		if(currentFragment == Fragments.ERROR || currentFragment == Fragments.LOADING) {
			AppEngine.stopBackgroundJobs();
			finish();
			return;
		}
		if(previousFragment == Fragments.ERROR) {
			currentFragment = Fragments.ERROR;
			if(findViewById(R.id.fragment_container1) == null) {
				getFragmentManager().beginTransaction().replace(R.id.contentRoot, new ErrorFragment()).commit();
			}
			return;
		}
		if(previousFragment == Fragments.LOADING) {
			currentFragment = Fragments.LOADING;
			if(findViewById(R.id.fragment_container1) == null) {
				getFragmentManager().beginTransaction().replace(R.id.contentRoot, new ConnectingFragment()).commit();
			}
			return;
		}

		if(currentFragment != Fragments.PLAYER) {
			currentFragment = Fragments.PLAYER;
			if(findViewById(R.id.fragment_container1) != null) {
				getFragmentManager().beginTransaction().replace(R.id.fragment_container2, new PlaylistFragment()).commit();
			} else {
				getFragmentManager().beginTransaction().replace(R.id.contentRoot, new PlayerFragment()).commit();
			}
			return;
		}

		AppEngine.stopBackgroundJobs();
		finish();
	}
    
}

