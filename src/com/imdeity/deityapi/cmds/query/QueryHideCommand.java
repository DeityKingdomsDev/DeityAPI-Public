package com.imdeity.deityapi.cmds.query;

import org.bukkit.entity.Player;

import com.imdeity.deityapi.DeityAPI;
import com.imdeity.deityapi.api.DeityCommandReceiver;

/**
 * Sub-command of query. Turns off console logging
 * 
 * @author vanZeben
 */
public class QueryHideCommand extends DeityCommandReceiver {
    
    @Override
    public boolean onPlayerRunCommand(Player player, String[] args) {
        DeityAPI.getAPI().getDataAPI().getMySQL().setShowQueries(false);
        DeityAPI.plugin.chat.sendPlayerMessage(player, "You disabled console mysql query logging");
        DeityAPI.plugin.chat.out(player.getName() + " disabled console mysql query logging");
        return true;
    }
    
    @Override
    public boolean onConsoleRunCommand(String[] args) {
        DeityAPI.getAPI().getDataAPI().getMySQL().setShowQueries(true);
        DeityAPI.plugin.chat.out("You disabled console mysql query logging");
        return true;
    }
    
}
