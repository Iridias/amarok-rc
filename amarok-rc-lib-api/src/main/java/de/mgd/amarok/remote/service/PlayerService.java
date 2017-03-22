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
package de.mgd.amarok.remote.service;

import de.mgd.amarok.remote.model.Track;

public interface PlayerService {

	enum PlayerState { PLAYING, STOPPED, PAUSED };
	
	/**
	 * Wrapper for Amarok.Engine.engineState()
	 * 
	 * @return
	 */
	PlayerState state();

	Track currentTrack();

	long trackPositionMs();

	void play();
	
	void pause();
	
	void stop();
	
	void forceStop();
	
	void togglePlayPause();
	
	void next();
	
	void previous();
	
	void mute();
	
	/**
	 * Same as {@link #volumeUp(int)} but defaults to 5 ticks.
	 * 
	 */
	void volumeUp();
	
	/**
	 * Increases the volume by the specified amount of ticks.
	 * 
	 * @param ticks must be between 1 and 99
	 */
	void volumeUp(final int ticks);
	
	/**
	 * Same as {@link #volumeDown(int)} but defaults to 5 ticks.
	 * 
	 */
	void volumeDown();
	
	/**
	 * Decreases the volume by the specified amount of ticks.
	 * 
	 * @param ticks must be between 1 and 99
	 */
	void volumeDown(final int ticks);
	
	/**
	 * 
	 * @param positionInMs
	 */
	void jumpToPositionInTrack(final int positionInMs);

	byte[] currentCover();

	int currentVolume();

	void setVolume(int volume);
	
}
