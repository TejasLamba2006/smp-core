package com.tejaslamba.smpcore.commands;

import com.tejaslamba.smpcore.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SmpCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player p)) {
                sender.sendMessage("§c[SMP] Menu can only be opened by a player");
                return true;
            }
            Main.getInstance().getMenuManager().openMainMenu(p);
            return true;
        }

        if (args[0].equalsIgnoreCase("menu")) {
            if (!(sender instanceof Player p)) {
                sender.sendMessage("§c[SMP] Menu can only be opened by a player");
                return true;
            }
            Main.getInstance().getMenuManager().openMainMenu(p);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("smpcore.reload")) {
                sender.sendMessage("§c[SMP] You don't have permission to reload");
                return true;
            }
            Main.getInstance().getConfigManager().load();
            Main.getInstance().getBanManager().load();
            Main.getInstance().getCombatManager().load();
            Main.getInstance().getMenuConfigManager().load();
            sender.sendMessage("§a[SMP] Configuration reloaded successfully!");
            return true;
        }

        sender.sendMessage("§c[SMP] Unknown subcommand. Usage: /smp [menu|reload]");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            completions.add("menu");
            if (sender.hasPermission("smpcore.reload")) {
                completions.add("reload");
            }
            return completions;
        }
        return Collections.emptyList();
    }

}
