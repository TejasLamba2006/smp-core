package com.tejaslamba.smpcore.features;

import com.tejaslamba.smpcore.feature.BaseFeature;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class MaceBanFeature extends BaseFeature {

    @Override
    public String getName() {
        return "Ban Mace";
    }

    @Override
    public String getConfigPath() {
        return "features.ban-mace";
    }

    @Override
    public Listener getListener() {
        return plugin != null ? plugin.getSharedItemBanListener() : null;
    }

    @Override
    public ItemStack getMenuItem() {
        return createMenuItem(Material.MACE,
                "§4Ban Mace",
                "§7Prevent mace usage");
    }
}
