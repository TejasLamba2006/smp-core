package com.tejaslamba.smpcore.features;

import com.tejaslamba.smpcore.Main;
import com.tejaslamba.smpcore.feature.BaseFeature;
import com.tejaslamba.smpcore.listener.InfiniteRestockListener;
import com.tejaslamba.smpcore.infiniterestock.InfiniteRestockManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class InfiniteRestockFeature extends BaseFeature {

    private InfiniteRestockListener listener;
    private InfiniteRestockManager manager;

    public static final String GUI_TITLE = "§6Infinite Restock Manager";

    @Override
    public void onEnable(Main plugin) {
        listener = new InfiniteRestockListener(plugin);
        manager = new InfiniteRestockManager(plugin);
        super.onEnable(plugin);

        boolean verbose = plugin.getConfigManager().get().getBoolean("plugin.verbose", false);
        if (verbose) {
            plugin.getLogger().info("[VERBOSE] Infinite Restock - Feature loaded");
        }
    }

    @Override
    public Listener getListener() {
        return listener;
    }

    @Override
    public String getName() {
        return "Infinite Restock";
    }

    @Override
    public String getConfigPath() {
        return "features.infinite-restock";
    }

    @Override
    public ItemStack getMenuItem() {
        return createMenuItem(Material.EMERALD, "§5Infinite Restock",
                "§7Villagers never run out of trades");
    }

    @Override
    public List<String> getMenuLore() {
        List<String> lore = new ArrayList<>();
        lore.add(enabled ? "§aEnabled" : "§cDisabled");
        lore.add("");
        lore.add("§7Villagers will always have");
        lore.add("§7trades available and prices");
        lore.add("§7won't increase from demand");
        lore.add("");
        lore.add("§eLeft Click: Toggle");
        lore.add("§eRight Click: Open Manager");
        return lore;
    }

    @Override
    public void onLeftClick(Player player) {
        toggleDefault(player);
        if (enabled) {
            player.sendMessage("§a[SMP] §7Infinite Restock enabled");
        } else {
            player.sendMessage("§c[SMP] §7Infinite Restock disabled");
        }
    }

    @Override
    public void onRightClick(Player player) {
        openRestockGUI(player);
    }

    public void openRestockGUI(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        Inventory gui = Bukkit.createInventory(null, 27, GUI_TITLE);

        ItemStack maxTrades = createGuiItem(Material.ANVIL, "§eSet Max Trades",
                "§7Current: §f" + manager.getMaxTrades(),
                "§70 = Unlimited");

        ItemStack pricePenalty = createToggleItem(
                manager.isDisablePricePenalty(),
                Material.GOLD_NUGGET,
                "§eDisable Price Penalty",
                new String[] { "§7Villager demand reset to 0" });

        ItemStack allowWT = createToggleItem(
                manager.isAllowWanderingTraders(),
                Material.EMERALD_BLOCK,
                "§eAllow Wandering Traders",
                new String[] { "§7Apply to travelling merchants" });

        ItemStack uninstall = createToggleItem(
                manager.isUninstallMode(),
                Material.BARRIER,
                "§eUninstall Mode",
                new String[] { "§7Restore original villager trades" });

        ItemStack back = createGuiItem(Material.ARROW, "§cBack",
                "§7Return to main menu");

        gui.setItem(10, maxTrades);
        gui.setItem(12, pricePenalty);
        gui.setItem(14, allowWT);
        gui.setItem(16, uninstall);
        gui.setItem(22, back);

        fillBorder(gui, Material.GRAY_STAINED_GLASS_PANE);
        player.openInventory(gui);
    }

    public void handleRestockGUIClick(InventoryClickEvent event, Player player) {
        int raw = event.getRawSlot();
        if (raw >= event.getView().getTopInventory().getSize()) {
            return;
        }
        event.setCancelled(true);

        if (event.getCurrentItem() == null) {
            return;
        }

        switch (raw) {
            case 10 -> {
                player.closeInventory();
                openChatInput(player);
            }
            case 12 -> {
                boolean v = !manager.isDisablePricePenalty();
                manager.setDisablePricePenalty(v);
                Bukkit.getScheduler().runTask(plugin, () -> openRestockGUI(player));
            }
            case 14 -> {
                boolean v = !manager.isAllowWanderingTraders();
                manager.setAllowWanderingTraders(v);
                Bukkit.getScheduler().runTask(plugin, () -> openRestockGUI(player));
            }
            case 16 -> {
                boolean v = !manager.isUninstallMode();
                manager.setUninstallMode(v);
                Bukkit.getScheduler().runTask(plugin, () -> openRestockGUI(player));
            }
            case 22 -> {
                player.closeInventory();
                plugin.getMenuManager().openMainMenu(player);
            }
        }
    }

    private void openChatInput(Player player) {
        player.sendMessage("");
        player.sendMessage("§6Set Max Trades");
        player.sendMessage("");
        player.sendMessage("§aEnter the maximum trades per villager (0 = unlimited):");
        player.sendMessage("§7Type §c'cancel' §7to cancel");
        player.sendMessage("");

        plugin.getChatInputManager().requestInput(player, (p, input) -> {
            if (input.equalsIgnoreCase("cancel")) {
                p.sendMessage("§c[SMP Core] §7Input cancelled.");
                Bukkit.getScheduler().runTask(plugin, () -> openRestockGUI(p));
                return;
            }

            try {
                int value = Integer.parseInt(input.trim());
                if (value < 0 || value > 64000) {
                    p.sendMessage("§c[SMP Core] §7Invalid number! Must be between 0 and 64000");
                    Bukkit.getScheduler().runTask(plugin, () -> openRestockGUI(p));
                    return;
                }
                manager.setMaxTrades(value);
                p.sendMessage("§a[SMP Core] §7Max trades set to: " + value);
                Bukkit.getScheduler().runTask(plugin, () -> openRestockGUI(p));
            } catch (NumberFormatException e) {
                p.sendMessage("§c[SMP Core] §7Please enter a valid number!");
                Bukkit.getScheduler().runTask(plugin, () -> openRestockGUI(p));
            }
        });
    }

    private ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore.length > 0) {
                List<String> loreList = new ArrayList<>();
                for (String line : lore) {
                    loreList.add(line);
                }
                meta.setLore(loreList);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createToggleItem(boolean enabled, Material base, String name, String[] details) {
        Material mat = enabled ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
        List<String> lore = new ArrayList<>();
        lore.add(enabled ? "§aEnabled" : "§cDisabled");
        if (details != null) {
            for (String d : details)
                lore.add(d);
        }
        return createGuiItem(mat, name, lore.toArray(new String[0]));
    }

    private void fillBorder(Inventory inv, Material material) {
        ItemStack glass = new ItemStack(material);
        ItemMeta meta = glass.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            glass.setItemMeta(meta);
        }

        int size = inv.getSize();
        for (int i = 0; i < 9; i++) {
            if (inv.getItem(i) == null)
                inv.setItem(i, glass);
            if (inv.getItem(size - 9 + i) == null)
                inv.setItem(size - 9 + i, glass);
        }
        for (int i = 9; i < size - 9; i += 9) {
            if (inv.getItem(i) == null)
                inv.setItem(i, glass);
            if (inv.getItem(i + 8) == null)
                inv.setItem(i + 8, glass);
        }
    }

    public InfiniteRestockManager getManager() {
        return manager;
    }
}
