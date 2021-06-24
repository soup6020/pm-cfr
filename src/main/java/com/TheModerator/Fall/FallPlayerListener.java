/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.block.Block
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Creeper
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.block.BlockPlaceEvent
 *  org.bukkit.event.player.AsyncPlayerChatEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerMoveEvent
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.util.Vector
 */
package com.TheModerator.Fall;

import java.util.Date;
import java.util.Random;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class FallPlayerListener
implements Listener {
    static Random rndgen = new Random(1264453L);
    Logger log = Logger.getLogger("Minecraft");
    Fall master;

    FallPlayerListener(Fall plugin) {
        this.master = plugin;
    }

    public static String getStrDirection(Player player) {
        double rot = (player.getLocation().getYaw() - 90.0f) % 360.0f;
        if (rot < 0.0) {
            rot += 360.0;
        }
        return FallPlayerListener.getDirection(rot);
    }

    private static String getDirection(double rot) {
        if (0.0 <= rot && rot < 22.5) {
            return "North";
        }
        if (22.5 <= rot && rot < 67.5) {
            return "Northeast";
        }
        if (67.5 <= rot && rot < 112.5) {
            return "East";
        }
        if (112.5 <= rot && rot < 157.5) {
            return "Southeast";
        }
        if (157.5 <= rot && rot < 202.5) {
            return "South";
        }
        if (202.5 <= rot && rot < 247.5) {
            return "Southwest";
        }
        if (247.5 <= rot && rot < 292.5) {
            return "West";
        }
        if (292.5 <= rot && rot < 337.5) {
            return "Northwest";
        }
        if (337.5 <= rot && rot < 360.0) {
            return "North";
        }
        return null;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (Fall.BeingPunished.containsKey(event.getPlayer())) {
            if (Fall.BeingPunished.get(event.getPlayer()) == Fall.Punishments.BABBLE) {
                event.setMessage(ChatColor.MAGIC + event.getMessage() + rndgen.nextInt(10));
            }
            if (Fall.BeingPunished.get(event.getPlayer()) == Fall.Punishments.MUTE) {
                event.setCancelled(true);
            }
            if (Fall.BeingPunished.get(event.getPlayer()) == Fall.Punishments.ARAB) {
                if (event.getMessage().contains("</string>")) {
                    event.setMessage(event.getMessage().replace("</string>", "").replace("<string xmlns=\"http://schemas.microsoft.com/2003/10/Serialization/\">", ""));
                    return;
                }
                Comms communications = new Comms(this.master);
                communications.TranslateChat(event.getPlayer(), event.getMessage(), "ar");
                event.setCancelled(true);
            }
            if (Fall.BeingPunished.get(event.getPlayer()) == Fall.Punishments.METRO) {
                if (event.getMessage().contains("</string>")) {
                    event.setMessage(event.getMessage().replace("</string>", "").replace("<string xmlns=\"http://schemas.microsoft.com/2003/10/Serialization/\">", ""));
                    return;
                }
                int lang = 0 + (int)(Math.random() * 11.0);
                String to = "en";
                switch (lang) {
                    case 0: {
                        to = "he";
                        break;
                    }
                    case 1: {
                        to = "ht";
                        break;
                    }
                    case 2: {
                        to = "tlh";
                        break;
                    }
                    case 3: {
                        to = "es";
                        break;
                    }
                    case 4: {
                        to = "fr";
                        break;
                    }
                    case 5: {
                        to = "ar";
                        break;
                    }
                    case 6: {
                        to = "no";
                        break;
                    }
                    case 7: {
                        to = "lt";
                        break;
                    }
                    case 8: {
                        to = "fr";
                        break;
                    }
                    case 9: {
                        to = "cy";
                        break;
                    }
                    case 10: {
                        to = "fa";
                        break;
                    }
                    case 11: {
                        to = "ro";
                        break;
                    }
                    case 12: {
                        to = "ru";
                        break;
                    }
                    case 13: {
                        to = "it";
                    }
                }
                Comms communications = new Comms(this.master);
                communications.TranslateChat(event.getPlayer(), event.getMessage(), to);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (Fall.BeingPunished.containsKey(event.getPlayer())) {
            int rnd;
            PotionEffect pe;
            Player subject;
            if (Fall.BeingPunished.get(event.getPlayer()) == Fall.Punishments.IDTHEFT) {
                char[] chars = "abcdefghijklmnopqrstuvwxyz1234567890!\u00a3$%^&*()[];'#,./{}:@~<>?'".toCharArray();
                StringBuilder sb = new StringBuilder();
                Random random = new Random();
                int i = 0;
                while (i < 8) {
                    char c = chars[random.nextInt(chars.length)];
                    sb.append(c);
                    ++i;
                }
                event.getPlayer().setDisplayName(sb.toString());
            }
            if (Fall.BeingPunished.get(event.getPlayer()) == Fall.Punishments.INFALL && event.getTo().getBlockY() < 3 + event.getPlayer().getWorld().getHighestBlockYAt(event.getPlayer().getLocation().getBlockX(), event.getPlayer().getLocation().getBlockZ())) {
                Location lc = event.getPlayer().getLocation();
                Vector vel = event.getPlayer().getVelocity();
                lc.setY(event.getPlayer().getLocation().getY() + 50.0);
                event.getPlayer().teleport(lc);
                event.getPlayer().setVelocity(vel);
            }
            if (Fall.BeingPunished.get(subject = event.getPlayer()) == Fall.Punishments.BLIND) {
                pe = new PotionEffect(PotionEffectType.BLINDNESS, 999999, 2);
                subject.addPotionEffect(pe);
            }
            if (Fall.BeingPunished.get(subject) == Fall.Punishments.DRUNK) {
                pe = new PotionEffect(PotionEffectType.CONFUSION, 999999, 2);
                subject.addPotionEffect(pe);
            }
            if (Fall.BeingPunished.get(subject) == Fall.Punishments.STARVE) {
                pe = new PotionEffect(PotionEffectType.HUNGER, 999999, 2);
                subject.addPotionEffect(pe);
            }
            if (Fall.BeingPunished.get(subject) == Fall.Punishments.SLOW) {
                pe = new PotionEffect(PotionEffectType.SLOW, 999999, 2);
                subject.addPotionEffect(pe);
            }
            if (Fall.BeingPunished.get(subject) == Fall.Punishments.POISON) {
                pe = new PotionEffect(PotionEffectType.POISON, 999999, 2);
                subject.addPotionEffect(pe);
            }
            if (Fall.BeingPunished.get(subject) == Fall.Punishments.BRITTLE && (double)subject.getFallDistance() > 0.001) {
                subject.setFallDistance(100.0f);
                Fall.BeingPunished.remove(subject);
            }
            if (Fall.BeingPunished.get(event.getPlayer()) == Fall.Punishments.PARANOIA && (rnd = rndgen.nextInt(Fall.configSettings.ParanoiaProbability)) == 1) {
                Location LC = subject.getLocation();
                int distance = rndgen.nextInt(10);
                if (FallPlayerListener.getStrDirection(subject) == "North") {
                    LC.add(0.0, 0.0, (double)distance);
                } else if (FallPlayerListener.getStrDirection(subject) == "Northeast") {
                    LC = LC.add((double)distance, 0.0, (double)distance);
                } else if (FallPlayerListener.getStrDirection(subject) == "Northwest") {
                    LC = LC.add((double)(-distance), 0.0, (double)distance);
                } else if (FallPlayerListener.getStrDirection(subject) == "South") {
                    LC = LC.add(0.0, 0.0, (double)(-distance));
                } else if (FallPlayerListener.getStrDirection(subject) == "Southeast") {
                    LC = LC.add((double)distance, 0.0, (double)(-distance));
                } else if (FallPlayerListener.getStrDirection(subject) == "Southwest") {
                    LC = LC.add((double)(-distance), 0.0, (double)(-distance));
                } else if (FallPlayerListener.getStrDirection(subject) == "West") {
                    LC = LC.add((double)(-distance), 0.0, 0.0);
                } else if (FallPlayerListener.getStrDirection(subject) == "East") {
                    LC = LC.add((double)distance, 0.0, 0.0);
                }
                event.getPlayer().playSound(LC, FallPlayerListener.randomEnum(Sound.class), 1.0f, 1.0f);
            }
            if (Fall.BeingPunished.get(event.getPlayer()) == Fall.Punishments.INVOID || Fall.BeingPunished.get(subject) == Fall.Punishments.POTATO) {
                event.getPlayer().setHealth(20.0);
                Location location = event.getPlayer().getLocation();
                if (location.getBlockY() > -10) {
                    location.setY(-10.0);
                    event.getPlayer().teleport(location);
                }
            }
            if (Fall.BeingPunished.get(event.getPlayer()) == Fall.Punishments.FREEZE) {
                event.setTo(event.getFrom());
            }
        } else {
            Fall.BeingPunished.remove(event.getPlayer());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (Fall.BeingPunished.containsKey(event.getPlayer())) {
            if (Fall.BeingPunished.get(event.getPlayer()) == Fall.Punishments.LAVABLOCK) {
                event.setCancelled(true);
                event.getPlayer().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.LAVA);
                Fall.BeingPunished.remove(event.getPlayer());
            } else if (Fall.BeingPunished.get(event.getPlayer()) == Fall.Punishments.FREEZE || Fall.BeingPunished.get(event.getPlayer()) == Fall.Punishments.BLOCK) {
                event.getPlayer().sendMessage(ChatColor.RED + "Some weird force stops you from doing that :/");
                event.setCancelled(true);
            } else if (Fall.BeingPunished.get(event.getPlayer()) == Fall.Punishments.EXBLOCK) {
                event.getPlayer().getWorld().createExplosion(event.getPlayer().getLocation(), 4.0f);
                Fall.BeingPunished.remove(event.getPlayer());
                this.log.info("EXPLODE!");
            }
            if (Fall.BeingPunished.get(event.getPlayer()) == Fall.Punishments.FREEZE) {
                event.setCancelled(true);
            }
        }
    }

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = rndgen.nextInt(((Enum[])clazz.getEnumConstants()).length);
        return (T)((Enum[])clazz.getEnumConstants())[x];
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        Player subject;
        if (Fall.autopunishData.Enabled && Fall.autopunishData.ExplodeBlock == event.getBlock().getType().getId() && !event.getPlayer().hasPermission("Punish.EXEMPT")) {
            subject = event.getPlayer();
            subject.getWorld().createExplosion(subject.getLocation(), 0.0f);
            subject.sendMessage(ChatColor.RED + "Do not place banned blocks");
            subject.damage(20.0);
            event.getBlock().setType(Material.AIR);
            event.setCancelled(true);
        }
        if (Fall.autopunishData.Enabled && Fall.autopunishData.CreeperBlock == event.getBlock().getType().getId() && !event.getPlayer().hasPermission("Punish.EXEMPT")) {
            subject = event.getPlayer();
            subject.getWorld().createExplosion(subject.getLocation(), 0.0f);
            subject.sendMessage(ChatColor.RED + "Do not place banned blocks");
            event.setCancelled(true);
            Location LC2 = subject.getLocation();
            Creeper cchasing = (Creeper)subject.getWorld().spawnEntity(LC2, EntityType.CREEPER);
            cchasing.setPowered(true);
            cchasing.damage(1.0, (Entity)subject);
            Fall.BeingPunished.put(subject, Fall.Punishments.CREEPER);
        }
        if (Fall.autopunishData.Enabled && Fall.autopunishData.FallBlock == event.getBlock().getType().getId() && !event.getPlayer().hasPermission("Punish.EXEMPT")) {
            subject = event.getPlayer();
            subject.getWorld().createExplosion(subject.getLocation(), 0.0f);
            subject.sendMessage(ChatColor.RED + "Do not place banned blocks");
            subject.chat("ARRRRRRRRRRRRGGGGGGHHHHHH...");
            Location location = null;
            location = subject.getLocation();
            location.setY(300.0);
            subject.teleport(location);
            event.setCancelled(true);
        }
        if (Fall.autopunishData.Enabled && Fall.autopunishData.AnvilBlock == event.getBlock().getType().getId() && !event.getPlayer().hasPermission("Punish.EXEMPT")) {
            subject = event.getPlayer();
            Fall.BeingPunished.put(subject, Fall.Punishments.FREEZE);
            subject.sendMessage("What the?");
            Location playerLocation = subject.getLocation();
            double x = playerLocation.getBlockX();
            double y = playerLocation.getBlockY() + 15;
            double z = playerLocation.getBlockZ();
            Location loc = new Location(playerLocation.getWorld(), x, y, z);
            Block block = subject.getWorld().getBlockAt(loc);
            block.setType(Material.ANVIL);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (Fall.BeingPunished.containsKey(event.getPlayer())) {
            Player subject;
            if (Fall.autopunishData.Enabled && Fall.autopunishData.LavaBucket && event.getMaterial() == Material.LAVA_BUCKET && !event.getPlayer().hasPermission("Punish.EXEMPT")) {
                subject = event.getPlayer();
                subject.chat("ARRRRRRRRRRRRGGGGGGHHHHHH...");
                Location location = null;
                location = subject.getLocation();
                location.setY(300.0);
                subject.teleport(location);
                subject.sendMessage(ChatColor.RED + "Using a lava bucket is banned on this server");
                event.setCancelled(true);
            }
            if (Fall.autopunishData.Enabled && Fall.autopunishData.FlintAndSteel && event.getMaterial() == Material.FLINT_AND_STEEL && !event.getPlayer().hasPermission("Punish.EXEMPT")) {
                subject = event.getPlayer();
                subject.setFireTicks(50000);
                subject.sendMessage(ChatColor.RED + "Placing fire is banned on this server");
                event.setCancelled(true);
            }
            if (Fall.autopunishData.Enabled && Fall.autopunishData.WaterBucket && event.getMaterial() == Material.WATER_BUCKET && !event.getPlayer().hasPermission("Punish.EXEMPT")) {
                subject = event.getPlayer();
                subject.getWorld().strikeLightningEffect(subject.getLocation());
                subject.damage(20.0);
                subject.sendMessage(ChatColor.RED + "Using a Water bucket is banned on this server");
                event.setCancelled(true);
            }
            if (Fall.BeingPunished.get(event.getPlayer()) == Fall.Punishments.LAVABLOCK) {
                event.setCancelled(true);
                event.getPlayer().getWorld().getBlockAt(event.getClickedBlock().getLocation()).setType(Material.LAVA);
                Fall.BeingPunished.remove(event.getPlayer());
            } else if (Fall.BeingPunished.get(event.getPlayer()) == Fall.Punishments.FREEZE || Fall.BeingPunished.get(event.getPlayer()) == Fall.Punishments.BLOCK) {
                event.getPlayer().sendMessage(ChatColor.RED + "Some weird force stops you from doing that :/");
                event.setCancelled(true);
            }
            if (Fall.BeingPunished.get(event.getPlayer()) == Fall.Punishments.FREEZE) {
                event.setCancelled(true);
            }
        }
    }

    public HandlerList getHandlers() {
        return null;
    }
}

