package com.tejaslamba.smpcore.listener;

import com.tejaslamba.smpcore.Main;
import com.tejaslamba.smpcore.features.InfiniteRestockFeature;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

public class InfiniteRestockListener implements Listener {

    private final Main plugin;

    public InfiniteRestockListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMerchantOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType() != InventoryType.MERCHANT) {
            return;
        }

        InfiniteRestockFeature feature = plugin.getFeatureManager().getFeature(InfiniteRestockFeature.class);

        if (feature == null || !feature.isEnabled()) {
            return;
        }

        if (!(event.getInventory() instanceof MerchantInventory merchantInv)) {
            return;
        }

        if (!(merchantInv.getHolder() instanceof AbstractVillager villager)) {
            return;
        }

        setInfiniteTrades(villager);

        boolean verbose = plugin.getConfigManager().get().getBoolean("plugin.verbose", false);
        if (verbose) {
            plugin.getLogger().info("[VERBOSE] Infinite Restock - Set infinite trades on merchant open");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTradeClick(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.MERCHANT) {
            return;
        }

        if (event.getSlotType() != InventoryType.SlotType.RESULT) {
            return;
        }

        InfiniteRestockFeature feature = plugin.getFeatureManager().getFeature(InfiniteRestockFeature.class);

        if (feature == null || !feature.isEnabled()) {
            return;
        }

        if (!(event.getInventory() instanceof MerchantInventory merchantInv)) {
            return;
        }

        if (!(merchantInv.getHolder() instanceof AbstractVillager villager)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            setInfiniteTrades(villager);
            player.updateInventory();
        }, 1L);
    }

    private void setInfiniteTrades(AbstractVillager villager) {
        List<MerchantRecipe> newRecipes = new ArrayList<>();

        for (MerchantRecipe oldRecipe : villager.getRecipes()) {
            MerchantRecipe newRecipe = new MerchantRecipe(
                    oldRecipe.getResult(),
                    0,
                    Integer.MAX_VALUE,
                    oldRecipe.hasExperienceReward(),
                    oldRecipe.getVillagerExperience(),
                    oldRecipe.getPriceMultiplier(),
                    0,
                    0);
            newRecipe.setIngredients(oldRecipe.getIngredients());
            newRecipes.add(newRecipe);
        }

        villager.setRecipes(newRecipes);
    }
}
