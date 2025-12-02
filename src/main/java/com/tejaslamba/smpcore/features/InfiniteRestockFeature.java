package com.tejaslamba.smpcore.features;

import com.tejaslamba.smpcore.Main;
import com.tejaslamba.smpcore.feature.BaseFeature;
import com.tejaslamba.smpcore.listener.InfiniteRestockListener;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InfiniteRestockFeature extends BaseFeature {

    private InfiniteRestockListener listener;

    @Override
    public void onEnable(Main plugin) {
        listener = new InfiniteRestockListener(plugin);
        super.onEnable(plugin);

        boolean verbose = plugin.getConfigManager().get().getBoolean("plugin.verbose", false);
        if (verbose) {
            plugin.getLogger().info("[VERBOSE] Infinite Restock - Feature loaded");
        }
    }

    @Override
    public Listener getListener() {
        return listener;
    }

    @Override
    public String getName() {
        return "Infinite Restock";
    }

    @Override
    public String getConfigPath() {
        return "features.infinite-restock";
    }

    @Override
    public ItemStack getMenuItem() {
        return createMenuItem(Material.EMERALD, "§5Infinite Restock",
                "§7Villagers never run out of trades");
    }

    @Override
    public List<String> getMenuLore() {
        List<String> lore = new ArrayList<>();
        lore.add(enabled ? "§aEnabled" : "§cDisabled");
        lore.add("");
        lore.add("§7Villagers will always have");
        lore.add("§7trades available and prices");
        lore.add("§7won't increase from demand");
        lore.add("");
        lore.add("§eLeft Click: Toggle");
        return lore;
    }

    @Override
    public void onLeftClick(Player player) {
        toggleDefault(player);
    }

    @Override
    public void onRightClick(Player player) {
        player.sendMessage("§6§l=== Infinite Restock ===");
        player.sendMessage("");
        player.sendMessage("§7Status: " + (isEnabled() ? "§aEnabled" : "§cDisabled"));
        player.sendMessage("");
        player.sendMessage("§7When enabled, villagers will:");
        player.sendMessage("§a• §7Never run out of trades");
        player.sendMessage("§a• §7Always restock immediately");
        player.sendMessage("§a• §7Not increase prices from demand");
        player.sendMessage("");
        player.sendMessage("§7Great for economy balance and");
        player.sendMessage("§7preventing trade monopolies.");
    }
}
