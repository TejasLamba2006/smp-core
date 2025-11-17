package com.tejaslamba.smpcore.listener;

import com.tejaslamba.smpcore.Main;
import com.tejaslamba.smpcore.command.EnchantCommand;
import com.tejaslamba.smpcore.utils.EnchantmentUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentReplacementListener implements Listener {

    private final Main plugin;

    public EnchantmentReplacementListener(Main plugin) {
        this.plugin = plugin;
    }

    private int getCap(Enchantment enchantment) {
        String key = enchantment.getKey().getKey();
        return plugin.getConfigManager().get()
                .getInt("features.enchantment-replacement.caps." + key, -1);
    }

    private boolean isScanJoinEnabled() {
        return plugin.getConfigManager().get()
                .getBoolean("features.enchantment-replacement.scan-on-join", true);
    }

    private boolean isScanInventoryEnabled() {
        return plugin.getConfigManager().get()
                .getBoolean("features.enchantment-replacement.scan-on-inventory-open", true);
    }

    private boolean isScanVillagerEnabled() {
        return plugin.getConfigManager().get()
                .getBoolean("features.enchantment-replacement.scan-on-villager-trade", true);
    }

    private boolean isScanPickupEnabled() {
        return plugin.getConfigManager().get()
                .getBoolean("features.enchantment-replacement.scan-on-pickup", false);
    }

    private boolean isScanCraftEnabled() {
        return plugin.getConfigManager().get()
                .getBoolean("features.enchantment-replacement.scan-on-craft", true);
    }

    private boolean isKeepBestProtection() {
        return plugin.getConfigManager().get()
                .getBoolean("features.enchantment-replacement.keep-only-best-protection", true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!isScanJoinEnabled()) {
            return;
        }

        Player player = event.getPlayer();
        boolean verbose = plugin.getConfigManager().get().getBoolean("plugin.verbose", false);

        if (verbose) {
            plugin.getLogger().info("[VERBOSE] Scanning inventory for " + player.getName() + " on join");
        }

        scanInventory(player.getInventory(), verbose);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!isScanInventoryEnabled()) {
            return;
        }

        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        boolean verbose = plugin.getConfigManager().get().getBoolean("plugin.verbose", false);

        if (verbose) {
            plugin.getLogger().info("[VERBOSE] Scanning inventory for " + player.getName() + " on open");
        }

        scanInventory(event.getInventory(), verbose);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onVillagerTrade(VillagerAcquireTradeEvent event) {
        if (!isScanVillagerEnabled()) {
            return;
        }

        boolean verbose = plugin.getConfigManager().get().getBoolean("plugin.verbose", false);

        ItemStack result = event.getRecipe().getResult();

        if (result != null && result.hasItemMeta()) {
            if (verbose) {
                plugin.getLogger().info("[VERBOSE] Scanning villager trade result");
            }
            processItem(result, verbose);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        if (!isScanPickupEnabled()) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        ItemStack item = event.getItem().getItemStack();
        if (item == null || !item.hasItemMeta()) {
            return;
        }

        boolean verbose = plugin.getConfigManager().get().getBoolean("plugin.verbose", false);

        if (verbose) {
            plugin.getLogger().info("[VERBOSE] Scanning picked up item");
        }

        processItem(item, verbose);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCraft(PrepareItemCraftEvent event) {
        if (!isScanCraftEnabled()) {
            return;
        }

        ItemStack result = event.getInventory().getResult();

        if (result == null || !result.hasItemMeta()) {
            return;
        }

        boolean verbose = plugin.getConfigManager().get().getBoolean("plugin.verbose", false);

        if (verbose) {
            plugin.getLogger().info("[VERBOSE] Scanning crafted item");
        }

        processItem(result, verbose);
    }

    private void scanInventory(Inventory inventory, boolean verbose) {
        ItemStack[] contents = inventory.getContents();

        for (ItemStack item : contents) {
            if (item != null && item.hasItemMeta()) {
                if (!EnchantCommand.isWhitelisted(item)) {
                    processItem(item, verbose);
                }
            }
        }
    }

    private void processItem(ItemStack item, boolean verbose) {
        if (item == null) {
            return;
        }

        if (EnchantCommand.isWhitelisted(item)) {
            return;
        }

        Map<Enchantment, Integer> enchants = EnchantmentUtils.getAllEnchantments(item);

        if (enchants.isEmpty()) {
            return;
        }

        Map<Enchantment, Integer> newEnchants = new HashMap<>();
        boolean modified = false;

        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            Enchantment enchant = entry.getKey();
            int level = entry.getValue();
            int cap = getCap(enchant);

            if (cap == -1) {
                newEnchants.put(enchant, level);
                continue;
            }

            if (cap == 0) {
                if (verbose) {
                    plugin.getLogger().info("[VERBOSE] Removing banned enchantment: "
                            + enchant.getKey().getKey() + " level " + level);
                }
                modified = true;
                continue;
            }

            if (level > cap) {
                if (verbose) {
                    plugin.getLogger().info("[VERBOSE] Downgrading " + enchant.getKey().getKey()
                            + " from " + level + " to " + cap);
                }
                newEnchants.put(enchant, cap);
                modified = true;
            } else {
                newEnchants.put(enchant, level);
            }
        }

        if (isKeepBestProtection()) {
            Enchantment highest = EnchantmentUtils.getHighestProtection(newEnchants);

            if (highest != null) {
                int sizeBefore = newEnchants.size();
                newEnchants.entrySet().removeIf(entry -> {
                    if (EnchantmentUtils.isProtectionEnchantment(entry.getKey()) &&
                            entry.getKey() != highest) {
                        if (verbose) {
                            plugin.getLogger().info("[VERBOSE] Removing conflicting protection: "
                                    + entry.getKey().getKey().getKey());
                        }
                        return true;
                    }
                    return false;
                });

                if (newEnchants.size() != sizeBefore) {
                    modified = true;
                }
            }
        }

        if (modified) {
            if (verbose) {
                plugin.getLogger().info("[VERBOSE] Updating item enchantments: " + item.getType());
            }
            EnchantmentUtils.setEnchantments(item, newEnchants);
        }
    }
}
