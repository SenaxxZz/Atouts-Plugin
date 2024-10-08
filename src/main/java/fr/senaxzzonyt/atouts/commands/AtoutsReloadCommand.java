package fr.senaxzzonyt.atouts.commands;

import fr.senaxzzonyt.atouts.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AtoutsReloadCommand implements CommandExecutor {

    private final Main plugin;

    public AtoutsReloadCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("atouts.reload")) {
                player.sendMessage(ChatColor.RED + "Erreur : " + ChatColor.RED + "Vous n'avez pas la permission d'exécuter cette commande.");
                return true;
            }
        }

        try {
            plugin.reloadConfig();
            sender.sendMessage(ChatColor.GOLD + "La configuration du plugin a bien été rechargée.");
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Erreur : " + ChatColor.GOLD + "Impossible de recharger la configuration. " + ChatColor.DARK_RED + "Regardez les logs dans la console.");
            e.printStackTrace();
        }

        return true;
    }
}