package com.tejaslamba.smpcore.listener;

import com.tejaslamba.smpcore.Main;
import com.tejaslamba.smpcore.features.EndLockFeature;
import com.tejaslamba.smpcore.features.NetherLockFeature;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class DimensionLockListener implements Listener {

    private final Main plugin;

    public DimensionLockListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        if (plugin.isVerbose()) {
            plugin.getLogger()
                    .info("[VERBOSE] Dimension Lock - " + player.getName() + " is attempting to enter "
                            + event.getTo().getWorld().getEnvironment().name());
        }
        if (event.getTo() == null || event.getTo().getWorld() == null) {
            return;
        }

        World.Environment toEnvironment = event.getTo().getWorld().getEnvironment();

        boolean hasBypass = player.hasPermission("smpcore.dimension.bypass");

        if (toEnvironment == World.Environment.THE_END) {
            EndLockFeature endLock = plugin.getFeatureManager().getFeature(EndLockFeature.class);

            if (endLock != null && endLock.isLocked() && !hasBypass
                    && !player.hasPermission("smpcore.dimension.bypass.end")) {
                event.setCancelled(true);
                String message = plugin.getConfigManager().get()
                        .getString("features.dimension-lock-end.locked-message", "§cThe End is currently locked!");
                player.sendMessage(message);

                if (plugin.isVerbose()) {
                    plugin.getLogger()
                            .info("[VERBOSE] Dimension Lock - Blocked " + player.getName() + " from entering The End");
                }
            }
        } else if (toEnvironment == World.Environment.NETHER) {
            NetherLockFeature netherLock = plugin.getFeatureManager().getFeature(NetherLockFeature.class);

            if (netherLock != null && netherLock.isLocked() && !hasBypass
                    && !player.hasPermission("smpcore.dimension.bypass.nether")) {
                event.setCancelled(true);
                String message = plugin.getConfigManager().get()
                        .getString("features.dimension-lock-nether.locked-message",
                                "§cThe Nether is currently locked!");
                player.sendMessage(message);

                if (plugin.isVerbose()) {
                    plugin.getLogger().info(
                            "[VERBOSE] Dimension Lock - Blocked " + player.getName() + " from entering The Nether");
                }
            }
        }
    }
}
