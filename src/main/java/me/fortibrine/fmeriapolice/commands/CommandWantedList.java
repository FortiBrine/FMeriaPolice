package me.fortibrine.fmeriapolice.commands;

import me.fortibrine.fmeriapolice.FMeriaPolice;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class CommandWantedList implements CommandExecutor {

    private FMeriaPolice plugin;
    public CommandWantedList() {
        plugin = FMeriaPolice.getMain();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        FileConfiguration config = plugin.getConfig();

        if (!(sender instanceof Player)) {
            sender.sendMessage(config.getString("messages.not_player"));
            return true;
        }

        Player player = (Player) sender;

        player.closeInventory();

        Inventory inventory = plugin.generateInventory();

        plugin.putWantedList(player, inventory);

        player.openInventory(inventory);

        return true;
    }
}
