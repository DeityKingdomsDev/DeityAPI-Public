package com.imdeity.deityapi.object;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.server.Packet39AttachEntity;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;

import com.imdeity.deityapi.DeityAPI;

/**
 * Handles general player actions
 * 
 * @author vanZeben
 */
public class PlayerObject {
    
    private HashMap<String, String> frozenPlayers = new HashMap<String, String>();
    public InventoryObject inventory = new InventoryObject();
    private HashMap<String, Integer> playerMoving = new HashMap<String, Integer>();
    
    /**
     * Returns a Player that is online
     * 
     * @param username
     * @return Player
     */
    public Player getOnlinePlayer(String username) {
        return DeityAPI.plugin.getServer().getPlayer(username);
    }
    
    /**
     * Returns the exact Player that is online
     * 
     * @param username
     * @return Player
     */
    public Player getOnlinePlayerExact(String username) {
        return DeityAPI.plugin.getServer().getPlayerExact(username);
    }
    
    /**
     * Returns a list of all online players
     * 
     * @return ArrayList<Player>
     */
    public Player[] getOnlinePlayers() {
        return DeityAPI.plugin.getServer().getOnlinePlayers();
    }
    
    public void clearAllInventory(Player player) {
        if (player != null) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
        }
    }
    
    public void clearHotBar(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        for (int i = 0; i < 9; i++) {
            playerInventory.setItem(i, null);
        }
    }
    
    public void clearInventorySlot(Player player, int slot) {
        PlayerInventory playerInventory = player.getInventory();
        playerInventory.setItem(slot, null);
    }
    
    public void clearPotionEffects(Player player) {
        if (player != null) {
            for (PotionEffectType p : PotionEffectType.values()) {
                if (p == null) {
                    continue;
                }
                if (player.hasPotionEffect(p)) {
                    player.addPotionEffect(p.createEffect(0, 0));
                }
            }
        }
    }
    
    public void clearStats(Player player) {
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setExhaustion(10);
        player.setSaturation(10);
        player.setLevel(0);
        player.setExp(0);
    }
    
    public void freezePlayer(String player, String reason) {
        this.frozenPlayers.put(player, reason);
    }
    
    public String getFrozenMessage(String player) {
        return this.frozenPlayers.get(player);
    }
    
    public int getNumberOfCatsOwned(String playername) {
        int numCats = 0;
        for (World w : DeityAPI.plugin.getServer().getWorlds()) {
            for (Ocelot e : w.getEntitiesByClass(Ocelot.class)) {
                if (e.isTamed()) {
                    if (e.getOwner() instanceof Player) {
                        if (((Player) e.getOwner()).getName().equalsIgnoreCase(playername)) {
                            numCats++;
                        }
                    } else {
                        e.remove();
                    }
                }
            }
        }
        return numCats;
    }
    
    public int getNumberOfWolvesOwned(String playername) {
        int numWolves = 0;
        for (World w : DeityAPI.plugin.getServer().getWorlds()) {
            for (Wolf e : w.getEntitiesByClass(Wolf.class)) {
                if (e.isTamed()) {
                    if (e.getOwner() instanceof Player) {
                        if (((Player) e.getOwner()).getName().equalsIgnoreCase(playername)) {
                            numWolves++;
                        }
                    } else {
                        e.remove();
                    }
                }
            }
        }
        return numWolves;
    }
    
    public ArrayList<Ocelot> getOwnedCats(String playername) {
        ArrayList<Ocelot> ent = new ArrayList<Ocelot>();
        for (World w : DeityAPI.plugin.getServer().getWorlds()) {
            for (Ocelot ocelot : w.getEntitiesByClass(Ocelot.class)) {
                if (ocelot.isTamed()) {
                    if (ocelot.getOwner() instanceof Player) {
                        if (((Player) ocelot.getOwner()).getName().equalsIgnoreCase(playername)) {
                            ent.add(ocelot);
                        }
                    } else {
                        ocelot.remove();
                    }
                }
            }
        }
        return ent;
    }
    
    public ArrayList<Wolf> getOwnedWolves(String playername) {
        ArrayList<Wolf> ent = new ArrayList<Wolf>();
        for (World w : DeityAPI.plugin.getServer().getWorlds()) {
            for (Wolf wolf : w.getEntitiesByClass(Wolf.class)) {
                if (wolf.isTamed()) {
                    if (wolf.getOwner() instanceof Player) {
                        if (((Player) wolf.getOwner()).getName().equalsIgnoreCase(playername)) {
                            ent.add(wolf);
                        }
                    } else {
                        wolf.remove();
                    }
                }
            }
        }
        return ent;
    }
    
    public int getPlayerMovingCount(String player) {
        return (this.playerMoving.get(player) == null ? 1 : this.playerMoving.get(player) + 1);
    }
    
    public int getTotalNumberTamedMobs(String playername) {
        int numMobs = 0;
        numMobs += this.getNumberOfWolvesOwned(playername);
        numMobs += this.getNumberOfCatsOwned(playername);
        return numMobs;
    }
    
    public void incrementPlayerCounter(String player) {
        this.playerMoving.put(player, (this.playerMoving.get(player) == null ? 1 : this.playerMoving.get(player) + 1));
    }
    
    public boolean isFrozen(String player) {
        return this.frozenPlayers.containsKey(player);
    }
    
    /**
     * Sets the players armour to the specified array of ItemStack
     * 
     * @param player
     *            Player that you want to alter
     * @param items
     *            Array of ItemStack (helmet, chestplate, leggings, boots)
     *            respectively
     * @return whether or not the input was correct
     */
    public boolean setArmor(Player player, ItemStack[] items) {
        PlayerInventory inv = player.getInventory();
        if (items.length == 4) {
            if ((items[0].getType().compareTo(Material.GOLD_HELMET) == 0) || (items[0].getType().compareTo(Material.DIAMOND_HELMET) == 0) || (items[0].getType().compareTo(Material.IRON_HELMET) == 0) || (items[0].getType().compareTo(Material.CHAINMAIL_HELMET) == 0)) {
                inv.setHelmet(items[0]);
            }
            if ((items[1].getType().compareTo(Material.GOLD_CHESTPLATE) == 0) || (items[1].getType().compareTo(Material.DIAMOND_CHESTPLATE) == 0) || (items[1].getType().compareTo(Material.IRON_CHESTPLATE) == 0) || (items[1].getType().compareTo(Material.CHAINMAIL_CHESTPLATE) == 0)) {
                inv.setChestplate(items[1]);
            }
            if ((items[2].getType().compareTo(Material.GOLD_LEGGINGS) == 0) || (items[2].getType().compareTo(Material.DIAMOND_LEGGINGS) == 0) || (items[2].getType().compareTo(Material.IRON_LEGGINGS) == 0) || (items[2].getType().compareTo(Material.CHAINMAIL_LEGGINGS) == 0)) {
                inv.setLeggings(items[2]);
            }
            if ((items[3].getType().compareTo(Material.GOLD_BOOTS) == 0) || (items[3].getType().compareTo(Material.DIAMOND_BOOTS) == 0) || (items[3].getType().compareTo(Material.IRON_BOOTS) == 0) || (items[3].getType().compareTo(Material.CHAINMAIL_BOOTS) == 0)) {
                inv.setBoots(items[3]);
            }
            return true;
        }
        return false;
    }
    
    public boolean sit(Player player) {
        if (player.isOnline()) {
            ((CraftPlayer) player).getHandle().netServerHandler.sendPacket(new Packet39AttachEntity(((CraftPlayer) player).getHandle(), ((CraftPlayer) player).getHandle()));
            return true;
        }
        return false;
    }
    
    public void stopPlayerCounter(String player) {
        this.playerMoving.remove(player);
    }
    
    /**
     * Teleports a player to the specified location
     * 
     * @param player
     *            Player to teleport
     * @param location
     *            Location to send them to
     */
    public boolean teleport(Player player, Location location) {
        if (location != null) {
            Chunk chunk = player.getWorld().getChunkAt(location);
            if (!chunk.isLoaded()) {
                chunk.load();
            }
            return player.teleport(location);
        }
        return false;
    }
    
    /**
     * Teleports a player to the specified location
     * 
     * @param player
     *            Player to teleport
     * @param location
     *            Location to send them to
     */
    public boolean teleportAsCommand(Player player, Location location) {
        if (location != null) {
            Chunk chunk = player.getWorld().getChunkAt(location);
            if (!chunk.isLoaded()) {
                chunk.load();
            }
            return player.teleport(location, TeleportCause.COMMAND);
        }
        return false;
    }
    
    public void unfreezePlayer(String player) {
        this.frozenPlayers.remove(player);
    }
    
    public int getCardinalDirection(Player player) {
        double rot = (player.getLocation().getYaw() - 90) % 360;
        if (rot < 0) {
            rot += 360;
        }
        if (0.0 <= rot && rot < 45.0) {
            // NORTH
            return 2;
        } else if (45.0 <= rot && rot < 135.0) {
            // EAST
            return 3;
        } else if (135.0 <= rot && rot < 225.0) {
            // SOUTH
            return 0;
        } else if (225.0 <= rot && rot < 315.0) {
            // WEST
            return 1;
        } else if (315.0 <= rot && rot < 360.0) {
            // NORTH
            return 2;
        } else {
            return 0;
        }
    }
    
    public String getDirectionTo(Location originalLocation, Location checkedLocation) {
        int origX = originalLocation.getBlockX();
        int origZ = originalLocation.getBlockZ();
        
        int newX = checkedLocation.getBlockX();
        int newZ = checkedLocation.getBlockZ();
        String direction = "";
        if (newZ > origZ) {
            direction = "South";
        } else if (newZ < origZ) {
            direction = "North";
        }
        if (newX > origX) {
            direction += "East";
        } else if (newX < origX) {
            direction += "West";
        }
        return direction;
    }
    
    public char getFrontFriendlyCardinalDirection(int frontCardinalDirection) {
        switch (frontCardinalDirection) {
            case 0:
                return 'E';
            case 1:
                return 'S';
            case 2:
                return 'W';
            case 3:
                return 'N';
            default:
                return ' ';
        }
    }
    
    public char getBackFriendlyCardinalDirection(int frontCardinalDirection) {
        switch (frontCardinalDirection) {
            case 0:
                return 'W';
            case 1:
                return 'N';
            case 2:
                return 'E';
            case 3:
                return 'S';
            default:
                return ' ';
        }
    }
    
    public char getLeftFriendlyCardinalDirection(int frontCardinalDirection) {
        switch (frontCardinalDirection) {
            case 0:
                return 'N';
            case 1:
                return 'E';
            case 2:
                return 'S';
            case 3:
                return 'W';
            default:
                return ' ';
        }
    }
    
    public char getRightFriendlyCardinalDirection(int frontCardinalDirection) {
        switch (frontCardinalDirection) {
            case 0:
                return 'S';
            case 1:
                return 'W';
            case 2:
                return 'N';
            case 3:
                return 'E';
            default:
                return ' ';
        }
    }
    
    public boolean isHelmetArmor(Material mat) {
        return (mat == Material.LEATHER_HELMET || mat == Material.CHAINMAIL_HELMET || mat == Material.IRON_HELMET || mat == Material.GOLD_HELMET || mat == Material.DIAMOND_HELMET);
    }
    
    public boolean isChestplateArmor(Material mat) {
        return (mat == Material.LEATHER_CHESTPLATE || mat == Material.CHAINMAIL_CHESTPLATE || mat == Material.IRON_CHESTPLATE || mat == Material.GOLD_CHESTPLATE || mat == Material.DIAMOND_CHESTPLATE);
    }
    
    public boolean isLeggingArmor(Material mat) {
        return (mat == Material.LEATHER_LEGGINGS || mat == Material.CHAINMAIL_LEGGINGS || mat == Material.IRON_LEGGINGS || mat == Material.GOLD_LEGGINGS || mat == Material.DIAMOND_LEGGINGS);
    }
    
    public boolean isBootArmor(Material mat) {
        return (mat == Material.LEATHER_BOOTS || mat == Material.CHAINMAIL_BOOTS || mat == Material.IRON_BOOTS || mat == Material.GOLD_BOOTS || mat == Material.DIAMOND_BOOTS);
    }
    
    public boolean isArmour(Material mat) {
        return (isHelmetArmor(mat) || isChestplateArmor(mat) || isLeggingArmor(mat) || isBootArmor(mat));
    }
    
}
