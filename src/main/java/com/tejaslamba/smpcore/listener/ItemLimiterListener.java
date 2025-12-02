package com.tejaslamba.smpcore.listener;

import com.tejaslamba.smpcore.Main;
import com.tejaslamba.smpcore.features.ItemLimiterFeature;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;

public class ItemLimiterListener implements Listener {

    private final Main plugin;

    public ItemLimiterListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        ItemLimiterFeature feature = plugin.getFeatureManager().getFeature(ItemLimiterFeature.class);

        if (feature == null || !feature.isEnabled()) {
            return;
        }

        if (!feature.getCheckMethod().equalsIgnoreCase("on-move")) {
            return;
        }

        checkAndLimitItems(event.getPlayer(), feature);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        ItemLimiterFeature feature = plugin.getFeatureManager().getFeature(ItemLimiterFeature.class);

        if (feature == null || !feature.isEnabled()) {
            return;
        }

        if (!feature.getCheckMethod().equalsIgnoreCase("on-hit")) {
            return;
        }

        checkAndLimitItems(victim, feature);
    }

    private void checkAndLimitItems(Player player, ItemLimiterFeature feature) {
        PlayerInventory inv = player.getInventory();
        Map<Material, Integer> limits = feature.getItemLimits();

        for (Map.Entry<Material, Integer> entry : limits.entrySet()) {
            Material material = entry.getKey();
            int limit = entry.getValue();

            int totalAmount = countMaterial(inv, material);

            if (totalAmount > limit) {
                int excess = totalAmount - limit;
                removeExcess(player, material, excess);

                String itemName = material.name().toLowerCase().replace("_", " ");
                player.sendMessage(
                        "§c[SMP Core] §7You exceeded the item limit for §e" + itemName + " §7(max: §e" + limit + "§7)");

                boolean verbose = plugin.getConfigManager().get().getBoolean("plugin.verbose", false);
                if (verbose) {
                    plugin.getLogger().info("[VERBOSE] Item Limiter - " + player.getName() + " exceeded limit for "
                            + material.name() + " (had " + totalAmount + ", limit " + limit + ")");
                }
            }
        }
    }

    private int countMaterial(PlayerInventory inv, Material material) {
        int total = 0;

        for (ItemStack item : inv.getContents()) {
            if (item != null && item.getType() == material) {
                total += item.getAmount();
            }
        }

        ItemStack offhand = inv.getItemInOffHand();
        if (offhand.getType() == material) {
            total += offhand.getAmount();
        }

        return total;
    }

    private void removeExcess(Player player, Material material, int excess) {
        PlayerInventory inv = player.getInventory();
        int remaining = excess;

        for (int i = 0; i < inv.getSize() && remaining > 0; i++) {
            ItemStack item = inv.getItem(i);

            if (item == null || item.getType() != material) {
                continue;
            }

            int amount = item.getAmount();
            int toRemove = Math.min(amount, remaining);

            ItemStack dropStack = item.clone();
            dropStack.setAmount(toRemove);

            Item droppedItem = player.getWorld().dropItem(player.getEyeLocation(), dropStack);
            droppedItem.setPickupDelay(40);

            if (toRemove >= amount) {
                inv.setItem(i, null);
            } else {
                item.setAmount(amount - toRemove);
            }

            remaining -= toRemove;
        }

        ItemStack offhand = inv.getItemInOffHand();
        if (remaining > 0 && offhand.getType() == material) {
            int amount = offhand.getAmount();
            int toRemove = Math.min(amount, remaining);

            ItemStack dropStack = offhand.clone();
            dropStack.setAmount(toRemove);

            Item droppedItem = player.getWorld().dropItem(player.getEyeLocation(), dropStack);
            droppedItem.setPickupDelay(40);

            if (toRemove >= amount) {
                inv.setItemInOffHand(null);
            } else {
                offhand.setAmount(amount - toRemove);
            }
        }
    }
}
