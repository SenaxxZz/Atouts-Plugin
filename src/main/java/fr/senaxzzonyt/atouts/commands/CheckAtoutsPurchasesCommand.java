package fr.senaxzzonyt.atouts.commands;

import fr.senaxzzonyt.atouts.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckAtoutsPurchasesCommand implements CommandExecutor {

    private final Main plugin;

    public CheckAtoutsPurchasesCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("atouts.checkpurchases")) {
            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED + "Erreur d'utilisation : " + ChatColor.GOLD + " /atouts check <player>");
                return false;
            }

            String playerName = args[1];
            Player targetPlayer = this.plugin.getServer().getPlayer(playerName);

            if (targetPlayer == null) {
                sender.sendMessage(ChatColor.RED + "Erreur : " + ChatColor.GOLD + "Le joueur " + playerName + " n'est pas connecté ou n'est pas trouvé.");
                return false;
            }

            String[] atouts = {"force", "speed", "haste", "fireresistance", "nightvision"};
            String[] atoutsNames = {"Force I", "Speed I", "Haste I", "Fire Resistance", "Night Vision"};
            ChatColor[] colors = {ChatColor.RED, ChatColor.BLUE, ChatColor.YELLOW, ChatColor.GOLD, ChatColor.DARK_PURPLE};

            for (int i = 0; i < atouts.length; i++) {
                String permission = getPermissionFromAtout(atouts[i]);
                ChatColor color = colors[i];
                if (targetPlayer.hasPermission(permission)) {
                    sender.sendMessage(color + atoutsNames[i] + ": " + ChatColor.GREEN + "Possédé");
                } else {
                    sender.sendMessage(color + atoutsNames[i] + ": " + ChatColor.RED + "Non possédé");
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED +"Erreur : " + ChatColor.GOLD + " Vous n'avez pas la permission d'exécuter cette commande");
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