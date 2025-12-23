package com.tejaslamba.smpcore.features;

import com.tejaslamba.smpcore.Main;
import com.tejaslamba.smpcore.enchantlimiter.EnchantmentLimiterManager;
import com.tejaslamba.smpcore.feature.BaseFeature;
import com.tejaslamba.smpcore.listener.EnchantmentLimiterListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnchantmentLimiterFeature extends BaseFeature {

    public static final String GUI_TITLE = "§6Enchantment Limiter";

    private EnchantmentLimiterListener listener;
    private EnchantmentLimiterManager manager;

    @Override
    public String getName() {
        return "Enchantment Limiter";
    }

    @Override
    public String getConfigPath() {
        return "features.enchantment-limiter";
    }

    @Override
    public void onEnable(Main plugin) {
        manager = new EnchantmentLimiterManager(plugin);
        listener = new EnchantmentLimiterListener(plugin, manager);
        super.onEnable(plugin);
        manager.load();

        if (plugin.isVerbose()) {
            plugin.getLogger()
                    .info("[VERBOSE] Enchantment Limiter enabled with " + manager.getLimitsCount() + " limits");
        }
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            String title = player.getOpenInventory().getTitle();
            if (title.equals(GUI_TITLE)) {
                player.closeInventory();
            }
        }

        if (manager != null) {
            manager.shutdown();
        }
        super.onDisable();
    }

    @Override
    public void reload() {
        super.reload();
        if (manager != null) {
            manager.load();
        }
    }

    @Override
    public Listener getListener() {
        return listener;
    }

    @Override
    public ItemStack getMenuItem() {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6Enchantment Limiter");

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
        }
        return item;
    }

    @Override
    public void onRightClick(Player player) {
        Map<Enchantment, Integer> limits = manager.getEnchantmentLimits();

        player.sendMessage("§6§l=== Enchantment Limiter ===");
        player.sendMessage("");
        player.sendMessage("§7Status: " + (isEnabled() ? "§aEnabled" : "§cDisabled"));
        player.sendMessage("");
        player.sendMessage("§7Limits enchantments on all items.");
        player.sendMessage("§7Items exceeding limits are §cautomatically fixed§7.");
        player.sendMessage("");
        player.sendMessage("§6Triggers:");
        player.sendMessage("  §7• Enchanting Table");
        player.sendMessage("  §7• Anvil Results");
        player.sendMessage("  §7• Inventory Click");
        player.sendMessage("  §7• Item Pickup");
        player.sendMessage("");
        player.sendMessage("§6Enchantment Limits:");

        if (limits.isEmpty()) {
            player.sendMessage("  §cNo limits configured");
        } else {
            limits.forEach((enchant, limit) -> {
                String limitText = limit == 0 ? "§c§lBANNED" : "§e" + limit;
                player.sendMessage("  §7• §f" + manager.getEnchantDisplayName(enchant) + "§7: " + limitText);
            });
        }

        player.sendMessage("");
        player.sendMessage("§7Configure limits in §fconfig.yml");
    }

    @Override
    public List<String> getMenuLore() {
        List<String> lore = new ArrayList<>();
        lore.add("§7Enforces enchantment level caps");
        lore.add("§7on all items automatically");
        lore.add("");

        int limitsCount = manager != null ? manager.getLimitsCount() : 0;
        lore.add("§7Configured Limits: §e" + limitsCount);

        return lore;
    }

    public EnchantmentLimiterManager getManager() {
        return manager;
    }
}
