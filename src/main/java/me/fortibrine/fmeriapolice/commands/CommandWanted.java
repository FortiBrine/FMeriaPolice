package me.fortibrine.fmeriapolice.commands;

import me.fortibrine.fmeriapolice.FMeriaPolice;
import me.fortibrine.fmeriapolice.utils.FMeriaUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CommandWanted implements CommandExecutor {

    private FMeriaPolice plugin;
    public CommandWanted() {
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

        if (!config.getStringList("permissions.wanted.factions").contains(faction)) {
            player.sendMessage(config.getString("messages.nonpermission"));
            return true;
        }

        int rank = FMeriaUtil.getPlayerRank(faction, player.getName());

        if (rank < config.getInt("permissions.wanted.rank")) {
            player.sendMessage(config.getString("messages.nonpermission"));
            return true;
        }

        if (args.length < 2) {
            return false;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
        if (!offlinePlayer.isOnline()) {
            player.sendMessage(config.getString("messages.not_online"));
            return true;
        }

        Player playerWanted = Bukkit.getPlayer(args[0]);
        plugin.addWanted(playerWanted, args[1]);

        player.sendMessage(config.getString("messages.addwanted")
                .replace("%player", playerWanted.getName())
                .replace("%user", playerWanted.getDisplayName()));

        return true;
    }
}
