package com.imdeity.deityapi.object;

import java.io.File;
import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.imdeity.deityapi.DeityAPI;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.DisallowedItemException;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.InvalidFilenameException;
import com.sk89q.worldedit.InvalidItemException;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.UnknownItemException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldVector;
import com.sk89q.worldedit.bags.BlockBag;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockType;
import com.sk89q.worldedit.blocks.ClothColor;
import com.sk89q.worldedit.blocks.NoteBlock;
import com.sk89q.worldedit.blocks.SignBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.patterns.Pattern;
import com.sk89q.worldedit.patterns.SingleBlockPattern;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

/**
 * Handles interaction with WorldEdit
 * 
 * @author vanZeben
 */
public class EditObject {
    
    /**
     * WorldEditPlugin for API calls
     */
    private static WorldEditPlugin WorldEdit = null;
    
    /**
     * Instantiates the data member
     * 
     * @param worldEdit
     *            WorldEditPlugin
     */
    public EditObject(WorldEditPlugin worldEdit) {
        EditObject.WorldEdit = worldEdit;
    }
    
    public BaseBlock getBlock(String arg, LocalWorld world) throws UnknownItemException, DisallowedItemException {
        BlockType blockType;
        arg = arg.replace("_", " ");
        arg = arg.replace(";", "|");
        String[] blockAndExtraData = arg.split("\\|");
        String[] typeAndData = blockAndExtraData[0].split(":", 2);
        String testID = typeAndData[0];
        int blockId = -1;
        
        int data = -1;
        
        // Attempt to parse the item ID or otherwise resolve an item/block
        // name to its numeric ID
        try {
            blockId = Integer.parseInt(testID);
            blockType = BlockType.fromID(blockId);
        } catch (NumberFormatException e) {
            blockType = BlockType.lookup(testID);
            if (blockType == null) {
                
            }
        }
        
        if ((blockId == -1) && (blockType == null)) {
            // Maybe it's a cloth
            ClothColor col = ClothColor.lookup(testID);
            
            if (col != null) {
                blockType = BlockType.CLOTH;
                data = col.getID();
            } else {
                throw new UnknownItemException(arg);
            }
        }
        
        // Read block ID
        if (blockId == -1) {
            blockId = blockType.getID();
        }
        
        if (!world.isValidBlockType(blockId)) { throw new UnknownItemException(arg); }
        
        if (data == -1) { // Block data not yet detected
            // Parse the block data (optional)
            try {
                data = typeAndData.length > 1 ? Integer.parseInt(typeAndData[1]) : 0;
                if ((data > 15) || ((data < 0) && !(data == -1))) {
                    data = 0;
                }
            } catch (NumberFormatException e) {
                if (blockType != null) {
                    switch (blockType) {
                        case CLOTH:
                            ClothColor col = ClothColor.lookup(typeAndData[1]);
                            
                            if (col != null) {
                                data = col.getID();
                            } else {
                                throw new InvalidItemException(arg, "Unknown cloth color '" + typeAndData[1] + "'");
                            }
                            break;
                        
                        case STEP:
                        case DOUBLE_STEP:
                            BlockType dataType = BlockType.lookup(typeAndData[1]);
                            
                            if (dataType != null) {
                                switch (dataType) {
                                    case STONE:
                                        data = 0;
                                        break;
                                    
                                    case SANDSTONE:
                                        data = 1;
                                        break;
                                    
                                    case WOOD:
                                        data = 2;
                                        break;
                                    
                                    case COBBLESTONE:
                                        data = 3;
                                        break;
                                    case BRICK:
                                        data = 4;
                                        break;
                                    case STONE_BRICK:
                                        data = 5;
                                        
                                    default:
                                        throw new InvalidItemException(arg, "Invalid step type '" + typeAndData[1] + "'");
                                }
                            } else {
                                throw new InvalidItemException(arg, "Unknown step type '" + typeAndData[1] + "'");
                            }
                            break;
                        
                        default:
                            throw new InvalidItemException(arg, "Unknown data value '" + typeAndData[1] + "'");
                    }
                } else {
                    throw new InvalidItemException(arg, "Unknown data value '" + typeAndData[1] + "'");
                }
            }
        }
        
        // Check if the item is allowed
        if (blockType != null) {
            switch (blockType) {
                case SIGN_POST:
                case WALL_SIGN:
                    // Allow special sign text syntax
                    String[] text = new String[4];
                    text[0] = blockAndExtraData.length > 1 ? blockAndExtraData[1] : "";
                    text[1] = blockAndExtraData.length > 2 ? blockAndExtraData[2] : "";
                    text[2] = blockAndExtraData.length > 3 ? blockAndExtraData[3] : "";
                    text[3] = blockAndExtraData.length > 4 ? blockAndExtraData[4] : "";
                    return new SignBlock(blockType.getID(), data, text);
                    
                case MOB_SPAWNER:

                case NOTE_BLOCK:
                    if (blockAndExtraData.length > 1) {
                        byte note = Byte.parseByte(blockAndExtraData[1]);
                        if ((note < 0) || (note > 24)) {
                            throw new InvalidItemException(arg, "Out of range note value: '" + blockAndExtraData[1] + "'");
                        } else {
                            return new NoteBlock(data, note);
                        }
                    } else {
                        return new NoteBlock(data, (byte) 0);
                    }
                    
                default:
                    return new BaseBlock(blockId, data);
            }
        } else {
            return new BaseBlock(blockId, data);
        }
    }
    
    /**
     * Gets a cuboid from two locations passed
     * 
     * @param worldName
     *            World to get cuboid from
     * @param placementPosition
     *            Where the "player" is standing when they get the cuboid
     * @param maxPoint
     *            Top point of the cuboid
     * @param minPoint
     *            Bottom point of the cuboid
     * @return CuboidClipboard The cuboid object for other use
     * @throws IncompleteRegionException
     *             Thrown if the points to not match up
     */
    public CuboidClipboard getCuboidFromLocation(String worldName, Vector placementPosition, Vector maxPoint, Vector minPoint)
            throws IncompleteRegionException {
        
        Vector min = minPoint;
        Vector max = maxPoint;
        Vector pos = placementPosition;
        
        CuboidClipboard clipboard = new CuboidClipboard(max.subtract(min).add(new Vector(1, 1, 1)), min, min.subtract(pos));
        
        return clipboard;
    }
    
    /**
     * Gets the configuation of WorldEdit
     * 
     * @return LocalConfiguration Configuration
     */
    private LocalConfiguration getLocalConfig() {
        return EditObject.WorldEdit.getWorldEdit().getConfiguration();
    }
    
    /**
     * Gets a new session
     * 
     * @return LocalSession Session used for other tasks
     */
    private LocalSession getLocalSession() {
        return new LocalSession(this.getLocalConfig());
    }
    
    /**
     * Transfers a worldname into a LocalWorld
     * 
     * @param worldName
     *            Name of world to convert
     * @return LocalWorld LocalWorld object from Worldedit
     */
    private LocalWorld getLocalWorld(String worldName) {
        return new BukkitWorld(DeityAPI.plugin.getServer().getWorld(worldName));
    }
    
    public void pasteSchematicAtLocation(String worldName, String schematicName, Location loc) {
        try {
            this.pasteSchematicAtVector(worldName, schematicName, DeityAPI.getAPI().getSecAPI().toVector(loc));
        } catch (EmptyClipboardException e) {
            e.printStackTrace();
        } catch (DataException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Pastes a pre-existing Schematic at any coordinates
     * 
     * @param worldName
     *            World to paste in
     * @param schematicName
     *            Schematic to paste
     * @param loc
     *            Location to paste the Schematic
     * @throws DataException
     *             Thrown if the data doesnt match
     * @throws IOException
     *             Thrown if the schematic cannot be reached
     * @throws EmptyClipboardException
     *             Thrown if the schematic is empty
     */
    public void pasteSchematicAtVector(String worldName, String schematicName, Vector loc) throws DataException, IOException,
            EmptyClipboardException {
        
        // variables
        LocalSession session = this.getLocalSession();
        LocalWorld localWorld = this.getLocalWorld(worldName);
        Vector pos = WorldVector.toBlockPoint(localWorld, loc.getX(), loc.getY(), loc.getZ());
        
        // load
        String filename = schematicName + ".schematic";
        File dir = EditObject.WorldEdit.getWorldEdit().getWorkingDirectoryFile(this.getLocalConfig().saveDir);
        File f = new File(dir, filename);
        session.setClipboard(CuboidClipboard.loadSchematic(f));
        
        BlockBag blockBag = null;
        // paste
        EditSession editSession = new EditSession(localWorld, session.getBlockChangeLimit(), blockBag);
        editSession.setFastMode(session.hasFastMode());
        editSession.setMask(session.getMask());
        
        try {
            session.getClipboard().paste(editSession, pos, false);
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        }
    }
    
    public boolean regenRegion(World world, String regionName) {
        LocalSession session = this.getLocalSession();
        ProtectedRegion proRegion = DeityAPI.getAPI().getSecAPI().getRegionFromName(world, regionName);
        RegionSelector selector = session.getRegionSelector(this.getLocalWorld(world.getName()));
        selector.selectPrimary(proRegion.getMinimumPoint());
        selector.selectSecondary(proRegion.getMaximumPoint());
        EditSession editSession = new EditSession(this.getLocalWorld(world.getName()), -1);
        Region region = null;
        try {
            region = selector.getRegion();
            
        } catch (IncompleteRegionException e) {
            e.printStackTrace();
        }
        
        this.getLocalWorld(world.getName()).regenerate(region, editSession);
        
        return true;
        
    }
    
    public boolean regenArea(Location minLocation, Location maxLocation) {
        LocalSession session = this.getLocalSession();
        RegionSelector selector = session.getRegionSelector(this.getLocalWorld(minLocation.getWorld().getName()));
        selector.selectPrimary(DeityAPI.getAPI().getSecAPI().toVector(minLocation));
        selector.selectSecondary(DeityAPI.getAPI().getSecAPI().toVector(maxLocation));
        EditSession editSession = new EditSession(this.getLocalWorld(minLocation.getWorld().getName()), -1);
        Region region = null;
        try {
            region = selector.getRegion();
            this.getLocalWorld(minLocation.getWorld().getName()).regenerate(region, editSession);
        } catch (IncompleteRegionException e) {
            e.printStackTrace();
        }
        return true;
    }
    
    /**
     * Saves a cuboid to memory
     * 
     * @param schematicName
     *            Name of schematic to save
     * @param schematic
     *            Cuboid of the area wanted to save
     * @throws InvalidFilenameException
     *             Thrown if the file already exists
     * @throws EmptyClipboardException
     *             Thrown if no cuboid is passed
     * @throws IOException
     *             Thrown if the file location cannot be reached
     * @throws DataException
     *             Thrown if the save fails
     */
    public void saveSchematic(String schematicName, CuboidClipboard schematic) throws InvalidFilenameException,
            EmptyClipboardException, IOException, DataException {
        
        LocalSession session = this.getLocalSession();
        session.setClipboard(schematic);
        
        String filename = schematicName + ".schematic";
        if (!filename.matches("^[A-Za-z0-9_\\- \\./\\\\'\\$@~!%\\^\\*\\(\\)\\[\\]\\+\\{\\},\\?]+\\.[A-Za-z0-9]+$")) { throw new InvalidFilenameException(
                filename, "Invalid characters or extension missing"); }
        File dir = EditObject.WorldEdit.getWorldEdit().getWorkingDirectoryFile(this.getLocalConfig().saveDir);
        File f = new File(dir, filename);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                DeityAPI.getAPI().getChatAPI()
                        .outWarn("DeityAPI", "Error on Saving schematic. The storage folder could not be created.");
            }
        }
        File parent = f.getParentFile();
        if ((parent != null) && !parent.exists()) {
            parent.mkdirs();
        }
        
        session.getClipboard().saveSchematic(f);
        
    }
    
    /**
     * Saves a cuboid to memory
     * 
     * @param schematicName
     *            Name of schematic to save
     * @param schematic
     *            Cuboid of the area wanted to save
     */
    public void saveSchematic(String schematicName, String regionNameToSave, String worldName) {
        World world = DeityAPI.plugin.getServer().getWorld(worldName);
        ProtectedRegion region = DeityAPI.getAPI().getSecAPI().getRegionFromName(world, regionNameToSave);
        
        Vector min = region.getMinimumPoint();
        Vector max = region.getMaximumPoint();
        Vector pos = region.getMinimumPoint();
        
        CuboidClipboard clipboard = null;
        try {
            clipboard = this.getCuboidFromLocation(worldName, pos, max, min);
        } catch (IncompleteRegionException e1) {
            e1.printStackTrace();
        }
        LocalSession session = this.getLocalSession();
        session.setClipboard(clipboard);
        
        String filename = schematicName + ".schematic";
        if (!filename.matches("^[A-Za-z0-9_\\- \\./\\\\'\\$@~!%\\^\\*\\(\\)\\[\\]\\+\\{\\},\\?]+\\.[A-Za-z0-9]+$")) {
            try {
                throw new InvalidFilenameException(filename, "Invalid characters or extension missing");
            } catch (InvalidFilenameException e) {
                e.printStackTrace();
            }
        }
        File dir = EditObject.WorldEdit.getWorldEdit().getWorkingDirectoryFile(this.getLocalConfig().saveDir);
        File f = new File(dir, filename);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                DeityAPI.getAPI().getChatAPI()
                        .outWarn("DeityAPI", "Error on Saving schematic. The storage folder could not be created.");
                return;
            }
        }
        File parent = f.getParentFile();
        if ((parent != null) && !parent.exists()) {
            parent.mkdirs();
        }
        
        try {
            session.getClipboard().saveSchematic(f);
        } catch (EmptyClipboardException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DataException e) {
            e.printStackTrace();
        }
        
    }
    
    public void saveSchematicFromCoords(String schematicName, String worldName, Location minPoint, Location maxPoint) {
        try {
            this.saveSchematic(
                    schematicName,
                    this.getCuboidFromLocation(worldName, DeityAPI.getAPI().getSecAPI().toVector(minPoint), DeityAPI.getAPI()
                            .getSecAPI().toVector(maxPoint), DeityAPI.getAPI().getSecAPI().toVector(minPoint)));
        } catch (InvalidFilenameException e) {
            e.printStackTrace();
        } catch (EmptyClipboardException e) {
            e.printStackTrace();
        } catch (IncompleteRegionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DataException e) {
            e.printStackTrace();
        }
    }
    
    public void saveSchematicFromRegion(String schematicName, String regionName, String worldName) {
        this.saveSchematic(schematicName, regionName, worldName);
    }
    
    public int setAreaWithBlock(String world, Location minPoint, Location maxPoint, String blockId) {
        LocalSession session = this.getLocalSession();
        RegionSelector selector = session.getRegionSelector(this.getLocalWorld(world));
        selector.selectPrimary(DeityAPI.getAPI().getSecAPI().toVector(minPoint));
        selector.selectSecondary(DeityAPI.getAPI().getSecAPI().toVector(maxPoint));
        EditSession editSession = new EditSession(this.getLocalWorld(world), -1);
        Pattern pattern = null;
        try {
            pattern = new SingleBlockPattern(this.getBlock(blockId, this.getLocalWorld(world)));
        } catch (UnknownItemException e) {
            e.printStackTrace();
        } catch (DisallowedItemException e) {
            e.printStackTrace();
        }
        int affected = -1;
        
        if (pattern instanceof SingleBlockPattern) {
            try {
                affected = editSession.setBlocks(selector.getRegion(), ((SingleBlockPattern) pattern).getBlock());
            } catch (MaxChangedBlocksException e) {
                e.printStackTrace();
            } catch (IncompleteRegionException e) {
                e.printStackTrace();
            }
        } else {
            try {
                affected = editSession.setBlocks(selector.getRegion(), pattern);
            } catch (MaxChangedBlocksException e) {
                e.printStackTrace();
            } catch (IncompleteRegionException e) {
                e.printStackTrace();
            }
            
        }
        return affected;
    }
    
    public Location getMinLocation(Player player) {
        Selection selection = EditObject.WorldEdit.getSelection(player);
        return selection.getMinimumPoint();
    }
    
    public Location getMaxLocation(Player player) {
        Selection selection = EditObject.WorldEdit.getSelection(player);
        return selection.getMaximumPoint();
    }
}
