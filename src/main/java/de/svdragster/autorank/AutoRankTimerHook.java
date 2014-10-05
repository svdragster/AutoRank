package de.svdragster.autorank;

import net.canarymod.hook.Hook;

public class AutoRankTimerHook extends Hook {

	/**
	 * Calls when 'current' in AutoRankTimer reaches the 'autosave' time
	 */
	
	private boolean save;
	
	public AutoRankTimerHook(boolean save) {
		this.save = save;
	}
	
	public boolean shouldSave() {
		return save;
	}
	
}
