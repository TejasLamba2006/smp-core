package com.tejaslamba.smpcore.listener;

import com.tejaslamba.smpcore.Main;
import com.tejaslamba.smpcore.utils.EnchantmentUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.HashMap;
import java.util.Map;

public class CustomAnvilCapsListener implements Listener {

    private final Main plugin;

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

        boolean verbose = plugin.getConfigManager().get().getBoolean("plugin.verbose", false);
        Map<Enchantment, Integer> toAdd = event.getEnchantsToAdd();

        boolean hadViolations = toAdd.entrySet().removeIf(entry -> {
            Enchantment enchant = entry.getKey();
            int level = entry.getValue();
            int cap = getCap(enchant);

            if (cap == -1) {
                return false;
            }

            if (cap == 0 || level > cap) {
                if (verbose) {
                    plugin.getLogger().info("[VERBOSE] Blocked enchantment " + enchant.getKey().getKey()
                            + " level " + level + " at enchanting table (cap: " + cap + ")");
                }
                return true;
            }

            return false;
        });

        if (hadViolations) {
            Player player = event.getEnchanter();
            player.sendMessage(plugin.getConfigManager().get().getString("plugin.prefix", "§8[§6SMP§8]§r")
                    + " §cSome enchantments exceed the allowed limit!");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        if (!isBlockAnvilEnabled()) {
            return;
        }

        ItemStack left = event.getInventory().getItem(0);
        ItemStack right = event.getInventory().getItem(1);

        if (left == null || right == null) {
            return;
        }

        boolean verbose = plugin.getConfigManager().get().getBoolean("plugin.verbose", false);

        ItemStack result = combineItems(left, right, verbose);

        if (result != null) {
            event.setResult(result);
        }
    }

    private ItemStack combineItems(ItemStack left, ItemStack right, boolean verbose) {
        boolean leftBook = left.getType() == Material.ENCHANTED_BOOK;
        boolean rightBook = right.getType() == Material.ENCHANTED_BOOK;

        ItemStack result;
        if (leftBook && rightBook) {
            result = combineBooks(left, right, verbose);
        } else if (leftBook || rightBook) {
            result = combineBookWithItem(left, right, leftBook, verbose);
        } else if (left.getType() == right.getType()) {
            result = combineSameTypeItems(left, right, verbose);
        } else {
            return null;
        }

        return result;
    }

    private ItemStack combineBooks(ItemStack left, ItemStack right, boolean verbose) {
        ItemStack result = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta resultMeta = (EnchantmentStorageMeta) result.getItemMeta();

        Map<Enchantment, Integer> leftEnchants = EnchantmentUtils.getAllEnchantments(left);
        Map<Enchantment, Integer> rightEnchants = EnchantmentUtils.getAllEnchantments(right);
        Map<Enchantment, Integer> merged = mergeEnchantments(leftEnchants, rightEnchants, verbose);

        if (isKeepBestProtection()) {
            merged = filterProtections(merged);
        }

        for (Map.Entry<Enchantment, Integer> entry : merged.entrySet()) {
            if (entry.getValue() > 0) {
                resultMeta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
            }
        }

        result.setItemMeta(resultMeta);
        return result;
    }

    private ItemStack combineBookWithItem(ItemStack left, ItemStack right, boolean isLeftBook, boolean verbose) {
        ItemStack base = isLeftBook ? right.clone() : left.clone();
        ItemStack book = isLeftBook ? left : right;

        Map<Enchantment, Integer> baseEnchants = EnchantmentUtils.getAllEnchantments(base);
        Map<Enchantment, Integer> bookEnchants = EnchantmentUtils.getAllEnchantments(book);
        Map<Enchantment, Integer> merged = mergeEnchantments(baseEnchants, bookEnchants, verbose);

        if (isKeepBestProtection()) {
            merged = filterProtections(merged);
        }

        merged.entrySet().removeIf(entry -> !entry.getKey().canEnchantItem(base));

        EnchantmentUtils.setEnchantments(base, merged);
        return base;
    }

    private ItemStack combineSameTypeItems(ItemStack left, ItemStack right, boolean verbose) {
        ItemStack result = left.clone();

        Map<Enchantment, Integer> leftEnchants = EnchantmentUtils.getAllEnchantments(left);
        Map<Enchantment, Integer> rightEnchants = EnchantmentUtils.getAllEnchantments(right);
        Map<Enchantment, Integer> merged = mergeEnchantments(leftEnchants, rightEnchants, verbose);

        if (isKeepBestProtection()) {
            merged = filterProtections(merged);
        }

        merged.entrySet().removeIf(entry -> !entry.getKey().canEnchantItem(result));

        EnchantmentUtils.setEnchantments(result, merged);
        return result;
    }

    private Map<Enchantment, Integer> mergeEnchantments(Map<Enchantment, Integer> left,
            Map<Enchantment, Integer> right,
            boolean verbose) {
        Map<Enchantment, Integer> merged = new HashMap<>();

        for (Map.Entry<Enchantment, Integer> entry : left.entrySet()) {
            Enchantment enchant = entry.getKey();
            int level = entry.getValue();
            int cap = getCap(enchant);

            if (cap == 0) {
                if (verbose) {
                    plugin.getLogger().info("[VERBOSE] Removed banned enchantment: "
                            + enchant.getKey().getKey());
                }
                continue;
            }

            int finalLevel = (cap != -1) ? Math.min(level, cap) : level;
            merged.put(enchant, finalLevel);
        }

        for (Map.Entry<Enchantment, Integer> entry : right.entrySet()) {
            Enchantment enchant = entry.getKey();
            int rightLevel = entry.getValue();
            int leftLevel = merged.getOrDefault(enchant, 0);

            int combinedLevel;
            if (leftLevel == rightLevel && leftLevel > 0) {
                combinedLevel = leftLevel + 1;
            } else {
                combinedLevel = Math.max(leftLevel, rightLevel);
            }

            int cap = getCap(enchant);
            if (cap == 0) {
                if (verbose) {
                    plugin.getLogger().info("[VERBOSE] Blocked banned enchantment: "
                            + enchant.getKey().getKey());
                }
                continue;
            }

            int finalLevel = (cap != -1) ? Math.min(combinedLevel, cap) : combinedLevel;
            merged.put(enchant, finalLevel);
        }

        return merged;
    }

    private Map<Enchantment, Integer> filterProtections(Map<Enchantment, Integer> enchants) {
        Enchantment highest = EnchantmentUtils.getHighestProtection(enchants);

        if (highest == null) {
            return enchants;
        }

        Map<Enchantment, Integer> filtered = new HashMap<>();
        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            if (!EnchantmentUtils.isProtectionEnchantment(entry.getKey()) ||
                    entry.getKey() == highest) {
                filtered.put(entry.getKey(), entry.getValue());
            }
        }

        return filtered;
    }
}
