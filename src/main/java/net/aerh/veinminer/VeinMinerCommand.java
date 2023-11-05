package net.aerh.veinminer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class VeinMinerCommand implements CommandExecutor {

    private static final String USAGE_MESSAGE = ChatColor.RED + "Usage: /%s <toggle/reload>";

    private final VeinMinerPlugin plugin;

    public VeinMinerCommand(VeinMinerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("veinminer.command")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            return false;
        }

        if (args.length < 1) {
            sender.sendMessage(USAGE_MESSAGE.formatted(label));
            return false;
        }

        switch (args[0]) {
            case "reload" -> {
                plugin.reloadConfig();

                VeinMinerConfig veinMinerConfig = new VeinMinerConfig(plugin.getConfig());
                plugin.setVeinMinerConfig(veinMinerConfig);

                sender.sendMessage(ChatColor.GREEN + "You reloaded the VeinMiner plugin configuration file!");
            }

            case "toggle" -> {
                VeinMinerConfig veinMinerConfig = plugin.getVeinMinerConfig();
                veinMinerConfig.setEnabled(!veinMinerConfig.isEnabled());
                sender.sendMessage(ChatColor.GREEN + "You " + booleanToString(veinMinerConfig.isEnabled()) + " vein mining functionality for everyone!");
            }

            default -> sender.sendMessage(USAGE_MESSAGE.formatted(label));
        }

        return true;
    }

    private String booleanToString(boolean bool) {
        return bool ? "enabled" : "disabled";
    }
}
