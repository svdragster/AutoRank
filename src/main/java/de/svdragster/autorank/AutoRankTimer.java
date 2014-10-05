package de.svdragster.autorank;

import java.util.TimerTask;

import net.canarymod.Canary;

public class AutoRankTimer extends TimerTask {

	/**
	 * Runs every minute to check the stats, especially playtime.
	 */
	
	private int autosave; // When the plugin should save the stuff into the files
	private int current;
	
	public AutoRankTimer(int autosave) {
		this.autosave = autosave;
		this.current = 0;
	}
	
	@Override
	public void run() {
		current++;
		boolean save = false;
		if (current >= autosave) {
			current = 0;
			save = true;
		}
		Canary.hooks().callHook(new AutoRankTimerHook(save));
	}	
}
