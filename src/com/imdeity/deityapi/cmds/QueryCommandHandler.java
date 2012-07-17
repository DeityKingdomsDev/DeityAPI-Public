package com.imdeity.deityapi.cmds;

import com.imdeity.deityapi.api.DeityCommandHandler;
import com.imdeity.deityapi.cmds.query.QueryHideCommand;
import com.imdeity.deityapi.cmds.query.QueryShowCommand;

/**
 * Command to turn on logging of MySQL queries to console
 * 
 * @author vanZeben
 */
public class QueryCommandHandler extends DeityCommandHandler {
    
    public QueryCommandHandler(String pluginName) {
        super(pluginName, "Query");
    }
    
    @Override
    protected void initRegisteredCommands() {
        this.registerCommand("show", "", "Echos queries to console before executing", new QueryShowCommand(), "DeityAPI.admin");
        this.registerCommand("hide", "", "Stops echoing queries to console", new QueryHideCommand(), "DeityAPI.admin");
    }
    
}
