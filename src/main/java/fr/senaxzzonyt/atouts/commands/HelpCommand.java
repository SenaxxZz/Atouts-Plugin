package fr.senaxzzonyt.atouts.commands;

import fr.senaxzzonyt.atouts.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class HelpCommand implements CommandExecutor {

    private final Main plugin;

    public HelpCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("atouts.help")) {
                player.sendMessage(ChatColor.RED + "Erreur : " + ChatColor.GOLD + "Vous n'avez pas la permission d'exécuter cette commande.");
                return true;
            }
        }
        sender.sendMessage(ChatColor.GOLD + "Atouts - Aide");
        sender.sendMessage(ChatColor.RED + "Liste des commandes disponibles:");
        sender.sendMessage(ChatColor.AQUA + "Commande : " + ChatColor.GOLD + "/atouts: Ouvre le menu des atouts.");
        sender.sendMessage(ChatColor.AQUA + "Commande : " + ChatColor.GOLD + "/atouts reload: Recharge la configuration du plugin.");
        sender.sendMessage(ChatColor.AQUA + "Commande : " + ChatColor.GOLD + "/atouts give <joueur> <nom de l'atouts>: Donne un atout à un joueur.");
        sender.sendMessage(ChatColor.AQUA + "Commande : " + ChatColor.GOLD + "/atouts reset <joueur> <nom de l'atouts>: Supprime un atout d'un joueur.");
        sender.sendMessage(ChatColor.AQUA + "Commande : " + ChatColor.GOLD + "/atouts check <joueur>: Vérifie les achats d'un joueur.");

        return true;
    }
}