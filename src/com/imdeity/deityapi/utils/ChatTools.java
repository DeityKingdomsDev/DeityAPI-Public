package com.imdeity.deityapi.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Do NOT reference this class directly, all interaction with these methods can
 * be achieved through the UtilAPI
 * 
 * @author vanZeben
 */
public class ChatTools {
    public final char deg = '\u00A7';
    
    /**
     * Removes the ending chars as long as they are deg or deg+'anychar' or a
     * space As I see it we would never want those chars at the end of a msg.
     */
    private String cleanMsgEnding(String msg) {
        
        while (msg.length() > 0) {
            if (msg.endsWith(String.valueOf(this.deg)) || msg.endsWith(" ")) {
                msg = msg.substring(0, msg.length() - 1);
            } else if ((msg.length() >= 2) && (msg.charAt(msg.length() - 2) == this.deg)) {
                msg = msg.substring(0, msg.length() - 2);
            } else {
                break;
            }
        }
        return msg;
    }
    
    /**
     * This method wraps the msg for you at row lengths of 53, avoids client
     * crash scenarios and makes the previous color continue on the next line.
     * The upsides with filtering your messages through this method are: - No
     * client crashes. - Line wrapping with preserved color. The downsides are:
     * - The width of the chat window will not be used to it's fullest. For
     * example you can fit more that 53 commas (,) in a chatwindow row but the
     * line would break after 53 displayed chars. Suggested usage: NO NEED TO
     * USE the fix method for static help pages in your plugin. As the text is
     * static you can make sure there is no client crash yourself and be able to
     * use the full line length. DO USE in cases like where you output colored
     * messages with playernames in your plugin. As the player names have
     * different length there is potential for client crash.
     */
    public String fix(String msg) {
        // Make sure the end of msg is good
        msg = this.cleanMsgEnding(msg);
        return msg.trim();
    }
    
    /**
     * Formats and sends output
     * 
     * @param msg
     * @param player
     */
    public void formatAndSend(String msg, Player player) {
        String message = this.formatMessage(msg, true);
        player.sendMessage(this.fix(message));
    }
    
    /**
     * Formats and sends output with a header
     * 
     * @param msg
     * @param option
     * @param player
     */
    public void formatAndSend(String msg, String option, Player player) {
        String message = this.formatMessage(msg, true, option);
        player.sendMessage(this.fix(message));
    }
    
    /**
     * Format and sends output with lines
     * 
     * @param msg
     * @param player
     */
    public void formatAndSendWithNewLines(String msg, Player player) {
        String message = this.formatMessage(msg, true);
        for (String s : message.split("&/")) {
            player.sendMessage(this.fix(s));
        }
    }
    
    /**
     * Formats a message both &<colourcode> and <colourname> are supported
     * 
     * @param msg
     * @param formatting
     * @param option
     * @return
     */
    public String formatMessage(String msg, boolean formatting, String... option) {
        String message = msg;
        if (formatting) {
            if (message.contains("<option>")) {
                message = message.replaceAll("<option>", ("<gray>[<red>" + option[0] + "<gray>] "));
            }
            if (message.contains("<subheader>")) {
                message = message.replaceAll("<subheader>", ("<gray>[<red>*<gray>] "));
            }
            if (message.contains("<black>") || message.contains("&0")) {
                message = message.replaceAll("<black>", "" + ChatColor.BLACK).replaceAll("&0", "" + ChatColor.BLACK);
            }
            if (message.contains("<darkblue>") || message.contains("&1")) {
                message = message.replaceAll("<darkblue>", "" + ChatColor.DARK_BLUE).replaceAll("&1", "" + ChatColor.DARK_BLUE);
            }
            if (message.contains("<darkgreen>") || message.contains("&2")) {
                message = message.replaceAll("<darkgreen>", "" + ChatColor.DARK_GREEN).replaceAll("&2", "" + ChatColor.DARK_GREEN);
            }
            if (message.contains("<teal>") || message.contains("&3")) {
                message = message.replaceAll("<teal>", "" + ChatColor.DARK_AQUA).replaceAll("&3", "" + ChatColor.DARK_AQUA);
            }
            if (message.contains("<darkred>") || message.contains("&4")) {
                message = message.replaceAll("<darkred>", "" + ChatColor.DARK_RED).replaceAll("&4", "" + ChatColor.DARK_RED);
            }
            if (message.contains("<darkpurple>") || message.contains("&5")) {
                message = message.replaceAll("<darkpurple>", "" + ChatColor.DARK_PURPLE).replaceAll("&5", "" + ChatColor.DARK_PURPLE);
            }
            if (message.contains("<gold>") || message.contains("&6")) {
                message = message.replaceAll("<gold>", "" + ChatColor.GOLD).replaceAll("&6", "" + ChatColor.GOLD);
            }
            if (message.contains("<gray>") || message.contains("&7")) {
                message = message.replaceAll("<gray>", "" + ChatColor.GRAY).replaceAll("&7", "" + ChatColor.GRAY);
            }
            if (message.contains("<darkgray>") || message.contains("&8")) {
                message = message.replaceAll("<darkgray>", "" + ChatColor.DARK_GRAY).replaceAll("&8", "" + ChatColor.DARK_GRAY);
            }
            if (message.contains("<blue>") || message.contains("&9")) {
                message = message.replaceAll("<blue>", "" + ChatColor.BLUE).replaceAll("&9", "" + ChatColor.BLUE);
            }
            if (message.contains("<green>") || message.contains("&a")) {
                message = message.replaceAll("<green>", "" + ChatColor.GREEN).replaceAll("&a", "" + ChatColor.GREEN);
            }
            if (message.contains("<aqua>") || message.contains("&b")) {
                message = message.replaceAll("<aqua>", "" + ChatColor.AQUA).replaceAll("&b", "" + ChatColor.AQUA);
            }
            if (message.contains("<red>") || message.contains("&c")) {
                message = message.replaceAll("<red>", "" + ChatColor.RED).replaceAll("&c", "" + ChatColor.RED);
            }
            if (message.contains("<purple>") || message.contains("&d")) {
                message = message.replaceAll("<purple>", "" + ChatColor.LIGHT_PURPLE).replaceAll("&d", "" + ChatColor.LIGHT_PURPLE);
            }
            if (message.contains("<yellow>") || message.contains("&e")) {
                message = message.replaceAll("<yellow>", "" + ChatColor.YELLOW).replaceAll("&e", "" + ChatColor.YELLOW);
            }
            if (message.contains("<white>") || message.contains("&f")) {
                message = message.replaceAll("<white>", "" + ChatColor.WHITE).replaceAll("&f", "" + ChatColor.WHITE);
            }
            if (message.contains("&l")) {
                message = message.replaceAll("&l", "" + ChatColor.BOLD);
            }
            if (message.contains("&o")) {
                message = message.replaceAll("&o", "" + ChatColor.ITALIC);
            }
            if (message.contains("&k")) {
                message = message.replaceAll("&k", "" + ChatColor.MAGIC);
            }
            if (message.contains("&m")) {
                message = message.replaceAll("&m", "" + ChatColor.STRIKETHROUGH);
            }
            if (message.contains("&n")) {
                message = message.replaceAll("&n", "" + ChatColor.UNDERLINE);
            }
        } else {
            if (message.contains("<option>")) {
                message = message.replaceAll("<option>", "");
            }
            if (message.contains("<subheader>")) {
                message = message.replaceAll("<subheader>", "");
            }
            if (message.contains("<black>") || message.contains("&0")) {
                message = message.replaceAll("<black>", "").replaceAll("&0", "");
            }
            if (message.contains("<darkblue>") || message.contains("&1")) {
                message = message.replaceAll("<darkblue>", "").replaceAll("&1", "");
            }
            if (message.contains("<darkgreen>") || message.contains("&2")) {
                message = message.replaceAll("<darkgreen>", "").replaceAll("&2", "");
            }
            if (message.contains("<teal>") || message.contains("&3")) {
                message = message.replaceAll("<teal>", "").replaceAll("&3", "");
            }
            if (message.contains("<darkred>") || message.contains("&4")) {
                message = message.replaceAll("<darkred>", "").replaceAll("&4", "");
            }
            if (message.contains("<darkpurple>") || message.contains("&5")) {
                message = message.replaceAll("<darkpurple>", "").replaceAll("&5", "");
            }
            if (message.contains("<gold>") || message.contains("&6")) {
                message = message.replaceAll("<gold>", "").replaceAll("&6", "");
            }
            if (message.contains("<gray>") || message.contains("&7")) {
                message = message.replaceAll("<gray>", "").replaceAll("&7", "");
            }
            if (message.contains("<darkgray>") || message.contains("&8")) {
                message = message.replaceAll("<darkgray>", "").replaceAll("&8", "");
            }
            if (message.contains("<blue>") || message.contains("&9")) {
                message = message.replaceAll("<blue>", "").replaceAll("&9", "");
            }
            if (message.contains("<green>") || message.contains("&a")) {
                message = message.replaceAll("<green>", "").replaceAll("&a", "");
            }
            if (message.contains("<aqua>") || message.contains("&b")) {
                message = message.replaceAll("<aqua>", "").replaceAll("&b", "");
            }
            if (message.contains("<red>") || message.contains("&c")) {
                message = message.replaceAll("<red>", "").replaceAll("&c", "");
            }
            if (message.contains("<purple>") || message.contains("&d")) {
                message = message.replaceAll("<purple>", "").replaceAll("&d", "");
            }
            if (message.contains("<yellow>") || message.contains("&e")) {
                message = message.replaceAll("<yellow>", "").replaceAll("&e", "");
            }
            if (message.contains("<white>") || message.contains("&f")) {
                message = message.replaceAll("<white>", "").replaceAll("&f", "");
            }
            if (message.contains("&l")) {
                message = message.replaceAll("&l", "");
            }
            if (message.contains("&o")) {
                message = message.replaceAll("&o", "");
            }
            if (message.contains("&k")) {
                message = message.replaceAll("&k", "");
            }
            if (message.contains("&m")) {
                message = message.replaceAll("&m", "");
            }
            if (message.contains("&n")) {
                message = message.replaceAll("&n", "");
            }
        }
        return message;
    }
    
    /**
     * Sends a message without formatting
     * 
     * @param msg
     * @param player
     */
    public void send(String msg, Player player) {
        String message = this.formatMessage(msg, false);
        player.sendMessage(this.fix(message));
    }
    
    /**
     * Strips colour from a message
     * 
     * @param line
     * @return
     */
    public String strip(String line) {
        for (ChatColor cc : ChatColor.values()) {
            line.replaceAll(cc.toString(), "");
        }
        line.replaceAll("å¤0", "");
        line.replaceAll("å¤1", "");
        line.replaceAll("å¤2", "");
        line.replaceAll("å¤3", "");
        line.replaceAll("å¤4", "");
        line.replaceAll("å¤5", "");
        line.replaceAll("å¤6", "");
        line.replaceAll("å¤7", "");
        line.replaceAll("å¤a", "");
        line.replaceAll("å¤b", "");
        line.replaceAll("å¤c", "");
        line.replaceAll("å¤d", "");
        line.replaceAll("å¤e", "");
        line.replaceAll("å¤f", "");
        return line;
    }
}
