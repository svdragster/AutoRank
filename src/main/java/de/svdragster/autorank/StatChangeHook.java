package de.svdragster.autorank;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.statistics.Stat;
import net.canarymod.hook.Hook;

public class StatChangeHook extends Hook {

	private Player player;
	private Stat stat;
	
	public StatChangeHook(Player player, Stat stat) {
		this.player = player;
		this.stat = stat;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Stat getStat() {
		return stat;
	}
}
