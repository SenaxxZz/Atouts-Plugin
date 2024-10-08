package fr.senaxzzonyt.atouts;

import fr.senaxzzonyt.atouts.commands.AtoutsCommand;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Economy economy;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        if (!setupEconomy()) {
            getLogger().severe(ChatColor.RED + "Erreur : " + ChatColor.GOLD + "Vault n'est pas installé sur le serveur.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.getCommand("atouts").setExecutor(new AtoutsCommand(this));
        getServer().getPluginManager().registerEvents(new AtoutsGUI(this), this);
        getLogger().info(ChatColor.AQUA + "Atouts activé avec succès!");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public static Economy getEconomy() {
        return economy;
    }
}