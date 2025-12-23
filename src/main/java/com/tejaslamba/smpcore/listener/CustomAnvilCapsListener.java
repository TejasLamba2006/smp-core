package com.tejaslamba.smpcore.listener;

import com.tejaslamba.smpcore.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CustomAnvilCapsListener implements Listener {

    private final Main plugin;
    private static final Enchantment[] PROTECTION_ENCHANTS = {
            Enchantment.PROTECTION,
            Enchantment.FIRE_PROTECTION,
            Enchantment.BLAST_PROTECTION,
            Enchantment.PROJECTILE_PROTECTION
    };

    public CustomAnvilCapsListener(Main plugin) {
        this.plugin = plugin;
    }

    private int getCap(Enchantment enchantment) {
        String key = enchantment.getKey().getKey();
        return plugin.getConfigManager().get()
                .getInt("features.custom-anvil-caps.caps." + key, -1);
    }

    private boolean isBlockEnchantingTableEnabled() {
        return plugin.getConfigManager().get()
                .getBoolean("features.custom-anvil-caps.block-enchanting-table", true);
    }

    private boolean isBlockAnvilEnabled() {
        return plugin.getConfigManager().get()
                .getBoolean("features.custom-anvil-caps.block-anvil", true);
    }

    private boolean isKeepBestProtection() {
        return plugin.getConfigManager().get()
                .getBoolean("features.custom-anvil-caps.keep-only-best-protection", true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEnchantItem(EnchantItemEvent event) {
        if (!isBlockEnchantingTableEnabled()) {
            return;
        }

        Map<Enchantment, Integer> toAdd = event.getEnchantsToAdd();
        boolean hadViolations = false;

        for (var entry : new HashSet<>(toAdd.entrySet())) {
            Enchantment enchant = entry.getKey();
            int level = entry.getValue();
            int cap = getCap(enchant);

            if (cap == 0) {
                toAdd.remove(enchant);
                hadViolations = true;
                if (plugin.isVerbose()) {
                    plugin.getLogger().info("[VERBOSE] Blocked banned enchantment " + enchant.getKey().getKey()
                            + " at enchanting table");
                }
            } else if (cap > 0 && level > cap) {
                toAdd.put(enchant, cap);
                hadViolations = true;
                if (plugin.isVerbose()) {
                    plugin.getLogger().info("[VERBOSE] Capped enchantment " + enchant.getKey().getKey()
                            + " from " + level + " to " + cap + " at enchanting table");
                }
            }
        }

        if (hadViolations) {
            String prefix = plugin.getConfigManager().get().getString("plugin.prefix", "§8[§6SMP§8]§r");
            event.getEnchanter().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    prefix + " &cSome enchantments were limited!"));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        if (!isBlockAnvilEnabled()) {
            return;
        }

        ItemStack result = event.getResult();
        if (result == null || result.getType() == Material.AIR) {
            return;
        }

        if (!result.hasItemMeta()) {
            return;
        }

        boolean modified = false;
        ItemMeta meta = result.getItemMeta();

        if (meta instanceof EnchantmentStorageMeta bookMeta) {
            Map<Enchantment, Integer> enchants = new HashMap<>(bookMeta.getStoredEnchants());
            modified = checkAndFixEnchantments(enchants);

            if (modified) {
                for (Enchantment e : bookMeta.getStoredEnchants().keySet()) {
                    bookMeta.removeStoredEnchant(e);
                }
                for (var entry : enchants.entrySet()) {
                    if (entry.getValue() > 0) {
                        bookMeta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
                    }
                }
                result.setItemMeta(bookMeta);
            }
        } else {
            Map<Enchantment, Integer> enchants = new HashMap<>(result.getEnchantments());
            modified = checkAndFixEnchantments(enchants);

            if (modified) {
                for (Enchantment e : result.getEnchantments().keySet()) {
                    meta.removeEnchant(e);
                }
                for (var entry : enchants.entrySet()) {
                    if (entry.getValue() > 0) {
                        meta.addEnchant(entry.getKey(), entry.getValue(), true);
                    }
                }
                result.setItemMeta(meta);
            }
        }

        if (modified) {
            event.setResult(result);

            for (HumanEntity viewer : event.getViewers()) {
                if (viewer instanceof Player player) {
                    player.updateInventory();
                }
            }
        }
    }

    private boolean checkAndFixEnchantments(Map<Enchantment, Integer> enchants) {
        boolean modified = false;

        for (var entry : new HashSet<>(enchants.entrySet())) {
            Enchantment enchant = entry.getKey();
            int level = entry.getValue();
            int cap = getCap(enchant);

            if (cap == 0) {
                enchants.remove(enchant);
                modified = true;
            } else if (cap > 0 && level > cap) {
                enchants.put(enchant, cap);
                modified = true;
            }
        }

        if (isKeepBestProtection()) {
            Enchantment best = null;
            int bestLevel = 0;

            for (Enchantment prot : PROTECTION_ENCHANTS) {
                Integer level = enchants.get(prot);
                if (level != null && level > bestLevel) {
                    best = prot;
                    bestLevel = level;
                }
            }

            if (best != null) {
                for (Enchantment prot : PROTECTION_ENCHANTS) {
                    if (prot != best && enchants.containsKey(prot)) {
                        enchants.remove(prot);
                        modified = true;
                    }
                }
            }
        }

        return modified;
    }
}
