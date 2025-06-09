package fr.senaxzzonyt.atouts;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import java.util.logging.Logger;

public class AtoutsGUI implements Listener {

    private final JavaPlugin plugin;
    private static Economy economy;
    private static final Logger log = Logger.getLogger("Minecraft");
    private final Map<Player, ItemStack> pendingConfirm = new HashMap<>();

    public AtoutsGUI(JavaPlugin plugin) {
        this.plugin = plugin;
        economy = Main.getEconomy();
    }

    // Opens the main perks GUI for the player
    public void openAtoutsGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Atouts");

        gui.setItem(11, createGuiItem(Material.DIAMOND_SWORD, ChatColor.RED + "Force I", "Permanent " + ChatColor.RED + "Force I", plugin.getConfig().getInt("prices.force"), player.hasPermission("atouts.force")));
        gui.setItem(12, createGuiItem(Material.FEATHER, ChatColor.AQUA + "Speed I", "Permanent " + ChatColor.AQUA + "Speed I", plugin.getConfig().getInt("prices.speed"), player.hasPermission("atouts.speed")));
        gui.setItem(13, createGuiItem(Material.IRON_PICKAXE, ChatColor.YELLOW + "Haste I", "Permanent " + ChatColor.YELLOW + "Haste I", plugin.getConfig().getInt("prices.haste"), player.hasPermission("atouts.haste")));
        gui.setItem(14, createGuiItem(Material.MAGMA_CREAM, ChatColor.GOLD + "Fire Resistance", "Permanent " + ChatColor.GOLD + "Fire Resistance", plugin.getConfig().getInt("prices.fireresistance"), player.hasPermission("atouts.fireresistance")));
        gui.setItem(15, createGuiItem(Material.EYE_OF_ENDER, ChatColor.DARK_PURPLE + "Night Vision", "Permanent " + ChatColor.DARK_PURPLE + "Night Vision", plugin.getConfig().getInt("prices.nightvision"), player.hasPermission("atouts.nightvision")));

        player.openInventory(gui);
        log.info("[Atouts] Opened perks GUI for " + player.getName());
    }

    // Helper to create a GUI item with price and unlock status
    private ItemStack createGuiItem(Material mat, String name, String desc, int price, boolean unlocked) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + desc,
                "",
                ChatColor.GOLD + "Prix: " + ChatColor.GREEN + price + "$",
                ChatColor.GOLD + "Débloqué: " + (unlocked ? ChatColor.GREEN + "Oui" : ChatColor.RED + "Non")
        ));
        item.setItemMeta(meta);
        return item;
    }

    // Helper for colored wool buttons in confirmation GUI
    private ItemStack createColoredWool(DyeColor color, String name, String desc, int price) {
        ItemStack item = new ItemStack(Material.WOOL, 1, color.getWoolData());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + desc,
                "",
                ChatColor.GOLD + "Prix: " + ChatColor.GREEN + price + "$"
        ));
        item.setItemMeta(meta);
        return item;
    }

    // Opens the confirmation GUI for a purchase
    public void openConfirmationGUI(Player player, ItemStack item, int price) {
        Inventory confirmGui = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Confirmer l'achat");
        confirmGui.setItem(3, createColoredWool(DyeColor.GREEN, ChatColor.GREEN + "Confirmer", "Click to confirm", price));
        confirmGui.setItem(5, createColoredWool(DyeColor.RED, ChatColor.RED + "Annuler", "Click to cancel", price));
        pendingConfirm.put(player, item);
        player.openInventory(confirmGui);
        log.info("[Atouts] Opened confirmation GUI for " + player.getName() + " (" + item.getItemMeta().getDisplayName() + ")");
    }

    // Handles clicks in the perks and confirmation GUIs
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (!title.equals(ChatColor.GOLD + "Atouts") && !title.equals(ChatColor.GOLD + "Confirmer l'achat")) return;
        event.setCancelled(true);

        if (clicked == null || clicked.getType() == Material.AIR) return;
        ItemMeta meta = clicked.getItemMeta();
        if (meta == null || meta.getDisplayName() == null || meta.getLore() == null) return;

        String itemName = meta.getDisplayName();

        if (title.equals(ChatColor.GOLD + "Atouts")) {
            // If not unlocked, open confirmation
            if (meta.getLore().get(3).contains(ChatColor.RED + "Non")) {
                try {
                    int price = extractPrice(meta.getLore().get(2));
                    openConfirmationGUI(player, clicked, price);
                } catch (NumberFormatException e) {
                    log.severe("[Atouts] Price parse error: " + e.getMessage());
                }
            } else {
                toggleEffect(player, itemName);
            }
        } else if (title.equals(ChatColor.GOLD + "Confirmer l'achat")) {
            if (itemName.equals(ChatColor.GREEN + "Confirmer")) {
                ItemStack item = pendingConfirm.get(player);
                if (item != null) {
                    try {
                        int price = extractPrice(item.getItemMeta().getLore().get(2));
                        if (economy.withdrawPlayer(player, price).transactionSuccess()) {
                            String perm = getPermissionFromItemName(item.getItemMeta().getDisplayName());
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " permission set " + perm + " true");
                            player.sendMessage(ChatColor.GREEN + "Achat effectué!");
                            player.closeInventory();
                        } else {
                            player.sendMessage(ChatColor.RED + "Fonds insuffisants!");
                        }
                    } catch (NumberFormatException e) {
                        log.severe("[Atouts] Price parse error: " + e.getMessage());
                    }
                }
            } else if (itemName.equals(ChatColor.RED + "Annuler")) {
                player.closeInventory();
                openAtoutsGUI(player);
            }
        }
    }

    // Toggle the effect for the player if they have permission
    private void toggleEffect(Player player, String itemName) {
        String perm = getPermissionFromItemName(itemName);
        if (player.hasPermission(perm)) {
            PotionEffectType type = getPotionEffectTypeFromItemName(itemName);
            if (type != null) {
                if (player.hasPotionEffect(type)) {
                    player.removePotionEffect(type);
                    player.sendMessage(ChatColor.RED + "Effet " + itemName + " désactivé.");
                } else {
                    player.addPotionEffect(new PotionEffect(type, Integer.MAX_VALUE, 0, true, false));
                    player.sendMessage(ChatColor.GREEN + "Effet " + itemName + " activé.");
                }
            }
        } else {
            int price = Integer.parseInt(getItemPriceFromName(itemName));
            openConfirmationGUI(player, createGuiItem(getMaterialFromItemName(itemName), itemName, "Description", price, false), price);
        }
    }

    // Extracts the price from a lore line
    private int extractPrice(String loreLine) {
        return Integer.parseInt(loreLine.replaceAll("[^0-9]", ""));
    }

    // Maps item names to potion effects
    private PotionEffectType getPotionEffectTypeFromItemName(String itemName) {
        switch (itemName) {
            case "\u00A7cForce I": return PotionEffectType.INCREASE_DAMAGE;
            case "\u00A7bSpeed I": return PotionEffectType.SPEED;
            case "\u00A7eHaste I": return PotionEffectType.FAST_DIGGING;
            case "\u00A75Night Vision": return PotionEffectType.NIGHT_VISION;
            case "\u00A76Fire Resistance": return PotionEffectType.FIRE_RESISTANCE;
            default: return null;
        }
    }

    // Maps item names to permissions
    private String getPermissionFromItemName(String itemName) {
        switch (itemName) {
            case "\u00A7cForce I": return "atouts.force";
            case "\u00A7bSpeed I": return "atouts.speed";
            case "\u00A7eHaste I": return "atouts.haste";
            case "\u00A75Night Vision": return "atouts.nightvision";
            case "\u00A76Fire Resistance": return "atouts.fireresistance";
            default: return "";
        }
    }

    // Gets the price from config for a given item name
    private String getItemPriceFromName(String itemName) {
        switch (itemName) {
            case "\u00A7cForce I": return String.valueOf(plugin.getConfig().getInt("prices.force"));
            case "\u00A7bSpeed I": return String.valueOf(plugin.getConfig().getInt("prices.speed"));
            case "\u00A7eHaste I": return String.valueOf(plugin.getConfig().getInt("prices.haste"));
            case "\u00A75Night Vision": return String.valueOf(plugin.getConfig().getInt("prices.nightvision"));
            case "\u00A76Fire Resistance": return String.valueOf(plugin.getConfig().getInt("prices.fireresistance"));
            default: return "0";
        }
    }

    // Maps item names to their material
    private Material getMaterialFromItemName(String itemName) {
        switch (itemName) {
            case "\u00A7cForce I": return Material.DIAMOND_SWORD;
            case "\u00A7bSpeed I": return Material.FEATHER;
            case "\u00A7eHaste I": return Material.IRON_PICKAXE;
            case "\u00A75Night Vision": return Material.EYE_OF_ENDER;
            case "\u00A76Fire Resistance": return Material.MAGMA_CREAM;
            default: return Material.AIR;
        }
    }
}