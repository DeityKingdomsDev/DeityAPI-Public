package com.imdeity.deityapi.cmds.query;

import org.bukkit.entity.Player;

import com.imdeity.deityapi.DeityAPI;
import com.imdeity.deityapi.api.DeityCommandReceiver;

/**
 * Sub-command of Query. Turns on console logging
 * 
 * @author vanZeben
 */
public class QueryShowCommand extends DeityCommandReceiver {
    
    @Override
    public boolean onPlayerRunCommand(Player player, String[] args) {
        DeityAPI.getAPI().getDataAPI().getMySQL().setShowQueries(true);
        DeityAPI.plugin.chat.sendPlayerMessage(player, "You enabled console mysql query logging");
        DeityAPI.plugin.chat.out(player.getName() + " enabled console mysql query logging");
        return true;
    }
    
    @Override
    public boolean onConsoleRunCommand(String[] args) {
        DeityAPI.getAPI().getDataAPI().getMySQL().setShowQueries(true);
        DeityAPI.plugin.chat.out("You enabled console mysql query logging");
        return true;
    }
    
}
