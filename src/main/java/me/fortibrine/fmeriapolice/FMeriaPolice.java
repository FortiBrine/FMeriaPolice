package me.fortibrine.fmeriapolice;

import me.fortibrine.fmeriapolice.commands.*;
import me.fortibrine.fmeriapolice.listeners.Listener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FMeriaPolice extends JavaPlugin {

    private static FMeriaPolice instance;
    private Map<Player, Inventory> wantedList = new HashMap<>();
    private List<Player> handcuffed = new ArrayList<>();
    private Map<Player, String> wanted = new HashMap<>();

    public static FMeriaPolice getMain() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        File config = new File(this.getDataFolder() + File.separator + "config.yml");
        if (!config.exists()) {
            this.getConfig().options().copyDefaults(true);
            this.saveDefaultConfig();
        }

        if (Bukkit.getPluginManager().getPlugin("FMeria") == null) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.getCommand("gethandcuff").setExecutor(new CommandGetHandCuff());
        this.getCommand("wantedlist").setExecutor(new CommandWantedList());
        this.getCommand("wanted").setExecutor(new CommandWanted());
        this.getCommand("unwanted").setExecutor(new CommandUnwanted());
        this.getCommand("arrest").setExecutor(new CommandArrest());

        Bukkit.getPluginManager().registerEvents(new Listener(), this);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (this.getConfig().getConfigurationSection("arrest").getKeys(false).contains(player.getName())) {
                    Location loc = player.getLocation();

                    double playerX = loc.getX();
                    double playerY = loc.getY();
                    double playerZ = loc.getZ();

                    String world = loc.getWorld().getName();

                    Location location = new Location(Bukkit.getWorld(this.getConfig().getString("jail.world")), this.getConfig().getDouble("jail.x"), this.getConfig().getDouble("jail.y"), this.getConfig().getDouble("jail.z"));

                    double radius = 20;
                    if (!(loc.getX() - radius <= location.getX() && location.getX() <= loc.getX() + radius)) {
                        player.teleport(location);
                    }
                    if (!(loc.getY() - radius <= location.getY() && location.getY() <= loc.getY() + radius)) {
                        player.teleport(location);
                    }
                    if (!(loc.getZ() - radius <= location.getZ() && location.getZ() <= loc.getZ() + radius)) {
                        player.teleport(location);
                    }
                    if (!world.equals(location.getWorld().getName())) {
                        player.teleport(location);
                    }

                    long time = this.getConfig().getLong("arrest_time."+player.getName());

                    time-=3;

                    if (time <= 0) {
                        this.getConfig().set("arrest." + player.getName(), null);
                        this.getConfig().set("arrest_time." + player.getName(), null);

                        player.teleport(player.getBedSpawnLocation());
                    } else {
                        this.getConfig().set("arrest_time." + player.getName(), time);
                    }

                    long hours = time / 3600;
                    time = time % 3600;
                    long minutes = time / 60;
                    time = time % 60;
                    long seconds = time;

                    String title = this.getConfig().getString("jail_title.title")
                            .replace("%h", ""+hours)
                            .replace("%m", ""+minutes)
                            .replace("%s", ""+seconds);

                    String subtitle = this.getConfig().getString("jail_title.subtitle")
                            .replace("%h", ""+hours)
                            .replace("%m", ""+minutes)
                            .replace("%s", ""+seconds);

                    player.sendTitle(title, subtitle, 10, 70, 20);

                    this.saveConfig();
                    this.reloadConfig();
                }
            }
        }, 60L, 60L);
    }

    public boolean getHandCuffed(Player player) {
        if (handcuffed.contains(player)) {
            return true;
        } else {
            return false;
        }
    }

    public void handcuff(Player player) {
        handcuffed.add(player);
    }

    public void unhandcuff(Player player) {
        handcuffed.remove(player);
    }

    public Inventory generateInventory() {
        Inventory wantedList = Bukkit.createInventory(null, 54, this.getConfig().getString("wanted.title"));

        for (Player player : this.wanted.keySet()) {
            ItemStack item = new ItemStack(Material.matchMaterial(this.getConfig().getString("wanted.item.material")));

            ItemMeta meta = item.getItemMeta();

            meta.setDisplayName(this.getConfig().getString("wanted.item.name")
                    .replace("%player", player.getName())
                    .replace("%user", player.getDisplayName())
                    .replace("%reason", this.wanted.get(player)));

            List<String> lore = this.getConfig().getStringList("wanted.item.lore");

            for (int i = 0; i < lore.size(); i++) {
                lore.set(i, lore.get(i)
                        .replace("%player", player.getName())
                        .replace("%user", player.getDisplayName())
                        .replace("%reason", this.wanted.get(player)));
            }

            meta.setLore(lore);

            item.setItemMeta(meta);

            wantedList.addItem(item);
        }

        return wantedList;
    }

    public void addWanted(Player player, String reason) {
        this.wanted.put(player, reason);
    }

    public boolean containsWanted(Player player) {
        return this.wanted.containsKey(player);
    }

    public void removeWanted(Player player) {
        this.wanted.remove(player);
    }

    public String getReasonWanted(Player player) {
        return this.wanted.get(player);
    }

    public void putWantedList(Player player, Inventory inventory) {
        this.wantedList.put(player, inventory);
    }

    public Inventory getWantedList(Player player) {
        return this.wantedList.get(player);
    }

    public boolean containsWantedList(Player player) {
        return this.wantedList.containsKey(player);
    }

}
