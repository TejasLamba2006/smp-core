package com.tejaslamba.smpcore.features;

import com.tejaslamba.smpcore.Main;
import com.tejaslamba.smpcore.feature.BaseFeature;
import com.tejaslamba.smpcore.listener.EnchantmentReplacementListener;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentReplacementFeature extends BaseFeature {

    private EnchantmentReplacementListener listener;

    @Override
    public String getName() {
        return "Enchantment Replacement";
    }

    @Override
    public String getConfigPath() {
        return "features.enchantment-replacement";
    }

    @Override
    public void onEnable(Main plugin) {
        super.onEnable(plugin);
        listener = new EnchantmentReplacementListener(plugin);
    }

    @Override
    public void onDisable() {
        listener = null;
    }

    @Override
    public Listener getListener() {
        return listener;
    }

    @Override
    public ItemStack getMenuItem() {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6Enchantment Replacement");

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(isEnabled() ? "§aEnabled" : "§cDisabled");
        lore.add("");
        lore.addAll(getMenuLore());
        lore.add("");
        lore.add("§eLeft-Click §7to toggle");
        lore.add("§eRight-Click §7for more info");
        lore.add("");
        lore.add("§8Config: §7" + getConfigPath() + ".enabled");

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void onRightClick(Player player) {
        boolean scanJoin = plugin.getConfigManager().get()
                .getBoolean("features.enchantment-replacement.scan-on-join", true);
        boolean scanInventory = plugin.getConfigManager().get()
                .getBoolean("features.enchantment-replacement.scan-on-inventory-open", true);
        boolean scanVillager = plugin.getConfigManager().get()
                .getBoolean("features.enchantment-replacement.scan-on-villager-trade", true);
        boolean scanPickup = plugin.getConfigManager().get()
                .getBoolean("features.enchantment-replacement.scan-on-pickup", false);
        boolean scanCraft = plugin.getConfigManager().get()
                .getBoolean("features.enchantment-replacement.scan-on-craft", true);

        player.sendMessage("§6§l=== Enchantment Replacement ===");
        player.sendMessage("");
        player.sendMessage("§7Status: " + (isEnabled() ? "§aEnabled" : "§cDisabled"));
        player.sendMessage("");
        player.sendMessage("§7Automatically downgrades enchantments");
        player.sendMessage("§7exceeding configured caps.");
        player.sendMessage("");
        player.sendMessage("§6Scan Triggers:");
        player.sendMessage("  §7• Player Join: " + (scanJoin ? "§aEnabled" : "§cDisabled"));
        player.sendMessage("  §7• Inventory Open: " + (scanInventory ? "§aEnabled" : "§cDisabled"));
        player.sendMessage("  §7• Villager Trade: " + (scanVillager ? "§aEnabled" : "§cDisabled"));
        player.sendMessage("  §7• Item Pickup: " + (scanPickup ? "§aEnabled" : "§cDisabled"));
        player.sendMessage("  §7• Item Craft: " + (scanCraft ? "§aEnabled" : "§cDisabled"));
        player.sendMessage("");
        player.sendMessage("§7Uses caps from §fCustom Anvil Caps");
        player.sendMessage("§7Configure in §fconfig.yml");
    }

    @Override
    public List<String> getMenuLore() {
        List<String> lore = new ArrayList<>();
        lore.add("§7Scans and downgrades enchantments");
        lore.add("§7that exceed configured caps");
        lore.add("");

        boolean scanJoin = plugin.getConfigManager().get()
                .getBoolean("features.enchantment-replacement.scan-on-join", true);
        boolean scanInventory = plugin.getConfigManager().get()
                .getBoolean("features.enchantment-replacement.scan-on-inventory-open", true);
        boolean scanVillager = plugin.getConfigManager().get()
                .getBoolean("features.enchantment-replacement.scan-on-villager-trade", true);

        int enabledScans = 0;
        if (scanJoin)
            enabledScans++;
        if (scanInventory)
            enabledScans++;
        if (scanVillager)
            enabledScans++;

        lore.add("§7Active Scans: §e" + enabledScans + "+");
        lore.add("§7Shares caps with §fCustom Anvil Caps");

        return lore;
    }
}
