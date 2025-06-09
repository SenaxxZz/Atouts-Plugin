package fr.senaxzzonyt.atouts.commands;

import fr.senaxzzonyt.atouts.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveAtoutsCommand implements CommandExecutor {

    private final Main plugin;

    public GiveAtoutsCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Permission check
        if (!sender.hasPermission("atouts.atouts")) {
            sender.sendMessage(ChatColor.RED + "Erreur : " + ChatColor.GOLD + "Vous n'avez pas la permission d'exécuter cette commande.");
            return true;
        }

        // Usage check
        if (args.length != 3) {
            sender.sendMessage(ChatColor.RED + "Utilisation : " + ChatColor.GOLD + "/atouts give <player> <atout>");
            return false;
        }

        String targetName = args[1];
        String atout = args[2];
        String perm = getAtoutPermission(atout);

        if (perm.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Erreur : " + ChatColor.GOLD + "Nom de l'atout invalide.");
            return false;
        }

        Player target = plugin.getServer().getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Erreur : " + ChatColor.GOLD + "Le joueur " + targetName + " n'est pas connecté ou introuvable.");
            return false;
        }

        if (target.hasPermission(perm)) {
            sender.sendMessage(ChatColor.RED + "Erreur : " + ChatColor.GOLD + "Le joueur possède déjà l'atout " + atout + ".");
            return true;
        }

        // Give the permission using LuckPerms
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                "lp user " + targetName + " permission set " + perm + " true");
        sender.sendMessage(ChatColor.GREEN + atout + " a bien été ajouté à " + targetName + ".");
        return true;
    }

    // Maps atout name to permission string
    private String getAtoutPermission(String atout) {
        switch (atout.toLowerCase()) {
            case "force": return "atouts.force";
            case "speed": return "atouts.speed";
            case "haste": return "atouts.haste";
            case "fireresistance": return "atouts.fireresistance";
            case "nightvision": return "atouts.nightvision";
            default: return "";
        }
    }
}