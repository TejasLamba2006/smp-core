package com.tejaslamba.smpcore.commands;

import com.tejaslamba.smpcore.Main;
import com.tejaslamba.smpcore.command.EnchantCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SmpCommand implements CommandExecutor, TabCompleter {

    private final EnchantCommand enchantCommand;

    public SmpCommand() {
        this.enchantCommand = new EnchantCommand(Main.getInstance());
    }

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

        if (args[0].equalsIgnoreCase("enchant")) {
            String[] enchantArgs = new String[args.length - 1];
            System.arraycopy(args, 1, enchantArgs, 0, args.length - 1);
            return enchantCommand.onCommand(sender, command, label, enchantArgs);
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

        sender.sendMessage("§c[SMP] Unknown subcommand. Usage: /smp [menu|reload|enchant]");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            completions.add("menu");
            completions.add("enchant");
            if (sender.hasPermission("smpcore.reload")) {
                completions.add("reload");
            }
            return completions;
        }

        if (args.length >= 2 && args[0].equalsIgnoreCase("enchant")) {
            String[] enchantArgs = new String[args.length - 1];
            System.arraycopy(args, 1, enchantArgs, 0, args.length - 1);
            return enchantCommand.onTabComplete(sender, command, alias, enchantArgs);
        }

        return Collections.emptyList();
    }

}
