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
import de.mgd.amarok.remote.service.AmarokService;
import de.mgd.amarok.remote.service.PlayerService;
import de.mgd.amarok.remote.service.PlayerService.PlayerState;
import de.mgd.amarok.remote.core.factory.ServiceFactory;
import de.mgd.amarok.remote.service.PlaylistService;

public class AppEngine extends Application {

	private static final Logger log = LoggerFactory.getLogger(AppEngine.class);

	private static int backendVersion = -1;
	private static PlayerService.PlayerState playerState = PlayerState.STOPPED;
	private static Track currentTrack;
	private static long currentTrackPositionInMs = 0;
	private static Drawable currentCover;
	private static Drawable noCover;
	private static int currentVolume;
	private static boolean isMuted = false;
	private static PlaylistService.PlaylistMode playlistMode;
	
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

		startBackgroundJobs();
	}

	public static void notifySettingsChanged() {
		ServiceFactory.invalidate();
		communicationService.setPlayerService(ServiceFactory.getPlayerService());
		communicationService.setAmarokService(ServiceFactory.getAmarokService());
		communicationService.setPlaylistService(ServiceFactory.getPlaylistService());
	}

	public static void startBackgroundJobs() {
		if(communicationService != null && communicationService.isRunning()) {
			return;
		}

		communicationService = new CommunicationService();
		communicationService.setPriority(Thread.MIN_PRIORITY);
		communicationService.start();
	}

	public static void stopBackgroundJobs() {
		if(communicationService != null) {
			communicationService.requestStop();
		}
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

	public static PlaylistService.PlaylistMode getPlaylistMode() {
		return playlistMode;
	}

	public static void setPlaylistMode(PlaylistService.PlaylistMode playlistMode) {
		AppEngine.playlistMode = playlistMode;
	}

	public static int getBackendVersion() {
		return backendVersion;
	}

	public static void setBackendVersion(int backendVersion) {
		AppEngine.backendVersion = backendVersion;
	}
}
