package com.shinoirokanashi.coreprotecttotem;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;

public class Util {
    public static String getDamagerName(Entity damager) {
        if (damager instanceof Player) {
            return damager.getName();
        } else if (damager instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) damager).getShooter();
            if (shooter instanceof Player) {
                return ((Player) shooter).getName();
            } else if (shooter instanceof Entity) {
                return ((Entity) shooter).getName();
            }
        } else if (damager != null) {
            return damager.getName();
        }
        return "UNKNOWN";
    }
}
