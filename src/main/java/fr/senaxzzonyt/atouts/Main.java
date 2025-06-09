package fr.senaxzzonyt.atouts;

import fr.senaxzzonyt.atouts.commands.AtoutsCommand;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Logger;

public class Main extends JavaPlugin {

    private static Economy economy = null;
    private Logger logger;
    private int economyAttempts = 0;
    private static final int MAX_ATTEMPTS = 5;

    @Override
    public void onEnable() {
        logger = getLogger();
        saveDefaultConfig();

        logger.info("Atouts starting up, checking dependencies...");

        // Try to hook into Vault economy, retrying if needed
        startEconomySetup();
    }

    // Tries to hook into Vault's economy, with retries if not available yet
    private void startEconomySetup() {
        new BukkitRunnable() {
            @Override
            public void run() {
                economyAttempts++;
                logger.info("Economy setup attempt #" + economyAttempts);

                // List all registered services for debug
                logger.info("Registered Bukkit services:");
                Bukkit.getServicesManager().getRegistrations(Economy.class).forEach(reg -> {
                    logger.info(" - " + reg.getProvider().getName() + " by " + reg.getPlugin().getName());
                });

                if (setupEconomy()) {
                    logger.info(ChatColor.GREEN + "Economy service hooked successfully!");
                    registerPluginStuff();
                    logger.info(ChatColor.AQUA + "Atouts enabled and ready!");
                    cancel();
                } else if (economyAttempts >= MAX_ATTEMPTS) {
                    logger.severe(ChatColor.RED + "Failed after " + MAX_ATTEMPTS + " attempts.");
                    logger.severe(ChatColor.RED + "Error: " + ChatColor.GOLD + "Could not hook into economy service.");
                    printPluginStatus();
                    Bukkit.getPluginManager().disablePlugin(Main.this);
                    cancel();
                } else {
                    logger.warning("Economy service not found. Retrying in 2 seconds...");
                }
            }
        }.runTaskTimer(this, 200L, 40L); // 10s initial delay (200 ticks), puis toutes les 2s (40 ticks)
    }

    // Print status of all loaded plugins for debug
    private void printPluginStatus() {
        logger.info("Loaded plugins: " + Bukkit.getPluginManager().getPlugins().length);
        for (org.bukkit.plugin.Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            logger.info("Plugin: " + plugin.getName() + " (" + (plugin.isEnabled() ? "enabled" : "disabled") + ")");
        }
    }

    // Register commands and events
    private void registerPluginStuff() {
        if (getCommand("atouts") != null) {
            getCommand("atouts").setExecutor(new AtoutsCommand(this));
        }
        getServer().getPluginManager().registerEvents(new AtoutsGUI(this), this);
    }

    // Try to setup Vault economy
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            logger.severe("Vault plugin not found!");
            return false;
        }
        try {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                logger.severe("No economy service registered!");
                return false;
            }
            economy = rsp.getProvider();
            logger.info("Economy provider found: " + economy.getName());
            return economy != null;
        } catch (Exception e) {
            logger.severe("Error setting up economy: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static Economy getEconomy() {
        return economy;
    }
}