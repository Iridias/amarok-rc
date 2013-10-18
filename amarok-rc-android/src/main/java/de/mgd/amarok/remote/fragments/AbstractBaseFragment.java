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
