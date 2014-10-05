package de.svdragster.autorank;

import java.util.HashMap;

import net.canarymod.api.entity.EntityType;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.blocks.BlockType;

public class RankUp {

	private HashMap<BlockType, Integer> minDestroyed;
	private HashMap<BlockType, Integer> minPlaced;
	private HashMap<EntityType, Integer> minKilled;
	private HashMap<ItemType, Integer> minCrafted;
	private HashMap<ItemType, Integer> minHave; // minimal amount of an item type a player needs to have in his inventory
	private int minTime;
	private int minLevel;
	private String currentGroup; // the group the player needs to be at the moment
	private String newGroup; // the group the player will be moved to when he fulfills the things above
	//private String id; // The filename without .properties
	
	public RankUp(String currentGroup) {
		minDestroyed = new HashMap<BlockType, Integer>();
		minPlaced = new HashMap<BlockType, Integer>();
		minKilled = new HashMap<EntityType, Integer>();
		minCrafted = new HashMap<ItemType, Integer>();
		minHave = new HashMap<ItemType, Integer>();
		minTime = 0;
		minLevel = 0;
		this.currentGroup = currentGroup;
		newGroup = "";
		//this.id = id;
	}
	
	/*public String getId() {
		return id;
	}*/
	
	public int getMinDestroyed(BlockType type) {
		if (minDestroyed.containsKey(type)) {
			return minDestroyed.get(type);
		}
		return 0;
	}
	
	public HashMap<BlockType, Integer> getDestroyed() {
		return minDestroyed;
	}
	
	public void setMinDestroyed(BlockType type, int num) {
		if (num > 0) {
			minDestroyed.put(type, num);
		} else {
			if (minDestroyed.containsKey(type)) {
				minDestroyed.remove(type);
			}
		}
	}
	
	public int getMinPlaced(BlockType type) {
		if (minPlaced.containsKey(type)) {
			return minPlaced.get(type);
		}
		return 0;
	}
	
	public HashMap<BlockType, Integer> getPlaced() {
		return minPlaced;
	}
	
	public void setMinPlaced(BlockType type, int num) {
		if (num > 0) {
			minPlaced.put(type, num);
		} else {
			if (minPlaced.containsKey(type)) {
				minPlaced.remove(type);
			}
		}
	}
	
	public int getMinKilled(EntityType type) {
		if (minKilled.containsKey(type)) {
			return minKilled.get(type);
		}
		return 0;
	}
	
	public HashMap<EntityType, Integer> getKilled() {
		return minKilled;
	}
	
	public void setMinKilled(EntityType type, int num) {
		if (num > 0) {
			minKilled.put(type, num);
		} else {
			if (minKilled.containsKey(type)) {
				minKilled.remove(type);
			}
		}
	}
	
	public int getMinCrafted(ItemType type) {
		if (minCrafted.containsKey(type)) {
			return minCrafted.get(type);
		}
		return 0;
	}
	
	public HashMap<ItemType, Integer> getCrafted() {
		return minCrafted;
	}
	
	public void setMinCrafted(ItemType type, int num) {
		if (num > 0) {
			minCrafted.put(type, num);
		} else {
			if (minCrafted.containsKey(type)) {
				minCrafted.remove(type);
			}
		}
	}
	
	public int getMinAmount(ItemType type) {
		if (minHave.containsKey(type)) {
			return minHave.get(type);
		}
		return 0;
	}
	
	public HashMap<ItemType, Integer> getHave() {
		return minHave;
	}
	
	public void setMinAmount(ItemType type, int num) {
		if (num > 0) {
			minHave.put(type, num);
		} else {
			if (minHave.containsKey(type)) {
				minHave.remove(type);
			}
		}
	}
	
	public int getMinTime() {
		return minTime;
	}
	
	public void setMinTime(int num) {
		minTime = num;
	}
	
	public int getMinLevel() {
		return minLevel;
	}
	
	public void setMinLevel(int minLevel) {
		this.minLevel = minLevel;
	}
	
	public String getCurrentGroup() {
		return currentGroup;
	}
	
	public void setCurrentGroup(String str) {
		currentGroup = str;
	}
	
	public String getNewGroup() {
		return newGroup;
	}
	
	public void setNewGroup(String str) {
		newGroup = str;
	}
}
