package com.imdeity.deityapi;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.imdeity.deityapi.api.DeityPlugin;
import com.imdeity.deityapi.api.DeityRegistration;
import com.imdeity.deityapi.cmds.QueryCommandHandler;
import com.imdeity.deityapi.object.BanObject;
import com.imdeity.deityapi.object.ChatObject;
import com.imdeity.deityapi.object.DataObject;
import com.imdeity.deityapi.object.DeityPermObject;
import com.imdeity.deityapi.object.EconObject;
import com.imdeity.deityapi.object.EditObject;
import com.imdeity.deityapi.object.EffectObject;
import com.imdeity.deityapi.object.MobObject;
import com.imdeity.deityapi.object.PermObject;
import com.imdeity.deityapi.object.PlayerObject;
import com.imdeity.deityapi.object.SecObject;
import com.imdeity.deityapi.object.ServerObject;
import com.imdeity.deityapi.object.UtilsObject;
import com.imdeity.deityapi.utils.Metrics;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class DeityAPI extends DeityPlugin implements Listener {
    
    public static DeityAPI plugin;
    private static InternalAPI internalAPI;
    public static DeityRegistration registration = new DeityRegistration();
    
    private QueryCommandHandler queryCommandHandler = new QueryCommandHandler("DeityAPI");
    
    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();
        initPlugin();
        config = new DeityPluginConfig(getDescription().getName(), this.getConfig(), "plugins/" + getDescription().getName()
                + "/config.yml");
        initConfig();
        config.saveConfig();
        language = new DeityPluginLanguage(getDescription().getName(), YamlConfiguration.loadConfiguration(new File("plugins/"
                + getDescription().getName() + "/language.yml")), "plugins/" + getDescription().getName() + "/language.yml");
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
        chat.out("Enabled - " + ((double) (finalTime - startTime) / (double) 1000)
                + (((double) (finalTime - startTime) / (double) 1000) == 1 ? " second" : " seconds"));
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
        }
        if (DeityAPI.plugin.config.getBoolean(DeityAPIConfigHelper.INFORM_ON_UPDATE)) {
            if (plugin.getDescription().getWebsite() != null) {
                new AutoUpdater(this);
            }
        }
    }
    
    @Override
    protected void initConfig() {
        this.config.addDefaultConfigValue(DeityAPIConfigHelper.SHOULD_PROFILE, true);
        this.config.addDefaultConfigValue(DeityAPIConfigHelper.INFORM_ON_UPDATE, true);
        this.config.addDefaultConfigValue(DeityAPIConfigHelper.MYSQL_SERVER_ADDRESS, "localhost");
        this.config.addDefaultConfigValue(DeityAPIConfigHelper.MYSQL_SERVER_PORT, 3306);
        this.config.addDefaultConfigValue(DeityAPIConfigHelper.MYSQL_DATABASE_NAME, "kingdoms");
        this.config.addDefaultConfigValue(DeityAPIConfigHelper.MYSQL_DATABASE_USERNAME, "root");
        this.config.addDefaultConfigValue(DeityAPIConfigHelper.MYSQL_DATABASE_PASSWORD, "root");
        if (this.config.getString("deityapi.mcbans.api_token") != null) {
            this.config.set("deityapi.mcbans.api_token", "");
        }
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
        private PermObject perm;
        private SecObject sec;
        private UtilsObject utils;
        
        public InternalAPI() {
            try {
                if (getServer().getPluginManager().getPlugin("Vault") != null) {
                    getServer().getPluginManager().enablePlugin(getServer().getPluginManager().getPlugin("Vault"));
                    RegisteredServiceProvider<Chat> rsp = DeityAPI.plugin.getServer().getServicesManager().getRegistration(Chat.class);
                    if (rsp == null) { throw new NoClassDefFoundError("Vault found, but chat source not linked"); }
                    chat = new ChatObject(rsp.getProvider());
                    getChatAPI().out("DeityAPI", "EconAPI hooked and loaded");
                } else {
                    throw new NoClassDefFoundError("Vault not found");
                }
            } catch (NoClassDefFoundError e) {
                chat = new ChatObject();
                getChatAPI().outSevere("DeityAPI", "ChatAPI not hooked into vault");
            }
            
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
                    RegisteredServiceProvider<Economy> rsp = DeityAPI.plugin.getServer().getServicesManager()
                            .getRegistration(Economy.class);
                    if (rsp == null) { throw new NoClassDefFoundError("Vault found, but economy source not linked"); }
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
                if (getServer().getPluginManager().getPlugin("Vault") != null) {
                    getServer().getPluginManager().enablePlugin(getServer().getPluginManager().getPlugin("Vault"));
                    RegisteredServiceProvider<Permission> rsp = DeityAPI.plugin.getServer().getServicesManager()
                            .getRegistration(Permission.class);
                    if (rsp == null) { throw new NoClassDefFoundError("Vault found, but perm source not linked"); }
                    perm = new PermObject(rsp.getProvider());
                    getChatAPI().out("DeityAPI", "PermAPI hooked and loaded");
                } else {
                    throw new NoClassDefFoundError("Vault not found");
                }
            } catch (NoClassDefFoundError e) {
                getChatAPI().outSevere("DeityAPI", "PermAPI Offline: " + e.getLocalizedMessage());
                econ = null;
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
         * Verifies that the api is online
         * 
         * @return
         */
        public boolean isChatAPIOnline() {
            return chat != null;
        }
        
        /**
         * Returns access to the ChatAPI, will return null if the API is offline
         * 
         * @return ChatObject
         */
        public ChatObject getChatAPI() {
            if (chat == null) {
                Logger.getLogger("Minecraft").warning(
                        "[DeityAPI] " + new Exception().getStackTrace()[1].getClassName()
                                + " attempted to access the chat api which is offline (Check startup logs)");
                return null;
            }
            return chat;
        }
        
        /**
         * Verifies that the api is online
         * 
         * @return
         */
        public boolean isDataAPIOnline() {
            return data != null;
        }
        
        /**
         * Returns access to the DataAPI, will return null if the API is offline
         * 
         * @return DataObject
         */
        public DataObject getDataAPI() {
            if (data == null) {
                getChatAPI().outWarn(
                        "DeityAPI",
                        new Exception().getStackTrace()[1].getClassName()
                                + " attempted to access the data api which is offline (Check startup logs)");
                return null;
            }
            return data;
        }
        
        /**
         * Verifies that the api is online
         * 
         * @return
         */
        public boolean isEconAPIOnline() {
            return econ != null;
        }
        
        /**
         * Returns access to the EconAPI, will return null if the API is offline
         * 
         * @return EconObject
         */
        public EconObject getEconAPI() {
            if (econ == null) {
                getChatAPI().outWarn(
                        "DeityAPI",
                        new Exception().getStackTrace()[1].getClassName()
                                + " attempted to access the econ api which is offline (Check startup logs)");
                return null;
            }
            return econ;
        }
        
        /**
         * Verifies that the api is online
         * 
         * @return
         */
        public boolean isWorldEditAPIOnline() {
            return edit != null;
        }
        
        /**
         * Returns access to the WorldEditAPI, will return null if the API is
         * offline
         * 
         * @return EditObject
         */
        public EditObject getWorldEditAPI() {
            if (edit == null) {
                getChatAPI().outWarn(
                        "DeityAPI",
                        new Exception().getStackTrace()[1].getClassName()
                                + " attempted to access the edit api which is offline (Check startup logs)");
                return null;
            }
            return edit;
        }
        
        /**
         * Verifies that the api is online
         * 
         * @return
         */
        public boolean isEffectAPIOnline() {
            return effect != null;
        }
        
        /**
         * Returns access to the EffectAPI, will return null if the API is
         * offline
         * 
         * @return EffectObject
         */
        public EffectObject getEffectAPI() {
            if (effect == null) {
                getChatAPI().outWarn(
                        "DeityAPI",
                        new Exception().getStackTrace()[1].getClassName()
                                + " attempted to access the effect api which is offline (Check startup logs)");
                return null;
            }
            return effect;
        }
        
        /**
         * Verifies that the api is online
         * 
         * @return
         */
        public boolean isMobAPIOnline() {
            return mob != null;
        }
        
        /**
         * Returns access to the MobAPI, will return null if the API is offline
         * 
         * @return MobObject
         */
        public MobObject getMobAPI() {
            if (mob == null) {
                getChatAPI().outWarn(
                        "DeityAPI",
                        new Exception().getStackTrace()[1].getClassName()
                                + " attempted to access the mob api which is offline (Check startup logs)");
                return null;
            }
            return mob;
        }
        
        /**
         * Verifies that the api is online
         * 
         * @return
         */
        public boolean isPlayerAPIOnline() {
            return player != null;
        }
        
        /**
         * Returns access to the PlayerAPI, will return null if the API if
         * offline
         * 
         * @return PlayerObject
         */
        public PlayerObject getPlayerAPI() {
            if (player == null) {
                getChatAPI().outWarn(
                        "DeityAPI",
                        new Exception().getStackTrace()[1].getClassName()
                                + " attempted to access the player api which is offline (Check startup logs)");
                return null;
            }
            return player;
        }
        
        /**
         * Verifies that the api is online
         * 
         * @return
         */
        public boolean isPermAPIOnline() {
            return perm != null;
        }
        
        /**
         * Returns access to the PermAPI, will return null if the API is offline
         * 
         * @return EconObject
         */
        public PermObject getPermAPI() {
            if (perm == null) {
                getChatAPI().outWarn(
                        "DeityAPI",
                        new Exception().getStackTrace()[1].getClassName()
                                + " attempted to access the perm api which is offline (Check startup logs)");
                return null;
            }
            return perm;
        }
        
        /**
         * Verifies that the api is online
         * 
         * @return
         */
        public boolean isSecAPIOnline() {
            return sec != null;
        }
        
        /**
         * Returns access to the SecAPI, will return null if the API is offline
         * 
         * @return SecObject
         */
        public SecObject getSecAPI() {
            if (sec == null) {
                getChatAPI().outWarn(
                        "DeityAPI",
                        new Exception().getStackTrace()[1].getClassName()
                                + " attempted to access the sec api which is offline (Check startup logs)");
                return null;
            }
            return sec;
        }
        
        /**
         * Verifies that the api is online
         * 
         * @return
         */
        public boolean isUtilsAPIOnline() {
            return utils != null;
        }
        
        /**
         * Returns access to the UtilAPI, will return null if the API is offline
         * 
         * @return UtilsObject
         */
        public UtilsObject getUtilAPI() {
            if (utils == null) {
                getChatAPI().outWarn(
                        "DeityAPI",
                        new Exception().getStackTrace()[1].getClassName()
                                + " attempted to access the util api which is offline (Check startup logs)");
                return null;
            }
            return utils;
        }
        
        /**
         * For use on the ImDeity Kingdoms server only
         * 
         * @return
         */
        public ServerObject getServerAPI() {
            return new ServerObject();
        }
        
        /**
         * For use on the ImDeity Kingdoms server only
         * 
         * @return
         */
        public BanObject getBanAPI() {
            return new BanObject();
        }
        
        /**
         * For use on the ImDeity Kingdoms server only
         * 
         * @return
         */
        public DeityPermObject getDeityPermAPI() {
            return new DeityPermObject();
        }
    }
}
