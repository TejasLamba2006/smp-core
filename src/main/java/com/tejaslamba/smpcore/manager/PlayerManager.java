package com.tejaslamba.smpcore.manager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerManager {

    private final JavaPlugin plugin;

    private final Map<UUID, Player> players = new ConcurrentHashMap<>();

    public PlayerManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void add(Player player) {
        players.put(player.getUniqueId(), player);
    }

    public void remove(Player player) {
        players.remove(player.getUniqueId());
    }

    public Player get(UUID uuid) {
        return players.get(uuid);
    }

    public void shutdown() {
        players.clear();
    }

}
