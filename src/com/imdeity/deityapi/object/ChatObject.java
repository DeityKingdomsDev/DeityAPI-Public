package com.imdeity.deityapi.object;

import java.util.logging.Logger;

import org.bukkit.entity.Player;

import com.imdeity.deityapi.DeityAPI;

/**
 * Handles interaction with messaging
 * 
 * @author vanZeben
 */
public class ChatObject {
    
    /**
     * Used to message to console
     */
    private Logger log = Logger.getLogger("Minecraft");
    
    /**
     * Sends a message to console
     * 
     * @param msg
     *            Message to send
     * @param option
     *            Title of plugin
     */
    public void out(String option, String msg) {
        this.log.info("[" + option + "] " + msg);
    }
    
    /**
     * Sends a warning to console
     * 
     * @param msg
     *            Warning to send
     * @param option
     *            Title of plugin
     */
    public void outWarn(String option, String msg) {
        this.log.warning("[" + option + "] " + msg);
    }
    
    /**
     * Sends a severe to console
     * 
     * @param msg
     *            Warning to send
     * @param option
     *            Title of plugin
     */
    public void outSevere(String option, String msg) {
        this.log.severe("[" + option + "] " + msg);
    }
    
    /**
     * Sends a message to all players who are online
     * 
     * @param msg
     *            Message to send
     */
    public void sendGlobalMessage(String option, String msg) {
        if (option == null || option.isEmpty() || option.equals("")) {
            DeityAPI.plugin.getServer().broadcastMessage(DeityAPI.getAPI().getUtilAPI().getChatUtils().formatMessage(msg, true, ""));
            return;
        }
        DeityAPI.plugin.getServer().broadcastMessage(DeityAPI.getAPI().getUtilAPI().getChatUtils().formatMessage("<option>" + msg, true, option));
    }
    
    /**
     * Sends a error to a player (Message in red color)
     * 
     * @param player
     *            Player to send message to
     * @param option
     *            Text to put at the beginning
     * @param msg
     *            Message to send
     * @return boolean Whether the message was sent
     */
    public boolean sendPlayerError(Player player, String option, String msg) {
        if (player != null) {
            if (player.isOnline()) {
                DeityAPI.getAPI().getUtilAPI().getChatUtils().formatAndSend(("<option><red>" + msg), option, player);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Sends a specific player a message
     * 
     * @param player
     *            Player to send message to
     * @param option
     *            Text to put at the beginning
     * @param msg
     *            Message to send
     * @return boolean Whether or not the message was sent
     */
    public boolean sendPlayerMessage(Player player, String option, String msg) {
        if (player != null) {
            if (player.isOnline()) {
                DeityAPI.getAPI().getUtilAPI().getChatUtils().formatAndSend("<option>&f" + msg, option, player);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Sends a specific player a message
     * 
     * @param player
     *            Player to send message to
     * @param msg
     *            Message to send
     * @return boolean Whether or not the message was sent
     */
    public boolean sendPlayerMessageNoTitle(Player player, String msg) {
        if (player != null) {
            if (player.isOnline()) {
                DeityAPI.getAPI().getUtilAPI().getChatUtils().formatAndSend(msg, "", player);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Sends a specific player a message
     * 
     * @param player
     *            Player to send message to
     * @param msg
     *            Message to send
     * @return boolean Whether or not the message was sent
     */
    public boolean sendPlayerMessageNoTitleNewLine(Player player, String msg) {
        if (player != null) {
            if (player.isOnline()) {
                DeityAPI.getAPI().getUtilAPI().getChatUtils().formatAndSendWithNewLines(msg, player);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Sends a specific player a non-formatted message
     * 
     * @param player
     *            Player to send message to
     * @param msg
     *            Message to send
     * @return boolean Whether or not the message was sent
     */
    public boolean sendPlayerNonFormattedMessage(Player player, String msg) {
        if (player != null) {
            if (player.isOnline()) {
                DeityAPI.getAPI().getUtilAPI().getChatUtils().send(msg, player);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Sends a warning to a player (Message in yellow color)
     * 
     * @param player
     *            Player to send message to
     * @param option
     *            Text to put at the beginning
     * @param msg
     *            Message to send
     * @return boolean Whether the message was sent
     */
    public boolean sendPlayerWarning(Player player, String option, String msg) {
        if (player != null) {
            if (player.isOnline()) {
                DeityAPI.getAPI().getUtilAPI().getChatUtils().formatAndSend(("<option><yellow>" + msg), option, player);
                return true;
            }
        }
        return false;
    }
}
