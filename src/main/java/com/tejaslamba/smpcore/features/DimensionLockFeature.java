package com.tejaslamba.smpcore.features;

import com.tejaslamba.smpcore.Main;
import com.tejaslamba.smpcore.feature.BaseFeature;
import com.tejaslamba.smpcore.listener.DimensionLockListener;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class DimensionLockFeature extends BaseFeature {

    private final String dimension;
    private boolean locked;
    private DimensionLockListener sharedListener;

    public DimensionLockFeature(String dimension) {
        this.dimension = dimension;
    }

    @Override
    public void onEnable(Main plugin) {
        if (sharedListener == null) {
            sharedListener = new DimensionLockListener(plugin);
        }

        super.onEnable(plugin);
        locked = plugin.getConfigManager().get().getBoolean("features.dimension-lock-" + dimension + ".locked", false);

        boolean verbose = plugin.getConfigManager().get().getBoolean("plugin.verbose", false);
        if (verbose) {
            plugin.getLogger().info("[VERBOSE] Dimension Lock (" + dimension + ") - Loaded state: locked=" + locked);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        plugin.getConfigManager().get().set("features.dimension-lock-" + dimension + ".locked", locked);
        plugin.getConfigManager().save();
    }

    @Override
    public Listener getListener() {
        return sharedListener;
    }

    @Override
    public String getName() {
        return dimension.substring(0, 1).toUpperCase() + dimension.substring(1) + " Lock";
    }

    @Override
    public String getConfigPath() {
        return "features.dimension-lock-" + dimension;
    }

    @Override
    public ItemStack getMenuItem() {
        Material material = dimension.equals("end") ? Material.END_PORTAL_FRAME : Material.NETHERRACK;
        String name = "§5" + getName();
        return createMenuItem(material, name,
                "§7Control access to the " + dimension);
    }

    @Override
    public List<String> getMenuLore() {
        List<String> lore = new ArrayList<>();
        lore.add(locked ? "§cCurrently: Locked" : "§aCurrently: Open");
        lore.add("");
        lore.add("§eClick to " + (locked ? "Open" : "Lock"));
        return lore;
    }

    @Override
    public void onLeftClick(Player player) {
        toggleLock(player);
    }

    @Override
    public void onRightClick(Player player) {
        toggleLock(player);
    }

    private void toggleLock(Player player) {
        locked = !locked;
        plugin.getConfigManager().get().set("features.dimension-lock-" + dimension + ".locked", locked);
        plugin.getConfigManager().save();

        String dimensionName = dimension.substring(0, 1).toUpperCase() + dimension.substring(1);
        player.sendMessage("§6[SMP] §7The " + dimensionName + " is now " + (locked ? "§cLocked" : "§aOpen"));

        boolean verbose = plugin.getConfigManager().get().getBoolean("plugin.verbose", false);
        if (verbose) {
            plugin.getLogger().info("[VERBOSE] Dimension Lock (" + dimension + ") - " + player.getName()
                    + " toggled to: locked=" + locked);
        }
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
        plugin.getConfigManager().get().set("features.dimension-lock-" + dimension + ".locked", locked);
        plugin.getConfigManager().save();

        boolean verbose = plugin.getConfigManager().get().getBoolean("plugin.verbose", false);
        if (verbose) {
            plugin.getLogger().info("[VERBOSE] Dimension Lock (" + dimension + ") - State changed: locked=" + locked);
        }
    }

    public String getDimension() {
        return dimension;
    }
}
