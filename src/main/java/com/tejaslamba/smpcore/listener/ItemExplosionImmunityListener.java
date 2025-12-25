package com.tejaslamba.smpcore.listener;

import com.tejaslamba.smpcore.Main;
import com.tejaslamba.smpcore.features.ItemExplosionImmunityFeature;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class ItemExplosionImmunityListener implements Listener {

    private final Main plugin;

    public ItemExplosionImmunityListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Item)) {
            return;
        }

        ItemExplosionImmunityFeature feature = plugin.getFeatureManager()
                .getFeature(ItemExplosionImmunityFeature.class);

        if (feature == null || !feature.isEnabled()) {
            return;
        }

        DamageCause cause = event.getCause();
        if (cause == DamageCause.BLOCK_EXPLOSION || cause == DamageCause.ENTITY_EXPLOSION) {
            event.setCancelled(true);

            if (plugin.isVerbose()) {
                Item item = (Item) event.getEntity();
                plugin.getLogger().info("[VERBOSE] Item Explosion Immunity - Protected "
                        + item.getItemStack().getType().name() + " x" + item.getItemStack().getAmount()
                        + " from " + cause.name());
            }
        }
    }
}
