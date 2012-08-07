package com.imdeity.deityapi.object;

import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.imdeity.deityapi.DeityAPI;
import com.imdeity.mail.MailMain;

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
    
    private Chat chat = null;
    
    /**
     * Main Constructor
     */
    public ChatObject() {
        this.chat = null;
    }
    
    public ChatObject(Chat chat) {
        this.chat = chat;
    }
    
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
    
    /**
     * Sends a message to all online staff
     * 
     * @param option
     *            Text to put at the beginning
     * @param msg
     *            Message to send
     */
    public void sendMessageToOnlineStaff(String option, String msg) {
        for (Player p : DeityAPI.plugin.getServer().getOnlinePlayers()) {
            if (DeityAPI.getAPI().getPermAPI().hasPermission(p, "deityapi.staff")) {
                this.sendPlayerMessage(p, option, msg);
            }
        }
    }
    
    /**
     * Sends a mail if the Mail plugin is available
     * 
     * @param sender
     *            Person who sent the mail
     * @param receiver
     *            Person who receives the mail
     * @param message
     *            Message that is sent
     */
    public void sendMailToPlayer(String sender, String receiver, String message) {
        if (DeityAPI.plugin.getServer().getPluginManager().getPlugin("Mail") != null) {
            if (((MailMain) DeityAPI.plugin.getServer().getPluginManager().getPlugin("Mail")).getMailPlayerAPI(sender) != null) {
                ((MailMain) DeityAPI.plugin.getServer().getPluginManager().getPlugin("Mail")).getMailPlayerAPI(sender).sendMail(receiver, message);
            }
        }
    }
    
    /**
     * Retrieves a players prefix
     * 
     * @param world
     *            World the prefix is in
     * @param playername
     *            Player whos prefix you wish to retrieve
     * @return
     */
    public String getPlayerPrefix(World world, String playername) {
        return chat.getPlayerPrefix(world, playername);
    }
    
    /**
     * Retrieves a players suffix
     * 
     * @param world
     *            World the suffix is in
     * @param playername
     *            Player whos suffix you wish to retrieve
     * @return
     */
    public String getPlayerSuffix(World world, String playername) {
        return chat.getPlayerSuffix(world, playername);
    }
    
}
