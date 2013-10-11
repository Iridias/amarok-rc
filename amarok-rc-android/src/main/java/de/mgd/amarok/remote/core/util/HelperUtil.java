package de.mgd.amarok.remote.core.util;

import de.mgd.amarok.remote.R;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

public class HelperUtil {

	/**
	 * 
	 * @param lengthInMs
	 * @return as string in the format [hh]:mm:ss
	 */
	public static String convertToTimeString(final long lengthInMs) {
		final long totalSeconds = (lengthInMs < 1000) ? 0 : lengthInMs / 1000;
		
		if(totalSeconds < 3600) { // do not show hours if length is < 1 hour
			return String.format("%02d:%02d", (totalSeconds%3600)/60, (totalSeconds%60));
		}
		
		return String.format("%d:%02d:%02d", totalSeconds/3600, (totalSeconds%3600)/60, (totalSeconds%60));
	}
	
	public static void applyBackgroundColor(final Context context, final int position, final View v) {
		if(position % 2 == 0) {
			v.setBackgroundColor(context.getResources().getColor(R.color.playlistBackground));
		} else {
			v.setBackgroundColor(context.getResources().getColor(R.color.playlistBackgroundAlternate));
		}
	}
	
	public static void runInBackground(final Runnable runnable) {
		new BackgroundRunner().execute(runnable);
	}
	
	static class BackgroundRunner extends AsyncTask<Runnable, Void, Void> {
		@Override
		protected Void doInBackground(Runnable... params) {
			params[0].run();
			return null;
		}
	}
	
}
