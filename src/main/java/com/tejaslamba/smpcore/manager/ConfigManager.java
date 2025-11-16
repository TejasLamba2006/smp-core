package com.tejaslamba.smpcore.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final JavaPlugin plugin;
    private static final int CURRENT_CONFIG_VERSION = 1;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        migrateConfig();
    }

    private void migrateConfig() {
        FileConfiguration config = plugin.getConfig();
        int configVersion = config.getInt("config-version", 0);
        boolean verbose = config.getBoolean("plugin.verbose", false);

        if (configVersion == CURRENT_CONFIG_VERSION) {
            if (verbose) {
                plugin.getLogger().info("[VERBOSE] Config is up to date (version " + CURRENT_CONFIG_VERSION + ")");
            }
            return;
        }

        plugin.getLogger().info("Migrating config from version " + configVersion + " to " + CURRENT_CONFIG_VERSION);

        Map<String, Object> defaults = getDefaultValues();
        boolean modified = false;

        for (Map.Entry<String, Object> entry : defaults.entrySet()) {
            if (!config.contains(entry.getKey())) {
                config.set(entry.getKey(), entry.getValue());
                modified = true;
                if (verbose) {
                    plugin.getLogger()
                            .info("[VERBOSE] Added missing config key: " + entry.getKey() + " = " + entry.getValue());
                }
            }
        }

        config.set("config-version", CURRENT_CONFIG_VERSION);
        modified = true;

        if (modified) {
            save();
            plugin.getLogger().info("Config migration completed. Added " + defaults.size() + " missing entries.");
        }
    }

    private Map<String, Object> getDefaultValues() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("config-version", CURRENT_CONFIG_VERSION);
        defaults.put("plugin.name", "SMP Core");
        defaults.put("plugin.prefix", "§8[§6SMP§8]§r");
        defaults.put("plugin.verbose", false);
        defaults.put("messages.welcome", "§aWelcome to the server!");
        defaults.put("messages.reload", "§aConfiguration reloaded successfully!");
        defaults.put("combat.tag-duration", 15000);
        defaults.put("bans.enchantments.enabled", false);
        defaults.put("bans.effects.enabled", false);
        defaults.put("enchantment-limits.protection.enabled", false);
        defaults.put("enchantment-limits.protection.max-level", 4);
        defaults.put("enchantment-limits.sharpness.enabled", false);
        defaults.put("enchantment-limits.sharpness.max-level", 5);

        String[] features = {
                "ban-mace", "ban-anchors", "ban-crystals", "ban-pearls", "ban-netherite",
                "combat-tag", "anti-restock", "anti-elytra-combat", "anti-naked-killing",
                "anti-afk-killing", "one-player-sleep", "infinite-restock", "pvp-toggle",
                "first-join-kit", "spectator-on-death", "ban-tipped-arrows", "prevent-bed-bombing",
                "restrict-tnt-minecart", "prevent-villager-killing", "dimension-nether",
                "dimension-end", "breach-swap-ban", "invisibility-qol", "item-limiter"
        };

        for (String feature : features) {
            boolean defaultEnabled = feature.equals("pvp-toggle") || feature.equals("dimension-nether")
                    || feature.equals("dimension-end");
            defaults.put("features." + feature + ".enabled", defaultEnabled);
        }

        return defaults;
    }

    public void save() {
        try {
            plugin.getConfig().save(new File(plugin.getDataFolder(), "config.yml"));
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to save config: " + e.getMessage());
        }
    }

    public FileConfiguration get() {
        return plugin.getConfig();
    }

}
