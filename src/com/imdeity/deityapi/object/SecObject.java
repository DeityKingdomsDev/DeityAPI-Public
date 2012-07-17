package com.imdeity.deityapi.object;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.imdeity.deityapi.DeityAPI;
import com.imdeity.deityapi.exception.DuplicateRegionException;
import com.imdeity.deityapi.exception.InvalidRegionException;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.GlobalRegionManager;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion.CircularInheritanceException;

/**
 * Handles interaction with WorldGuard
 * 
 * @author vanZeben
 */
@SuppressWarnings("unused")
public class SecObject {
    
    private static WorldGuardPlugin WorldGuard = null;
    
    private static GlobalRegionManager globalRegionManager = null;
    private static RegionManager regionManager = null;
    
    public SecObject(WorldGuardPlugin worldGuard) {
        SecObject.WorldGuard = worldGuard;
        SecObject.globalRegionManager = SecObject.WorldGuard.getGlobalRegionManager();
    }
    
    /**
     * Adds a member to a region
     * 
     * @param player
     *            Player to add to the region
     * @param world
     *            World the region is in
     * @param id
     *            Region name @ * Thrown if the regionmanager cannot be found
     */
    public void addMemberToRegion(String player, World world, String id) {
        this.setRegionManager(world);
        if ((id != null) && (this.getRegionManager() != null)) {
            ProtectedRegion region = this.getRegionFromName(world, id);
            if (region != null) {
                DefaultDomain members = region.getMembers();
                members.addPlayer(player);
                this.saveRegionManager();
            } else {
                DeityAPI.getAPI().getChatAPI().outWarn("DeityAPI", "Could not add " + player + " to region " + id);
            }
        }
    }
    
    /**
     * Adds an owner to a region
     * 
     * @param player
     *            Player to add to the region
     * @param world
     *            World the region is in
     * @param id
     *            Region name @ * Thrown if the regionmanager cannot be found
     * @throws InvalidRegionException
     */
    
    public void addOwnerToRegion(String player, World world, String id) throws IOException, InvalidRegionException {
        this.setRegionManager(world);
        if ((id != null) && (this.getRegionManager() != null)) {
            ProtectedRegion region;
            if (this.getRegionFromName(world, id) != null) {
                region = this.getRegionFromName(world, id);
                
                DefaultDomain owners = region.getOwners();
                owners.addPlayer(player);
                this.saveRegionManager();
            } else {
                throw new InvalidRegionException();
            }
        }
    }
    
    /**
     * Used privately to add a region to the regionManager
     * 
     * @param region
     *            Region to add to manager
     */
    private void addRegionToManager(ProtectedRegion region) {
        DeityAPI.getAPI().getSecAPI().getRegionManager().addRegion(region);
    }
    
    /**
     * Checks to see if the region exists
     * 
     * @param world
     *            World the region is in
     * @param regionName
     *            Region to check for
     * @return boolean Whether or not is exists
     */
    public boolean checkRegion(World world, String regionName) {
        this.setRegionManager(world);
        if (this.getRegionManager().hasRegion(regionName)) { return true; }
        return false;
    }
    
    public void clearChestAccessFlag(String regionname, World world) {
        DeityAPI.getAPI().getSecAPI().setRegionManager(world);
        ProtectedRegion region = DeityAPI.getAPI().getSecAPI().getRegionFromName(world, regionname);
        
        region.setFlag(DefaultFlag.CHEST_ACCESS, null);
        DeityAPI.getAPI().getSecAPI().saveRegionManager();
    }
    
    public void clearGreetingFlag(String regionname, World world) {
        DeityAPI.getAPI().getSecAPI().setRegionManager(world);
        ProtectedRegion region = DeityAPI.getAPI().getSecAPI().getRegionFromName(world, regionname);
        region.setFlag(DefaultFlag.GREET_MESSAGE, null);
        DeityAPI.getAPI().getSecAPI().saveRegionManager();
    }
    
    public void clearMobDamageFlag(String regionname, World world) {
        DeityAPI.getAPI().getSecAPI().setRegionManager(world);
        ProtectedRegion region = DeityAPI.getAPI().getSecAPI().getRegionFromName(world, regionname);
        region.setFlag(DefaultFlag.MOB_DAMAGE, null);
        DeityAPI.getAPI().getSecAPI().saveRegionManager();
    }
    
    public void clearMobSpawningFlag(String regionname, World world) {
        DeityAPI.getAPI().getSecAPI().setRegionManager(world);
        ProtectedRegion region = DeityAPI.getAPI().getSecAPI().getRegionFromName(world, regionname);
        region.setFlag(DefaultFlag.MOB_SPAWNING, null);
        DeityAPI.getAPI().getSecAPI().saveRegionManager();
    }
    
    public void clearPVPFlag(String regionname, World world) {
        DeityAPI.getAPI().getSecAPI().setRegionManager(world);
        ProtectedRegion region = DeityAPI.getAPI().getSecAPI().getRegionFromName(world, regionname);
        region.setFlag(DefaultFlag.PVP, null);
        DeityAPI.getAPI().getSecAPI().saveRegionManager();
    }
    
    public void clearSpawnFlag(String regionname, World world) {
        DeityAPI.getAPI().getSecAPI().setRegionManager(world);
        ProtectedRegion region = DeityAPI.getAPI().getSecAPI().getRegionFromName(world, regionname);
        region.setFlag(DefaultFlag.SPAWN_LOC, null);
        DeityAPI.getAPI().getSecAPI().saveRegionManager();
    }
    
    public void clearUseFlag(String regionname, World world) {
        DeityAPI.getAPI().getSecAPI().setRegionManager(world);
        ProtectedRegion region = DeityAPI.getAPI().getSecAPI().getRegionFromName(world, regionname);
        region.setFlag(DefaultFlag.USE, null);
        DeityAPI.getAPI().getSecAPI().saveRegionManager();
    }
    
    /**
     * Returns the WorldGuard GlobalRegionManager which is used by this class
     * only
     * 
     * @return GlobalRegionManager
     */
    private GlobalRegionManager getGlobalRegionManager() {
        return SecObject.globalRegionManager;
    }
    
    /**
     * Returns the greeting flag of a region
     * 
     * @param regionname
     *            Name of the region
     * @param world
     *            World the region is in
     * @return String Greeting
     */
    public String getGreetingFlag(String regionname, World world) {
        ProtectedRegion region = DeityAPI.getAPI().getSecAPI().getRegionFromName(world, regionname);
        return region.getFlag(DefaultFlag.GREET_MESSAGE);
    }
    
    public Set<String> getMembersFromRegion(String player, World world, String id) {
        this.setRegionManager(world);
        if ((id != null) && (this.getRegionManager() != null)) {
            ProtectedRegion region = this.getRegionFromName(world, id);
            if (region != null) {
                DefaultDomain members = region.getMembers();
                if (members != null) {
                    return members.getPlayers();
                } else {
                    return null;
                }
            }
        }
        return null;
    }
    
    /**
     * Returns the state of Mob Damage in a region
     * 
     * @param regionname
     *            Region you wish to check
     * @param world
     *            World the region is in
     * @return boolean Whether or not mob damage is allowed
     */
    public boolean getMobDamageFlag(String regionname, World world) {
        ProtectedRegion region = DeityAPI.getAPI().getSecAPI().getRegionFromName(world, regionname);
        State flag = region.getFlag(DefaultFlag.MOB_DAMAGE);
        if ((flag == null) || flag.equals(State.DENY)) { return false; }
        return true;
    }
    
    /**
     * Returns the state of Mob spawning in a region
     * 
     * @param regionname
     *            Region you wish to check
     * @param world
     *            World the region is in
     * @return boolean Whether or not Mob Spawning is allowed
     */
    public boolean getMobSpawningFlag(String regionname, World world) {
        ProtectedRegion region = DeityAPI.getAPI().getSecAPI().getRegionFromName(world, regionname);
        if (region == null) { return false; }
        State flag = region.getFlag(DefaultFlag.MOB_SPAWNING);
        if ((flag == null) || flag.equals(State.DENY)) { return false; }
        return true;
    }
    
    public Set<String> getOwnersFromRegion(String player, World world, String id) {
        this.setRegionManager(world);
        if ((id != null) && (this.getRegionManager() != null)) {
            ProtectedRegion region = this.getRegionFromName(world, id);
            if (region != null) {
                DefaultDomain owners = region.getOwners();
                if (owners != null) {
                    return owners.getPlayers();
                } else {
                    return null;
                }
            }
        }
        return null;
    }
    
    /**
     * Returns the state of PVP in a region
     * 
     * @param regionname
     *            Region you wish to check
     * @param world
     *            World the region is in
     * @return boolean Whether or not PVP is allowed
     */
    public boolean getPVPFlag(String regionname, World world) {
        ProtectedRegion region = DeityAPI.getAPI().getSecAPI().getRegionFromName(world, regionname);
        State flag = region.getFlag(DefaultFlag.PVP);
        if ((flag == null) || flag.equals(State.DENY)) { return false; }
        return true;
    }
    
    /**
     * Checks a location to see if it is in a region borders
     * 
     * @param regionNames
     *            regions to check
     * @param loc
     *            location to check
     * @return boolean Whether or not in region
     */
    public String getRegionFromLocation(Location loc) {
        this.setRegionManager(loc.getWorld());
        RegionManager manager = this.getRegionManager();
        ApplicableRegionSet tempReg = manager.getApplicableRegions(this.toVector(loc));
        Iterator<ProtectedRegion> iterator = tempReg.iterator();
        while (iterator.hasNext()) {
            ProtectedRegion region = iterator.next();
            if (region.getId().equalsIgnoreCase("__Global__")) {
                continue;
            } else {
                return region.getId();
            }
        }
        
        return "";
    }
    
    /**
     * Gets a region from the name of it
     * 
     * @param world
     *            World the region is in
     * @param id
     *            Name of the region
     * @return ProtectedRegion Region
     */
    public ProtectedRegion getRegionFromName(World world, String id) {
        return DeityAPI.getAPI().getSecAPI().getGlobalRegionManager().get(world).getRegion(id);
    }
    
    /**
     * @return RegionManager Returns the current Region manager
     */
    private RegionManager getRegionManager() {
        return SecObject.regionManager;
    }
    
    public List<String> getRegionsAtLocation(Location location) {
        if (location != null) {
            DeityAPI.getAPI().getSecAPI().setRegionManager(location.getWorld());
            ApplicableRegionSet regions = DeityAPI.getAPI().getSecAPI().getGlobalRegionManager().get(location.getWorld()).getApplicableRegions(new Vector(location.getX(), location.getY(), location.getZ()));
            List<String> returnValue = new ArrayList<String>();
            for (ProtectedRegion region : regions) {
                returnValue.add(region.getId());
            }
            return returnValue;
        }
        return null;
    }
    
    public List<String> getRegionsThatContainBlock(Block block) {
        if (block != null) { return (this.getRegionsAtLocation(block.getLocation())); }
        return null;
    }
    
    /**
     * Protects a chunk of land
     * 
     * @param world
     *            World the region should be in
     * @param id
     *            Name of the region
     * @param cornerOne
     *            Coordinates of the lowest point of the region
     * @param cornerTwo
     *            Coordinates of the hightes point of the region @ * Thrown if
     *            the folder cannot be found
     * @throws DuplicateRegionException
     *             Thrown if the region already exists
     */
    // public void protectPolyRegion(World world, String id, Location cornerOne,
    // Location cornerTwo) , DuplicateRegionException {
    // setRegionManager(world);
    // if (getRegionManager() != null) {
    // Polygonal2DRegion region;
    // Polygonal2DRegionSelector sel = new Polygonal2DRegionSelector();
    //
    //
    // Vector pointOne = DeityAPI.getAPI().getSecAPI().toVector(cornerOne);
    // Vector pointTwo = DeityAPI.getAPI().getSecAPI().toVector(cornerTwo);
    // hasRegion(id, world);
    // region = new Polygonal2DRegion();
    //
    //
    // for (Vector pt : getRegionCorners()) {
    // region.addPoint(pt);
    // }
    //
    //
    // Polygonal2DSelection cuboidSel = new Polygonal2DSelection(world, sel,
    // region);
    //
    //
    //
    // DeityAPI.getAPI().getSecAPI().addRegionToManager(region);
    // DeityAPI.getAPI().getSecAPI().saveRegionManager();
    // }
    // }
    
    public List<String> getRegionsThatContainPlayer(Player player) {
        if (player != null) { return (this.getRegionsAtLocation(player.getLocation())); }
        return null;
    }
    
    /**
     * Returns a Location object of the regions spawn
     * 
     * @param regionname
     *            Region you wish to check
     * @param world
     *            World the region is in
     * @return Location Location of the spawn
     */
    public Location getSpawnFlag(String regionname, World world) {
        ProtectedRegion region = DeityAPI.getAPI().getSecAPI().getRegionFromName(world, regionname);
        com.sk89q.worldedit.Location flag = region.getFlag(DefaultFlag.SPAWN_LOC);
        return DeityAPI.getAPI().getSecAPI().toLocation(flag);
    }
    
    /**
     * @return WorldGuardPlugin The worldguard plugin object
     */
    private WorldGuardPlugin getWorldGuard() {
        return SecObject.WorldGuard;
    }
    
    /**
     * Checks a world for the specified region
     * 
     * @param id
     *            Region to look for
     * @param world
     *            World to look in
     * @return boolean Whether or not the world has the region
     */
    public boolean hasRegion(String id, World world) {
        this.setRegionManager(world);
        if (DeityAPI.getAPI().getSecAPI().getRegionManager().hasRegion(id)) { return true; }
        return false;
    }
    
    /**
     * Checks a location to see if it is in a region borders
     * 
     * @param regionNames
     *            regions to check
     * @param loc
     *            location to check
     * @return boolean Whether or not in region
     */
    public boolean inRegion(Location loc) {
        this.setRegionManager(loc.getWorld());
        RegionManager manager = this.getRegionManager();
        ApplicableRegionSet tempReg = manager.getApplicableRegions(this.toVector(loc));
        Iterator<ProtectedRegion> iterator = tempReg.iterator();
        while (iterator.hasNext()) {
            ProtectedRegion region = iterator.next();
            if (region.getId().equalsIgnoreCase("__Global__")) {
                continue;
            } else {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Checks a location to see if it is in a region borders
     * 
     * @param regionNames
     *            regions to check
     * @param loc
     *            location to check
     * @return boolean Whether or not in region
     */
    public boolean isInRegionBorders(ArrayList<String> regionNames, Location loc) {
        this.setRegionManager(loc.getWorld());
        RegionManager manager = this.getRegionManager();
        ProtectedRegion tempReg = null;
        for (String s : regionNames) {
            tempReg = manager.getRegion(s);
            if (tempReg != null) {
                if (tempReg.contains(new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))) { return true; }
            }
        }
        return false;
    }
    
    public boolean isInRegionBorders(String regionNames, Location loc) {
        this.setRegionManager(loc.getWorld());
        RegionManager manager = this.getRegionManager();
        ProtectedRegion tempReg = null;
        tempReg = manager.getRegion(regionNames);
        if (tempReg != null) {
            if (tempReg.contains(new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))) { return true; }
        }
        return false;
    }
    
    /**
     * Checks an object to see if it is a vector
     * 
     * @param object
     *            Object to check
     * @return boolean Whether or not its a vector
     */
    private boolean isVector(Object object) {
        if (object instanceof Vector) { return true; }
        return false;
    }
    
    /**
     * Protects a chunk of land
     * 
     * @param world
     *            World the region should be in
     * @param id
     *            Name of the region
     * @param cornerOne
     *            Coordinates of the lowest point of the region
     * @param cornerTwo
     *            Coordinates of the hightes point of the region @ * Thrown if
     *            the folder cannot be found
     * @throws DuplicateRegionException
     *             Thrown if the region already exists
     */
    public void protectRegion(World world, String id, Location cornerOne, Location cornerTwo) {
        this.setRegionManager(world);
        if (this.getRegionManager() != null) {
            ProtectedRegion region;
            
            Vector pointOne = DeityAPI.getAPI().getSecAPI().toVector(cornerOne);
            Vector pointTwo = DeityAPI.getAPI().getSecAPI().toVector(cornerTwo);
            
            CuboidSelection cuboidSel = new CuboidSelection(world, pointOne, pointTwo);
            
            BlockVector min = cuboidSel.getNativeMinimumPoint().toBlockVector();
            BlockVector max = cuboidSel.getNativeMaximumPoint().toBlockVector();
            
            this.hasRegion(id, world);
            region = new ProtectedCuboidRegion(id, min, max);
            
            DeityAPI.getAPI().getSecAPI().addRegionToManager(region);
            DeityAPI.getAPI().getSecAPI().saveRegionManager();
        }
    }
    
    public void removeAllMembersFromRegion(String id, World world) {
        this.setRegionManager(world);
        if ((id != null) && (this.getRegionManager() != null)) {
            ProtectedRegion region = this.getRegionFromName(world, id);
            if (region != null) {
                if (region.getMembers() != null) {
                    try {
                        for (String s : region.getMembers().getPlayers()) {
                            try {
                                region.getMembers().removePlayer(s);
                            } catch (Exception e) {
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }
            this.saveRegionManager();
        }
    }
    
    public void removeAllOwnersFromRegion(String id, World world) {
        this.setRegionManager(world);
        if ((id != null) && (this.getRegionManager() != null)) {
            ProtectedRegion region = this.getRegionFromName(world, id);
            if (region != null) {
                if (region.getOwners() != null) {
                    try {
                        for (String s : region.getOwners().getPlayers()) {
                            try {
                                region.getOwners().removePlayer(s);
                            } catch (Exception e) {
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }
            this.saveRegionManager();
        }
    }
    
    /**
     * Removes a player from the specified region
     * 
     * @param player
     *            Player to remove
     * @param world
     *            World the region is in
     * @param id
     *            Name of the region @ * Thrown if the regionmanager cannot be
     *            found
     */
    public void removeMemberFromRegion(String player, World world, String id) {
        this.setRegionManager(world);
        if ((id != null) && (this.getRegionManager() != null)) {
            ProtectedRegion region = this.getRegionFromName(world, id);
            
            DefaultDomain members = region.getMembers();
            members.removePlayer(player);
            this.saveRegionManager();
        }
    }
    
    /**
     * Removes an owner from the specified region
     * 
     * @param player
     *            Player to remove
     * @param world
     *            World the region is in
     * @param id
     *            Name of the region @ * Thrown if the regionmanager cannot be
     *            found
     */
    public void removeOwnerFromRegion(String player, World world, String id) {
        this.setRegionManager(world);
        if ((id != null) && (this.getRegionManager() != null)) {
            ProtectedRegion region = this.getRegionFromName(world, id);
            
            DefaultDomain owners = region.getOwners();
            if (owners != null) {
                owners.removePlayer(player);
            }
            this.saveRegionManager();
        }
    }
    
    public void removeRegion(World world, String id) {
        this.setRegionManager(world);
        if (this.getRegionManager() != null) {
            DeityAPI.getAPI().getSecAPI().removeRegionFromManager(id);
            DeityAPI.getAPI().getSecAPI().saveRegionManager();
        }
    }
    
    /**
     * Used privately to remove a region
     * 
     * @param region
     *            Region to remove
     */
    private void removeRegionFromManager(String region) {
        DeityAPI.getAPI().getSecAPI().getRegionManager().removeRegion(region);
    }
    
    /**
     * Used privately to save the region manager @ * Thrown if the manager
     * doesn't exist
     */
    private void saveRegionManager() {
        
        try {
            DeityAPI.getAPI().getSecAPI().getRegionManager().save();
        } catch (ProtectionDatabaseException e) {
            e.printStackTrace();
        }
        
    }
    
    public void setChestAccessFlag(String regionname, CommandSender sender, boolean allow) {
        DeityAPI.getAPI().getSecAPI().setRegionManager(((Player) sender).getWorld());
        DeityAPI.getAPI().getSecAPI().setFlag(DeityAPI.getAPI().getSecAPI().getRegionFromName(((Player) sender).getWorld(), regionname), DefaultFlag.CHEST_ACCESS, sender, (allow ? "allow" : "deny"));
        DeityAPI.getAPI().getSecAPI().saveRegionManager();
    }
    
    /**
     * Private class to alter flags
     * 
     * @param region
     *            Region to change
     * @param flag
     *            Flag to change
     * @param sender
     *            CommandSender object
     * @param value
     *            Value to set flag to
     */
    private <V> void setFlag(ProtectedRegion region, Flag<V> flag, CommandSender sender, String value) {
        try {
            region.setFlag(flag, flag.parseInput(SecObject.WorldGuard, sender, value));
        } catch (InvalidFlagFormat e) {
            e.printStackTrace();
        }
        
    }
    
    /**
     * Sets the greeting flag of a region
     * 
     * @param region
     *            Name of region to change
     * @param sender
     *            CommandSender object
     * @param value
     *            The greeting you want it to say
     */
    public void setGreetingFlag(String regionname, CommandSender sender, String value) {
        DeityAPI.getAPI().getSecAPI().setRegionManager(((Player) sender).getWorld());
        DeityAPI.getAPI().getSecAPI().setFlag(DeityAPI.getAPI().getSecAPI().getRegionFromName(((Player) sender).getWorld(), regionname), DefaultFlag.GREET_MESSAGE, sender, value);
        DeityAPI.getAPI().getSecAPI().saveRegionManager();
        
    }
    
    /**
     * Sets the greeting flag of a region
     * 
     * @param region
     *            Name of region to change
     * @param sender
     *            CommandSender object
     * @param value
     *            The greeting you want it to say
     */
    public void setGreetingFlag(String regionname, World world, String value) {
        DeityAPI.getAPI().getSecAPI().setRegionManager(world);
        DeityAPI.getAPI().getSecAPI().setFlag(DeityAPI.getAPI().getSecAPI().getRegionFromName(world, regionname), DefaultFlag.GREET_MESSAGE, DeityAPI.plugin.getServer().getConsoleSender(), value);
        DeityAPI.getAPI().getSecAPI().saveRegionManager();
        
    }
    
    /**
     * Sets whether or not mobs can hurt players
     * 
     * @param region
     *            Name of region to change
     * @param sender
     *            CommandSender Object
     * @param boolean Whether or not to allow mob damage
     */
    public void setMobDamageFlag(String regionname, CommandSender sender, boolean allow) {
        DeityAPI.getAPI().getSecAPI().setRegionManager(((Player) sender).getWorld());
        DeityAPI.getAPI().getSecAPI().setFlag(DeityAPI.getAPI().getSecAPI().getRegionFromName(((Player) sender).getWorld(), regionname), DefaultFlag.MOB_DAMAGE, sender, (allow ? "allow" : "deny"));
        DeityAPI.getAPI().getSecAPI().saveRegionManager();
    }
    
    /**
     * Sets whether or not mobs can spawn in a region
     * 
     * @param region
     *            Name of region to change
     * @param sender
     *            CommandSender Object
     * @param allow
     *            Whether or not to allow mobs to spawn
     */
    public void setMobSpawningFlag(String regionname, CommandSender sender, boolean allow) {
        DeityAPI.getAPI().getSecAPI().setRegionManager(((Player) sender).getWorld());
        DeityAPI.getAPI().getSecAPI().setFlag(DeityAPI.getAPI().getSecAPI().getRegionFromName(((Player) sender).getWorld(), regionname), DefaultFlag.MOB_SPAWNING, sender, (allow ? "allow" : "deny"));
        DeityAPI.getAPI().getSecAPI().saveRegionManager();
    }
    
    /**
     * Sets the parent of one region to another
     * 
     * @param world
     *            World the regions are located in
     * @param regionChild
     *            Child Regions name
     * @param regionParent
     *            Parent Regions name
     */
    public void setParent(World world, String regionChild, String regionParent) {
        ProtectedRegion childRegion;
        ProtectedRegion parentRegion;
        childRegion = DeityAPI.getAPI().getSecAPI().getRegionFromName(world, regionChild);
        parentRegion = DeityAPI.getAPI().getSecAPI().getRegionFromName(world, regionParent);
        
        try {
            childRegion.setParent(parentRegion);
        } catch (CircularInheritanceException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Sets the pvp status of a region
     * 
     * @param region
     *            Name of region you want to change
     * @param sender
     *            CommandSender Object
     * @param allow
     *            Whether or not to allow pvp
     */
    public void setPVPFlag(String regionname, CommandSender sender, boolean allow) {
        
        DeityAPI.getAPI().getSecAPI().setRegionManager(((Player) sender).getWorld());
        DeityAPI.getAPI().getSecAPI().setFlag(DeityAPI.getAPI().getSecAPI().getRegionFromName(((Player) sender).getWorld(), regionname), DefaultFlag.PVP, sender, (allow ? "allow" : "deny"));
        DeityAPI.getAPI().getSecAPI().saveRegionManager();
    }
    
    /**
     * Sets the region manager to the corresponding world
     * 
     * @param world
     *            World to set the region to
     */
    private void setRegionManager(World world) {
        SecObject.regionManager = this.getGlobalRegionManager().get(world);
    }
    
    /**
     * Sets the spawn of a region
     * 
     * @param region
     *            Name of region to change
     * @param sender
     *            CommandSender Object
     */
    public void setSpawnFlag(String regionname, CommandSender sender) {
        DeityAPI.getAPI().getSecAPI().setRegionManager(((Player) sender).getWorld());
        DeityAPI.getAPI().getSecAPI().setFlag(DeityAPI.getAPI().getSecAPI().getRegionFromName(((Player) sender).getWorld(), regionname), DefaultFlag.SPAWN_LOC, sender, "here");
        DeityAPI.getAPI().getSecAPI().saveRegionManager();
    }
    
    public void setUseFlag(String regionname, CommandSender sender, boolean allow) {
        DeityAPI.getAPI().getSecAPI().setRegionManager(((Player) sender).getWorld());
        DeityAPI.getAPI().getSecAPI().setFlag(DeityAPI.getAPI().getSecAPI().getRegionFromName(((Player) sender).getWorld(), regionname), DefaultFlag.USE, sender, (allow ? "allow" : "deny"));
        DeityAPI.getAPI().getSecAPI().saveRegionManager();
    }
    
    /**
     * Converts a object to a location
     * 
     * @param world
     *            World of the object
     * @param object
     *            Object to convert
     * @return Location
     */
    public Location toLocation(com.sk89q.worldedit.Location location) {
        return new Location(DeityAPI.plugin.getServer().getWorld(location.getWorld().getName()), location.getPosition().getX(), location.getPosition().getY(), location.getPosition().getZ(), location.getYaw(), location.getPitch());
    }
    
    /**
     * Converts a player to a location
     * 
     * @param player
     * @return
     */
    public Location toLocation(Player player) {
        Location loc = new Location(player.getWorld(), (int) player.getLocation().getX(), -128, (int) player.getLocation().getZ());
        return loc;
    }
    
    /**
     * Converts a vector to a location
     * 
     * @param world
     *            World of the vector
     * @param vector
     *            Vector to convert
     * @return Location
     */
    private Location toLocation(World world, Vector vector) {
        return BukkitUtil.toLocation(world, vector);
    }
    
    /**
     * Used to convert a set of coords to a vector object
     * 
     * @param x
     * @param y
     * @param z
     * @return Vector Vector object
     */
    private Vector toVector(double x, double y, double z) {
        return new Vector(x, y, z);
    }
    
    /**
     * Used to convert a location to a vector object
     * 
     * @param loc
     *            Location to convert
     * @return Vector Vector object
     */
    public Vector toVector(Location loc) {
        return new Vector(loc.getX(), loc.getY(), loc.getZ());
    }
    
    /**
     * Attempts to convert an object into a vector
     * 
     * @param object
     *            Object to convert
     * @return Vector Vector object
     */
    private Vector toVector(Object object) {
        return (Vector) object;
    }
    
    /**
     * Updates a regions land protection
     * 
     * @param world
     *            World the region is in
     * @param regionname
     *            Existing region name
     * @param cornerOne
     *            Coordinates of the new regions lowest point
     * @param cornerTwo
     *            Coordinates of the new regions highest point @ * Thrown if the
     *            regionmanager cannot be found
     */
    public void updateLand(World world, String regionname, Location cornerOne, Location cornerTwo) {
        ProtectedRegion region = null;
        ProtectedRegion existing = null;
        existing = this.getRegionFromName(world, regionname);
        Vector pointOne = DeityAPI.getAPI().getSecAPI().toVector(cornerOne);
        Vector pointTwo = DeityAPI.getAPI().getSecAPI().toVector(cornerTwo);
        
        if (existing != null) {
            String id = existing.getId();
            
            CuboidSelection sel = new CuboidSelection(world, pointOne, pointTwo);
            
            BlockVector min = sel.getNativeMinimumPoint().toBlockVector();
            BlockVector max = sel.getNativeMaximumPoint().toBlockVector();
            region = new ProtectedCuboidRegion(id, min, max);
            
            region.setMembers(existing.getMembers());
            region.setOwners(existing.getOwners());
            region.setFlags(existing.getFlags());
            region.setPriority(existing.getPriority());
            try {
                region.setParent(existing.getParent());
            } catch (CircularInheritanceException e) {
                e.printStackTrace();
            }
        }
        DeityAPI.getAPI().getSecAPI().addRegionToManager(region);
        DeityAPI.getAPI().getSecAPI().saveRegionManager();
    }
    
}
