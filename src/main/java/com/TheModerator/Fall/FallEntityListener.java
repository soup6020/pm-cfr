/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.IronGolem
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Wolf
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.EntityDeathEvent
 *  org.bukkit.event.entity.EntityExplodeEvent
 *  org.bukkit.potion.PotionEffectType
 */
package com.TheModerator.Fall;

import com.TheModerator.Fall.Fall;
import java.util.logging.Logger;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.potion.PotionEffectType;

public class FallEntityListener
implements Listener {
    Logger log = Logger.getLogger("Minecraft");

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (Fall.noDrops) {
            event.setYield(0.0f);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player subject = (Player)event.getEntity();
            if (!Fall.HealthTarget.containsKey(subject)) {
                return;
            }
            subject.getHealth();
            if (Fall.BeingPunished.containsKey(subject)) {
                Wolf w;
                IronGolem g;
                if (Fall.BeingPunished.get(subject) == Fall.Punishments.BURN && (double)Fall.HealthTarget.get(subject).intValue() >= subject.getHealth()) {
                    event.setCancelled(true);
                    Fall.HealthTarget.remove(subject);
                    subject.setFireTicks(0);
                }
                if (Fall.BeingPunished.get(subject) == Fall.Punishments.HOUNDS && (double)Fall.HealthTarget.get(subject).intValue() >= subject.getHealth() && event instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent)event).getDamager() instanceof Wolf) {
                    w = (Wolf)((EntityDamageByEntityEvent)event).getDamager();
                    w.setHealth(0.0);
                    Fall.HealthTarget.remove(subject);
                    Fall.BeingPunished.remove(subject);
                }
                if (Fall.BeingPunished.get(subject) == Fall.Punishments.DRUNK && (double)Fall.HealthTarget.get(subject).intValue() >= subject.getHealth()) {
                    subject.removePotionEffect(PotionEffectType.CONFUSION);
                    Fall.HealthTarget.remove(subject);
                    Fall.BeingPunished.remove(subject);
                }
                if (Fall.BeingPunished.get(subject) == Fall.Punishments.BLIND && (double)Fall.HealthTarget.get(subject).intValue() >= subject.getHealth()) {
                    subject.removePotionEffect(PotionEffectType.BLINDNESS);
                    Fall.HealthTarget.remove(subject);
                    Fall.BeingPunished.remove(subject);
                }
                if (Fall.BeingPunished.get(subject) == Fall.Punishments.STARVE && (double)Fall.HealthTarget.get(subject).intValue() >= subject.getHealth()) {
                    subject.removePotionEffect(PotionEffectType.HUNGER);
                    Fall.HealthTarget.remove(subject);
                    Fall.BeingPunished.remove(subject);
                }
                if (Fall.BeingPunished.get(subject) == Fall.Punishments.POISON && (double)Fall.HealthTarget.get(subject).intValue() >= subject.getHealth()) {
                    subject.removePotionEffect(PotionEffectType.POISON);
                    Fall.HealthTarget.remove(subject);
                    Fall.BeingPunished.remove(subject);
                }
                if (Fall.BeingPunished.get(subject) == Fall.Punishments.IRONGOLEM && (double)Fall.HealthTarget.get(subject).intValue() >= subject.getHealth() && event instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent)event).getDamager() instanceof IronGolem) {
                    g = (IronGolem)((EntityDamageByEntityEvent)event).getDamager();
                    g.setHealth(0.0);
                    Fall.HealthTarget.remove(subject);
                    Fall.BeingPunished.remove(subject);
                }
                if (Fall.BeingPunished.get(subject) == Fall.Punishments.CREEPER && (double)Fall.HealthTarget.get(subject).intValue() >= subject.getHealth()) {
                    event.getEntity().getLastDamageCause().getEntity().remove();
                    Fall.HealthTarget.remove(subject);
                }
                if (Fall.BeingPunished.get(subject) == Fall.Punishments.EXPLODE && (double)Fall.HealthTarget.get(subject).intValue() >= subject.getHealth()) {
                    subject.setHealth((double)Fall.HealthTarget.get(subject).intValue());
                    Fall.HealthTarget.remove(subject);
                }
                if (Fall.BeingPunished.get(subject) == Fall.Punishments.INVOID || Fall.BeingPunished.get(subject) == Fall.Punishments.POTATO) {
                    subject.setHealth(20.0);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player subject = null;
        if (event.getEntity() instanceof Player) {
            subject = (Player)event.getEntity();
            if (Fall.BeingPunished.containsKey(subject)) {
                if (Fall.BeingPunished.get(subject) == Fall.Punishments.FALL) {
                    subject.chat("*Splat*");
                    if (Fall.HealthTarget.get(subject) != 0) {
                        event.getDrops().clear();
                        subject.setHealth((double)Fall.HealthTarget.get(subject).intValue());
                    }
                    Fall.HealthTarget.remove(subject);
                    Fall.BeingPunished.remove(subject);
                }
                if (Fall.BeingPunished.get(subject) == Fall.Punishments.CREEPER) {
                    event.getEntity().getLastDamageCause().getEntity().remove();
                    Fall.BeingPunished.remove(subject);
                }
                if (Fall.BeingPunished.get(subject) == Fall.Punishments.FREEZE) {
                    Fall.BeingPunished.remove(subject);
                }
                if (Fall.BeingPunished.get(subject) == Fall.Punishments.HOUNDS) {
                    this.log.info(event.getEntity().getLastDamageCause().getEntity().toString());
                    event.getEntity().getLastDamageCause().getEntity().remove();
                    Fall.BeingPunished.remove(subject);
                }
                if (Fall.BeingPunished.get(subject) == Fall.Punishments.INVOID) {
                    Fall.BeingPunished.remove(subject);
                }
                if (Fall.BeingPunished.get(subject) == Fall.Punishments.SLOW) {
                    subject.setWalkSpeed(0.2f);
                }
                if (Fall.BeingPunished.get(subject) == Fall.Punishments.DRUNK) {
                    subject.removePotionEffect(PotionEffectType.CONFUSION);
                    Fall.HealthTarget.remove(subject);
                    Fall.BeingPunished.remove(subject);
                }
                if (Fall.BeingPunished.get(subject) == Fall.Punishments.BLIND) {
                    subject.removePotionEffect(PotionEffectType.BLINDNESS);
                    Fall.HealthTarget.remove(subject);
                    Fall.BeingPunished.remove(subject);
                }
                if (Fall.BeingPunished.get(subject) == Fall.Punishments.STARVE) {
                    subject.removePotionEffect(PotionEffectType.HUNGER);
                    Fall.HealthTarget.remove(subject);
                    Fall.BeingPunished.remove(subject);
                }
                if (Fall.BeingPunished.get(subject) == Fall.Punishments.POISON) {
                    subject.removePotionEffect(PotionEffectType.POISON);
                    Fall.HealthTarget.remove(subject);
                    Fall.BeingPunished.remove(subject);
                }
            } else {
                Fall.BeingPunished.remove(subject);
            }
        }
    }

    public HandlerList getHandlers() {
        return null;
    }
}

