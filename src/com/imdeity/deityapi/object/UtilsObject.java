package com.imdeity.deityapi.object;

import org.bukkit.Location;

import com.imdeity.deityapi.utils.ChatTools;
import com.imdeity.deityapi.utils.FileMgmt;
import com.imdeity.deityapi.utils.HumanTime;
import com.imdeity.deityapi.utils.StringMgmt;

/**
 * Utility functions
 * 
 * @author vanZeben
 */
public class UtilsObject {
    
    private StringMgmt string = new StringMgmt();
    private ChatTools chat = new ChatTools();
    private FileMgmt file = new FileMgmt();
    private HumanTime time = new HumanTime();
    
    /**
     * String utilities
     * 
     * @return
     */
    public StringMgmt getStringUtils() {
        return string;
    }
    
    /**
     * Chat Utilities
     * 
     * @return
     */
    public ChatTools getChatUtils() {
        return chat;
    }
    
    /**
     * File utilities
     * 
     * @return
     */
    public FileMgmt getFileUtils() {
        return file;
    }
    
    /**
     * Time utilities
     * 
     * @return
     */
    public HumanTime getTimeUtils() {
        return time;
    }
    
    /**
     * Zeros out a loction.
     * 
     * @param loc
     * @return
     */
    public Location fixLocation(Location loc) {
        loc.setX(loc.getBlockX());
        loc.setY(loc.getBlockY());
        loc.setZ(loc.getBlockZ());
        loc.setPitch(0);
        loc.setYaw(0);
        return loc;
    }
}
