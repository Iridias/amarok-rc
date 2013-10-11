package de.mgd.amarok.remote.core;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import de.mgd.amarok.remote.R;
import de.mgd.amarok.remote.model.Track;
import de.mgd.amarok.remote.service.PlayerService;
import de.mgd.amarok.remote.service.PlayerService.PlayerState;
import de.mgd.amarok.remote.core.factory.ServiceFactory;

public class AppEngine extends Application {

	private static final Logger log = LoggerFactory.getLogger(AppEngine.class);
	
	private static PlayerService.PlayerState playerState = PlayerState.STOPPED;
	private static Track currentTrack;
	private static long currentTrackPositionInMs = 0;
	private static Drawable currentCover;
	private static Drawable noCover;
	private static int currentVolume;
	private static boolean isMuted = false;
	
	private static final String SETTINGS = "AmarokRemoteSettings";
	private static final String SETTING_REMOTE_HOST = "RemoteHost";
	private static final String SETTING_REMOTE_PORT = "RemotePort";
	
	private static AppEngine instance;
	private static CommunicationService communicationService; 
	
	public void onCreate() {
		log.info("Starting AppEngine...");
		instance = this;
		ServiceFactory.init();
		
		noCover = getResources().getDrawable(R.drawable.nocover);
		
		communicationService = new CommunicationService();
		communicationService.setPriority(Thread.MIN_PRIORITY);
		communicationService.start();
	}
	

	public static PlayerService.PlayerState getPlayerState() {
		return playerState;
	}

	public static void setPlayerState(PlayerService.PlayerState playerState) {
		AppEngine.playerState = playerState;
	}

	public static Track getCurrentTrack() {
		return currentTrack;
	}

	public static void setCurrentTrack(final Track currentTrack) {
		AppEngine.currentTrack = currentTrack;
	}

	public static long getCurrentTrackPositionInMs() {
		return currentTrackPositionInMs;
	}

	public static void setCurrentTrackPositionInMs(final long currentTrackPositionInMs) {
		AppEngine.currentTrackPositionInMs = currentTrackPositionInMs;
	}
	
	public static String remoteHost() {
		SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(instance);
		return setting.getString(SETTING_REMOTE_HOST, "");
	}
	public static int remotePort() {
		SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(instance);
		
		return NumberUtils.toInt(setting.getString(SETTING_REMOTE_PORT, "8484"));
	}

	public static Drawable getCurrentCover() {
		return currentCover;
	}

	public static void setCurrentCover(Drawable currentCover) {
		AppEngine.currentCover = currentCover;
	}

	public static int getCurrentVolume() {
		return currentVolume;
	}

	public static void setCurrentVolume(int currentVolume) {
		AppEngine.currentVolume = currentVolume;
	}

	public static boolean isMuted() {
		return isMuted;
	}

	public static void setMuted(boolean isMuted) {
		AppEngine.isMuted = isMuted;
	}

	public static Drawable getNoCover() {
		return noCover;
	}
	
}
