package com.imdeity.deityapi.api;

import java.util.HashMap;
import java.util.Map;

/**
 * Class used to keep track of all registered plugins using the api.
 * 
 * @author vanZeben
 */
public class DeityRegistration {
    
    private Map<String, DeityPlugin> registeredPlugins = new HashMap<String, DeityPlugin>();
    
    /**
     * Registeres a plugin
     * 
     * @param plugin
     */
    public void registerPlugin(DeityPlugin plugin) {
        if (!this.registeredPlugins.containsKey(plugin.getDescription().getName().toLowerCase())) {
            this.registeredPlugins.put(plugin.getDescription().getName().toLowerCase(), plugin);
        }
    }
}
