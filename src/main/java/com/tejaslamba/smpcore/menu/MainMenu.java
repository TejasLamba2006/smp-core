package com.tejaslamba.smpcore.menu;

import com.tejaslamba.smpcore.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.List;

public class MainMenu extends BaseMenu {

    public MainMenu(Main plugin) {
        super(plugin, null);
        setupItems();
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(this, 54, "§6§lSMP Core Settings");
    }

    private void setupItems() {
        inventory.clear();
        List<ItemStack> items = plugin.getFeatureManager().getMenuItems();

        int slot = 0;
        for (ItemStack item : items) {
            if (slot >= 53)
                break;
            if (slot == 49)
                slot++;
            inventory.setItem(slot, item);
            slot++;
        }

        inventory.setItem(49, createMenuItem(Material.OAK_DOOR, "§c§lClose Menu",
                "§7Close this menu"));
    }

    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();
        boolean isRightClick = event.isRightClick();

        if (slot == 49) {
            player.closeInventory();
            return;
        }

        ItemStack item = event.getCurrentItem();
        if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
            List<String> lore = item.getItemMeta().getLore();
            if (lore != null) {
                for (String line : lore) {
                    if (line.contains("§8Config: §7")) {
                        String configPath = line.replace("§8Config: §7", "").replaceAll("§.", "");
                        String featureConfigPath = configPath.replace(".enabled", "");

                        plugin.getFeatureManager().getFeatures().forEach(feature -> {
                            if (feature.getConfigPath().equals(featureConfigPath)) {
                                if (isRightClick) {
                                    feature.onRightClick(player);
                                } else {
                                    feature.onLeftClick(player);
                                }
                                refresh(player);
                                setupItems();
                            }
                        });
                        return;
                    }
                }
            }
        }
    }

}
