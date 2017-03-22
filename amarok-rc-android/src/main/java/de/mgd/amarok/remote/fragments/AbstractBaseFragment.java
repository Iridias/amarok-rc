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

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Fragment;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import de.mgd.amarok.remote.R;
import de.mgd.amarok.remote.core.util.HelperUtil;

public abstract class AbstractBaseFragment extends Fragment {

	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	protected long pauseBetweenUpdatesInMs = 500;
	protected long lastUpdate = 0;
	
	protected View view;
	protected Timer timer;
	protected Handler handler = new Handler();
	
	public AbstractBaseFragment() {
		timer = new Timer();
        timer.schedule(new TimerTask() {
			@Override
			public void run() {
				updateAsync();
			}
		}, 500, 500);
	}
	
	protected boolean isUpdateNecessary() {
		return System.currentTimeMillis() > (lastUpdate + pauseBetweenUpdatesInMs);
	}
	
	public void updateAsync() {
		handler.post(new Runnable() {
			public void run() {
				updateStatus();
			}
		});
	}
	
	@Override
	public View getView() {
		View v = super.getView();
		if(v != null) {
			return v;
		}
		
		//log.error("getView returned null for fragment view!!!");
		return view;
	}
	
	public abstract void updateStatus();


	protected void runInBackgroundAfterAnimation(final View v, final Runnable r) {
		Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.buttonpress);
		anim.setAnimationListener(new ButtonPressAnimationBackgroundRunner(r));
		v.startAnimation(anim);
	}

	static class ButtonPressAnimationBackgroundRunner implements Animation.AnimationListener {
		private Runnable r = null;

		public ButtonPressAnimationBackgroundRunner(final Runnable r) {
			this.r = r;
		}

		public void onAnimationEnd(Animation animation) {
			HelperUtil.runInBackground(r);
		}

		public void onAnimationRepeat(Animation animation) { }

		public void onAnimationStart(Animation animation) { }
	}
}
