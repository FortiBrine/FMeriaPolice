package me.fortibrine.fmeriapolice.listeners;

import me.fortibrine.fmeriapolice.FMeriaPolice;
import me.fortibrine.fmeriapolice.utils.FMeriaUtil;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Listener implements org.bukkit.event.Listener {

    @EventHandler
    public void onClick(PlayerInteractAtEntityEvent event) {
        FMeriaPolice plugin = FMeriaPolice.getMain();

        FileConfiguration config = plugin.getConfig();

        Entity entity = event.getRightClicked();

        if (!(entity instanceof Player)) {
            return;
        }

        Player player = event.getPlayer();
        Player playerClicked = (Player) entity;

        ItemStack itemClicked = player.getInventory().getItemInMainHand();

        ItemMeta meta = itemClicked.getItemMeta();

        if (itemClicked.getType() != Material.matchMaterial(config.getString("item.material").toUpperCase())) {
            return;
        }

        if (!meta.getDisplayName().equals(config.getString("item.name"))) {
            return;
        }

        if (!meta.getLore().equals(config.getStringList("item.lore"))) {
            return;
        }

        String faction = FMeriaUtil.getPlayerFaction(player.getName());

        if (faction == null) {
            return;
        }

        if (!config.getStringList("permissions.handcuff.factions").contains(faction)) {
            return;
        }

        int rank = FMeriaUtil.getPlayerRank(faction, player.getName());

        if (rank < config.getInt("permissions.handcuff.rank")) {
            return;
        }

        if (!plugin.containsWanted(playerClicked)) {
            return;
        }

        if (plugin.getHandCuffed(playerClicked)) {
            plugin.unhandcuff(playerClicked);
            String message = config.getString("messages.unhandcuff")
                    .replace("%player1", player.getName())
                    .replace("%player2", playerClicked.getName())
                    .replace("%user1", player.getDisplayName())
                    .replace("%user2", playerClicked.getDisplayName());

            player.sendMessage(message);
            playerClicked.sendMessage(message);

        } else {
            plugin.handcuff(playerClicked);

            String message = config.getString("messages.handcuff")
                    .replace("%player1", player.getName())
                    .replace("%player2", playerClicked.getName())
                    .replace("%user1", player.getDisplayName())
                    .replace("%user2", playerClicked.getDisplayName());

            player.sendMessage(message);
            playerClicked.sendMessage(message);
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        FMeriaPolice plugin = FMeriaPolice.getMain();

        plugin.unhandcuff(event.getPlayer());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        FMeriaPolice plugin = FMeriaPolice.getMain();

        Player player = (Player) event.getWhoClicked();

        if (event.getClickedInventory() == null) return;
        if (!plugin.containsWanted(player)) return;
        if (plugin.getWantedList(player) != event.getClickedInventory()) return;

        event.setCancelled(true);

    }

}
