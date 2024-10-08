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
    private final AtoutsReloadCommand reloadCommand;
    private final GiveAtoutsCommand giveCommand;
    private final DeleteAtoutsCommand deleteCommand;
    private final CheckAtoutsPurchasesCommand checkCommand;
    private final HelpCommand helpCommand;

    public AtoutsCommand(Main plugin) {
        this.plugin = plugin;
        this.reloadCommand = new AtoutsReloadCommand(plugin);
        this.giveCommand = new GiveAtoutsCommand(plugin);
        this.deleteCommand = new DeleteAtoutsCommand(plugin);
        this.checkCommand = new CheckAtoutsPurchasesCommand(plugin);
        this.helpCommand = new HelpCommand(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                new AtoutsGUI(plugin).openAtoutsGUI(player);
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Erreur : " + ChatColor.GOLD + "Cette commande doit être exécutée par un joueur.");
                return false;
            }
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                return reloadCommand.onCommand(sender, command, label, args);
            case "give":
                return giveCommand.onCommand(sender, command, label, args);
            case "delete":
                return deleteCommand.onCommand(sender, command, label, args);
            case "check":
                return checkCommand.onCommand(sender, command, label, args);
            case "help":
                return helpCommand.onCommand(sender, command, label, args);
            default:
                sender.sendMessage(ChatColor.RED + "Erreur : " + ChatColor.GOLD + "Commande inconnue.");
                return false;
        }
    }
}