package me.fortibrine.fmeriapolice.commands;

import me.fortibrine.fmeriapolice.FMeriaPolice;
import me.fortibrine.fmeriapolice.utils.FMeriaUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandGetHandCuff implements CommandExecutor {

    private FMeriaPolice plugin;
    public CommandGetHandCuff() {
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

        String faction = FMeriaUtil.getPlayerFaction(player.getName());

        if (faction == null) {
            player.sendMessage(config.getString("messages.nonpermission"));
            return true;
        }

        if (!config.getStringList("permissions.gethandcuff.factions").contains(faction)) {
            player.sendMessage(config.getString("messages.nonpermission"));
            return true;
        }

        int rank = FMeriaUtil.getPlayerRank(faction, player.getName());

        if (rank < config.getInt("permissions.gethandcuff.rank")) {
            player.sendMessage(config.getString("messages.nonpermission"));
            return true;
        }

        ItemStack item = new ItemStack(Material.matchMaterial(config.getString("item.material").toUpperCase()));
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(config.getString("item.name"));
        meta.setLore(config.getStringList("item.lore"));

        item.setItemMeta(meta);

        player.getInventory().addItem(item);

        player.sendMessage(config.getString("messages.gethandcuff"));

        return true;
    }
}
