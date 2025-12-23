package com.tejaslamba.smpcore.features;

import com.tejaslamba.smpcore.Main;
import com.tejaslamba.smpcore.feature.BaseFeature;
import com.tejaslamba.smpcore.listener.CustomAnvilCapsListener;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomAnvilCapsFeature extends BaseFeature {

    private CustomAnvilCapsListener listener;

    @Override
    public String getName() {
        return "Custom Anvil Caps";
    }

    @Override
    public String getConfigPath() {
        return "features.custom-anvil-caps";
    }

    @Override
    public void onEnable(Main plugin) {
        listener = new CustomAnvilCapsListener(plugin);
        super.onEnable(plugin);
    }

    @Override
    public void onDisable() {
        listener = null;
        super.onDisable();
    }

    @Override
    public Listener getListener() {
        return listener;
    }

    @Override
    public ItemStack getMenuItem() {
        ItemStack item = new ItemStack(Material.ANVIL);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6Custom Anvil Caps");

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
        boolean blockTable = plugin.getConfigManager().get()
                .getBoolean("features.custom-anvil-caps.block-enchanting-table", true);
        boolean blockAnvil = plugin.getConfigManager().get()
                .getBoolean("features.custom-anvil-caps.block-anvil", true);
        boolean keepBest = plugin.getConfigManager().get()
                .getBoolean("features.custom-anvil-caps.keep-only-best-protection", true);

        var capsSection = plugin.getConfigManager().get()
                .getConfigurationSection("features.custom-anvil-caps.caps");
        Map<String, Object> caps = capsSection != null ? capsSection.getValues(false) : Map.of();

        player.sendMessage("§6§l=== Custom Anvil Caps ===");
        player.sendMessage("");
        player.sendMessage("§7Status: " + (isEnabled() ? "§aEnabled" : "§cDisabled"));
        player.sendMessage("§7Block Enchanting Table: " + (blockTable ? "§aYes" : "§cNo"));
        player.sendMessage("§7Block Anvil: " + (blockAnvil ? "§aYes" : "§cNo"));
        player.sendMessage("§7Keep Only Best Protection: " + (keepBest ? "§aYes" : "§cNo"));
        player.sendMessage("");
        player.sendMessage("§6Enchantment Caps:");

        if (caps.isEmpty()) {
            player.sendMessage("  §cNo caps configured");
        } else {
            caps.forEach((enchant, cap) -> {
                String capText = cap.equals(0) ? "§c§lBANNED" : "§e" + cap;
                player.sendMessage("  §7• §f" + enchant + "§7: " + capText);
            });
        }

        player.sendMessage("");
        player.sendMessage("§7Configure caps in §fconfig.yml");
    }

    @Override
    public List<String> getMenuLore() {
        List<String> lore = new ArrayList<>();
        lore.add("§7Blocks high-level enchants at");
        lore.add("§7enchanting tables and anvils");
        lore.add("");

        boolean blockTable = plugin.getConfigManager().get()
                .getBoolean("features.custom-anvil-caps.block-enchanting-table", true);
        boolean blockAnvil = plugin.getConfigManager().get()
                .getBoolean("features.custom-anvil-caps.block-anvil", true);

        lore.add("§7Block Table: " + (blockTable ? "§aYes" : "§cNo"));
        lore.add("§7Block Anvil: " + (blockAnvil ? "§aYes" : "§cNo"));

        var capsSection = plugin.getConfigManager().get()
                .getConfigurationSection("features.custom-anvil-caps.caps");
        int capsCount = capsSection != null ? capsSection.getKeys(false).size() : 0;

        lore.add("§7Configured Caps: §e" + capsCount);

        return lore;
    }
}
