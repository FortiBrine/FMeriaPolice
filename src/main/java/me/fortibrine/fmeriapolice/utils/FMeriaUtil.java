package me.fortibrine.fmeriapolice.utils;

import me.fortibrine.fmeriapolice.FMeriaPolice;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class FMeriaUtil {

    public static FileConfiguration getFMeriaConfig() {
        return Bukkit.getPluginManager().getPlugin("FMeria").getConfig();
    }

    public static String getPlayerFaction(String playerName) {
        FileConfiguration config = FMeriaUtil.getFMeriaConfig();

        for (String faction : config.getKeys(false)) {

            ConfigurationSection users = config.getConfigurationSection(faction + ".users");

            for (String user : users.getKeys(false)) {
                if (user.equals(playerName)) {
                    return faction;
                }
            }

        }

        return null;

    }

    public static int getPlayerRank(String faction, String playerName) {
        FileConfiguration config = FMeriaUtil.getFMeriaConfig();

        return config.getInt(faction+".users."+playerName);
    }

}
