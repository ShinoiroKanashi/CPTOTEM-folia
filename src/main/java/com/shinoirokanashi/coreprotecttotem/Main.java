package com.shinoirokanashi.coreprotecttotem;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
    public static Main instance;
    private CoreProtectAPI api;

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(this, this);
        Plugin depend = getServer().getPluginManager().getPlugin("CoreProtect");
        if (depend == null) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        api = ((CoreProtect) depend).getAPI();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        boolean hasTotem = player.getInventory().contains(Material.TOTEM_OF_UNDYING);
        if (!hasTotem) {
            return;
        }

        double finalDamage = event.getFinalDamage();
        double playerHealth = player.getHealth();

        if (playerHealth - finalDamage <= 0) {
            String damagerName = "UNKNOWN";
            if (event instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {
                damagerName = Util.getDamagerName(entityDamageByEntityEvent.getDamager());
            }

            api.logRemoval(damagerName + " сломал тотем " + player.getName(), player.getLocation(), Material.TOTEM_OF_UNDYING, null);
        }
    }
}
