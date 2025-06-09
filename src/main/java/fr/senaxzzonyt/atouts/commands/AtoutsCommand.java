package fr.senaxzzonyt.atouts.commands;

import fr.senaxzzonyt.atouts.Main;
import fr.senaxzzonyt.atouts.AtoutsGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AtoutsCommand implements CommandExecutor {

    private final Main plugin;
    private final AtoutsReloadCommand reloadCmd;
    private final GiveAtoutsCommand giveCmd;
    private final DeleteAtoutsCommand deleteCmd;
    private final CheckAtoutsPurchasesCommand checkCmd;
    private final HelpCommand helpCmd;

    public AtoutsCommand(Main plugin) {
        this.plugin = plugin;
        this.reloadCmd = new AtoutsReloadCommand(plugin);
        this.giveCmd = new GiveAtoutsCommand(plugin);
        this.deleteCmd = new DeleteAtoutsCommand(plugin);
        this.checkCmd = new CheckAtoutsPurchasesCommand(plugin);
        this.helpCmd = new HelpCommand(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // If no arguments, open the GUI for players, else show error for console
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                new AtoutsGUI(plugin).openAtoutsGUI(player);
                return true;
            }
            sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.GOLD + "This command must be run by a player.");
            return false;
        }

        // Handle subcommands
        switch (args[0].toLowerCase()) {
            case "reload":
                return reloadCmd.onCommand(sender, command, label, args);
            case "give":
                return giveCmd.onCommand(sender, command, label, args);
            case "delete":
                return deleteCmd.onCommand(sender, command, label, args);
            case "check":
                return checkCmd.onCommand(sender, command, label, args);
            case "help":
                return helpCmd.onCommand(sender, command, label, args);
            default:
                sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.GOLD + "Unknown command.");
                return false;
        }
    }
}