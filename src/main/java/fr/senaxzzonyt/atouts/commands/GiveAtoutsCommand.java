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
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("atouts.atouts")) {
            if (args.length != 3) {
                sender.sendMessage(ChatColor.RED + "Erreur d'utilisation: " + ChatColor.GOLD + " /atouts give <player> <atout>");
                return false;
            }

            String playerName = args[1];
            String atout = args[2];
            String permission = getPermissionFromAtout(atout);

            if (permission.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "Erreur : " + ChatColor.GOLD + "Nom de l'atout invalide.");
                return false;
            }

            Player targetPlayer = this.plugin.getServer().getPlayer(playerName);
            if (targetPlayer != null && targetPlayer.hasPermission(permission)) {
                sender.sendMessage(ChatColor.RED + "Erreur : " + ChatColor.GOLD + "Cette personne possède déjà l'effet " + atout + ".");
            } else {
                this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), "lp user " + playerName + " permission set " + permission + " true");
                sender.sendMessage(ChatColor.GREEN + atout + " a bien été ajouté à " + playerName + ".");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Erreur : " + ChatColor.GOLD + "Vous n'avez pas la permission d'exécuter cette commande.");
        }
        return true;
    }

    private String getPermissionFromAtout(String atout) {
        switch (atout.toLowerCase()) {
            case "force":
                return "atouts.force";
            case "speed":
                return "atouts.speed";
            case "haste":
                return "atouts.haste";
            case "fireresistance":
                return "atouts.fireresistance";
            case "nightvision":
                return "atouts.nightvision";
            default:
                return "";
        }
    }
}