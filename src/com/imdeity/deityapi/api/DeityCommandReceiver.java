package com.imdeity.deityapi.api;

import org.bukkit.entity.Player;

/**
 * This class should be extended by all sub-command classes
 * 
 * @author vanZeben
 */
public abstract class DeityCommandReceiver {
    
    /**
     * Called when a player runs the sub-command
     * 
     * @param player
     *            Player who ran the sub-command
     * @param args
     *            Arguments the player entered
     * @return boolean Whether or not the command has valid args/Whether the
     *         player has permission
     */
    public abstract boolean onPlayerRunCommand(Player player, String[] args);
    
    /**
     * Called when console runs the sub-command
     * 
     * @param args
     *            Arguments the console entered
     * @return boolean Whether or not the command has valid args
     */
    public abstract boolean onConsoleRunCommand(String[] args);
}
