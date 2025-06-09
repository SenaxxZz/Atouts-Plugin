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
        // Permission check for the command
        if (!sender.hasPermission("atouts.checkpurchases")) {
            sender.sendMessage(ChatColor.RED + "Erreur : " + ChatColor.GOLD + "Vous n'avez pas la permission d'exécuter cette commande.");
            return true;
        }

        // Usage: /atouts check <player>
        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Utilisation : " + ChatColor.GOLD + "/atouts check <joueur>");
            return false;
        }

        String playerName = args[1];
        Player target = plugin.getServer().getPlayer(playerName);

        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Erreur : " + ChatColor.GOLD + "Le joueur " + playerName + " n'est pas connecté ou introuvable.");
            return false;
        }

        // List of perks to check
        String[] atouts = {"force", "speed", "haste", "fireresistance", "nightvision"};
        String[] atoutsNames = {"Force I", "Speed I", "Haste I", "Fire Resistance", "Night Vision"};
        ChatColor[] colors = {ChatColor.RED, ChatColor.AQUA, ChatColor.YELLOW, ChatColor.GOLD, ChatColor.DARK_PURPLE};

        sender.sendMessage(ChatColor.GOLD + "Atouts de " + ChatColor.WHITE + playerName + ChatColor.GOLD + " :");
        for (int i = 0; i < atouts.length; i++) {
            String perm = getPermissionFromAtout(atouts[i]);
            ChatColor color = colors[i];
            boolean has = target.hasPermission(perm);
            sender.sendMessage(color + atoutsNames[i] + ": " + (has ? ChatColor.GREEN + "Possédé" : ChatColor.RED + "Non possédé"));
        }
        return true;
    }

    // Maps perk name to permission string
    private String getPermissionFromAtout(String atout) {
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