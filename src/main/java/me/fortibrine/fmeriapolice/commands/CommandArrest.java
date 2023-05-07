package me.fortibrine.fmeriapolice.commands;

import me.fortibrine.fmeriapolice.FMeriaPolice;
import me.fortibrine.fmeriapolice.utils.FMeriaUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CommandArrest implements CommandExecutor {

    private FMeriaPolice plugin;
    public CommandArrest() {
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

        if (!config.getStringList("permissions.arrest.factions").contains(faction)) {
            player.sendMessage(config.getString("messages.nonpermission"));
            return true;
        }

        int rank = FMeriaUtil.getPlayerRank(faction, player.getName());

        if (rank < config.getInt("permissions.arrest.rank")) {
            player.sendMessage(config.getString("messages.nonpermission"));
            return true;
        }

        if (args.length < 1) {
            return false;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
        if (!offlinePlayer.isOnline()) {
            player.sendMessage(config.getString("messages.not_online"));
            return true;
        }

        Player playerWanted = Bukkit.getPlayer(args[0]);

        if (!plugin.getHandCuffed(playerWanted)) {
            player.sendMessage(config.getString("messages.nothandcuff"));
            return true;
        }

        config.set("arrest." + args[0], plugin.getReasonWanted(playerWanted));
        config.set("arrest_time." + args[0], 1800);
        plugin.saveConfig();
        plugin.reloadConfig();

        Location location = new Location(Bukkit.getWorld(config.getString("jail.world")), config.getDouble("jail.x"), config.getDouble("jail.y"), config.getDouble("jail.z"));

        playerWanted.teleport(location);

        player.sendMessage(config.getString("messages.arrest")
                .replace("%player", playerWanted.getName())
                .replace("%user", playerWanted.getDisplayName()));

        plugin.removeWanted(playerWanted);

        return true;
    }
}
