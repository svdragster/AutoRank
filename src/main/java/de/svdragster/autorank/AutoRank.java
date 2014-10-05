package de.svdragster.autorank;

import java.util.Timer;
import java.util.TimerTask;

import net.canarymod.Canary;
import net.canarymod.commandsys.CommandDependencyException;
import net.canarymod.plugin.Plugin;

public class AutoRank extends Plugin {

	AutoRankListener listener = new AutoRankListener(true);
	AutoRankTimer timer = new AutoRankTimer(30);
	
	@Override
	public void disable() {
		timer.cancel();
		listener.saveRankUps();
		AutoRankListener.allRankUps.clear();
	}

	@Override
	public boolean enable() {
		Canary.hooks().registerListener(listener, this);
		listener.loadRankUps();
		
		try {
			Canary.commands().registerCommands(new AutoRankCommands(), this, false);
		} catch (CommandDependencyException e) {
			e.printStackTrace();
		}
		
		TimerTask task = timer;
		Timer timer = new Timer();
		timer.schedule(task, 60000,60000);
		return true;
	}
}
