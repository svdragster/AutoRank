package de.svdragster.autorank;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.hook.Hook;

public class RankUpHook extends Hook {
	
	private RankUp rankUp;
	private Player player;
	
	public RankUpHook(Player player, RankUp rankUp) {
		this.rankUp = rankUp;
		this.player = player;
	}
	
	public RankUp getRankUp() {
		return rankUp;
	}
	
	public Player getPlayer() {
		return player;
	}
}
