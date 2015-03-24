package de.svdragster.autorank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import net.canarymod.Canary;
import net.canarymod.api.entity.EntityType;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.factory.StatisticsFactory;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.inventory.PlayerInventory;
import net.canarymod.api.statistics.Stat;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.chat.ChatFormat;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.player.ConnectionHook;
import net.canarymod.hook.player.InventoryHook;
import net.canarymod.hook.player.ItemPickupHook;
import net.canarymod.hook.player.LevelUpHook;
import net.canarymod.hook.player.StatGainedHook;
import net.canarymod.plugin.PluginListener;
import net.canarymod.user.Group;
import net.visualillusionsent.utils.PropertiesFile;

public class AutoRankListener implements PluginListener {

	//public static ArrayList<PlayerStats> allPlayerStats = new ArrayList<PlayerStats>();
	public static ArrayList<RankUp> allRankUps = new ArrayList<RankUp>();
	private String VERSION;
	private AutoRank owner;
	ArrayList<String> stats = new ArrayList<String>();
	public static final String USEITEM = "stat.useItem.";
	public static final String MINEBLOCK = "stat.mineBlock.";
	public static final String KILLENTITY = "stat.killEntity.";
	public static final String CRAFTITEM = "stat.craftItem.";
	public static HashMap<EntityType, String> mobs = new HashMap<EntityType, String>();

	public AutoRankListener(AutoRank owner, boolean replace) {
		this.setOwner(owner);
		this.VERSION = owner.getVersion();
		if (replace) { 
			stats.addAll(Arrays.asList(new String[]{USEITEM, MINEBLOCK, KILLENTITY, CRAFTITEM}));
			ArrayList<String> strMobs = new ArrayList<String>();
			strMobs.addAll(Arrays.asList(new String[]{"Creeper", "Skeleton", "Spider", "Zombie", "Giant", "Slime", "Ghast", "PigZombie", "Enderman", "CaveSpider", "Silverfish", "Blaze", "LavaSlime", "EnderDragon", "WitherBoss", "Witch", "Endermite", "Guardian", "Bat", "Pig", "Sheep", "Cow", "Chicken", "Squid", "Wolf", "MushroomCow", "SnowMan", "Ozelot", "VillagerGolem", "EntityHorse", "Rabbit", "Villager"})); // http://minecraft.gamepedia.com/Data_values/Entity_IDs
			ArrayList<EntityType> entityTypeMobs = new ArrayList<EntityType>();
			entityTypeMobs.addAll(Arrays.asList(new EntityType[]{EntityType.CREEPER, EntityType.SKELETON, EntityType.SPIDER, EntityType.ZOMBIE, EntityType.GIANTZOMBIE, EntityType.SLIME, EntityType.GHAST,
					EntityType.PIGZOMBIE, EntityType.ENDERMAN, EntityType.CAVESPIDER, EntityType.SILVERFISH, EntityType.BLAZE, EntityType.MAGMACUBE, EntityType.ENDERDRAGON, EntityType.WITHER, EntityType.WITCH, null /* Endermite */, null /* Guardian */, EntityType.BAT, EntityType.PIG, EntityType.SHEEP, EntityType.COW, EntityType.CHICKEN, EntityType.SQUID, EntityType.WOLF, EntityType.MOOSHROOM, EntityType.SNOWMAN, EntityType.OCELOT, EntityType.IRONGOLEM, EntityType.HORSE, null /* Rabbit */, EntityType.VILLAGER}));
			for (int i=0; i<strMobs.size(); i++) {
				String str = strMobs.get(i);
				EntityType type = entityTypeMobs.get(i);
				if (type != null) {
					mobs.put(type, str);
					//Canary.getServer().broadcastMessage(type.name() + ": " + str);
				}
			}
		}
	}
	
	
	
	public boolean removeRankUp(String group) {
		RankUp rankUp = getRankUpFromGroup(group);
		if (rankUp != null) {
			allRankUps.remove(rankUp);
			return true;
		}
		return false;
	}
	
	/**
	 * Updates the RankUp in the arraylist to the new one
	 * @param rankUp
	 */
	public void overwriteRankUp(RankUp rankUp) {
		for (int i=0; i<allRankUps.size(); i++) {
			RankUp r = allRankUps.get(i);
			if (r.getCurrentGroup().equals(rankUp.getCurrentGroup())) {
				allRankUps.set(i, rankUp);
			}
		}
	}
	
	public void saveRankUps() {
		ArrayList<RankUp> rankUps = new ArrayList<RankUp>();
		rankUps.addAll(allRankUps);
		for (int i=0; i<rankUps.size(); i++) {
			RankUp rankUp = rankUps.get(i);
			HashMap<BlockType, Integer> minDestroyed = rankUp.getDestroyed();
			HashMap<BlockType, Integer> minPlaced = rankUp.getPlaced();
			HashMap<EntityType, Integer> minKilled = rankUp.getKilled();
			HashMap<ItemType, Integer> minCrafted = rankUp.getCrafted();
			HashMap<ItemType, Integer> minAmount = rankUp.getHave();
			int time = rankUp.getMinTime();
			int level = rankUp.getMinLevel();
			String currentGroup = rankUp.getCurrentGroup();
			String newGroup = rankUp.getNewGroup();
			File dir = new File("config/autorank/");
			if (!dir.exists()) {
				dir.mkdir();
			}
			PropertiesFile props = new PropertiesFile("config/autorank/" + currentGroup + ".properties");
			if (minDestroyed != null && !minDestroyed.isEmpty()) {
				for (BlockType type : minDestroyed.keySet()) {
					props.setInt("destroyed_" + type.getId(), minDestroyed.get(type));
				}
			}
			if (minPlaced != null && !minPlaced.isEmpty()) {
				for (BlockType type : minPlaced.keySet()) {
					props.setInt("placed_" + type.getId(), minPlaced.get(type));
				}
			}
			if (minKilled != null && !minKilled.isEmpty()) {
				for (EntityType type : minKilled.keySet()) {
					props.setInt("killed_" + type.name(), minKilled.get(type));
				}
			}
			if (minCrafted != null && !minCrafted.isEmpty()) {
				for (ItemType type : minCrafted.keySet()) {
					props.setInt("crafted_" + type.getId(), minCrafted.get(type));
				}
			}
			if (minAmount != null && !minAmount.isEmpty()) {
				for (ItemType type : minAmount.keySet()) {
					props.setInt("amount_" + type.getId(), minAmount.get(type));
				}
			}
			if (time > 0) {
				props.setInt("time", time);
			}
			if (level > 0) {
				props.setInt("level", level);
			}
			if (currentGroup != null && !currentGroup.isEmpty()) {
				props.setString("currentGroup", currentGroup);
			}
			if (newGroup != null && !newGroup.isEmpty()) {
				props.setString("newGroup", newGroup);
			}
			props.save();
		}
	}
	
	public void loadRankUps() {
		File dir = new File("config/autorank/");
		if (!dir.exists()) {
			dir.mkdir();
		} else {
			File[] files = new File("config/autorank/").listFiles();
			showFiles(files);
		}
	}
	
	public static void showFiles(File[] files) {
	    for (File file : files) {
	        if (!file.isDirectory()) {
	        	BufferedReader br = null;
	        	ArrayList<String> lines = new ArrayList<String>();
	    		try {
	     
	    			String sCurrentLine;
	     
	    			br = new BufferedReader(new FileReader(file));
	     
	    			while ((sCurrentLine = br.readLine()) != null) {
	    				if (!sCurrentLine.startsWith("#") && !sCurrentLine.startsWith(";")) {
	    					lines.add(sCurrentLine);
	    				}
	    			}
	     
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		} finally {
	    			try {
	    				if (br != null)
	    					br.close();
	    			} catch (IOException ex) {
	    				ex.printStackTrace();
	    			}
	    		}
	    		
	    		RankUp rankUp = new RankUp(file.getName().split(".properties")[0]); // Id is the filename without .properties
	    		for (int i=0; i<lines.size(); i++) {
	    			String line = lines.get(i);
	    			if (line.contains("=")) {
	    				String key = line.split("=")[0];
	    				String value = line.split("=")[1];
		    			if (key.startsWith("destroyed")) {
		    				if (key.contains("_")) {
		    					int id = Integer.parseInt(key.split("_")[1]);
		    					BlockType type = BlockType.fromId(id);
		    					int amount = Integer.parseInt(value);
		    					rankUp.setMinDestroyed(type, amount);
		    				}
		    			} else if (key.startsWith("placed")) {
		    				if (key.contains("_")) {
		    					int id = Integer.parseInt(key.split("_")[1]);
		    					BlockType type = BlockType.fromId(id);
		    					int amount = Integer.parseInt(value);
		    					rankUp.setMinPlaced(type, amount);
		    				}
		    			} else if (key.startsWith("killed")) {
		    				if (key.contains("_")) {
		    					String strType = key.split("_")[1];
		    					EntityType type = EntityType.valueOf(strType);
		    					int amount = Integer.parseInt(value);
		    					rankUp.setMinKilled(type, amount);
		    				}
		    			} else if (key.startsWith("crafted")) {
		    				if (key.contains("_")) {
		    					int id = Integer.parseInt(key.split("_")[1]);
		    					ItemType type = ItemType.fromId(id);
		    					int amount = Integer.parseInt(value);
		    					rankUp.setMinCrafted(type, amount);
		    				}
		    			} else if (key.startsWith("amount")) {
		    				if (key.contains("_")) {
		    					int id = Integer.parseInt(key.split("_")[1]);
		    					ItemType type = ItemType.fromId(id);
		    					int amount = Integer.parseInt(value);
		    					rankUp.setMinAmount(type, amount);
		    				}
		    			} else if (key.startsWith("time")) {
	    					int amount = Integer.parseInt(value);
	    					rankUp.setMinTime(amount);
		    			} else if (key.startsWith("level")) {
	    					int amount = Integer.parseInt(value);
	    					rankUp.setMinLevel(amount);
		    			} else if (key.startsWith("currentGroup")) {
	    					String currentGroup = value;
	    					rankUp.setCurrentGroup(currentGroup);
		    			} else if (key.startsWith("newGroup")) {
	    					String newGroup = value;
	    					rankUp.setNewGroup(newGroup);
		    			}
	    			}
	    		}
	    		allRankUps.add(rankUp);
	        }
	    }
	}
	
	
	@HookHandler
	public void onRankUp(RankUpHook hook) {
		Player player = hook.getPlayer();
		if (player.hasPermission("autorank.player.rankup")) {
			RankUp rankUp = hook.getRankUp();
			String newGroupStr = rankUp.getNewGroup();
			Group currentGroup = player.getGroup();
			Group newGroup = (Canary.usersAndGroups().getGroup(newGroupStr));
			if (newGroupStr != null && !newGroupStr.isEmpty()) {
				if (!currentGroup.getName().equals(newGroupStr)) {
					if (currentGroup.hasControlOver(newGroup)) { // Prevents downgrading, e.g. Admin to Moderator or Player to Visitor
						return;
					}
					player.setGroup(newGroup);
					Canary.getServer().broadcastMessage(ChatFormat.GOLD + "[AutoRank] " + ChatFormat.GREEN + player.getName() + ChatFormat.GREEN + " has ranked up to " + ChatFormat.GREEN + newGroupStr + ChatFormat.GREEN + "!");
				}
			}
		}
	}
	
	@HookHandler
	public void onAutoRankSave(AutoRankTimerHook hook) {
		ArrayList<Player> players = new ArrayList<Player>();
		players.addAll(Canary.getServer().getPlayerList());
		for (int i=0; i<players.size(); i++) {
			checkStats(players.get(i));
		}
		if (hook.shouldSave()) {
			saveRankUps();
		}
	}
	
	@HookHandler
	public void onStatChange(StatChangeHook hook) {
		checkStats(hook.getPlayer());
	}
	
	@HookHandler
	public void onStatGained(StatGainedHook hook) {
		Player player = hook.getPlayer();
		Stat stat = hook.getStat();
		ArrayList<String> allStats = new ArrayList<String>();
		allStats.addAll(stats);
		for (int i=0; i<allStats.size(); i++) {
			String tempstat = allStats.get(i);
			if (stat.getId().contains(tempstat)) {
				Canary.hooks().callHook(new StatChangeHook(player, stat));
			}
		}
	}
	
	@HookHandler
	public void onLogin(ConnectionHook hook) {
		Player player = hook.getPlayer();
		if (player.hasPermission("autorank.admin.checkforupdates")) {
			try {
				String result = sendGet(player.getName());
				if ((result != null) && (!result.isEmpty())) {
					player.message(result);
				}
			} catch (Error e) {
				Canary.getServer().broadcastMessageToAdmins(ChatFormat.GOLD + "[AutoRank] " + ChatFormat.RED + e.getMessage());
			} catch (Exception e) {
				Canary.getServer().broadcastMessageToAdmins(ChatFormat.GOLD + "[AutoRank] " + ChatFormat.RED + e.getMessage());
			}
		}
	}
	
	public String sendGet(String playername) throws Exception {
		String MYIDSTART = "svdragster>";
		String MYIDEND = "<svdragster";
		String url = "http://svdragster.dtdns.net/checkupdate.php?version=" + VERSION
				+ "&plugin=autorank&player=" + playername;

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setRequestMethod("GET");

		con.setRequestProperty("User-Agent", "canary_minecraft");

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));

		StringBuffer response = new StringBuffer();
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		String result = response.toString();
		if ((result.contains(MYIDSTART)) && (result.contains(MYIDEND))) {
			int endPos = result.indexOf(MYIDEND);
			result = "§6[AutoRank] §2Update available at: §f"
					+ result.substring(MYIDSTART.length(), endPos);
		}
		return result;
	}
	
	public RankUp getRankUpFromGroup(String group) {
		ArrayList<RankUp> rankups = new ArrayList<RankUp>();
		rankups.addAll(allRankUps);
		for (int i=0; i<rankups.size(); i++) {
			RankUp rankup = rankups.get(i);
			String minGroup = rankup.getCurrentGroup();
			if (group.equalsIgnoreCase(minGroup)) {
				return rankup;
			}
		}
		return null;
	}
	
	public HashMap<BlockType, Integer> getMinimalPlaced(String group) {
		RankUp rankup = getRankUpFromGroup(group);
		if (rankup != null) {
			return rankup.getPlaced();
		}
		return null;
	}
	
	public String getNewGroup(String group) {
		RankUp rankup = getRankUpFromGroup(group);
		if (rankup != null) {
			return rankup.getNewGroup();
		}
		return null;
	}
	
	public int getMinLevel(String group) {
		RankUp rankup = getRankUpFromGroup(group);
		if (rankup != null) {
			return rankup.getMinLevel();
		}
		return 0;
	}
	
	public HashMap<BlockType, Integer> getMinimalDestroyed(String group) {
		RankUp rankup = getRankUpFromGroup(group);
		if (rankup != null) {
			return rankup.getDestroyed();
		}
		return null;
	}
	
	public HashMap<ItemType, Integer> getMinimalCrafted(String group) {
		RankUp rankup = getRankUpFromGroup(group);
		if (rankup != null) {
			return rankup.getCrafted();
		}
		return null;
	}
	
	public HashMap<ItemType, Integer> getMinimalHave(String group) {
		RankUp rankup = getRankUpFromGroup(group);
		if (rankup != null) {
			return rankup.getHave();
		}
		return null;
	}
	
	public HashMap<EntityType, Integer> getMinimalKilled(String group) {
		RankUp rankup = getRankUpFromGroup(group);
		if (rankup != null) {
			return rankup.getKilled();
		}
		return null;
	}
	
	public int getMinimalTime(String group) {
		int num = 0;
		RankUp rankup = getRankUpFromGroup(group);
		if (rankup != null) {
			num = rankup.getMinTime();
		}
		return num;
	}
	
	public int getAmountOfItemInInventory(Player player, ItemType type) {
		PlayerInventory inv = player.getInventory();
		int amount = 0;
		for (int i=0; i<inv.getSize(); i++) {
			Item item = inv.getSlot(i);
			if (item != null) {
				if (item.getType().equals(type)) {
					amount = amount + item.getAmount(); // almost only did amount++
				}
			}
		}
		return amount;
	}
	
	// Checks if a player fulfills one of the RankUps and then may trigger the RankUpHook
	public void checkStats(Player player) {
		ArrayList<RankUp> rankUps = new ArrayList<RankUp>();
		rankUps.addAll(allRankUps);
		//PlayerStats playerStats = getPlayerStats(player);
		StatisticsFactory factory = Canary.factory().getStatisticsFactory();
		for (int i=0; i<rankUps.size(); i++) {
			int needed = 0;
			int have = 0;
			RankUp rankup = rankUps.get(i);
			HashMap<BlockType, Integer> destroyed = rankup.getDestroyed();
			if (destroyed != null && !destroyed.isEmpty()) {
				for (BlockType type : destroyed.keySet()) {
					needed++;
					int blockId = type.getId();
					Stat alreadyDestroyed = factory.getStat(MINEBLOCK + blockId);
					int current = player.getStat(alreadyDestroyed);
					int minimal = rankup.getMinDestroyed(type);
					//Canary.getServer().broadcastMessage(alreadyDestroyed.getId() + ": " + current + ", " + minimal);
					if (current >= minimal) {
						have++;
					}
				}
			}
			HashMap<BlockType, Integer> placed = rankup.getPlaced();
			if (placed != null && !placed.isEmpty()) {
				for (BlockType type : placed.keySet()) {
					needed++;
					int blockId = type.getId();
					Stat alreadyPlaced = factory.getStat(USEITEM + blockId);
					int current = player.getStat(alreadyPlaced);
					int minimal = rankup.getMinPlaced(type);
					//Canary.getServer().broadcastMessage(alreadyPlaced.getId() + ": " + current + ", " + minimal);
					if (current >= minimal) {
						have++;
					}
				}
			}
			HashMap<EntityType, Integer> killed = rankup.getKilled();
			if (killed != null && !killed.isEmpty()) {
				for (EntityType type : killed.keySet()) {
					needed++;
					String str = mobs.get(type);
					Stat alreadyKilled = factory.getStat(KILLENTITY + str);
					int current = player.getStat(alreadyKilled);
					int minimal = rankup.getMinKilled(type);
					if (current >= minimal) {
						have++;
					}
				}
			}
			HashMap<ItemType, Integer> crafted = rankup.getCrafted();
			if (crafted != null && !crafted.isEmpty()) {
				for (ItemType type : crafted.keySet()) {
					needed++;
					int id = type.getId();
					Stat alreadyCrafted = factory.getStat(CRAFTITEM + id);
					int current = player.getStat(alreadyCrafted);
					int minimal = rankup.getMinCrafted(type);
					if (current >= minimal) {
						have++;
					}
				}
			}
			HashMap<ItemType, Integer> amount = rankup.getHave();
			if (amount != null && !amount.isEmpty()) {
				for (ItemType type : amount.keySet()) {
					needed++;
					int current = getAmountOfItemInInventory(player, type);
					int minimal = rankup.getMinAmount(type);
					if (current >= minimal) {
						have++;
					}
				}
			}
			int time = rankup.getMinTime();
			if (time > 0) {
				needed++;
				if (player.getTimePlayed()/60 >= time) {
					have++;
				}
			}
			int level = rankup.getMinLevel();
			if (level > 0) {	
				needed++;
				if (player.getLevel() >= level) {
					have++;
				}
			}
			if (have >= needed) {
				Canary.hooks().callHook(new RankUpHook(player, rankup));
				return;
			}
		}
	}
	
	/**
	 * All the hooks that can cause a RankUp
	*/ 
	
	@HookHandler
	public void onLevelUp(LevelUpHook hook) {
		Player player = hook.getPlayer();
		checkStats(player);
	}
	
	@HookHandler
	public void onInventory(InventoryHook hook) {
		Player player = hook.getPlayer();
		checkStats(player);
	}
	
	@HookHandler
	public void onItemPickup(ItemPickupHook hook) {
		Player player = hook.getPlayer();
		checkStats(player);
	}



	public AutoRank getOwner() {
		return owner;
	}



	public void setOwner(AutoRank owner) {
		this.owner = owner;
	}
}
