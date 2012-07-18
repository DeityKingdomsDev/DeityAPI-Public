package com.imdeity.deityapi;

import java.io.File;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.imdeity.deityapi.api.DeityPlugin;
import com.imdeity.deityapi.api.DeityRegistration;
import com.imdeity.deityapi.cmds.QueryCommandHandler;
import com.imdeity.deityapi.object.ChatObject;
import com.imdeity.deityapi.object.DataObject;
import com.imdeity.deityapi.object.EconObject;
import com.imdeity.deityapi.object.EditObject;
import com.imdeity.deityapi.object.EffectObject;
import com.imdeity.deityapi.object.MobObject;
import com.imdeity.deityapi.object.PlayerObject;
import com.imdeity.deityapi.object.SecObject;
import com.imdeity.deityapi.object.UtilsObject;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class DeityAPI extends DeityPlugin {
    
    public static DeityAPI plugin;
    private static InternalAPI internalAPI;
    public static DeityRegistration registration = new DeityRegistration();
    
    private QueryCommandHandler queryCommandHandler = new QueryCommandHandler("DeityAPI");
    
    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();
        initPlugin();
        config = new DeityPluginConfig(getDescription().getName(), this.getConfig(), "plugins/" + getDescription().getName() + "/config.yml");
        initConfig();
        config.saveConfig();
        language = new DeityPluginLanguage(getDescription().getName(), YamlConfiguration.loadConfiguration(new File("plugins/" + getDescription().getName() + "/language.yml")), "plugins/" + getDescription().getName() + "/language.yml");
        initLanguage();
        language.save();
        internalAPI = this.new InternalAPI();
        chat = new DeityPluginChat(getDescription().getName());
        
        initDatabase();
        initCmds();
        initListeners();
        initTasks();
        initInternalDatamembers();
        registration.registerPlugin(this);
        long finalTime = System.currentTimeMillis();
        
        chat.out("Enabled - " + ((finalTime - startTime) / 1000) + (((finalTime - startTime) / 1000) == 1 ? " second" : " seconds"));
    }
    
    @Override
    protected void initConfig() {
        this.config.addDefaultConfigValue(DeityAPIConfigHelper.SHOULD_PROFILE, true);
        this.config.addDefaultConfigValue(DeityAPIConfigHelper.MYSQL_SERVER_ADDRESS, "localhost");
        this.config.addDefaultConfigValue(DeityAPIConfigHelper.MYSQL_SERVER_PORT, 3306);
        this.config.addDefaultConfigValue(DeityAPIConfigHelper.MYSQL_DATABASE_NAME, "kingdoms");
        this.config.addDefaultConfigValue(DeityAPIConfigHelper.MYSQL_DATABASE_USERNAME, "root");
        this.config.addDefaultConfigValue(DeityAPIConfigHelper.MYSQL_DATABASE_PASSWORD, "root");
    }
    
    @Override
    protected void initLanguage() {
    }
    
    @Override
    protected void initCmds() {
        this.registerCommand(queryCommandHandler);
    }
    
    @Override
    protected void initListeners() {
    }
    
    @Override
    protected void initDatabase() {
    }
    
    @Override
    protected void initTasks() {
    }
    
    @Override
    protected void initInternalDatamembers() {
    }
    
    @Override
    protected void initPlugin() {
        DeityAPI.plugin = this;
    }
    
    /**
     * Returns access to the API handler
     * 
     * @return InternalAPI
     */
    public static InternalAPI getAPI() {
        return internalAPI;
    }
    
    public class InternalAPI {
        private ChatObject chat;
        private DataObject data;
        private EconObject econ;
        private EditObject edit;
        private EffectObject effect;
        private MobObject mob;
        private PlayerObject player;
        private SecObject sec;
        private UtilsObject utils;
        
        public InternalAPI() {
            chat = new ChatObject();
            
            try {
                data = new DataObject();
                getChatAPI().out("DeityAPI", "DataAPI hooked and loaded");
            } catch (Exception e) {
                getChatAPI().outSevere("DeityAPI", "DataAPI Offline: " + e.getLocalizedMessage());
                data = null;
            }
            
            try {
                if (getServer().getPluginManager().getPlugin("Vault") != null) {
                    getServer().getPluginManager().enablePlugin(getServer().getPluginManager().getPlugin("Vault"));
                    RegisteredServiceProvider<Economy> rsp = DeityAPI.plugin.getServer().getServicesManager().getRegistration(Economy.class);
                    econ = new EconObject(rsp.getProvider());
                    getChatAPI().out("DeityAPI", "EconAPI hooked and loaded");
                } else {
                    throw new NoClassDefFoundError("Vault not found");
                }
            } catch (NoClassDefFoundError e) {
                getChatAPI().outSevere("DeityAPI", "EconAPI Offline: " + e.getLocalizedMessage());
                econ = null;
            }
            
            try {
                if (getServer().getPluginManager().getPlugin("WorldEdit") != null) {
                    edit = new EditObject((WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit"));
                    getChatAPI().out("DeityAPI", "EditAPI hooked and loaded");
                } else {
                    throw new NoClassDefFoundError("WorldEdit not found");
                }
            } catch (NoClassDefFoundError e) {
                getChatAPI().outSevere("DeityAPI", "EditAPI Offline: " + e.getLocalizedMessage());
                edit = null;
            }
            
            try {
                effect = new EffectObject();
                getChatAPI().out("DeityAPI", "EffectAPI hooked and loaded");
            } catch (Exception e) {
                getChatAPI().outSevere("DeityAPI", "EffectAPI Offline");
                effect = null;
            }
            
            try {
                mob = new MobObject();
                getChatAPI().out("DeityAPI", "MobAPI hooked and loaded");
            } catch (Exception e) {
                getChatAPI().outSevere("DeityAPI", "MobAPI Offline");
                mob = null;
            }
            
            try {
                player = new PlayerObject();
                getChatAPI().out("DeityAPI", "PlayerAPI hooked and loaded");
            } catch (Exception e) {
                getChatAPI().outSevere("DeityAPI", "PlayerAPI Offline");
                player = null;
            }
            
            try {
                if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
                    sec = new SecObject((WorldGuardPlugin) getServer().getPluginManager().getPlugin("WorldGuard"));
                    getChatAPI().out("DeityAPI", "SecAPI hooked and loaded");
                } else {
                    throw new NoClassDefFoundError("WorldGuard not found");
                }
            } catch (NoClassDefFoundError e) {
                getChatAPI().outSevere("DeityAPI", "SecAPI Offline: " + e.getLocalizedMessage());
                sec = null;
            }
            
            try {
                utils = new UtilsObject();
                getChatAPI().out("DeityAPI", "UtilsAPI hooked and loaded");
            } catch (Exception e) {
                getChatAPI().outSevere("DeityAPI", "UtilsAPI Offline");
                utils = null;
            }
        }
        
        /**
         * Returns access to the ChatAPI, will return null if the API is offline
         * 
         * @return ChatObject
         */
        public ChatObject getChatAPI() {
            if (chat == null) {
                Logger.getLogger("Minecraft").warning("[DeityAPI] " + this.getClass().getEnclosingClass().getName() + " attempted to access the chat api which is offline (Check startup logs)");
                return null;
            }
            return chat;
        }
        
        /**
         * Returns access to the DataAPI, will return null if the API is offline
         * 
         * @return DataObject
         */
        public DataObject getDataAPI() {
            if (data == null) {
                getChatAPI().outWarn("DeityAPI", this.getClass().getEnclosingClass().getName() + " attempted to access the data api which is offline (Check startup logs)");
                return null;
            }
            return data;
        }
        
        /**
         * Returns access to the EconAPI, will return null if the API is offline
         * 
         * @return EconObject
         */
        public EconObject getEconAPI() {
            if (econ == null) {
                getChatAPI().outWarn("DeityAPI", this.getClass().getEnclosingClass().getName() + " attempted to access the econ api which is offline (Check startup logs)");
                return null;
            }
            return econ;
        }
        
        /**
         * Returns access to the WorldEditAPI, will return null if the API is
         * offline
         * 
         * @return EditObject
         */
        public EditObject getWorldEditAPI() {
            if (edit == null) {
                getChatAPI().outWarn("DeityAPI", this.getClass().getEnclosingClass().getName() + " attempted to access the edit api which is offline (Check startup logs)");
                return null;
            }
            return edit;
        }
        
        /**
         * Returns access to the EffectAPI, will return null if the API is
         * offline
         * 
         * @return EffectObject
         */
        public EffectObject getEffectAPI() {
            if (effect == null) {
                getChatAPI().outWarn("DeityAPI", this.getClass().getEnclosingClass().getName() + " attempted to access the effect api which is offline (Check startup logs)");
                return null;
            }
            return effect;
        }
        
        /**
         * Returns access to the MobAPI, will return null if the API is offline
         * 
         * @return MobObject
         */
        public MobObject getMobAPI() {
            if (mob == null) {
                getChatAPI().outWarn("DeityAPI", this.getClass().getEnclosingClass().getName() + " attempted to access the mob api which is offline (Check startup logs)");
                return null;
            }
            return mob;
        }
        
        /**
         * Returns access to the PlayerAPI, will return null if the API if
         * offline
         * 
         * @return PlayerObject
         */
        public PlayerObject getPlayerAPI() {
            if (player == null) {
                getChatAPI().outWarn("DeityAPI", this.getClass().getEnclosingClass().getName() + " attempted to access the player api which is offline (Check startup logs)");
                return null;
            }
            return player;
        }
        
        /**
         * Returns access to the SecAPI, will return null if the API is offline
         * 
         * @return SecObject
         */
        public SecObject getSecAPI() {
            if (sec == null) {
                getChatAPI().outWarn("DeityAPI", this.getClass().getEnclosingClass().getName() + " attempted to access the sec api which is offline (Check startup logs)");
                return null;
            }
            return sec;
        }
        
        /**
         * Returns access to the UtilAPI, will return null if the API is offline
         * 
         * @return UtilsObject
         */
        public UtilsObject getUtilAPI() {
            if (utils == null) {
                getChatAPI().outWarn("DeityAPI", this.getClass().getEnclosingClass().getName() + " attempted to access the util api which is offline (Check startup logs)");
                return null;
            }
            return utils;
        }
    }
}
