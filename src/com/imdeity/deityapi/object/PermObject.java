package com.imdeity.deityapi.object;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.entity.Player;

;

public class PermObject {
    
    public Permission perm = null;
    public Chat chat = null;
    
    public PermObject(Permission perm) {
        this.perm = perm;
    }
    
    /**
     * Checks for a players permission. Op = true
     * 
     * @param player
     *            Player to check
     * @param permissionNode
     *            Node to check against
     * @return
     */
    public boolean hasPermission(Player player, String permissionNode) {
        if (player.isOp()) { return true; }
        return perm.has(player, permissionNode);
    }
    
    /**
     * Returns the registered Permission manager
     * 
     * @return
     */
    public Permission getPermissionManager() {
        return perm;
    }
}
