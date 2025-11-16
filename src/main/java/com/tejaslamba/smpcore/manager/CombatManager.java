package com.tejaslamba.smpcore.manager;

import com.tejaslamba.smpcore.Main;
import org.bukkit.entity.Player;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CombatManager {

    private final Main plugin;
    private final Map<UUID, Long> combatTags = new ConcurrentHashMap<>();
    private long combatTagDuration;

    public CombatManager(Main plugin) {
        this.plugin = plugin;
    }

    public void load() {
        combatTagDuration = plugin.getConfigManager().get().getLong("combat.tag-duration", 15000);
    }

    public void tagPlayer(Player player) {
        if (!plugin.getConfigManager().get().getBoolean("features.combat-tag.enabled", false)) {
            return;
        }
        combatTags.put(player.getUniqueId(), System.currentTimeMillis() + combatTagDuration);
    }

    public boolean isInCombat(Player player) {
        Long expiry = combatTags.get(player.getUniqueId());
        if (expiry == null) {
            return false;
        }

        if (System.currentTimeMillis() >= expiry) {
            combatTags.remove(player.getUniqueId());
            return false;
        }

        return true;
    }

    public long getRemainingCombatTime(Player player) {
        Long expiry = combatTags.get(player.getUniqueId());
        if (expiry == null) {
            return 0;
        }

        long remaining = expiry - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    public void removeTag(Player player) {
        combatTags.remove(player.getUniqueId());
    }

    public void cleanup() {
        long now = System.currentTimeMillis();
        combatTags.entrySet().removeIf(entry -> entry.getValue() <= now);
    }

    public void shutdown() {
        combatTags.clear();
    }

}
