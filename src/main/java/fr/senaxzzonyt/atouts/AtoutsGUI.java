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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class AtoutsGUI implements Listener {

    private final JavaPlugin plugin;
    private static Economy economy;
    private static final Logger logger = Logger.getLogger("Minecraft");
    private final Map<Player, ItemStack> confirmationItems = new HashMap<>();

    public AtoutsGUI(JavaPlugin plugin) {
        this.plugin = plugin;
        economy = Main.getEconomy();
    }

    public void openAtoutsGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Atouts");

        gui.setItem(11, createGuiItem(Material.DIAMOND_SWORD, ChatColor.RED + "Force I", ChatColor.GRAY + "Vous donne " + ChatColor.RED + "Force I" + ChatColor.GRAY + " permanent.", plugin.getConfig().getInt("prices.force"), player.hasPermission("atouts.force")));
        gui.setItem(12, createGuiItem(Material.FEATHER, ChatColor.AQUA + "Speed I", ChatColor.GRAY + "Vous donne " + ChatColor.AQUA + "Speed I" + ChatColor.GRAY + " permanent.", plugin.getConfig().getInt("prices.speed"), player.hasPermission("atouts.speed")));
        gui.setItem(13, createGuiItem(Material.IRON_PICKAXE, ChatColor.YELLOW + "Haste I", ChatColor.GRAY + "Vous donne " + ChatColor.YELLOW + "Haste I" + ChatColor.GRAY + " permanent.", plugin.getConfig().getInt("prices.haste"), player.hasPermission("atouts.haste")));
        gui.setItem(14, createGuiItem(Material.MAGMA_CREAM, ChatColor.GOLD + "Fire Resistance", ChatColor.GRAY + "Vous donne " + ChatColor.GOLD + "Fire Resistance" + ChatColor.GRAY + " permanent.", plugin.getConfig().getInt("prices.fireresistance"), player.hasPermission("atouts.fireresistance")));
        gui.setItem(15, createGuiItem(Material.EYE_OF_ENDER, ChatColor.DARK_PURPLE + "Night Vision", ChatColor.GRAY + "Vous donne " + ChatColor.DARK_PURPLE + "Night Vision" + ChatColor.GRAY + " permanent.", plugin.getConfig().getInt("prices.nightvision"), player.hasPermission("atouts.nightvision")));

        player.openInventory(gui);
        logger.info(ChatColor.BLUE + "Atouts Logs - Ouverture de l'interface Atouts pour le joueur: " + player.getName());
    }

    private ItemStack createGuiItem(Material material, String name, String description, int price, boolean unlocked) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + description,
                "",
                ChatColor.GOLD + "Prix: " + ChatColor.GREEN + price + "$",
                ChatColor.GOLD + "Débloqué: " + (unlocked ? ChatColor.GREEN + "Oui" : ChatColor.RED + "Non")
        ));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createColoredWool(DyeColor color, String name, String description, int price) {
        ItemStack item = new ItemStack(Material.WOOL, 1, color.getWoolData());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + description,
                "",
                ChatColor.GOLD + "Prix: " + ChatColor.GREEN + price + "$"
        ));
        item.setItemMeta(meta);
        return item;
    }

    public void openConfirmationGUI(Player player, ItemStack item, int price) {
        Inventory confirmationGui = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Confirmer l'achat");

        confirmationGui.setItem(3, createColoredWool(DyeColor.GREEN, ChatColor.GREEN + "Confirmer", "Cliquez pour confirmer l'achat", price));
        confirmationGui.setItem(5, createColoredWool(DyeColor.RED, ChatColor.RED + "Annuler", "Cliquez pour annuler l'achat", price));

        confirmationItems.put(player, item);
        player.openInventory(confirmationGui);
        logger.info(ChatColor.BLUE + "Atouts Logs - Ouverture de l'interface de confirmation pour le joueur: " + player.getName() + " avec l'objet: " + item.getItemMeta().getDisplayName());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (title.equals(ChatColor.GOLD + "Atouts") || title.equals(ChatColor.GOLD + "Confirmer l'achat")) {
            event.setCancelled(true);

            if (clickedItem == null || clickedItem.getType() == Material.AIR) {
                return;
            }

            ItemMeta itemMeta = clickedItem.getItemMeta();
            if (itemMeta == null || itemMeta.getDisplayName() == null || itemMeta.getLore() == null) {
                return;
            }

            String itemName = itemMeta.getDisplayName();

            if (title.equals(ChatColor.GOLD + "Atouts")) {
                if (itemMeta.getLore().get(3).contains(ChatColor.RED + "Non")) {
                    try {
                        String priceString = itemMeta.getLore().get(2).replaceAll("[^0-9]", "").trim();
                        int price = Integer.parseInt(priceString);
                        openConfirmationGUI(player, clickedItem, price);
                    } catch (NumberFormatException e) {
                        logger.severe(ChatColor.RED + "Erreur : Échec de l'analyse du prix: " + e.getMessage());
                    }
                } else {
                    toggleEffect(player, itemName);
                }
            } else if (title.equals(ChatColor.GOLD + "Confirmer l'achat")) {
                if (itemName.equals(ChatColor.GREEN + "Confirmer")) {
                    ItemStack item = confirmationItems.get(player);
                    if (item != null) {
                        try {
                            String priceString = item.getItemMeta().getLore().get(2).replaceAll("[^0-9]", "").trim();
                            int price = Integer.parseInt(priceString);
                            if (economy.withdrawPlayer(player, price).transactionSuccess()) {
                                String permission = getPermissionFromItemName(item.getItemMeta().getDisplayName());
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " permission set " + permission + " true");
                                player.sendMessage(ChatColor.GREEN + "Achat effectué!");
                                player.closeInventory();
                            } else {
                                player.sendMessage(ChatColor.RED + "Fonds insuffisants!");
                            }
                        } catch (NumberFormatException e) {
                            logger.severe(ChatColor.RED + "Erreur : de l'analyse du prix: " + e.getMessage());
                        }
                    }
                } else if (itemName.equals(ChatColor.RED + "Annuler")) {
                    player.closeInventory();
                    openAtoutsGUI(player);
                }
            }
        }
    }

    private void toggleEffect(Player player, String itemName) {
        if (player.hasPermission(getPermissionFromItemName(itemName))) {
            PotionEffectType effectType = getPotionEffectTypeFromItemName(itemName);
            if (effectType != null) {
                if (player.hasPotionEffect(effectType)) {
                    player.removePotionEffect(effectType);
                    player.sendMessage(ChatColor.RED + "Effet " + itemName + " désactivé.");
                } else {
                    player.addPotionEffect(new PotionEffect(effectType, Integer.MAX_VALUE, 0, true, false));
                    player.sendMessage(ChatColor.GREEN + "Effet " + itemName + " activé.");
                }
            }
        } else {
            int price = Integer.parseInt(getItemPriceFromName(itemName));
            openConfirmationGUI(player, createGuiItem(getMaterialFromItemName(itemName), itemName, "Description", price, false), price);
        }
    }

    private PotionEffectType getPotionEffectTypeFromItemName(String itemName) {
        if (itemName.equals(ChatColor.RED + "Force I")) {
            return PotionEffectType.INCREASE_DAMAGE;
        } else if (itemName.equals(ChatColor.AQUA + "Speed I")) {
            return PotionEffectType.SPEED;
        } else if (itemName.equals(ChatColor.YELLOW + "Haste I")) {
            return PotionEffectType.FAST_DIGGING;
        } else if (itemName.equals(ChatColor.DARK_PURPLE + "Night Vision")) {
            return PotionEffectType.NIGHT_VISION;
        } else if (itemName.equals(ChatColor.GOLD + "Fire Resistance")) {
            return PotionEffectType.FIRE_RESISTANCE;
        } else {
            return null;
        }
    }

    private String getPermissionFromItemName(String itemName) {
        if (itemName.equals(ChatColor.RED + "Force I")) {
            return "atouts.force";
        } else if (itemName.equals(ChatColor.AQUA + "Speed I")) {
            return "atouts.speed";
        } else if (itemName.equals(ChatColor.YELLOW + "Haste I")) {
            return "atouts.haste";
        } else if (itemName.equals(ChatColor.DARK_PURPLE + "Night Vision")) {
            return "atouts.nightvision";
        } else if (itemName.equals(ChatColor.GOLD + "Fire Resistance")) {
            return "atouts.fireresistance";
        } else {
            return "";
        }
    }

    private String getItemPriceFromName(String itemName) {
        if (itemName.equals(ChatColor.RED + "Force I")) {
            return String.valueOf(plugin.getConfig().getInt("prices.force"));
        } else if (itemName.equals(ChatColor.AQUA + "Speed I")) {
            return String.valueOf(plugin.getConfig().getInt("prices.speed"));
        } else if (itemName.equals(ChatColor.YELLOW + "Haste I")) {
            return String.valueOf(plugin.getConfig().getInt("prices.haste"));
        } else if (itemName.equals(ChatColor.DARK_PURPLE + "Night Vision")) {
            return String.valueOf(plugin.getConfig().getInt("prices.nightvision"));
        } else if (itemName.equals(ChatColor.GOLD + "Fire Resistance")) {
            return String.valueOf(plugin.getConfig().getInt("prices.fireresistance"));
        } else {
            return "0";
        }
    }

    private Material getMaterialFromItemName(String itemName) {
        if (itemName.equals(ChatColor.RED + "Force I")) {
            return Material.DIAMOND_SWORD;
        } else if (itemName.equals(ChatColor.AQUA + "Speed I")) {
            return Material.FEATHER;
        } else if (itemName.equals(ChatColor.YELLOW + "Haste I")) {
            return Material.IRON_PICKAXE;
        } else if (itemName.equals(ChatColor.DARK_PURPLE + "Night Vision")) {
            return Material.EYE_OF_ENDER;
        } else if (itemName.equals(ChatColor.GOLD + "Fire Resistance")) {
            return Material.MAGMA_CREAM;
        } else {
            return Material.AIR;
        }
    }
}