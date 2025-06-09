package fr.senaxzzonyt.atouts.commands;

import fr.senaxzzonyt.atouts.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class HelpCommand implements CommandExecutor {

    private final Main plugin;

    public HelpCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Check permission for help command
        if (!sender.hasPermission("atouts.help")) {
            sender.sendMessage(ChatColor.RED + "Erreur : " + ChatColor.GOLD + "Vous n'avez pas la permission d'exécuter cette commande.");
            return true;
        }

        // Display help menuG
        sender.sendMessage(ChatColor.GOLD + "=== Atouts - Aide ===");
        sender.sendMessage(ChatColor.AQUA + "/atouts" + ChatColor.GOLD + " : Ouvre le menu des atouts.");
        sender.sendMessage(ChatColor.AQUA + "/atouts reload" + ChatColor.GOLD + " : Recharge la configuration du plugin.");
        sender.sendMessage(ChatColor.AQUA + "/atouts give <joueur> <atout>" + ChatColor.GOLD + " : Donne un atout à un joueur.");
        sender.sendMessage(ChatColor.AQUA + "/atouts delete <joueur> <atout>" + ChatColor.GOLD + " : Supprime un atout d'un joueur.");
        sender.sendMessage(ChatColor.AQUA + "/atouts check <joueur>" + ChatColor.GOLD + " : Vérifie les atouts d'un joueur.");
        return true;
    }
}