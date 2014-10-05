package de.svdragster.autorank;

import java.util.HashMap;

import net.canarymod.Canary;
import net.canarymod.api.entity.EntityType;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.factory.StatisticsFactory;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.statistics.Stat;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.chat.Colors;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.commandsys.Command;
import net.canarymod.commandsys.CommandListener;

public class AutoRankCommands implements CommandListener {
	
	public static boolean isNumeric(String str) {
		try {
			@SuppressWarnings("unused")
			double d = Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
	
	// • <- For copy paste
	
	@Command(aliases = { "rankup" }, description = "Display your progress.", permissions = { "autorank.player.progress" }, toolTip = "/rankup")
	public void RankUpCommand(MessageReceiver caller, String[] parameters) {
		Player player = Canary.getServer().getPlayer(caller.getName());
		displayProgress(player, player.getGroup().getName());
	}
	
	public void displayProgress(Player player, String group) {
		AutoRankListener listener = new AutoRankListener(false);
		HashMap<BlockType, Integer> minPlaced = listener.getMinimalPlaced(group);
		HashMap<BlockType, Integer> minDestroyed = listener.getMinimalDestroyed(group);
		HashMap<EntityType, Integer> minKilled = listener.getMinimalKilled(group);
		HashMap<ItemType, Integer> minCrafted = listener.getMinimalCrafted(group);
		HashMap<ItemType, Integer> minAmount = listener.getMinimalHave(group);
		String newGroup = listener.getNewGroup(group);
		int minTime = listener.getMinimalTime(group);
		int minLevel = listener.getMinLevel(group);
		StatisticsFactory factory = Canary.factory().getStatisticsFactory();
		player.message(Colors.ORANGE + "  ====  " + Colors.PURPLE + "Progress to " + newGroup + Colors.ORANGE + " ====  ");
		if (minDestroyed != null && !minDestroyed.isEmpty()) {
			player.message(Colors.YELLOW + "  •Blocks Destroyed:");
			for (BlockType type : minDestroyed.keySet()) {
				Stat stat = factory.getStat(AutoRankListener.MINEBLOCK + type.getId());
				int done = player.getStat(stat);
				int min = minDestroyed.get(type);
				String str = Colors.LIGHT_GRAY + "    " + type.getMachineName() + ": "; // Put Colors.LIGHT_GRAY at the beginning or it won't indent in the minecraft chat
				if (done >= min) {
					str = str.concat(Colors.LIGHT_GREEN + min); // append min so it won't do stuff like   woodensword: crafted 400 of 10
				} else {
					str = str.concat(Colors.LIGHT_RED + done);
				}
				str = str.concat(Colors.GREEN + " / " + Colors.LIGHT_GREEN + min);
				player.message(str);
			}
		}
		if (minPlaced != null && !minPlaced.isEmpty()) {
			player.message(Colors.YELLOW + "  •Blocks Placed:");
			for (BlockType type : minPlaced.keySet()) {
				Stat stat = factory.getStat(AutoRankListener.USEITEM + type.getId()); // USEITEM is also placing blocks
				int done = player.getStat(stat);
				int min = minPlaced.get(type);
				String str = Colors.LIGHT_GRAY + "    " + type.getMachineName() + ": ";
				if (done >= min) {
					str = str.concat(Colors.LIGHT_GREEN + min); // append min so it won't do stuff like   woodensword: crafted 400 of 10
				} else {
					str = str.concat(Colors.LIGHT_RED + done);
				}
				str = str.concat(Colors.GREEN + " / " + Colors.LIGHT_GREEN + min);
				player.message(str);
			}
		}
		if (minKilled != null && !minKilled.isEmpty()) {
			player.message(Colors.YELLOW + "  •Mobs Killed:");
			for (EntityType type : minKilled.keySet()) {
				Stat stat = factory.getStat(AutoRankListener.KILLENTITY + AutoRankListener.mobs.get(type));
				int done = player.getStat(stat);
				int min = minKilled.get(type);
				String str = Colors.LIGHT_GRAY + "    " + type.name() + ": ";
				if (done >= min) {
					str = str.concat(Colors.LIGHT_GREEN + min); // append min so it won't do stuff like   woodensword: crafted 400 of 10
				} else {
					str = str.concat(Colors.LIGHT_RED + done);
				}
				str = str.concat(Colors.GREEN + " / " + Colors.LIGHT_GREEN + min);
				player.message(str);
			}
		}
		if (minCrafted != null && !minCrafted.isEmpty()) {
			player.message(Colors.YELLOW + "  •Items Crafted:");
			for (ItemType type : minCrafted.keySet()) {
				Stat stat = factory.getStat(AutoRankListener.CRAFTITEM + type.getId());
				int done = player.getStat(stat);
				int min = minCrafted.get(type);
				String str = Colors.LIGHT_GRAY + "    " + type.getMachineName() + ": ";
				if (done >= min) {
					str = str.concat(Colors.LIGHT_GREEN + min); // append min so it won't do stuff like   woodensword: crafted 400 of 10
				} else {
					str = str.concat(Colors.LIGHT_RED + done);
				}
				str = str.concat(Colors.GREEN + " / " + Colors.LIGHT_GREEN + min);
				player.message(str);
			}
		}
		if (minAmount != null && !minAmount.isEmpty()) {
			player.message(Colors.YELLOW + "  •Items in Inventory:");
			for (ItemType type : minAmount.keySet()) {
				int done = listener.getAmountOfItemInInventory(player, type);
				int min = minAmount.get(type);
				String str = Colors.LIGHT_GRAY + "    " + type.getMachineName() + ": ";
				if (done >= min) {
					str = str.concat(Colors.LIGHT_GREEN + min); // append min so it won't do stuff like   woodensword: crafted 400 of 10
				} else {
					str = str.concat(Colors.LIGHT_RED + done);
				}
				str = str.concat(Colors.GREEN + " / " + Colors.LIGHT_GREEN + min);
				player.message(str);
			}
		}
		if (minTime > 0) {
			player.message(Colors.YELLOW + "  •Time Played:");
			long actualTime = player.getTimePlayed()/60;
			String str = Colors.LIGHT_GRAY + "    Minutes: ";
			if (actualTime >= minTime) {
				str = str.concat(Colors.LIGHT_GREEN + minTime);
			} else {
				str = str.concat(Colors.LIGHT_RED + actualTime);
			}
			str = str.concat(Colors.GREEN + " / " + Colors.LIGHT_GREEN + minTime);
			player.message(str);
		}
		if (minLevel > 0) {
			player.message(Colors.YELLOW + "  •Level:");
			long level = minLevel;
			String str = Colors.LIGHT_GRAY + "    Level: ";
			if (level >= minLevel) {
				str = str.concat(Colors.LIGHT_GREEN + minLevel);
			} else {
				str = str.concat(Colors.LIGHT_RED + level);
			}
			str = str.concat(Colors.GREEN + " / " + Colors.LIGHT_GREEN + minLevel);
			player.message(str);
		}
	}
	
	@Command(aliases = { "autorank", "ar" }, description = "AutoRank admin commands", permissions = { "autorank.admin.command" }, toolTip = "/autorank <new|delete|list|group>")
	public void AutoRankCommand(MessageReceiver caller, String[] parameters) {
		int l = parameters.length;
		AutoRankListener listener = new AutoRankListener(false);
		if (l >= 2) {
			String p1 = parameters[1];
			if (p1.equalsIgnoreCase("new") || p1.equalsIgnoreCase("create") || p1.equalsIgnoreCase("add")) {
				if (l <= 2) {
					caller.notice("Usage: /autorank new <group>    -- The <group> you want to assign your new RankUp to, cAsE sEnSiTiVe!");
				} else {
					String p2 = parameters[2];
					if (listener.getRankUpFromGroup(p2) == null) {
						RankUp rankUp = new RankUp(p2);
						AutoRankListener.allRankUps.add(rankUp);
						caller.message(Colors.LIGHT_GREEN + "Created new RankUp assigned to " + p2 + ".");
						caller.message(Colors.LIGHT_GREEN + "Type /autorank " + p2 + " to view more commands.");
					} else {
						caller.notice("This RankUp exists already.");
					}
				}
				return;
			} else if (p1.equalsIgnoreCase("delete")|| p1.equalsIgnoreCase("remove")) {
				if (l <= 2) {
					caller.notice("Usage: /autorank remove <group>    -- The RankUp you want to remove assigned to <group>, cAsE sEnSiTiVe!");
					return;
				} else {
					String p2 = parameters[2];
					if (listener.removeRankUp(p2)) {
						caller.message(Colors.LIGHT_GREEN + "RankUp assigned to " + p2 + " has been deleted.");
					} else {
						caller.notice("RankUp assigned to " + p2 + " does not exist.");
					}
					return;
				}
			} else if (listener.getRankUpFromGroup(p1) != null) {
				if (l >= 4) {
					RankUp rankUp = listener.getRankUpFromGroup(p1);
					String p2 = parameters[2];
					String strType = parameters[3];
					if (p2.equalsIgnoreCase("destroyed")) {
						BlockType type = BlockType.fromString(strType);
						if (type != null) {
							if (l >= 5) {
								String strAmount = parameters[4];
								if (isNumeric(strAmount)) {
									int amount = Integer.parseInt(strAmount);
									rankUp.setMinDestroyed(type, amount);
									listener.overwriteRankUp(rankUp);
									caller.message(Colors.LIGHT_GREEN + "Added objective.");
								} else {
									caller.notice("You have entered " + strAmount + ", the amount must be a number.");
								}
							} else {
								caller.notice("Usage: /autorank " + p1 + " destroyed " + strType + " <amount>");
							}
						} else {
							caller.notice(strType + " is not a block.");
						}
						return;
					} else if (p2.equalsIgnoreCase("placed")) {
						BlockType type = BlockType.fromString(strType);
						if (type != null) {
							if (l >= 5) {
								String strAmount = parameters[4];
								if (isNumeric(strAmount)) {
									int amount = Integer.parseInt(strAmount);
									rankUp.setMinPlaced(type, amount);
									listener.overwriteRankUp(rankUp);
									caller.message(Colors.LIGHT_GREEN + "Added objective.");
								} else {
									caller.notice("You have entered " + strAmount + ", the amount must be a number.");
								}
							} else {
								caller.notice("Usage: /autorank " + p1 + " placed " + strType + " <amount>");
							}
						} else {
							caller.notice(strType + " is not a block.");
						}
						return;
					} else if (p2.equalsIgnoreCase("killed")) {
						HashMap<EntityType, String> mobs = new HashMap<EntityType, String>();
						mobs.putAll(AutoRankListener.mobs);
						EntityType type = null;
						try {
							type = EntityType.valueOf(strType.toUpperCase());
						} catch(IllegalArgumentException e) {
							caller.notice("This entity does not exist. Possible Entities (Only take mobs and animals): https://ci.visualillusionsent.net/job/CanaryLib/javadoc/net/canarymod/api/entity/EntityType.html");
							return;
						}
						if (type != null) {
							if (type.isLiving()) {
								String minecraftType = mobs.get(type);
								if (minecraftType != null && !minecraftType.isEmpty()) {
									if (l >= 5) {
										String strAmount = parameters[4];
										if (isNumeric(strAmount)) {
											int amount = Integer.parseInt(strAmount);
											rankUp.setMinKilled(type, amount);
											listener.overwriteRankUp(rankUp);
											caller.message(Colors.LIGHT_GREEN + "Added objective.");
										} else {
											caller.notice("You have entered " + strAmount + ", the amount must be a number.");
										}
									} else {
										caller.notice("Usage: /autorank " + p1 + " killed " + strType + " <amount>");
									}
								} else {
									caller.notice(type.name() + " is not possible to use for some reason. Please contact the author of the plugin.");
								}
							} else {
								caller.notice(type.name() + " is not a living entity, only mobs and animals are possible.");
							}
						}
						return;
					} else if (p2.equalsIgnoreCase("crafted")) {
						ItemType type = ItemType.fromString(strType);
						if (type != null) {
							if (l >= 5) {
								String strAmount = parameters[4];
								if (isNumeric(strAmount)) {
									int amount = Integer.parseInt(strAmount);
									rankUp.setMinCrafted(type, amount);
									listener.overwriteRankUp(rankUp);
									caller.message(Colors.LIGHT_GREEN + "Added objective.");
								} else {
									caller.notice("You have entered " + strAmount + ", the amount must be a number.");
								}
							} else {
								caller.notice("Usage: /autorank " + p1 + " crafted " + strType + " <amount>");
							}
						} else {
							caller.notice(strType + " is not an item.");
						}
						return;
					} else if (p2.equalsIgnoreCase("inventory")) {
						ItemType type = ItemType.fromString(strType);
						if (type != null) {
							if (l >= 5) {
								String strAmount = parameters[4];
								if (isNumeric(strAmount)) {
									int amount = Integer.parseInt(strAmount);
									rankUp.setMinAmount(type, amount);
									listener.overwriteRankUp(rankUp);
									caller.message(Colors.LIGHT_GREEN + "Added objective.");
								} else {
									caller.notice("You have entered " + strAmount + ", the amount must be a number.");
								}
							} else {
								caller.notice("Usage: /autorank " + p1 + " inventory " + strType + " <amount>");
							}
						} else {
							caller.notice(strType + " is not an item.");
						}
						return;
					} else if (p2.equalsIgnoreCase("time")) {
						if (isNumeric(strType)) {
							int time = Integer.parseInt(strType);
							rankUp.setMinTime(time);
							listener.overwriteRankUp(rankUp);
							caller.message(Colors.LIGHT_GREEN + "Added objective.");
						} else {
							caller.notice("You have entered " + strType + ", the time must be a number.");
						}
						return;
					} else if (p2.equalsIgnoreCase("level")) {
						if (isNumeric(strType)) {
							int level = Integer.parseInt(strType);
							rankUp.setMinLevel(level);
							listener.overwriteRankUp(rankUp);
							caller.message(Colors.LIGHT_GREEN + "Added objective.");
						} else {
							caller.notice("You have entered " + strType + ", the level must be a number.");
						}
						return;
					} else if (p2.equalsIgnoreCase("newgroup") || p2.equalsIgnoreCase("group")) {
						rankUp.setNewGroup(strType);
						listener.overwriteRankUp(rankUp);
						caller.message(Colors.LIGHT_GREEN + "Set newgroup to " + strType + ".");
						return;
					}
				}
				caller.message(Colors.LIGHT_GRAY + "/autorank " + p1 + " destroyed <block id> <amount>");
				caller.message(Colors.GRAY + "  Example: /autorank " + p1 + " destroyed minecraft:dirt 22");
				caller.message(Colors.LIGHT_GRAY + "/autorank " + p1 + " placed <block id> <amount>");
				caller.message(Colors.GRAY + "  Example: /autorank " + p1 + " placed minecraft:snow 200");
				caller.message(Colors.LIGHT_GRAY + "/autorank " + p1 + " killed <mobname> <amount>");
				caller.message(Colors.GRAY + "  Example: /autorank " + p1 + " killed Zombie 99");
				caller.message(Colors.GRAY + "Possible Entities: https://ci.visualillusionsent.net/job/CanaryLib/javadoc/net/canarymod/api/entity/EntityType.html");
				caller.message(Colors.LIGHT_GRAY + "/autorank " + p1 + " crafted <item id> <amount>  -- Crafting is also smelting in a furnace");
				caller.message(Colors.GRAY + "  Example: /autorank " + p1 + " crafted minecraft:crafting_table 7");
				caller.message(Colors.LIGHT_GRAY + "/autorank " + p1 + " inventory <item id> <amount>  -- The minimal amount of items a player needs to have in his inventory");
				caller.message(Colors.GRAY + "  Example: /autorank " + p1 + " inventory minecraft:diamond 100");
				caller.message(Colors.LIGHT_GRAY + "/autorank " + p1 + " time <minutes>  -- The minutes the player needs to have played");
				caller.message(Colors.GRAY + "  Example: /autorank " + p1 + " time 60");
				caller.message(Colors.LIGHT_GRAY + "/autorank " + p1 + " level <level>  -- The minimal level the player needs");
				caller.message(Colors.GRAY + "  Example: /autorank " + p1 + " level 42");
				caller.message(Colors.LIGHT_GRAY + "/autorank " + p1 + " newgroup <groupname>  -- The group the player will rank up to when he fulfills the objectives.");
				caller.message(Colors.GRAY + "  Example: /autorank " + p1 + " newgroup players");
				return;
			} else if (p1.equalsIgnoreCase("group")) {
				caller.notice("You have to enter a group name, not 'group'. For example: /autorank visitors");
				return;
			} else if (p1.equalsIgnoreCase("list")|| p1.equalsIgnoreCase("ls")) {
				if (l >= 3) {
					String p2 = parameters[2];
					displayProgress(Canary.getServer().getPlayer(caller.getName()), p2);
				} else {
					caller.notice("/autorank list <groupname>");
				}
				return;
			}
		}
		caller.notice("Usage: /autorank <new|delete|list|group>");
	}
}
