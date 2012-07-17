package com.imdeity.deityapi.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.imdeity.deityapi.DeityAPI;

/**
 * This class should be extended in all main command handler classes with the name <Command>CommandExecutor
 * @author vanZeben
 *
 */
public abstract class DeityCommandHandler implements CommandExecutor {
    
    private static final int NUM_ELEMENTS_PER_PAGE = 5;
    private static final String PAGE_FORMAT = "/%command% %subCommand% %arguments%: %description%";
    
    protected Map<String, DeityCommandReceiver> registeredCommands = new HashMap<String, DeityCommandReceiver>();
    private Map<String, String> commandDescriptions = new HashMap<String, String>();
    private Map<String, List<String>> commandArguments = new HashMap<String, List<String>>();
    private List<String> helpOutput = new ArrayList<String>();
    
    /**
     * Initializes all commands registered, will also build help pages
     */
    public DeityCommandHandler() {
        this.initRegisteredCommands();
        List<String> commandNames = getCommandNames();
        Collections.sort(commandNames);
        for (String cs : commandNames) {
            if (!cs.equalsIgnoreCase("")) {
                if (cs.equalsIgnoreCase("help")) {
                    helpOutput.add(PAGE_FORMAT.replaceAll("%command%", this.getName().toLowerCase()).replaceAll("%subCommand%", "help").replaceAll("%arguments%", "").replaceAll("%description%", "Shows the help files"));
                    continue;
                }
                for (String s : commandArguments.get(cs)) {
                    helpOutput.add(PAGE_FORMAT.replaceAll("%command%", this.getName().toLowerCase()).replaceAll("%subCommand%", cs).replaceAll("%arguments%", s).replaceAll("%description%", this.commandDescriptions.get(cs)));
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
        if (args.length > 0) {
            if (args[0].toLowerCase().equalsIgnoreCase("help")) {
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
            } else if (!this.registeredCommands.containsKey(args[0].toLowerCase())) {
                this.invalidCommand(sender, command);
                return true;
            }
            String subCommand = args[0].toLowerCase();
            args = DeityAPI.getAPI().getUtilAPI().getStringUtils().remFirstArg(args);
            if (sender instanceof Player) {
                if (!this.registeredCommands.get(subCommand).onPlayerRunCommand((Player) sender, args)) {
                    this.invalidPerms(sender, command);
                }
            } else {
                if (!this.registeredCommands.get(subCommand).onConsoleRunCommand(args)) {
                    this.invalidPerms(sender, command);
                }
            }
        } else {
            if (this.registeredCommands.containsKey("")) {
                if (sender instanceof Player) {
                    if (!this.registeredCommands.get("").onPlayerRunCommand((Player) sender, args)) {
                        this.invalidPerms(sender, command);
                    }
                } else {
                    if (!this.registeredCommands.get("").onConsoleRunCommand(args)) {
                        this.invalidPerms(sender, command);
                    }
                }
            } else {
                showHelp(sender, 1);
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
    protected void registerCommand(String commandName, List<String> arguments, String description, DeityCommandReceiver commandReceiver) {
        commandName = commandName.toLowerCase();
        this.registeredCommands.put(commandName, commandReceiver);
        this.commandDescriptions.put(commandName, description);
        this.commandArguments.put(commandName, arguments);
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
    protected void registerCommand(String commandName, String[] arguments, String description, DeityCommandReceiver commandReceiver) {
        commandName = commandName.toLowerCase();
        this.registeredCommands.put(commandName, commandReceiver);
        this.commandDescriptions.put(commandName, description);
        
        List<String> args = new ArrayList<String>();
        for (String s : arguments) {
            args.add(s);
        }
        
        this.commandArguments.put(commandName, args);
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
    protected void registerCommand(String commandName, String arguments, String description, DeityCommandReceiver commandReceiver) {
        commandName = commandName.toLowerCase();
        this.registeredCommands.put(commandName, commandReceiver);
        this.commandDescriptions.put(commandName, description);
        
        List<String> args = new ArrayList<String>();
        args.add(arguments);
        
        this.commandArguments.put(commandName, args);
    }
    
    /**
     * Sends an invalid command message to the user
     * 
     * @param sender
     *            Sender who called the command
     * @param command
     *            Command that was called
     */
    private void invalidCommand(CommandSender sender, Command command) {
        if (sender instanceof Player) {
            DeityAPI.getAPI().getChatAPI().sendPlayerMessage(((Player) sender), "DeityAPI", "You entered an &cinvalid &fcommand.&/ Type &e/" + this.getLowerCaseName() + " help &f for help");
        } else {
            DeityAPI.getAPI().getChatAPI().outWarn("DeityAPI", "You entered an invalid command");
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
    private void invalidPerms(CommandSender sender, Command command) {
        if (sender instanceof Player) {
            DeityAPI.getAPI().getChatAPI().sendPlayerMessage(((Player) sender), "DeityAPI", "You entered an &cinvalid &fcommand or do not have permission.&/ Type &e/" + this.getLowerCaseName() + " help &f for help");
        } else {
            DeityAPI.getAPI().getChatAPI().outWarn("DeityAPI", "You entered an &cinvalid &fcommand. Type &e/" + this.getLowerCaseName() + " help &f for help");
        }
    }
    
    /**
     * Sends the sender the help pages related to this command
     * 
     * @param sender
     * @param page
     */
    protected void showHelp(CommandSender sender, int page) {
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
        return this.getClass().getSimpleName().replaceAll("CommandHandler", "");
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
