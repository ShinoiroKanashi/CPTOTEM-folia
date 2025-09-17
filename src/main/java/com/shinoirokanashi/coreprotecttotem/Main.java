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
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.Locale;

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
            String damagerName;
            if (event instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {
                String specificDamager = Util.getDamagerName(entityDamageByEntityEvent.getDamager());
                switch (event.getCause()) {
                    case THORNS -> damagerName = specificDamager + " [#ШИПЫ]";
                    case MAGIC -> damagerName = specificDamager + " [#МАГИЯ]";
                    case LIGHTNING -> damagerName = "#МОЛНИЯ";
                    case FALLING_BLOCK -> damagerName = "#ПАДАЮЩИЙ-БЛОК";
                    default -> damagerName = specificDamager;
                }
            } else {
                damagerName = switch (event.getCause()) {
                    case BLOCK_EXPLOSION, ENTITY_EXPLOSION -> {
                        String explosionSource = "#НЕИЗВЕСТНЫЙ-ВЗРЫВ";
                        if (event instanceof EntityDamageByBlockEvent damageByBlockEvent) {
                            Block damagerBlock = damageByBlockEvent.getDamager();
                            if (damagerBlock != null) {
                                explosionSource = "#" + damagerBlock.getType().name().toUpperCase();
                            }
                        } else if (event instanceof EntityDamageByEntityEvent damageByEntityEvent) {
                            Entity damagerEntity = damageByEntityEvent.getDamager();
                            if (damagerEntity != null) {
                                explosionSource = Util.getDamagerName(damagerEntity);
                            }
                        }
                        yield explosionSource;
                    }
                    default -> getDamageCauseName(event.getCause());
                };
            }

            api.logRemoval(player.getName() +" (" + damagerName + ")", player.getLocation(), Material.TOTEM_OF_UNDYING, null);
        }
    }

    private String getDamageCauseName(EntityDamageEvent.DamageCause cause) {
        return switch (cause) {
            case CONTACT -> "#КОНТАКТ";
            case SUFFOCATION -> "#УДУШЕНИЕ";
            case FALL -> "#ПАДЕНИЕ";
            case FIRE -> "#ОГОНЬ";
            case LAVA -> "#ЛАВА";
            case VOID -> "#ПУСТОТА";
            case DROWNING -> "#УТОПЛЕНИЕ";
            case STARVATION -> "#ГОЛОД";
            case WITHER -> "#ИССУШЕНИЕ";
            case FREEZE -> "#ЗАМЕРЗАНИЕ";
            case HOT_FLOOR -> "#МАГМОВЫЙ-БЛОК";
            default -> "#НЕИЗВЕСТНО";
        };
    }
}
