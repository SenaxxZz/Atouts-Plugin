package fr.senaxzzonyt.atouts.commands;

import fr.senaxzzonyt.atouts.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AtoutsReloadCommand implements CommandExecutor {

    private final Main plugin;

    public AtoutsReloadCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check permission for both players and console
        if (!sender.hasPermission("atouts.reload")) {
            sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.GOLD + "You don't have permission to reload the plugin.");
            return true;
        }

        try {
            plugin.reloadConfig();
            sender.sendMessage(ChatColor.GOLD + "Plugin configuration reloaded successfully.");
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.GOLD + "Could not reload config. " + ChatColor.DARK_RED + "Check console logs for details.");
            e.printStackTrace();
        }

        return true;
    }
}