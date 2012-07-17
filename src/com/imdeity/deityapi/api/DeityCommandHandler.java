package com.imdeity.deityapi.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.imdeity.deityapi.DeityAPI;

/**
 * This class should be extended in all main command handler classes with the
 * name <Command>CommandExecutor
 * 
 * @author vanZeben
 */
public abstract class DeityCommandHandler implements CommandExecutor {
    
    private static final int NUM_ELEMENTS_PER_PAGE = 5;
    private static final String PAGE_FORMAT = "/%command% %subCommand% %arguments%: %description%";
    
    protected Map<String, DeityCommandReceiver> registeredCommands = new HashMap<String, DeityCommandReceiver>();
    private Map<String, String> commandDescriptions = new HashMap<String, String>();
    private Map<String, List<String>> commandArguments = new HashMap<String, List<String>>();
    private Map<String, String> commandPermissions = new HashMap<String, String>();
    private Map<String, String> helpOutput = new HashMap<String, String>();
    private String pluginName;
    private String baseCommandName;
    
    /**
     * Initializes the command and builds helpfiles
     * 
     * @param pluginName
     * @param baseCommand
     */
    public DeityCommandHandler(String pluginName, String baseCommandName) {
        this.pluginName = pluginName;
        this.baseCommandName = baseCommandName;
        this.initRegisteredCommands();
        List<String> commandNames = getCommandNames();
        Collections.sort(commandNames);
        for (String cs : commandNames) {
            if (!cs.equalsIgnoreCase("")) {
                if (cs.equalsIgnoreCase("help")) {
                    helpOutput.put(cs.toLowerCase(), PAGE_FORMAT.replaceAll("%command%", this.getLowerCaseName()).replaceAll("%subCommand%", "help").replaceAll("%arguments%", "<page-number>").replaceAll("%description%", "Shows the help files"));
                    continue;
                }
                for (String s : commandArguments.get(cs)) {
                    helpOutput.put(cs.toLowerCase(), PAGE_FORMAT.replaceAll("%command%", this.getLowerCaseName()).replaceAll("%subCommand%", cs).replaceAll("%arguments%", s).replaceAll("%description%", this.commandDescriptions.get(cs)));
                }
            }
        }
    }
    
    /**
     * Returns a list of the command names registered
     * 
     * @return
     */
    private List<String> getCommandNames() {
        ArrayList<String> commandNames = new ArrayList<String>();
        commandNames.add("help");
        for (String s : this.registeredCommands.keySet()) {
            commandNames.add(s);
        }
        return commandNames;
    }
    
    /**
     * Bukkit command handler. Will parse out sub-commands and the /command help
     * command as well
     */
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        String subCommand = "";
        if (args.length == 0) {
            subCommand = "help";
            args = new String[0];
        } else {
            subCommand = args[0].toLowerCase();
            args = DeityAPI.getAPI().getUtilAPI().getStringUtils().remFirstArg(args);
        }
        
        if (subCommand.equalsIgnoreCase("help")) {
            int page = 1;
            if (args.length > 1) {
                try {
                    page = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    page = 1;
                }
            }
            showHelp(sender, page);
            return true;
        }
        if (this.registeredCommands.get(subCommand) == null) {
            this.invalidSubCommand(sender);
            return true;
        }
        
        if (sender instanceof Player) {
            if (this.commandPermissions.get(subCommand) == null || this.commandPermissions.get(subCommand).isEmpty() || (this.commandPermissions.get(subCommand) != null && ((Player) sender).hasPermission(this.commandPermissions.get(subCommand)))) {
                if (!this.registeredCommands.get(subCommand).onPlayerRunCommand((Player) sender, args)) {
                    this.invalidSubCommand(sender);
                }
            } else {
                this.invalidPerms(sender);
            }
        } else {
            if (!this.registeredCommands.get(subCommand).onConsoleRunCommand(args)) {
                this.invalidSubCommand(sender);
            }
        }
        return true;
    }
    
    /**
     * Function to register commands
     */
    protected abstract void initRegisteredCommands();
    
    /**
     * Function to register a command
     * 
     * @param commandName
     *            Sub-command that relates to the DeityCommandReceiver
     * @param arguments
     *            Any additional parameters excluding the sub-command
     * @param description
     *            Description of what the command does
     * @param commandReceiver
     *            Where the Command Receiver is located
     */
    protected void registerCommand(String commandName, List<String> arguments, String description, DeityCommandReceiver commandReceiver, String commandPermissionNode) {
        commandName = commandName.toLowerCase();
        this.registeredCommands.put(commandName, commandReceiver);
        this.commandDescriptions.put(commandName, description);
        this.commandArguments.put(commandName, arguments);
        this.commandPermissions.put(commandName, commandPermissionNode);
    }
    
    /**
     * Function to register a command
     * 
     * @param commandName
     *            Sub-command that relates to the DeityCommandReceiver
     * @param arguments
     *            Any additional parameters excluding the sub-command
     * @param description
     *            Description of what the command does
     * @param commandReceiver
     *            Where the Command Receiver is located
     */
    protected void registerCommand(String commandName, String[] arguments, String description, DeityCommandReceiver commandReceiver, String commandPermissionNode) {
        commandName = commandName.toLowerCase();
        this.registeredCommands.put(commandName, commandReceiver);
        this.commandDescriptions.put(commandName, description);
        
        List<String> args = new ArrayList<String>();
        for (String s : arguments) {
            args.add(s);
        }
        
        this.commandArguments.put(commandName, args);
        this.commandPermissions.put(commandName, commandPermissionNode);
    }
    
    /**
     * Function to register a command
     * 
     * @param commandName
     *            Sub-command that relates to the DeityCommandReceiver
     * @param arguments
     *            Any additional parameters excluding the sub-command
     * @param description
     *            Description of what the command does
     * @param commandReceiver
     *            Where the Command Receiver is located
     */
    protected void registerCommand(String commandName, String arguments, String description, DeityCommandReceiver commandReceiver, String commandPermissionNode) {
        commandName = commandName.toLowerCase();
        this.registeredCommands.put(commandName, commandReceiver);
        this.commandDescriptions.put(commandName, description);
        
        List<String> args = new ArrayList<String>();
        args.add(arguments);
        
        this.commandArguments.put(commandName, args);
        this.commandPermissions.put(commandName, commandPermissionNode);
    }
    
    /**
     * Sends an invalid command message to the user
     * 
     * @param sender
     *            Sender who called the command
     * @param command
     *            Command that was called
     */
    private void invalidSubCommand(CommandSender sender) {
        if (sender instanceof Player) {
            DeityAPI.getAPI().getChatAPI().sendPlayerMessage(((Player) sender), this.pluginName, "You entered an &cinvalid &fsub-command");
            DeityAPI.getAPI().getChatAPI().sendPlayerMessage(((Player) sender), this.pluginName, "Type &e/" + this.getLowerCaseName() + " help &ffor help");
        } else {
            DeityAPI.getAPI().getChatAPI().outWarn(this.pluginName, "You entered an invalid command");
            DeityAPI.getAPI().getChatAPI().outWarn(this.pluginName, "Type /" + this.getLowerCaseName() + " help for help");
        }
    }
    
    /**
     * Sends an invalid command/perms error to the user
     * 
     * @param sender
     *            Sender who called the command
     * @param command
     *            Command that was called
     */
    private void invalidPerms(CommandSender sender) {
        if (sender instanceof Player) {
            DeityAPI.getAPI().getChatAPI().sendPlayerMessage(((Player) sender), this.pluginName, "You have &cinvalid &fpermissions");
            DeityAPI.getAPI().getChatAPI().sendPlayerMessage(((Player) sender), this.pluginName, "Type &e/" + this.getLowerCaseName() + " help &ffor a list of valid commands");
        }
    }
    
    /**
     * Sends the sender the help pages related to this command
     * 
     * @param sender
     * @param page
     */
    protected void showHelp(CommandSender sender, int page) {
        List<String> helpOutput = new ArrayList<String>();
        
        for (String name : this.helpOutput.keySet()) {
            if (this.commandPermissions.get(name) == null || this.commandPermissions.get(name).isEmpty()) {
                helpOutput.add(this.helpOutput.get(name));
                continue;
            } else {
                if ((sender instanceof ConsoleCommandSender) || (sender instanceof Player && ((Player) sender).hasPermission(this.commandPermissions.get(name)))) {
                    helpOutput.add(this.helpOutput.get(name));
                }
            }
        }
        
        // pagination
        int numPages = 0;
        if (helpOutput.size() % NUM_ELEMENTS_PER_PAGE != 0) {
            for (int i = 0; i < 5; i++) {
                if ((helpOutput.size() + i) % NUM_ELEMENTS_PER_PAGE == 0) {
                    numPages = ((helpOutput.size() + i) / NUM_ELEMENTS_PER_PAGE);
                }
            }
        } else {
            numPages = (helpOutput.size() / NUM_ELEMENTS_PER_PAGE);
        }
        
        if (page < 1) {
            page = 1;
        } else if (page > numPages) {
            page = numPages;
        }
        int numStartElementOnCurrentPage = ((page - 1) * NUM_ELEMENTS_PER_PAGE);
        int numMaxElemetsOnCurrentPage = (((page) * NUM_ELEMENTS_PER_PAGE) < helpOutput.size() ? ((page) * NUM_ELEMENTS_PER_PAGE) : helpOutput.size());
        
        if (sender instanceof Player) {
            Player player = (Player) sender;
            // title
            DeityAPI.getAPI().getChatAPI().sendPlayerMessageNoTitleNewLine(player, this.getName() + " Help Page [" + page + "/" + numPages + "]");
            DeityAPI.getAPI().getChatAPI().sendPlayerMessageNoTitleNewLine(player, "-------------------------");
            
            // content
            for (int i = numStartElementOnCurrentPage; i < numMaxElemetsOnCurrentPage; i++) {
                String[] split = helpOutput.get(i).split(":");
                String newFormat = "&3" + split[0].trim() + "&7: &b" + DeityAPI.getAPI().getUtilAPI().getStringUtils().join(DeityAPI.getAPI().getUtilAPI().getStringUtils().remFirstArg(split)).trim();
                DeityAPI.getAPI().getChatAPI().sendPlayerMessageNoTitleNewLine(player, newFormat);
            }
        } else {
            // title
            sender.sendMessage(this.getName() + " Help Page [" + page + "/" + numPages + "]");
            sender.sendMessage("-------------------------");
            
            // content
            for (int i = numStartElementOnCurrentPage; i < numMaxElemetsOnCurrentPage; i++) {
                sender.sendMessage(helpOutput.get(i));
            }
        }
    }
    
    /**
     * Returns the file name of the command, excluding the CommandHandler
     * 
     * @return
     */
    public String getName() {
        return this.baseCommandName;
    }
    
    /**
     * Returns the file name of the command, excluding the CommandHandler in
     * lower case
     * 
     * @return
     */
    public String getLowerCaseName() {
        return getName().toLowerCase();
    }
}
