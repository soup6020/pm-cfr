/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.BlockVector2D
 *  com.sk89q.worldedit.bukkit.WorldEditPlugin
 *  org.bukkit.ChatColor
 *  org.bukkit.Effect
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.TreeType
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.ConsoleCommandSender
 *  org.bukkit.entity.Arrow
 *  org.bukkit.entity.Creature
 *  org.bukkit.entity.Creeper
 *  org.bukkit.entity.Enderman
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Fireball
 *  org.bukkit.entity.IronGolem
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Villager
 *  org.bukkit.entity.Wolf
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerTeleportEvent$TeleportCause
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.projectiles.ProjectileSource
 *  org.bukkit.util.Vector
 */
package com.TheModerator.Fall;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

public class Fall
extends JavaPlugin {
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date date = new Date();
    boolean lethal;
    int FallHeight;
    public static Player falling;
    public static Location nukeLoc;
    public static String MOTD;
    Logger log = Logger.getLogger("Minecraft");
    int MaxTelDistance;
    int MinTelDistance;
    boolean RemoteDebug;
    WorldEditPlugin worldEdit = null;
    long lastTick;
    int lagCheckID;
    int ticklength = 50;
    public static HashMap<Location, Material> Restoration;
    public static HashMap<String, Integer> Tasks;
    public static HashMap<Player, Location> OldLocations;
    public static HashMap<Player, Punishments> BeingPunished;
    public static HashMap<Player, Integer> HealthTarget;
    public static HashMap<Punishments, Integer> PunishmentUsage;
    public static HashMap<String, VirtualPlayer> PlayerRestore;
    public static HashMap<String, Long> LastAutoIssue;
    Random rndgen = new Random(12649835L);
    String ServIP;
    public static boolean autopunish;
    ExceptionHandler eh = new ExceptionHandler();
    public static boolean noDrops;
    static Random rnddgen;
    int[] taskID;
    long SecondStart = 0L;
    int timesdone = 0;
    int punishmentsdone = 0;

    static {
        Restoration = new HashMap();
        Tasks = new HashMap();
        OldLocations = new HashMap();
        BeingPunished = new HashMap();
        HealthTarget = new HashMap();
        PunishmentUsage = new HashMap();
        PlayerRestore = new HashMap();
        LastAutoIssue = new HashMap();
        noDrops = false;
        rnddgen = new Random(1264453L);
    }

    public void startLagCheck() {
        if (configSettings.FailsafeLagMargin == 0 || !configSettings.Failsafe) {
            this.log.warning(ChatColor.DARK_GREEN + "[Punishmental] Lag Checker Disabled");
            return;
        }
        if (this.lagCheckID == 0) {
            this.log.warning(ChatColor.GREEN + "[Punishmental] Starting lag checker");
            this.lastTick = 0L;
            this.lagCheckID = this.getServer().getScheduler().scheduleAsyncRepeatingTask((Plugin) this, new Runnable() {

                @Override
                public void run() {
                    Fall.this.CheckLag();
                }
            }, 1L, 1L);
            this.log.warning(ChatColor.GREEN + "[Punishmental] Lag cheker running");
        } else {
            this.log.warning(ChatColor.GREEN + "[Punishmental] Restarting Lag Cheker");
            this.stopLagCheck();
            this.startLagCheck();
        }
    }

    public void stopLagCheck() {
        this.log.warning(ChatColor.DARK_GREEN + "[Punishmental] Stopping Lag Cheker");
        this.getServer().getScheduler().cancelTask(this.lagCheckID);
        this.lagCheckID = 0;
        this.lastTick = 0L;
    }

    public void CheckLag() {
        if (System.currentTimeMillis() > this.lastTick + (long) this.ticklength + (long) configSettings.FailsafeLagMargin) {
            this.activateFailsafe();
        }
        this.lastTick = System.currentTimeMillis();
    }

    public void UpdateDB(boolean increment) {
    }

    public void onDisable() {
        this.UpdateDB(false);
        this.log.info("[Punishmental]: COMMANDS DISABLED");
    }

    public void onEnable() {
        this.ServIP = "N/A";
        Punishments[] punishmentsArray = Punishments.values();
        int n = punishmentsArray.length;
        int n2 = 0;
        while (n2 < n) {
            Punishments punishment = punishmentsArray[n2];
            PunishmentUsage.put(punishment, 0);
            ++n2;
        }
        this.getServer().getPluginManager().registerEvents((Listener) new FallPlayerListener(this), (Plugin) this);
        this.getServer().getPluginManager().registerEvents((Listener) new FallEntityListener(), (Plugin) this);
        this.saveConfig();
        this.reloadConfig();
        this.getConfig();
        this.getConfig().options().copyDefaults(true);
        this.UpdateDB(false);
        this.log.info(this.getDescription().getVersion());

        this.MaxTelDistance = this.getConfig().getInt("Teleport.MaxDistance");
        this.MinTelDistance = this.getConfig().getInt("Teleport.MinDistance");
        this.FallHeight = this.getConfig().getInt("Fall.BlockHeight");
        this.RemoteDebug = this.getConfig().getBoolean("RemoteDebug.Enabled");
        autopunishData.Enabled = this.getConfig().getBoolean("Autopunish.Enabled");
        autopunishData.ExplodeBlock = this.getConfig().getInt("Autopunish.Explode");
        autopunishData.LavaBucket = this.getConfig().getBoolean("Autopunish.LavaBucket");
        autopunishData.WaterBucket = this.getConfig().getBoolean("Autopunish.WaterBucket");
        autopunishData.FlintAndSteel = this.getConfig().getBoolean("Autopunish.FlintAndSteel");
        autopunishData.FallBlock = this.getConfig().getInt("Autopunish.Fall");
        autopunishData.AnvilBlock = this.getConfig().getInt("Autopunish.Anvil");
        configSettings.ParanoiaProbability = this.getConfig().getInt("Paranoia.Probability");
        configSettings.HoleDepth = this.getConfig().getInt("Hole.Depth");
        configSettings.SurroundDistance = this.getConfig().getInt("Surround.FetchDistance");
        configSettings.ErrorReport = this.getConfig().getBoolean("ErrorReporting.Enable");
        configSettings.HostileRange = this.getConfig().getInt("Hostile.Range");
        configSettings.RemoveCreative = this.getConfig().getBoolean("Defaults.RemoveCreative");
        configSettings.Failsafe = this.getConfig().getBoolean("Failsafe.Enabled");
        configSettings.FailsafeTrigger = this.getConfig().getInt("Failsafe.Trigger");
        configSettings.FailsafeLagMargin = this.getConfig().getInt("Failsafe.LagMargin");
        configSettings.UsePunishmentLevels = this.getConfig().getBoolean("Permissions.UsePunishmentLevels");
        this.startLagCheck();
        if (autopunishData.Enabled) {
            ConsoleCommandSender console = this.getServer().getConsoleSender();
            console.sendMessage(ChatColor.RED + "WARNING! AUTOMATIC PUNISHMENTS ARE *ON*. PUNISHMENTS CAN BE ISSUED WITHOUT COMMANDS");
        }
        this.saveConfig();
        this.reloadConfig();

        this.log.info("[Punishmental]: Commands READY");
    }

    public void AddRestoreSector(Location loc, int distance) {
        int x = 0;
        while (x < distance) {
            int y = 0;
            while (y < distance) {
                int z = 0;
                while (z < distance) {
                    int newX = loc.getBlockX() + (x - distance / 2);
                    int newY = loc.getBlockY() + (y - distance / 2);
                    int newZ = loc.getBlockZ() + (z - distance / 2);
                    this.AddtoRestore(new Location(loc.getWorld(), (double) newX, (double) newY, (double) newZ));
                    ++z;
                }
                ++y;
            }
            ++x;
        }
    }

    public void RestoreDamaged(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.GRAY + "Clearing Up " + Restoration.size() + " Blocks, Please wait...");
        Player subject = (Player) sender;
        for (Map.Entry<Location, Material> entry : Restoration.entrySet()) {
            entry.getKey().getBlock().setType(entry.getValue());
        }
        Restoration.clear();
        sender.sendMessage(ChatColor.GREEN + "Clear up finished");
    }

    public void ExecuteFall(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        subject.chat("ARRRRRRRRRRRRGGGGGGHHHHHH...");
        Location location = null;
        location = subject.getLocation();
        location.setY((double) this.FallHeight);
        BeingPunished.put(subject, Punishments.FALL);
        subject.teleport(location);
        sender.sendMessage(ChatColor.GREEN + "Punishment Dispensed Successfuly");
    }

    public void ExecuteFreeze(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        subject.setWalkSpeed(0.01f);
        BeingPunished.put(subject, Punishments.FREEZE);
        sender.sendMessage(ChatColor.GREEN + "Player Frozen");
    }

    public void ExecuteTeleport(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        int xtel = (int) (this.rndgen.nextDouble() * (double) (this.MaxTelDistance - this.MinTelDistance) + (double) this.MinTelDistance);
        int ztel = (int) (this.rndgen.nextDouble() * (double) (this.MaxTelDistance - this.MinTelDistance) + (double) this.MinTelDistance);
        Location location = null;
        location = subject.getLocation();
        location.add((double) xtel, 0.0, (double) ztel);
        location = subject.getWorld().getHighestBlockAt(location).getLocation();
        subject.teleport(location);
        sender.sendMessage(ChatColor.GREEN + "Punishment Dispensed Successfuly");
    }

    public void AddtoRestore(Location tostore) {
        if (Restoration.containsKey(tostore)) {
            this.log.info(tostore.toString());
            this.log.info("Attempted to store a block where a block was already stored. Storing of the block that was already stored canceled.");
            return;
        }
        Restoration.put(tostore, tostore.getBlock().getType());
    }

    public void ExecuteExplode(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        this.AddRestoreSector(subject.getLocation(), 9);
        subject.getWorld().createExplosion((double) subject.getLocation().getBlockX(), (double) subject.getLocation().getBlockY(), (double) subject.getLocation().getBlockZ(), 4.0f);
        sender.sendMessage(ChatColor.GREEN + "Punishment Dispensed Successfuly");
    }

    public void ExecuteBomb(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        Location loc = subject.getLocation();
        int xdistance = 25;
        int ydistance = 20;
        int zdistance = 25;
        int seperation = 4;
        float power = 8.0f;
        int x = 0;
        this.AddRestoreSector(subject.getLocation(), 100);
        noDrops = true;
        while (x < xdistance) {
            int y = 0;
            while (y < ydistance) {
                int z = 0;
                while (z < zdistance) {
                    int newX = loc.getBlockX() + (x - xdistance / 2);
                    int newY = loc.getBlockY() + (y - ydistance / 2);
                    int newZ = loc.getBlockZ() + (z - zdistance / 2);
                    subject.getWorld().createExplosion((double) newX, (double) newY, (double) newZ, power, true, true);
                    z += seperation;
                }
                y += seperation;
            }
            x += seperation;
        }
        noDrops = false;
        sender.sendMessage(ChatColor.GRAY + "Server has explosion buffer information remaining... Reloading...");
        this.getServer().reload();
        sender.sendMessage(ChatColor.GREEN + "Punishment Dispensed Successfuly");
    }

    public void ExecuteNuke(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        sender.sendMessage(ChatColor.GRAY + "Setting clear-up buffer");
        this.AddRestoreSector(subject.getLocation(), 200);
        sender.sendMessage(ChatColor.GRAY + "SENDING EXPLOSON COMMAND");
        sender.sendMessage(ChatColor.GRAY + "The server will now crash until the detonation is complete.");
        sender.sendMessage(ChatColor.GRAY + "Commencing...");
        noDrops = true;
        subject.getWorld().createExplosion((double) subject.getLocation().getBlockX(), (double) subject.getLocation().getBlockY(), (double) subject.getLocation().getBlockZ(), 800.0f, true, true);
        noDrops = false;
        sender.sendMessage(ChatColor.GREEN + "Detonation Complete. Punishment Dispensed Successfuly");
        sender.sendMessage(ChatColor.GRAY + "Server has explosion bufferes present... Reloading...");
        this.getServer().reload();
    }

    public void ExecuteEnd(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        subject.setHealth(0.0);
        this.ExecuteStop(sender, args);
        Player[] playerArray = this.getServer().getOnlinePlayers().toArray(new Player[0]);
        int n = playerArray.length;
        int n2 = 0;
        while (n2 < n) {
            Player p = playerArray[n2];
            subject.showPlayer(p);
            ++n2;
        }
        BeingPunished.remove(subject);
    }

    public void ExecuteCreeper(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        Location LC2 = subject.getLocation();
        Creeper cchasing = (Creeper) subject.getWorld().spawnEntity(LC2, EntityType.CREEPER);
        cchasing.setPowered(true);
        cchasing.damage(1.0, (Entity) subject);
        BeingPunished.put(subject, Punishments.CREEPER);
        sender.sendMessage(ChatColor.GREEN + "Punishment Dispensed Successfuly");
    }

    public void ExecuteStrike(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        subject.getWorld().strikeLightning(subject.getLocation());
        sender.sendMessage(ChatColor.GREEN + "Punishment Dispensed Successfuly");
        subject.setHealth((double) HealthTarget.get(subject).intValue());
        BeingPunished.put(subject, Punishments.STRIKE);
    }

    public void ExecuteInfall(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        subject.chat("ARRRRRRRRRRRRGGGGGGHHHHHH...");
        Location location = null;
        location = subject.getLocation();
        location.setY((double) this.FallHeight);
        BeingPunished.put(subject, Punishments.INFALL);
        OldLocations.put(subject, subject.getLocation());
        subject.teleport(location);
        sender.sendMessage(ChatColor.GREEN + "Punishment Dispensed Successfuly");
    }

    public void ExecuteIgnite(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        subject.setFireTicks(99999);
        BeingPunished.put(subject, Punishments.BURN);
        sender.sendMessage(ChatColor.GREEN + "Punishment Dispensed Successfuly");
    }

    public void ExecuteVoid(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        Location location = null;
        location = subject.getLocation();
        location.setY(-10.0);
        subject.teleport(location);
    }

    public void ExecuteHailFire(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        Location location = null;
        location = subject.getLocation();
        location = location.add(0.0, 30.0, 0.0);
        this.log.info(subject.getLocation().toString());
        location.setPitch(90.0f);
        Fireball fb = (Fireball) subject.getWorld().spawn(location, Fireball.class);
        fb.setShooter((ProjectileSource) subject);
        fb.setFireTicks(9999999);
        fb.setIsIncendiary(true);
        sender.sendMessage(ChatColor.GREEN + "Punishment Dispensed Successfuly");
    }

    public void ExecutefExplode(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        subject.getWorld().createExplosion(subject.getLocation(), 0.0f);
        sender.sendMessage(ChatColor.GREEN + "Punishment Dispensed Successfuly");
    }

    public void ExecutefStrike(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        subject.getWorld().strikeLightningEffect(subject.getLocation());
        sender.sendMessage(ChatColor.GREEN + "Punishment Dispensed Successfuly");
    }

    public void ExecuteBlind(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        PotionEffect pe = new PotionEffect(PotionEffectType.BLINDNESS, 999999, 2);
        subject.addPotionEffect(pe);
        sender.sendMessage(ChatColor.GREEN + "Punishment Dispensed Successfuly");
    }

    public void ExecuteDrunk(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        PotionEffect pe = new PotionEffect(PotionEffectType.CONFUSION, 999999, 2);
        subject.addPotionEffect(pe);
        sender.sendMessage(ChatColor.GREEN + "Punishment Dispensed Successfuly");
    }

    public void ExecuteSlender(CommandSender sender, String[] args) {
        final Player subject = this.getServer().getPlayer(args[1]);
        PotionEffect pe = new PotionEffect(PotionEffectType.BLINDNESS, 999999, 4);
        subject.addPotionEffect(pe);
        final Enderman slender = (Enderman) subject.getWorld().spawnEntity(subject.getLocation(), EntityType.ENDERMAN);
        subject.sendMessage("Sl-enderman is behind you...");
        if (Tasks.containsKey(args[1])) {
            this.getServer().getScheduler().cancelTask(Tasks.get(args[1]).intValue());
            Tasks.remove(args[1]);
        }
        int taskID = this.getServer().getScheduler().scheduleAsyncRepeatingTask((Plugin) this, new Runnable() {

            @Override
            public void run() {
                Location LC2 = subject.getLocation();
                if (Fall.getStrDirection(subject) == "North") {
                    LC2.add(0.0, 0.0, 2.0);
                } else if (Fall.getStrDirection(subject) == "Northeast") {
                    LC2 = LC2.add(2.0, 0.0, 2.0);
                } else if (Fall.getStrDirection(subject) == "Northwest") {
                    LC2 = LC2.add(-2.0, 0.0, 2.0);
                } else if (Fall.getStrDirection(subject) == "South") {
                    LC2 = LC2.add(0.0, 0.0, -2.0);
                } else if (Fall.getStrDirection(subject) == "Southeast") {
                    LC2 = LC2.add(2.0, 0.0, -2.0);
                } else if (Fall.getStrDirection(subject) == "Southwest") {
                    LC2 = LC2.add(-2.0, 0.0, -2.0);
                } else if (Fall.getStrDirection(subject) == "West") {
                    LC2 = LC2.add(-2.0, 0.0, 0.0);
                } else if (Fall.getStrDirection(subject) == "East") {
                    LC2 = LC2.add(2.0, 0.0, 0.0);
                }
                slender.teleport(LC2);
                subject.playEffect(LC2, Effect.ENDER_SIGNAL, 1);
                slender.setFireTicks(0);
                Fall.this.log.info("Slender teleported");
            }
        }, 100L, 100L);
        Tasks.put(args[1], taskID);
        sender.sendMessage(ChatColor.GREEN + "Punishment Dispensed Successfuly");
    }

    public void ExecuteStarve(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        subject.setFoodLevel(0);
        PotionEffect pe = new PotionEffect(PotionEffectType.HUNGER, 999999, 2);
        subject.addPotionEffect(pe);
        sender.sendMessage(ChatColor.GREEN + "Punishment Dispensed Successfuly");
    }

    public void ExecuteSlow(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        subject.setWalkSpeed(0.02f);
        sender.sendMessage(ChatColor.GREEN + "Punishment Dispensed Successfuly");
    }

    public void ExecutePoison(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        PotionEffect pe = new PotionEffect(PotionEffectType.POISON, 999999, 2);
        subject.addPotionEffect(pe);
        sender.sendMessage(ChatColor.GREEN + "Punishment Dispensed Successfuly");
    }

    public void ExecuteInVoid(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        OldLocations.put(subject, subject.getLocation());
        Location location = null;
        location = subject.getLocation();
        location.setY(-10.0);
        subject.teleport(location);
        sender.sendMessage(ChatColor.GREEN + "Player is infinately in the void; to undo use /p stop " + subject.getDisplayName());
    }

    public void ExecuteLavablock(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        sender.sendMessage(ChatColor.GREEN + "Next block mined will produce lava. To Undo, use /p stop " + subject.getDisplayName());
    }

    public void ExecuteExblock(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        sender.sendMessage(ChatColor.GREEN + "Next block mined will explode. To Undo, use /p stop " + subject.getDisplayName());
    }

    public void ExecuteHole(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        Location location = null;
        Location permlocation = location = subject.getLocation();
        permlocation.setX((double) subject.getLocation().getBlockX() + 0.5);
        permlocation.setY((double) subject.getLocation().getBlockY());
        permlocation.setZ((double) subject.getLocation().getBlockZ() + 0.5);
        subject.teleport(permlocation);
        int i = 0;
        while (i < configSettings.HoleDepth) {
            Restoration.put(subject.getLocation().getBlock().getRelative(BlockFace.DOWN, i).getLocation(), subject.getLocation().getBlock().getRelative(BlockFace.DOWN, i).getType());
            Restoration.put(subject.getLocation().getBlock().getRelative(BlockFace.DOWN, 2).getLocation(), subject.getLocation().getBlock().getRelative(BlockFace.DOWN, 2).getType());
            subject.getLocation().getBlock().getRelative(BlockFace.DOWN, i).setType(Material.AIR);
            subject.getLocation().getBlock().getRelative(BlockFace.DOWN, 1).setType(Material.AIR);
            ++i;
            location = location.subtract(0.0, 1.0, 0.0);
        }
        sender.sendMessage(ChatColor.GREEN + "Punishment Dispensed Successfuly");
    }

    public void ExecuteParanoia(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.GREEN + "Punishment Dispensed Successfuly");
    }

    public void ExecuteStrip(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        subject.getInventory().clear();
        sender.sendMessage(ChatColor.GREEN + "Punishment Dispensed Successfuly");
    }

    public void ExecuteNether(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        Location loc = subject.getLocation();
        if (this.getServer().getWorlds().size() <= 1) {
            sender.sendMessage(ChatColor.RED + "Nether not enabled on this server :(");
            return;
        }
        loc.setWorld((World) this.getServer().getWorlds().get(1));
        loc.setY(33.0);
        Boolean okay = false;
        int step = 0;
        while (!okay.booleanValue()) {
            loc = loc.add(1.0, 0.0, 0.0);
            ++step;
            if (loc.getBlock().getType() == Material.AIR & loc.add(0.0, 1.0, 0.0).getBlock().getType() == Material.AIR) {
                okay = true;
                continue;
            }
            if (step <= 100) continue;
            sender.sendMessage(ChatColor.RED + "Could not find an adequate spawn location :(");
            sender.sendMessage(ChatColor.RED + "Please retry once the player has moved");
            return;
        }
        subject.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
        sender.sendMessage(ChatColor.GREEN + "Punishment Dispensed Successfuly");
    }

    public void ExecuteFloorPortal(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        Location loc = subject.getLocation();
        loc.getBlock().setType(Material.END_PORTAL);
        Location location = null;
        Location permlocation = location = subject.getLocation();
        permlocation.setX((double) subject.getLocation().getBlockX() + 0.5);
        permlocation.setY((double) subject.getLocation().getBlockY());
        permlocation.setZ((double) subject.getLocation().getBlockZ() + 0.5);
        subject.teleport(permlocation);
        int i = 0;
        Restoration.put(subject.getLocation().getBlock().getRelative(BlockFace.DOWN, 2).getLocation(), subject.getLocation().getBlock().getRelative(BlockFace.DOWN, 2).getType());
        while (i < 3) {
            Restoration.put(subject.getLocation().getBlock().getRelative(BlockFace.DOWN, i).getLocation(), subject.getLocation().getBlock().getRelative(BlockFace.DOWN, i).getType());
            subject.getLocation().getBlock().getRelative(BlockFace.DOWN, i).setType(Material.AIR);
            subject.getLocation().getBlock().getRelative(BlockFace.DOWN, 2).setType(Material.END_PORTAL);
            ++i;
            location = location.subtract(0.0, 1.0, 0.0);
        }
        sender.sendMessage(ChatColor.GREEN + "Punishment Dispensed Successfuly");
    }

    public void ExecuteChatTroll(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        int numofwords = args.length - 4;
        if (numofwords <= 0) {
            sender.sendMessage(ChatColor.RED + "Text to say not chosen. Please put it at the end of the command");
            return;
        }
        int i = 1;
        String message = "";
        while (i < numofwords) {
            message = String.valueOf(message) + " " + args[i + 4];
            ++i;
        }
        subject.chat(message);
        sender.sendMessage(ChatColor.GREEN + "Punishment succesfully ran");
    }

    public void ExecuteBabble(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.GREEN + "Punishment succesfully ran");
    }

    public void ExecuteFakeOp(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        subject.sendMessage(ChatColor.YELLOW + "You are now an OP!");
        sender.sendMessage(ChatColor.GREEN + "Punishment succesfully ran");
    }

    public void ExecuteStop(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        subject.resetPlayerTime();
        if (Tasks.containsKey(args[1])) {
            this.getServer().getScheduler().cancelTask(Tasks.get(args[1]).intValue());
        }
        if (OldLocations.containsKey(subject)) {
            subject.setFallDistance(0.0f);
            subject.teleport(OldLocations.get(subject));
            OldLocations.remove(subject);
            subject.setFallDistance(0.0f);
        }
        BeingPunished.remove(subject);
        HealthTarget.remove(subject);
        subject.setWalkSpeed(0.2f);
        subject.setCanPickupItems(true);
        Player[] playerArray = this.getServer().getOnlinePlayers().toArray(new Player[0]);
        int n = playerArray.length;
        int n2 = 0;
        while (n2 < n) {
            Player p = playerArray[n2];
            subject.showPlayer(p);
            p.showPlayer(subject);
            ++n2;
        }
        subject.removePotionEffect(PotionEffectType.HUNGER);
        subject.removePotionEffect(PotionEffectType.POISON);
        subject.removePotionEffect(PotionEffectType.SLOW);
        subject.removePotionEffect(PotionEffectType.BLINDNESS);
        subject.removePotionEffect(PotionEffectType.CONFUSION);
        subject.setDisplayName(subject.getName());
        sender.sendMessage(ChatColor.GREEN + "Subject cured of any punishments");
    }

    public void ExecuteLAG(CommandSender sender, String[] args) {
        final Player subject = this.getServer().getPlayer(args[1]);
        Location lc = subject.getLocation();
        if (180.0f < lc.getYaw()) {
            lc.setYaw(lc.getYaw() + 180.0f);
            subject.teleport(lc);
        } else {
            lc.setYaw(lc.getYaw() - 180.0f);
            subject.teleport(lc);
        }
        if (Tasks.containsKey(args[1])) {
            this.getServer().getScheduler().cancelTask(Tasks.get(args[1]).intValue());
            Tasks.remove(args[1]);
        }
        int taskid = this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin) this, new Runnable() {

            @Override
            public void run() {
                Location lc = subject.getLocation();
                if (359.0f < lc.getYaw()) {
                    lc.setYaw(lc.getYaw() + 1.0f);
                    subject.teleport(lc);
                } else {
                    lc.setYaw(lc.getYaw() - 358.0f);
                    subject.teleport(lc);
                }
            }
        }, 5L, 5L);
        Tasks.put(args[1], taskid);
        sender.sendMessage(ChatColor.GREEN + "Subject is Lagging");
    }

    public void ExecutePotate(CommandSender sender, String[] args) {
        final Player subject = this.getServer().getPlayer(args[1]);
        int taskid = this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin) this, new Runnable() {

            @Override
            public void run() {
                ItemStack is = new ItemStack(Material.POTATO);
                is.setType(Material.POTATO);
                is.setAmount(64);
                is.setDurability((short) 1);
                subject.getWorld().dropItemNaturally(subject.getLocation().add(0.0, 2.0, 0.0), is);
            }
        }, 1L, 1L);
        Tasks.put(args[1], taskid);
        subject.sendMessage("I'M POTATING!!!!!!!!!!!!!!11!!!!");
        sender.sendMessage(ChatColor.GREEN + "Glory to the potato, for thine is the starchy.");
    }

    public void ExecuteForceChoke(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        subject.sendMessage(ChatColor.RED + "An unknown force is choking you!");
        subject.setMaximumAir(60);
        subject.setRemainingAir(60);
        sender.sendMessage(ChatColor.GREEN + "Darth has deployed the force choke");
    }

    public void ExecuteUnaware(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        Player[] playerArray = this.getServer().getOnlinePlayers().toArray(new Player[0]);
        int n = playerArray.length;
        int n2 = 0;
        while (n2 < n) {
            Player p = playerArray[n2];
            subject.hidePlayer(p);
            ++n2;
        }
        subject.chat("Where did everybody go?");
        sender.sendMessage(ChatColor.GREEN + "Player no-longer sees other people (use /p stop to undo)");
    }

    public void ExecuteLevelDown(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        subject.setLevel(0);
        sender.sendMessage(ChatColor.GREEN + "Player reset to level 0");
    }

    public void ExecuteSurround(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        Random rg = new Random();
        Location playerLocation = subject.getLocation();
        double x = playerLocation.getBlockX();
        double y = playerLocation.getBlockY();
        double z = playerLocation.getBlockZ();
        World currentWorld = subject.getWorld();
        Location loc = new Location(currentWorld, x, y, z);
        loc.setX(loc.getX() + (double) (rg.nextInt(21) - 10));
        loc.setY(loc.getY());
        loc.setZ(loc.getZ() + (double) (rg.nextInt(21) - 10));
        List list = subject.getNearbyEntities((double) configSettings.SurroundDistance, (double) configSettings.SurroundDistance, (double) configSettings.SurroundDistance);
        int i = 0;
        if (list.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "There are no entities near the player. Punishment Failed. Plz Try again later");
            return;
        }
        for (Entity ent : subject.getNearbyEntities((double) configSettings.SurroundDistance, (double) configSettings.SurroundDistance, (double) configSettings.SurroundDistance)) {
            if (!(ent instanceof Creature)) continue;
            ++i;
            Creature mob = (Creature) ent;
            mob.teleport(subject.getLocation());
            mob.setTarget((LivingEntity) subject);
        }
        sender.sendMessage(new StringBuilder().append(ChatColor.GREEN + Integer.toString(i)).append(" Entities were teleported to the players location.").toString());
    }

    public void ExecuteHostile(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        Random rg = new Random();
        Location playerLocation = subject.getLocation();
        double x = playerLocation.getBlockX();
        double y = playerLocation.getBlockY();
        double z = playerLocation.getBlockZ();
        World currentWorld = subject.getWorld();
        Location loc = new Location(currentWorld, x, y, z);
        loc.setX(loc.getX() + (double) (rg.nextInt(21) - 10));
        loc.setY(loc.getY());
        loc.setZ(loc.getZ() + (double) (rg.nextInt(21) - 10));
        List list = subject.getNearbyEntities((double) configSettings.HostileRange, (double) configSettings.HostileRange, (double) configSettings.HostileRange);
        int i = 0;
        if (list.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "There are no entities near the player. Punishment Failed. Try again later");
            return;
        }
        for (Entity ent : subject.getNearbyEntities((double) configSettings.HostileRange, (double) configSettings.HostileRange, (double) configSettings.HostileRange)) {
            if (!(ent instanceof Creature)) continue;
            ++i;
            Creature mob = (Creature) ent;
            mob.setTarget((LivingEntity) subject);
        }
        sender.sendMessage(new StringBuilder().append(ChatColor.GREEN + Integer.toString(i)).append(" Entities made hostile.").toString());
    }

    public void ExecuteAnnoy(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        Location playerLocation = subject.getLocation();
        double y = playerLocation.getBlockY();
        double x = playerLocation.getBlockX();
        double z = playerLocation.getBlockZ();
        World currentWorld = subject.getPlayer().getWorld();
        Location Villager1 = new Location(currentWorld, x + 1.0, y, z);
        Villager v1 = (Villager) subject.getWorld().spawnEntity(Villager1, EntityType.VILLAGER);
        v1.setTarget((LivingEntity) subject);
        sender.sendMessage(ChatColor.GREEN + "Sent a villager");
    }

    public void ExecuteRotate(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        subject.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999999, 15));
        sender.sendMessage(ChatColor.GREEN + "Subject's view flipped upside-down");
    }

    public void ExecuteReverse(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        subject.setWalkSpeed(-0.1f);
        sender.sendMessage(ChatColor.GREEN + "Subject's actions inverted");
    }

    public void ExecuteRename(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        if (args[2].length() > 16) {
            sender.sendMessage(ChatColor.GREEN + "Name must be less than 16 charecters, and not in use");
            return;
        }
        subject.setDisplayName(args[2]);
        subject.setPlayerListName(args[2]);
        sender.sendMessage(ChatColor.GREEN + "Subject's NAME is now: " + subject.getDisplayName());
        sender.sendMessage(ChatColor.GREEN + "In commands, still refer to " + subject.getDisplayName() + " From their real name; " + subject.getName());
    }

    public void ExecuteKick(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        int numofwords = args.length - 1;
        if (numofwords <= 0) {
            sender.sendMessage(ChatColor.RED + "Text to say not chosen. Please put it at the end of the command");
            return;
        }
        int i = 1;
        String message = "";
        while (i < numofwords) {
            message = String.valueOf(message) + " " + args[i + 1];
            ++i;
        }
        subject.kickPlayer(message);
        sender.sendMessage(ChatColor.GREEN + "Subject kicked with custum message");
    }

    public void ExecuteDrop(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        subject.kickPlayer("Java Exception: Socket reset | End of Stream | in com.bukkit.minecraftServer.Connection ln 3712 at Java.Sockets.Connection.TCP Unhandled exception: Java.Exception.Streams.EndOFStream ");
        sender.sendMessage(ChatColor.GREEN + "Subject kicked witch custum message");
    }

    public void ExecuteBlock(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        BeingPunished.put(subject, Punishments.BLOCK);
        sender.sendMessage(ChatColor.GREEN + "Subject can no longer place blocks (do /punish stop to undo)");
    }

    public void ExecutePopular(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        Player[] playerArray = this.getServer().getOnlinePlayers().toArray(new Player[0]);
        int n = playerArray.length;
        int n2 = 0;
        while (n2 < n) {
            Player p = playerArray[n2];
            p.teleport(subject.getLocation());
            ++n2;
        }
        sender.sendMessage(ChatColor.GREEN + "All players teleported to player");
    }

    public void ExecutePumpkin(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        ItemStack is = subject.getItemInHand();
        is.setAmount(1);
        is.setType(Material.PUMPKIN);
        is.setDurability((short) 0);
        subject.getInventory().setHelmet(is);
        sender.sendMessage(ChatColor.GREEN + "Subject has a pumpkin on his/her head");
    }

    public void ExecuteArmour(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        ItemStack is = subject.getItemInHand();
        is.setAmount(1);
        is.setType(Material.BUCKET);
        is.setDurability((short) 0);
        subject.getInventory().setHelmet(is);
        subject.getInventory().setChestplate(is);
        subject.getInventory().setLeggings(is);
        subject.getInventory().setBoots(is);
        sender.sendMessage(ChatColor.GREEN + "Subject is wearing buckets");
    }

    public void ExecutePotato(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        ItemStack is = subject.getItemInHand();
        is.setAmount(1);
        is.setType(Material.POTATO);
        is.setDurability((short) 0);
        subject.getWorld().dropItemNaturally(subject.getLocation(), is);
        OldLocations.put(subject, subject.getLocation());
        Location location = null;
        location = subject.getLocation();
        location.setY(-10.0);
        subject.teleport(location);
        subject.chat(ChatColor.YELLOW + "*Potates*");
        subject.sendMessage("This is what it feels like to be a potato.");
        subject.sendMessage("It's all potatoey in here, which is why it's so dark.");
        subject.sendMessage("Potatoes - though they have eyes - can not see.");
        subject.sendMessage("They also have jackets, which protect them from dying of potatitus.");
        subject.sendMessage("To clarify, you are potato.");
        sender.sendMessage(ChatColor.GREEN + "Subject is a potato. Use /p stop " + args[1] + " to get the player back");
    }

    public void ExecuteDisplay(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        this.ExecuteBlock(sender, args);
        Location baselocation = subject.getLocation();
        this.Editblock(20, baselocation.add(0.0, -1.0, 0.0));
        int i = -1;
        while (i < 3) {
            baselocation = subject.getLocation();
            this.Editblock(20, baselocation.add(0.0, (double) i, 1.0));
            baselocation = subject.getLocation();
            this.Editblock(20, baselocation.add(0.0, (double) i, -1.0));
            baselocation = subject.getLocation();
            this.Editblock(20, baselocation.add(1.0, (double) i, 0.0));
            baselocation = subject.getLocation();
            this.Editblock(20, baselocation.add(1.0, (double) i, 1.0));
            baselocation = subject.getLocation();
            this.Editblock(20, baselocation.add(1.0, (double) i, -1.0));
            baselocation = subject.getLocation();
            this.Editblock(20, baselocation.add(-1.0, (double) i, 0.0));
            baselocation = subject.getLocation();
            this.Editblock(20, baselocation.add(-1.0, (double) i, 1.0));
            baselocation = subject.getLocation();
            this.Editblock(20, baselocation.add(-1.0, (double) i, -1.0));
            baselocation = subject.getLocation();
            ++i;
        }
        this.Editblock(20, baselocation.add(0.0, 2.0, 0.0));
        sender.sendMessage(ChatColor.GREEN + "Subject is now on display");
    }

    public void Editblock(int blockid, Location loc) {
        loc.getBlock().setType(Material.GLASS);
    }

    public void ExecuteWhite(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        subject.setTexturePack("http://software.roestudios.co.uk/Punishmental/shared/PunishmentalTexPack.zip");
        sender.sendMessage(ChatColor.GREEN + "Subject has been sent the texture switch request");
    }

    public void ExecutePopup(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        subject.openInventory((Inventory) subject.getInventory());
        sender.sendMessage(ChatColor.GREEN + "Popped up the players inventory");
    }

    public void ExecuteBooty(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        Player[] playerArray = this.getServer().getOnlinePlayers().toArray(new Player[0]);
        int n = playerArray.length;
        int n2 = 0;
        while (n2 < n) {
            Player p = playerArray[n2];
            p.openInventory((Inventory) subject.getInventory());
            ++n2;
        }
        subject.openInventory((Inventory) subject.getInventory());
        sender.sendMessage(ChatColor.GREEN + "Opened the player's inventory to everyone else");
    }

    public void ExecuteTree(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        Location playerLocation = subject.getLocation();
        Location centre = new Location(playerLocation.getWorld(), (double) playerLocation.getBlockX() + 0.5, (double) playerLocation.getBlockY(), (double) playerLocation.getBlockZ() + 0.5);
        subject.teleport(centre);
        boolean fail = false;
        if (!subject.getWorld().generateTree(subject.getLocation().add(1.0, 0.0, 0.0), TreeType.BIRCH)) {
            fail = true;
        }
        if (!subject.getWorld().generateTree(subject.getLocation().add(-1.0, 0.0, 0.0), TreeType.TREE)) {
            fail = true;
        }
        if (!subject.getWorld().generateTree(subject.getLocation().add(0.0, 0.0, -1.0), TreeType.TALL_REDWOOD)) {
            fail = true;
        }
        if (!subject.getWorld().generateTree(subject.getLocation().add(0.0, 0.0, 1.0), TreeType.BIG_TREE)) {
            fail = true;
        }
        if (fail) {
            sender.sendMessage(ChatColor.DARK_GREEN + " Some trees were unable to spawn");
        } else {
            sender.sendMessage(ChatColor.GREEN + " Player surrounded by trees");
        }
    }

    public void ExecuteHiss(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        subject.playSound(subject.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1.0f, 1.0f);
        sender.sendMessage(ChatColor.GREEN + " Imaginary Hiss Sent");
    }

    public void ExecuteAmbiance(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        subject.playSound(subject.getLocation(), Sound.AMBIENT_CAVE, 1.0f, 1.0f);
        sender.sendMessage(ChatColor.GREEN + " Ambiance sent");
    }

    public void ExecuteMaim(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        subject.setCanPickupItems(false);
        sender.sendMessage(ChatColor.GREEN + " Subject can't pick up items any more. Use '/p stop " + args[1] + "' to restore");
    }

    public void ExecuteBrittle(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        sender.sendMessage(ChatColor.GREEN + " Subject's next fall will kill them");
    }

    public void ExecuteTNTTrick(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        this.LockPlayer(subject);
        subject.getWorld().getBlockAt(subject.getEyeLocation().getBlockX() + 1, subject.getEyeLocation().getBlockY(), subject.getEyeLocation().getBlockZ()).setType(Material.TNT);
        subject.getWorld().getBlockAt(subject.getEyeLocation().getBlockX() - 1, subject.getEyeLocation().getBlockY(), subject.getEyeLocation().getBlockZ()).setType(Material.TNT);
        subject.getWorld().getBlockAt(subject.getEyeLocation().getBlockX(), subject.getEyeLocation().getBlockY(), subject.getEyeLocation().getBlockZ() + 1).setType(Material.TNT);
        subject.getWorld().getBlockAt(subject.getEyeLocation().getBlockX(), subject.getEyeLocation().getBlockY(), subject.getEyeLocation().getBlockZ() - 1).setType(Material.TNT);
        subject.playSound(subject.getLocation(), Sound.ENTITY_TNT_PRIMED, 1.0f, 1.0f);
        sender.sendMessage(ChatColor.GREEN + " Pulled a TNT Trick");
    }

    public void ExecuteTNT(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        this.LockPlayer(subject);
        subject.getWorld().getBlockAt(subject.getEyeLocation().getBlockX() + 1, subject.getEyeLocation().getBlockY(), subject.getEyeLocation().getBlockZ()).setType(Material.TNT);
        subject.getWorld().getBlockAt(subject.getEyeLocation().getBlockX() - 1, subject.getEyeLocation().getBlockY(), subject.getEyeLocation().getBlockZ()).setType(Material.TNT);
        subject.getWorld().getBlockAt(subject.getEyeLocation().getBlockX(), subject.getEyeLocation().getBlockY(), subject.getEyeLocation().getBlockZ() + 1).setType(Material.TNT);
        subject.getWorld().getBlockAt(subject.getEyeLocation().getBlockX(), subject.getEyeLocation().getBlockY(), subject.getEyeLocation().getBlockZ() - 1).setType(Material.TNT);
        subject.getWorld().getBlockAt(subject.getEyeLocation().getBlockX() + 1, subject.getEyeLocation().getBlockY() + 1, subject.getEyeLocation().getBlockZ()).setType(Material.FIRE);
        subject.getWorld().getBlockAt(subject.getEyeLocation().getBlockX() - 1, subject.getEyeLocation().getBlockY() + 1, subject.getEyeLocation().getBlockZ()).setType(Material.FIRE);
        subject.getWorld().getBlockAt(subject.getEyeLocation().getBlockX(), subject.getEyeLocation().getBlockY() + 1, subject.getEyeLocation().getBlockZ() + 1).setType(Material.FIRE);
        subject.getWorld().getBlockAt(subject.getEyeLocation().getBlockX(), subject.getEyeLocation().getBlockY() + 1, subject.getEyeLocation().getBlockZ() - 1).setType(Material.FIRE);
        sender.sendMessage(ChatColor.GREEN + " Surrounded the player with ignited TNT");
    }

    public void ExecuteFire(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        this.LockPlayer(subject);
        subject.getWorld().getBlockAt(subject.getLocation().getBlockX() + 1, subject.getLocation().getBlockY(), subject.getLocation().getBlockZ()).setType(Material.FIRE);
        subject.getWorld().getBlockAt(subject.getLocation().getBlockX() - 1, subject.getLocation().getBlockY(), subject.getLocation().getBlockZ()).setType(Material.FIRE);
        subject.getWorld().getBlockAt(subject.getLocation().getBlockX(), subject.getLocation().getBlockY(), subject.getLocation().getBlockZ() + 1).setType(Material.FIRE);
        subject.getWorld().getBlockAt(subject.getLocation().getBlockX(), subject.getLocation().getBlockY(), subject.getLocation().getBlockZ() - 1).setType(Material.FIRE);
        subject.sendMessage("Watch your step!");
        sender.sendMessage(ChatColor.GREEN + " Player surrounded with fire");
    }

    public void ExecuteSquid(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        Location loc = subject.getLocation();
        loc.setPitch(90.0f);
        Entity squid = subject.getWorld().spawnEntity(loc.add(0.0, 5.0, 0.0), EntityType.SQUID);
        sender.sendMessage(ChatColor.GREEN + " Suddenly, Squid");
    }

    public void ExecuteCrash(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        subject.setWalkSpeed(0.0f);
        subject.setWalkSpeed(-1.0f);
        subject.setWalkSpeed(1.0f);
        subject.setWalkSpeed(0.0f);
        subject.setWalkSpeed(0.2f);
        sender.sendMessage(ChatColor.GREEN + " Player's client is now crashed; They need to restart minecraft to continue as normal");
    }

    public void ExecuteUseless(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        ListIterator itr = subject.getInventory().iterator();
        while (itr.hasNext()) {
            try {
                ItemStack element = (ItemStack) itr.next();
                ItemMeta im = element.getItemMeta();
                im.setDisplayName("Useless");
                element.setItemMeta(im);
            } catch (Exception exception) {
                // empty catch block
            }
        }
        sender.sendMessage(ChatColor.GREEN + " Subjects' items renamed to 'useless'");
    }

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = rnddgen.nextInt(((Enum[]) clazz.getEnumConstants()).length);
        return (T) ((Enum[]) clazz.getEnumConstants())[x];
    }

    public void ExecuteIDTheft(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        sender.sendMessage(ChatColor.GREEN + " Test Criteria Executed");
    }

    public void ExecuteWrong(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        ListIterator itr = subject.getInventory().iterator();
        while (itr.hasNext()) {
            try {
                ItemStack element = (ItemStack) itr.next();
                ItemMeta im = element.getItemMeta();
                im.setDisplayName(Fall.randomEnum(Material.class).toString());
                element.setItemMeta(im);
            } catch (Exception exception) {
                // empty catch block
            }
        }
        sender.sendMessage(ChatColor.GREEN + " All player's items renamed to different things");
    }

    public void ExecuteTrip(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        for (ItemStack element : subject.getInventory()) {
            subject.getWorld().dropItemNaturally(subject.getLocation().add(0.0, 2.0, 0.0), element);
        }
        subject.getInventory().clear();
    }

    public void ExecuteTest(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        for (ItemStack element : subject.getInventory()) {
            subject.getWorld().dropItemNaturally(subject.getLocation().add(0.0, 2.0, 0.0), element);
        }
        subject.getInventory().clear();
        sender.sendMessage(ChatColor.GREEN + "Player item drop complete");
    }

    public void ExecuteScream(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        subject.playSound(subject.getLocation(), Sound.ENTITY_GHAST_SCREAM, 1.0f, 1.0f);
        sender.sendMessage(ChatColor.GREEN + "Scream sent");
    }

    public void ExecuteGlass(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        OldLocations.put(subject, subject.getLocation());
        Location location = null;
        Location permlocation = location = subject.getLocation();
        permlocation.setX((double) subject.getLocation().getBlockX() + 0.5);
        permlocation.setY(200.0);
        permlocation.setZ((double) subject.getLocation().getBlockZ() + 0.5);
        subject.teleport(permlocation);
        Location loc = subject.getLocation();
        loc = loc.subtract(1.0, 1.0, 1.0);
        this.AddtoRestore(loc);
        loc.getBlock().setType(Material.GLASS);
        int x = 1;
        while (x < 4) {
            loc = loc.add(0.0, 0.0, 1.0);
            this.AddtoRestore(loc);
            loc.getBlock().setType(Material.GLASS);
            loc = loc.add(0.0, 0.0, 1.0);
            this.AddtoRestore(loc);
            loc.getBlock().setType(Material.GLASS);
            loc = loc.add(0.0, 0.0, 1.0);
            this.AddtoRestore(loc);
            loc.getBlock().setType(Material.GLASS);
            loc = loc.add(1.0, 0.0, -3.0);
            this.AddtoRestore(loc);
            loc.getBlock().setType(Material.GLASS);
            ++x;
        }
        sender.sendMessage(ChatColor.GREEN + "Punishment Dispensed Successfuly");
    }

    public void ExecuteFlamingArrow(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        Location loc1 = subject.getEyeLocation();
        Location loc2 = subject.getLocation();
        loc2 = loc2.add(5.0, 10.0, 5.0);
        loc2.setY((double) (loc2.getBlockY() + 2));
        loc2.setX((double) loc2.getBlockX() + 0.5);
        loc2.setZ((double) loc2.getBlockZ() + 0.5);
        Arrow arr = subject.getWorld().spawnArrow(loc2, new org.bukkit.util.Vector(loc1.getX() - loc2.getX(), loc1.getY() - loc2.getY(), loc1.getZ() - loc2.getZ()), 3.0f, 12.0f);
        arr.setFireTicks(999);
        subject.playEffect(loc1, Effect.BOW_FIRE, 5);
        sender.sendMessage(ChatColor.GREEN + "Flaming Arrow sent");
    }

    public void ExecuteAnvil(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        if (HealthTarget.containsKey(subject) && HealthTarget.get(subject) == 0) {
            this.ExecuteFreeze(sender, args);
            subject.sendMessage("What the?");
        }
        Location playerLocation = subject.getLocation();
        double x = playerLocation.getBlockX();
        double y = playerLocation.getBlockY() + 15;
        double z = playerLocation.getBlockZ();
        Location loc = new Location(playerLocation.getWorld(), x, y, z);
        Block block = subject.getWorld().getBlockAt(loc);
        block.setType(Material.ANVIL);
        sender.sendMessage(ChatColor.GREEN + "Anvil dropped");
    }

    public void LockPlayer(Player subject) {
        Location playerLocation = subject.getLocation();
        Location centre = new Location(playerLocation.getWorld(), (double) playerLocation.getBlockX() + 0.5, (double) playerLocation.getBlockY(), (double) playerLocation.getBlockZ() + 0.5);
        subject.teleport(centre);
    }

    public void ExecuteCage(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        Location playerLocation = subject.getLocation();
        Location centre = new Location(playerLocation.getWorld(), (double) playerLocation.getBlockX() + 0.5, (double) playerLocation.getBlockY(), (double) playerLocation.getBlockZ() + 0.5);
        subject.teleport(centre);
        this.log.info(centre.toString());
        this.log.info(subject.getLocation().toString());
        this.ExecuteFreeze(sender, args);
        double x = centre.getBlockX() + 1;
        double y = centre.getBlockY() + 15;
        double z = centre.getBlockZ();
        Location loc = new Location(playerLocation.getWorld(), x, y, z);
        Block block = subject.getWorld().getBlockAt(loc);
        block.setType(Material.ANVIL);
        x = playerLocation.getBlockX() - 1;
        y = playerLocation.getBlockY() + 16;
        z = playerLocation.getBlockZ();
        loc = new Location(playerLocation.getWorld(), x, y, z);
        subject.getWorld().getBlockAt(loc).setType(Material.ANVIL);
        x = playerLocation.getBlockX();
        y = playerLocation.getBlockY() + 17;
        z = playerLocation.getBlockZ() - 1;
        loc = new Location(playerLocation.getWorld(), x, y, z);
        subject.getWorld().getBlockAt(loc).setType(Material.ANVIL);
        x = playerLocation.getBlockX();
        y = playerLocation.getBlockY() + 18;
        z = playerLocation.getBlockZ() + 1;
        loc = new Location(playerLocation.getWorld(), x, y, z);
        subject.getWorld().getBlockAt(loc).setType(Material.ANVIL);
        x = playerLocation.getBlockX() + 1;
        y = playerLocation.getBlockY() + 19;
        z = playerLocation.getBlockZ();
        loc = new Location(playerLocation.getWorld(), x, y, z);
        block = subject.getWorld().getBlockAt(loc);
        block.setType(Material.ANVIL);
        x = playerLocation.getBlockX() - 1;
        y = playerLocation.getBlockY() + 20;
        z = playerLocation.getBlockZ();
        loc = new Location(playerLocation.getWorld(), x, y, z);
        subject.getWorld().getBlockAt(loc).setType(Material.ANVIL);
        x = playerLocation.getBlockX();
        y = playerLocation.getBlockY() + 21;
        z = playerLocation.getBlockZ() - 1;
        loc = new Location(playerLocation.getWorld(), x, y, z);
        subject.getWorld().getBlockAt(loc).setType(Material.ANVIL);
        x = playerLocation.getBlockX();
        y = playerLocation.getBlockY() + 22;
        z = playerLocation.getBlockZ() + 1;
        loc = new Location(playerLocation.getWorld(), x, y, z);
        subject.getWorld().getBlockAt(loc).setType(Material.ANVIL);
        sender.sendMessage(ChatColor.GREEN + "Cage Built");
    }

    public void ExecuteChat(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        int numofwords = args.length - 1;
        if (numofwords <= 0) {
            sender.sendMessage(ChatColor.RED + "Text to say not chosen. Please put it at the end of the command");
            return;
        }
        int i = 1;
        String message = "";
        while (i < numofwords) {
            message = String.valueOf(message) + args[i + 1] + " ";
            ++i;
        }
        subject.chat(message);
        sender.sendMessage(ChatColor.GREEN + "Sent Chat Message");
    }

    public void ExecuteNight(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        subject.setPlayerTime(18000L, false);
        sender.sendMessage(ChatColor.GREEN + "Subject is now at midnight. Restore using /p stop " + sender.getName());
    }

    public void ExecuteRewind(CommandSender sender, String[] args) {
        final Player subject = this.getServer().getPlayer(args[1]);
        this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin) this, new Runnable() {

            @Override
            public void run() {
                subject.setPlayerTime(subject.getPlayerTime() - 50L, false);
            }
        }, 1L, 1L);
        sender.sendMessage(ChatColor.GREEN + "Subject's time is running at 50x speed backwards. Restore using /p stop " + sender.getName());
    }

    public void ExecuteMute(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.GREEN + "Subject can't chat any more. Restore using /p stop " + sender.getName());
    }

    public void ExecuteArab(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.GREEN + "Subject is now being translated into arabic. Restore using /p stop " + sender.getName());
    }

    public void ExecuteMetro(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.GREEN + "Subject is now multi-lingual. Restore using /p stop " + sender.getName());
    }

    public void ExecutePlynth(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
    }

    public static byte HexToByte(String s) {
        return Byte.parseByte(s, 16);
    }

    public void ExecuteSpam(CommandSender sender, String[] args) {
        final Player subject = this.getServer().getPlayer(args[1]);
        this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin) this, new Runnable() {

            @Override
            public void run() {
                switch (Fall.this.rndgen.nextInt(9)) {
                    case 1: {
                        subject.sendMessage(ChatColor.MAGIC + "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                        break;
                    }
                    case 2: {
                        subject.sendMessage(ChatColor.GOLD + "1.18 wen");
                        break;
                    }
                    case 3: {
                        subject.sendMessage(ChatColor.MAGIC + "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                        break;
                    }
                    case 4: {
                        subject.sendMessage(" !!!!!!!!!!!!!! CLICK HERE TO CLAIM YOUR FREE EYE PAD! !!!!!!!!!!!!!!!! ");
                        break;
                    }
                    case 5: {
                        subject.sendMessage(ChatColor.AQUA + ">>>>>>>>>> LOOKING FOR SOMETHING? TRY ASK JEVES! <<<<<<<<<<<");
                        break;
                    }
                    case 6: {
                        subject.sendMessage(ChatColor.GOLD + "WIN OVER 50 THOUSAND DOLLARS JUST BY CALLING THIS NUMBER!");
                        break;
                    }
                    case 7: {
                        subject.sendMessage(ChatColor.UNDERLINE + "TAKE OUR SURVAY ON HOW YOU THINK FUTURE SURVAYS SHOULD SURVAY THE POPULATION'S OPINION ON SURVAYS!");
                        break;
                    }
                    case 8: {
                        subject.sendMessage(ChatColor.BLACK + "NYAN" + ChatColor.BLUE + "NYAN" + ChatColor.RED + "NYAN" + ChatColor.GREEN + "NYAN" + ChatColor.RED + "NYAN" + ChatColor.YELLOW + "NYAN" + ChatColor.RED + "NYAN");
                        break;
                    }
                    case 9: {
                        subject.sendMessage(ChatColor.UNDERLINE + "'Knock knock' 'Who's there?' 'The landlord. Your rent is due'");
                        break;
                    }
                    case 10: {
                        subject.sendMessage(ChatColor.ITALIC + "I'm afraid I can't let you do that " + subject.getDisplayName());
                    }
                }
            }
        }, 5L, 5L);
        sender.sendMessage(ChatColor.GREEN + "Subject is being bombarded with spam! Restore using /p stop " + sender.getName());
    }

    public void ReleaseTheHounds(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        Location LC2 = subject.getLocation();
        if (Fall.getStrDirection(subject) == "North") {
            LC2.add(0.0, 0.0, 3.0);
        } else if (Fall.getStrDirection(subject) == "Northeast") {
            LC2 = LC2.add(3.0, 0.0, 3.0);
        } else if (Fall.getStrDirection(subject) == "Northwest") {
            LC2 = LC2.add(-3.0, 0.0, 3.0);
        } else if (Fall.getStrDirection(subject) == "South") {
            LC2 = LC2.add(0.0, 0.0, -3.0);
        } else if (Fall.getStrDirection(subject) == "Southeast") {
            LC2 = LC2.add(3.0, 0.0, -3.0);
        } else if (Fall.getStrDirection(subject) == "Southwest") {
            LC2 = LC2.add(-3.0, 0.0, -3.0);
        } else if (Fall.getStrDirection(subject) == "West") {
            LC2 = LC2.add(-3.0, 0.0, 0.0);
        } else if (Fall.getStrDirection(subject) == "East") {
            LC2 = LC2.add(3.0, 0.0, 0.0);
        }
        LC2 = subject.getWorld().getHighestBlockAt(LC2).getLocation();
        LC2 = LC2.add(0.0, 2.0, 0.0);
        Wolf wchasing = (Wolf) subject.getWorld().spawnEntity(LC2, EntityType.WOLF);
        wchasing.setAngry(true);
        wchasing.damage(1.0, (Entity) subject);
        wchasing.setTarget((LivingEntity) subject);
        sender.sendMessage(ChatColor.GREEN + "Hound Released");
    }

    public void ExecuteIronGolem(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        Location LC2 = subject.getLocation();
        if (Fall.getStrDirection(subject) == "North") {
            LC2.add(0.0, 0.0, 2.0);
        } else if (Fall.getStrDirection(subject) == "Northeast") {
            LC2 = LC2.add(2.0, 0.0, 2.0);
        } else if (Fall.getStrDirection(subject) == "Northwest") {
            LC2 = LC2.add(-2.0, 0.0, 2.0);
        } else if (Fall.getStrDirection(subject) == "South") {
            LC2 = LC2.add(0.0, 0.0, -2.0);
        } else if (Fall.getStrDirection(subject) == "Southeast") {
            LC2 = LC2.add(2.0, 0.0, -2.0);
        } else if (Fall.getStrDirection(subject) == "Southwest") {
            LC2 = LC2.add(-2.0, 0.0, -2.0);
        } else if (Fall.getStrDirection(subject) == "West") {
            LC2 = LC2.add(-2.0, 0.0, 0.0);
        } else if (Fall.getStrDirection(subject) == "East") {
            LC2 = LC2.add(2.0, 0.0, 0.0);
        }
        LC2 = subject.getWorld().getHighestBlockAt(LC2).getLocation();
        LC2 = LC2.add(0.0, 2.0, 0.0);
        IronGolem ichasing = (IronGolem) subject.getWorld().spawnEntity(LC2, EntityType.IRON_GOLEM);
        ichasing.damage(1.0, (Entity) subject);
        ichasing.setTarget((LivingEntity) subject);
        sender.sendMessage(ChatColor.GREEN + "Spawened an IronGolem behind them");
    }

    public void ExecuteArrow(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        Location LC2 = subject.getLocation();
        if (Fall.getStrDirection(subject) == "North") {
            LC2.add(0.0, 0.0, -2.0);
        } else if (Fall.getStrDirection(subject) == "Northeast") {
            LC2 = LC2.add(-2.0, 0.0, -2.0);
        } else if (Fall.getStrDirection(subject) == "Northwest") {
            LC2 = LC2.add(2.0, 0.0, -2.0);
        } else if (Fall.getStrDirection(subject) == "South") {
            LC2 = LC2.add(0.0, 0.0, 2.0);
        } else if (Fall.getStrDirection(subject) == "Southeast") {
            LC2 = LC2.add(-2.0, 0.0, 2.0);
        } else if (Fall.getStrDirection(subject) == "Southwest") {
            LC2 = LC2.add(2.0, 0.0, 2.0);
        } else if (Fall.getStrDirection(subject) == "West") {
            LC2 = LC2.add(2.0, 0.0, 0.0);
        } else if (Fall.getStrDirection(subject) == "East") {
            LC2 = LC2.add(-2.0, 0.0, 0.0);
        }
        org.bukkit.util.Vector arrowvec = LC2.subtract(subject.getLocation()).toVector();
        subject.getWorld().spawnArrow(LC2, arrowvec, 0.6f, 12.0f);
        subject.playEffect(LC2, Effect.BOW_FIRE, 0);
        sender.sendMessage(ChatColor.GREEN + "Subject took an arrow to the knee");
    }

    public static String getStrDirection(Player player) {
        double rot = (player.getLocation().getYaw() - 90.0f) % 360.0f;
        if (rot < 0.0) {
            rot += 360.0;
        }
        return Fall.getDirection(rot);
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

    public void ExecuteShoot(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        Location LC2 = subject.getLocation();
        if (Fall.getStrDirection(subject) == "North") {
            LC2 = LC2.add(0.0, 0.0, 2.0);
        } else if (Fall.getStrDirection(subject) == "Northeast") {
            LC2 = LC2.add(2.0, 0.0, 2.0);
        } else if (Fall.getStrDirection(subject) == "Northwest") {
            LC2 = LC2.add(-2.0, 0.0, 2.0);
        } else if (Fall.getStrDirection(subject) == "South") {
            LC2 = LC2.add(0.0, 0.0, -2.0);
        } else if (Fall.getStrDirection(subject) == "Southeast") {
            LC2 = LC2.add(2.0, 0.0, -2.0);
        } else if (Fall.getStrDirection(subject) == "Southwest") {
            LC2 = LC2.add(-2.0, 0.0, -2.0);
        } else if (Fall.getStrDirection(subject) == "West") {
            LC2 = LC2.add(-2.0, 0.0, 0.0);
        } else if (Fall.getStrDirection(subject) == "East") {
            LC2 = LC2.add(2.0, 0.0, 0.0);
        }
        org.bukkit.util.Vector p = LC2.toVector();
        org.bukkit.util.Vector e = subject.getLocation().toVector();
        org.bukkit.util.Vector v = e.subtract(p).normalize().multiply(5.0);
        v.setY((Math.abs(v.getY()) + 5.0) * 2.0);
        subject.setVelocity(v);
        sender.sendMessage(ChatColor.GREEN + "Punishment Dispensed Successfuly");
    }

    public void ExecuteSlap(CommandSender sender, String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        Location LC2 = subject.getLocation();
        if (Fall.getStrDirection(subject) == "North") {
            LC2 = LC2.add(0.0, -0.5, -2.0);
        } else if (Fall.getStrDirection(subject) == "Northeast") {
            LC2 = LC2.add(-2.0, -0.5, -2.0);
        } else if (Fall.getStrDirection(subject) == "Northwest") {
            LC2 = LC2.add(2.0, -0.5, -2.0);
        } else if (Fall.getStrDirection(subject) == "South") {
            LC2 = LC2.add(0.0, -0.5, 2.0);
        } else if (Fall.getStrDirection(subject) == "Southeast") {
            LC2 = LC2.add(-2.0, -0.5, 2.0);
        } else if (Fall.getStrDirection(subject) == "Southwest") {
            LC2 = LC2.add(2.0, -0.5, 2.0);
        } else if (Fall.getStrDirection(subject) == "West") {
            LC2 = LC2.add(2.0, -0.5, 0.0);
        } else if (Fall.getStrDirection(subject) == "East") {
            LC2 = LC2.add(-2.0, -0.5, 0.0);
        }
        org.bukkit.util.Vector v = new org.bukkit.util.Vector(subject.getLocation().getX() - LC2.getX(), subject.getLocation().getY() - LC2.getY(), subject.getLocation().getZ() - LC2.getZ());
        subject.setVelocity(v);
        sender.sendMessage(ChatColor.GREEN + "Punishment Dispensed Successfuly");
    }

    public boolean isHigherLevel(Player punisher, Player punished) {
        if (!configSettings.UsePunishmentLevels) {
            return true;
        }
        if (punisher.hasPermission("punishLevel.MASTER") || punisher.getName() == "") {
            return true;
        }
        if (punisher.hasPermission("punishLevel.two")) {
            return !punished.hasPermission("punishLevel.MASTER");
        }
        if (punisher.hasPermission("punishLevel.three")) {
            return !punished.hasPermission("punishLevel.MASTER") && !punished.hasPermission("punishLevel.two");
        }
        if (punisher.hasPermission("punishLevel.four")) {
            return !punished.hasPermission("punishLevel.MASTER") && !punished.hasPermission("punishLevel.two") && !punished.hasPermission("punishLevel.three");
        }
        if (punisher.hasPermission("punishLevel.five")) {
            return !punished.hasPermission("punishLevel.MASTER") && !punished.hasPermission("punishLevel.two") && !punished.hasPermission("punishLevel.three") && !punished.hasPermission("punishLevel.four");
        }
        this.log.info("Player level not specified. They can not punish");
        return false;
    }

    /*
     * Could not resolve type clashes
     * Unable to fully structure code
     */
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        try {
            Punishments.valueOf(args[1].toUpperCase());
        } catch (Throwable t) {
            sender.sendMessage(ChatColor.RED + "> Punishment not recognised. Please try again.");
            return false;
        }
        if (!sender.hasPermission("punish." + args[1].toLowerCase())) {
            sender.sendMessage(ChatColor.RED + "> You don't have permission to use that punishment. Protection aborted.");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("punish") || cmd.getName().equalsIgnoreCase("p")) {
            stonemonsteregg:
            {
                block98:
                {
                    try {
                        String str = args[0];
                        str.length();
                    } catch (Throwable t) {
                        sender.sendMessage(ChatColor.RED + "### soup6020 - PunishmentalEX ###");
                        sender.sendMessage(ChatColor.RED + "     Currently installed Punishments:");
                        this.printusage(sender);
                        return true;
                    }
                }
            }
        }

        if (cmd.getName().equalsIgnoreCase("punish") || cmd.getName().equalsIgnoreCase("p")) {
            stonemonsteregg2:
            {
                block98:
                {
                    try {
                        String str = args[0];
                        str.length();
                    } catch (Throwable t) {
                        sender.sendMessage(ChatColor.RED + "### soup6020 - PunishmentalEX ###");
                        sender.sendMessage(ChatColor.RED + "     Currently installed Punishments:");
                        this.printusage(sender);
                        return true;
                    }
                    if (sender.getName() == "CONSOLE" || sender.getName() == "") {
                        sender.sendMessage("Command being sent from console - Security bypassed");
                    } else {
                        if (!this.SecurityCheck((Player) sender)) {
                            sender.sendMessage("Invalid Credentials - could not run!");
                            return false;
                        }
                        if (args[0].equalsIgnoreCase("restore")) {
                            if (sender.hasPermission("punish.restore")) {
                                sender.sendMessage(ChatColor.RED + "Restore functions do not seem to be working. I'll try anyway, but please check that it's done it :)");
                                this.RestoreDamaged(sender, args);
                                return true;
                            }
                            sender.sendMessage(ChatColor.RED + "You do not have the correct permissions to run this command");
                            return false;
                        }
                    }
                    if (args.length < 2 || args[0].equalsIgnoreCase("help")) {
                        sender.sendMessage(ChatColor.RED + "### soup6020 - PunishmentalEX ###");
                        sender.sendMessage(ChatColor.RED + "     Currently installed Punishments:");
                        this.printusage(sender);
                        return true;
                    }

                    try {
                        Punishments.valueOf(args[1].toUpperCase());
                    } catch (Throwable t) {
                        sender.sendMessage(ChatColor.RED + "> Punishment not recognised. Please try again.");
                        return false;
                    }
                    if (!sender.hasPermission("punish." + args[1].toLowerCase())) {
                        sender.sendMessage(ChatColor.RED + "> You don't have permission to use that punishment. Protection aborted.");
                        return true;
                    }
                }
                if (cmd.getName().equalsIgnoreCase("punish") || cmd.getName().equalsIgnoreCase("p")) {
                    block97:
                    {
                        block98:
                        {
                            try {
                                String str = args[0];
                                str.length();
                            } catch (Throwable t) {
                                sender.sendMessage(ChatColor.RED + "### soup6020 - PunishmentalEX ###");
                                sender.sendMessage(ChatColor.RED + "     Currently installed Punishments:");
                                this.printusage(sender);
                                return true;
                            }
                            if (sender.getName() == "CONSOLE" || sender.getName() == "") {
                                sender.sendMessage("Command being sent from console - Security bypassed");
                            } else {
                                if (!this.SecurityCheck((Player) sender)) {
                                    sender.sendMessage("Invalid Credentials - could not run!");
                                    return false;
                                }
                                if (args[0].equalsIgnoreCase("restore")) {
                                    if (sender.hasPermission("punish.restore")) {
                                        sender.sendMessage(ChatColor.RED + "Restore functions do not seem to be working. I'll try anyway, but please check that it's done it :)");
                                        this.RestoreDamaged(sender, args);
                                        return true;
                                    }
                                    sender.sendMessage(ChatColor.RED + "You do not have the correct permissions to run this command");
                                    return false;
                                }
                            }
                            if (args.length < 2 || args[0].equalsIgnoreCase("help")) {
                                sender.sendMessage(ChatColor.RED + "### soup6020 - PunishmentalEX ###");
                                sender.sendMessage(ChatColor.RED + "     Currently installed Punishments:");
                                this.printusage(sender);
                                return true;
                            }

                            sender.sendMessage(ChatColor.GREEN + "All punishments complete!");
                            return true;
                        }
                    }
                }
            }
            lbl371:
            // 1 sources

            sender.sendMessage(ChatColor.GREEN + "All punishments complete!");
            return true;
        }

   return true;}


    public boolean VetPunishment(final CommandSender sender, final String[] args) {
        Player subject = this.getServer().getPlayer(args[1]);
        this.log.info("Punishing  " + subject.getName());
        if (sender.getName() != null && sender.getName() != "" && sender.getName() != "CONSOLE" && !this.isHigherLevel((Player)sender, subject)) {
            sender.sendMessage(ChatColor.RED + "ERROR: You can't do that as you are at a lower punishment level than " + subject.getDisplayName());
            return true;
        }
        if (args[0].equalsIgnoreCase("rename") || args[0].equalsIgnoreCase("kick") || args[0].equalsIgnoreCase("chat")) {
            this.RunPunishment(sender, args);
            this.UpdateDB(false);
            return true;
        }
        boolean keepstatus = false;
        if (args[0].equalsIgnoreCase("night") || args[0].equalsIgnoreCase("reverse") || args[0].equalsIgnoreCase("paranoia") || args[0].equalsIgnoreCase("chatroll") || args[0].equalsIgnoreCase("unaware") || args[0].equalsIgnoreCase("chat") || args[0].equalsIgnoreCase("chatroll") || args[0].equalsIgnoreCase("drunk") || args[0].equalsIgnoreCase("babble") || args[0].equalsIgnoreCase("mute") || args[0].equalsIgnoreCase("rename") || args[0].equalsIgnoreCase("spam") || args[0].equalsIgnoreCase("stop") || args[0].equalsIgnoreCase("fakeop") || args[0].equalsIgnoreCase("rewind") || args[0].equalsIgnoreCase("display") || args[0].equalsIgnoreCase("popup") || args[0].equalsIgnoreCase("white") || args[0].equalsIgnoreCase("night")) {
            keepstatus = true;
        }
        keepstatus = !configSettings.RemoveCreative;
        if (args.length == 2) {
            sender.sendMessage(ChatColor.GRAY + "Damage not specified. Punishments may kill player");
            subject = this.getServer().getPlayer(args[1]);
            HealthTarget.put(subject, 0);
            if (!keepstatus) {
                this.getServer().getPlayer(args[1]).setGameMode(GameMode.SURVIVAL);
            }
        } else if (args[2].equalsIgnoreCase("x")) {
            sender.sendMessage(ChatColor.GRAY + "Damage not specified. Punishments may kill player");
            subject = this.getServer().getPlayer(args[1]);
            HealthTarget.put(subject, 0);
            this.getServer().getPlayer(args[1]).setGameMode(GameMode.SURVIVAL);
        } else if (args[2].equalsIgnoreCase("y")) {
            sender.sendMessage(ChatColor.GRAY + "Player will be kept in creative mode - most punishments will not kill them");
            subject = this.getServer().getPlayer(args[1]);
            HealthTarget.put(subject, 0);
            this.getServer().getPlayer(args[1]).setFlying(false);
        } else {
            int tempHP;
            try {
                tempHP = Integer.parseInt(args[2]);
            }
            catch (Throwable t) {
                this.log.info(t.getMessage());
                sender.sendMessage(ChatColor.YELLOW + "### soup6020 - PunishmentalEX ###");
                sender.sendMessage(ChatColor.YELLOW + "Error!");
                sender.sendMessage(ChatColor.YELLOW + "The amount of damage (" + args[2] + ") was not a number or the letter x / y");
                sender.sendMessage(ChatColor.YELLOW + "If you do not wish to specify damage, use 'x'");
                sender.sendMessage(ChatColor.YELLOW + "If you want to keep the player in creative, use 'y'");
                return true;
            }
            if (tempHP > 0) {
                subject = this.getServer().getPlayer(args[1]);
                tempHP = (int)(subject.getHealth() - (double)tempHP);
                if (tempHP < 1) {
                    tempHP = 0;
                }
                if (tempHP > 20) {
                    tempHP = 20;
                }
                HealthTarget.put(subject, tempHP);
                this.getServer().getPlayer(args[1]).setGameMode(GameMode.SURVIVAL);
                sender.sendMessage(ChatColor.RED + "Player will be on at least " + HealthTarget.get(subject) + " hp after punishment");
            }
        }
        Fall plugin = this;
        if (args.length >= 4) {
            try {
                Integer.parseInt(args[3]);
            }
            catch (Throwable t) {
                sender.sendMessage(ChatColor.YELLOW + "The argument (" + args[3] + ") - amount of times to run punishment - was not a number.");
                sender.sendMessage(ChatColor.YELLOW + "Punishment not ran, argument 3 needs to be a number");
                return false;
            }
        }
        if (args.length >= 5) {
            try {
                Integer.parseInt(args[4]);
            }
            catch (Throwable t) {
                sender.sendMessage(ChatColor.YELLOW + "The argument (" + args[4] + ") - length of time to wait - was not a number.");
                sender.sendMessage(ChatColor.YELLOW + "Punishment not ran, argument 4 needs to be a number");
                return false;
            }
        }
        if (args.length <= 3) {
            this.RunPunishment(sender, args);
            this.UpdateDB(false);
            return true;
        }
        if (args.length >= 4 && Integer.parseInt(args[3]) == 0) {
            sender.sendMessage(ChatColor.RED + "I can't do somthing Zero times!");
            sender.sendMessage(ChatColor.RED + "/Punish [Punishment] [Username] [Opt. damage/x/y] [amount of times] [delay] [Other arguments]");
            return true;
        }
        if (args.length >= 5 & Integer.parseInt(args[3]) == 1) {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask((Plugin)plugin, new Runnable(){

                @Override
                public void run() {
                    Fall.this.RunPunishment(sender, args);
                    Fall.this.UpdateDB(false);
                }
            }, (long)(Integer.parseInt(args[4]) * 20));
        } else if (args.length >= 5 & Integer.parseInt(args[3]) > 1) {
            int taskid = plugin.getServer().getScheduler().scheduleAsyncRepeatingTask((Plugin)plugin, new Runnable(){

                @Override
                public void run() {
                    Fall.this.RunPunishment(sender, args);
                    Fall.this.UpdateDB(false);
                }
            }, (long)(Integer.parseInt(args[4]) * 20), (long)(Integer.parseInt(args[4]) * 20));
            if (Tasks.containsKey(args[1])) {
                this.getServer().getScheduler().cancelTask(Tasks.get(args[1]).intValue());
                Tasks.remove(args[1]);
            }
            Tasks.put(args[1], taskid);
        } else if (args.length >= 4 & Integer.parseInt(args[3]) > 1) {
            while (this.timesdone <= Integer.parseInt(args[3])) {
                ++this.timesdone;
                this.RunPunishment(sender, args);
                this.UpdateDB(false);
            }
            this.timesdone = 0;
        } else {
            this.RunPunishment(sender, args);
            this.UpdateDB(false);
        }
        this.UpdateDB(false);
        return true;
    }

    public void advanceFailsafe() {
        if (this.SecondStart == 0L) {
            this.SecondStart = System.currentTimeMillis();
        }
        if (System.currentTimeMillis() > this.SecondStart + 1000L) {
            this.SecondStart = System.currentTimeMillis();
            this.punishmentsdone = 0;
        }
        if (this.punishmentsdone >= configSettings.FailsafeTrigger) {
            this.activateFailsafe();
        }
    }

    public void activateFailsafe() {
        if (configSettings.Failsafe) {
            this.getServer().getScheduler().cancelTasks((Plugin)this);
            this.lagCheckID = 0;
            Player[] playerArray = this.getServer().getOnlinePlayers().toArray(new Player[0]);
            int n = playerArray.length;
            int n2 = 0;
            while (n2 < n) {
                Player p = playerArray[n2];
                String[] lockargs = new String[]{"stop", p.getName()};
                this.ExecuteStop((CommandSender)this.getServer().getConsoleSender(), lockargs);
                ++n2;
            }
            this.log.warning(ChatColor.RED + "[Punishmental] Failsafe Activated to prevent server lock-up");
            this.log.warning(ChatColor.RED + "[Punishmental] If the server was running fine when this happened, please consider deactivating or raising the failsafe value in the Punishmental Config Settings");
            this.startLagCheck();
        }
    }

    public boolean RunPunishment(CommandSender sender, String[] args) {
        if (Tasks.containsKey(args[1]) && args.length > 2 && Tasks.get(args[1]) != 0) {
            if (Integer.parseInt(args[3]) <= this.timesdone) {
                this.getServer().getScheduler().cancelTask(Tasks.get(args[1]).intValue());
                Tasks.remove(args[1]);
                this.timesdone = 0;
                return true;
            }
            ++this.timesdone;
        }
        this.advanceFailsafe();
        Player subject = this.getServer().getPlayer(args[1]);
        try {
            BeingPunished.put(subject, Punishments.valueOf(args[0].toUpperCase()));
        }
        catch (Exception ex) {
            sender.sendMessage(ChatColor.YELLOW + "Punishment No Comprende");
            sender.sendMessage(ChatColor.RED + "### soup6020 - PunishmentalEX ###");
            sender.sendMessage(ChatColor.RED + " ERROR found whilst using the punish command");
            sender.sendMessage(ChatColor.RED + "   Unknown Punishment Type: " + args[0]);
            sender.sendMessage(ChatColor.RED + "     Currently installed Punishments:");
            this.printusage(sender);
            return false;
        }
        try {
            subject.isOnline();
        }
        catch (Exception ex) {
            this.log.info("Player logged off");
            sender.sendMessage(ChatColor.RED + "The player logged off: The punishment was stopped");
            if (Tasks.containsKey(args[1])) {
                this.getServer().getScheduler().cancelTask(Tasks.get(args[1]).intValue());
                Tasks.remove(args[1]);
            }
            this.timesdone = 0;
            return true;
        }
        if (!subject.isOnline()) {
            this.log.info("Player logged off");
            sender.sendMessage(ChatColor.RED + "The player logged off: The punishment was stopped");
            if (Tasks.containsKey(args[1])) {
                this.getServer().getScheduler().cancelTask(Tasks.get(args[1]).intValue());
                Tasks.remove(args[1]);
            }
            this.timesdone = 0;
            return true;
        }
        if (!sender.hasPermission("punish." + args[0].toLowerCase())) {
            sender.sendMessage(ChatColor.RED + "You do not have the correct permissions (" + "punish." + args[0] + ") to run this command");
            return true;
        }
        Punishments punishment = Punishments.valueOf(args[0].toUpperCase());
        if (PunishmentUsage.containsKey((Object)punishment)) {
            int times = PunishmentUsage.get((Object)punishment);
            PunishmentUsage.remove((Object)punishment);
            PunishmentUsage.put(punishment, ++times);
        } else {
            PunishmentUsage.put(punishment, 1);
        }
        switch (punishment) {
            case ANNOY: {
                this.ExecuteAnnoy(sender, args);
                break;
            }
            case ANVIL: {
                this.ExecuteAnvil(sender, args);
                break;
            }
            case ARROW: {
                this.ExecuteArrow(sender, args);
                break;
            }
            case BABBLE: {
                this.ExecuteBabble(sender, args);
                break;
            }
            case BLIND: {
                this.ExecuteBlind(sender, args);
                break;
            }
            case BLOCK: {
                this.ExecuteBlock(sender, args);
                break;
            }
            case BURN: {
                this.ExecuteIgnite(sender, args);
                break;
            }
            case CAGE: {
                this.ExecuteCage(sender, args);
                break;
            }
            case CHAT: {
                this.ExecuteChat(sender, args);
                break;
            }
            case CHATROLL: {
                this.ExecuteChatTroll(sender, args);
                break;
            }
            case CHOKE: {
                this.ExecuteForceChoke(sender, args);
                break;
            }
            case CREEPER: {
                this.ExecuteCreeper(sender, args);
                break;
            }
            case DOWNLEVEL: {
                this.ExecuteLevelDown(sender, args);
                break;
            }
            case DRAGON: {
                sender.sendMessage(ChatColor.RED + "Sorry, but due to stability issues, that punishment has been removed");
                break;
            }
            case DROP: {
                this.ExecuteDrop(sender, args);
                break;
            }
            case DRUNK: {
                this.ExecuteDrunk(sender, args);
                break;
            }
            case END: {
                this.ExecuteEnd(sender, args);
                break;
            }
            case EXBLOCK: {
                this.ExecuteExblock(sender, args);
                break;
            }
            case EXPLODE: {
                this.ExecuteExplode(sender, args);
                break;
            }
            case FALL: {
                this.ExecuteFall(sender, args);
                break;
            }
            case FLAMINGARROW: {
                this.ExecuteFlamingArrow(sender, args);
                break;
            }
            case FLOORPORTAL: {
                this.ExecuteFloorPortal(sender, args);
                break;
            }
            case FRAGILE: {
                sender.sendMessage(ChatColor.RED + "Sorry, but due to stability issues, that punishment has been removed");
                break;
            }
            case FREEZE: {
                this.ExecuteFreeze(sender, args);
                break;
            }
            case GLASS: {
                this.ExecuteGlass(sender, args);
                break;
            }
            case HAILFIRE: {
                this.ExecuteHailFire(sender, args);
                break;
            }
            case HOLE: {
                this.ExecuteHole(sender, args);
                break;
            }
            case HOSTILE: {
                this.ExecuteHostile(sender, args);
                break;
            }
            case HOUNDS: {
                this.ReleaseTheHounds(sender, args);
                break;
            }
            case INFALL: {
                this.ExecuteInfall(sender, args);
                break;
            }
            case INVOID: {
                this.ExecuteInVoid(sender, args);
                break;
            }
            case IRONGOLEM: {
                this.ExecuteIronGolem(sender, args);
                break;
            }
            case KICK: {
                this.ExecuteKick(sender, args);
                break;
            }
            case LAVABLOCK: {
                this.ExecuteLavablock(sender, args);
                break;
            }
            case MUTE: {
                this.ExecuteMute(sender, args);
                break;
            }
            case NETHER: {
                this.ExecuteNether(sender, args);
                break;
            }
            case NIGHT: {
                this.ExecuteNight(sender, args);
                break;
            }
            case PARANOIA: {
                this.ExecuteParanoia(sender, args);
                break;
            }
            case POISON: {
                this.ExecutePoison(sender, args);
                break;
            }
            case RENAME: {
                this.ExecuteRename(sender, args);
                break;
            }
            case RESTORE: {
                this.RestoreDamaged(sender, args);
                break;
            }
            case REVERSE: {
                this.ExecuteReverse(sender, args);
                break;
            }
            case REWIND: {
                this.ExecuteRewind(sender, args);
                break;
            }
            case ROTATE: {
                this.ExecuteRotate(sender, args);
                break;
            }
            case SHOOT: {
                this.ExecuteShoot(sender, args);
                break;
            }
            case SLAP: {
                this.ExecuteSlap(sender, args);
                break;
            }
            case SLENDER: {
                this.ExecuteSlender(sender, args);
                break;
            }
            case SLOW: {
                this.ExecuteSlow(sender, args);
                break;
            }
            case SPAM: {
                this.ExecuteSpam(sender, args);
                break;
            }
            case LAG: {
                this.ExecuteLAG(sender, args);
                break;
            }
            case STARVE: {
                this.ExecuteStarve(sender, args);
                break;
            }
            case STOP: {
                this.ExecuteStop(sender, args);
                break;
            }
            case STRIKE: {
                this.ExecuteStrike(sender, args);
                break;
            }
            case STRIP: {
                this.ExecuteStrip(sender, args);
                break;
            }
            case SURROUND: {
                this.ExecuteSurround(sender, args);
                break;
            }
            case UNAWARE: {
                this.ExecuteUnaware(sender, args);
                break;
            }
            case VOID: {
                this.ExecuteVoid(sender, args);
                break;
            }
            case FEXPLODE: {
                this.ExecutefExplode(sender, args);
                break;
            }
            case FSTRIKE: {
                this.ExecutefStrike(sender, args);
                break;
            }
            case FAKEOP: {
                this.ExecuteFakeOp(sender, args);
                break;
            }
            case WHITE: {
                this.ExecuteWhite(sender, args);
                break;
            }
            case POPUP: {
                this.ExecutePopup(sender, args);
                break;
            }
            case BOOTY: {
                this.ExecuteBooty(sender, args);
                break;
            }
            case POPULAR: {
                this.ExecutePopular(sender, args);
                break;
            }
            case ARMOUR: {
                this.ExecuteArmour(sender, args);
                break;
            }
            case PUMPKIN: {
                this.ExecutePumpkin(sender, args);
                break;
            }
            case POTATO: {
                this.ExecutePotato(sender, args);
                break;
            }
            case DISPLAY: {
                this.ExecuteDisplay(sender, args);
                break;
            }
            case TREE: {
                this.ExecuteTree(sender, args);
                break;
            }
            case HISS: {
                this.ExecuteHiss(sender, args);
                break;
            }
            case AMBIANCE: {
                this.ExecuteAmbiance(sender, args);
                break;
            }
            case MAIM: {
                this.ExecuteMaim(sender, args);
                break;
            }
            case BRITTLE: {
                this.ExecuteBrittle(sender, args);
                break;
            }
            case TNTTRICK: {
                this.ExecuteTNTTrick(sender, args);
                break;
            }
            case TNT: {
                this.ExecuteTNT(sender, args);
                break;
            }
            case FIRE: {
                this.ExecuteFire(sender, args);
                break;
            }
            case SQUID: {
                this.ExecuteSquid(sender, args);
                break;
            }
            case CRASH: {
                sender.sendMessage(ChatColor.RED + " This punishment has been removed to conform with BukkitDev Regulations");
                break;
            }
            case TELEPORT: {
                this.ExecuteTeleport(sender, args);
                break;
            }
            case USELESS: {
                this.ExecuteUseless(sender, args);
                break;
            }
            case IDTHEFT: {
                this.ExecuteIDTheft(sender, args);
                break;
            }
            case WRONG: {
                this.ExecuteWrong(sender, args);
                break;
            }
            case POTATE: {
                this.ExecutePotate(sender, args);
                break;
            }
            case SCREAM: {
                this.ExecuteScream(sender, args);
                break;
            }
            case TRIP: {
                this.ExecuteTrip(sender, args);
                break;
            }
            case NUKE: {
                this.ExecuteNuke(sender, args);
                break;
            }
            case BOMB: {
                this.ExecuteBomb(sender, args);
                break;
            }
            case ARAB: {
                this.ExecuteArab(sender, args);
                break;
            }
            case METRO: {
                this.ExecuteMetro(sender, args);
                break;
            }
            case TEST: {
                this.ExecuteTest(sender, args);
                break;
            }
            default: {
                this.printusage(sender);
                sender.sendMessage(ChatColor.YELLOW + "Punishment No Comprende");
                sender.sendMessage(ChatColor.RED + "### soup6020 - PunishmentalEX ###");
                sender.sendMessage(ChatColor.RED + " ERROR found whilst using the punish command");
                sender.sendMessage(ChatColor.RED + "   Unknown Punishment Type: " + args[0]);
                return false;
            }
        }
        return true;
    }

    public void printusage(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_AQUA + "    Stop: Stops the punishment without killing");
        sender.sendMessage(ChatColor.AQUA + "Fall:" + ChatColor.DARK_AQUA + " Drops Player from block 200");
        sender.sendMessage(ChatColor.AQUA + "InFall:" + ChatColor.DARK_AQUA + " Makes the player fall continueously until you END them");
        sender.sendMessage(ChatColor.AQUA + "Explode:" + ChatColor.DARK_AQUA + " Causes an explosion at the players position");
        sender.sendMessage(ChatColor.AQUA + "Fexplode:" + ChatColor.DARK_AQUA + " Creates an explosion that will hurt the player, but not cause damage to the surroundings");
        sender.sendMessage(ChatColor.AQUA + "Strike:" + ChatColor.DARK_AQUA + " Hits player with lightning");
        sender.sendMessage(ChatColor.AQUA + "Fstrike:" + ChatColor.DARK_AQUA + " Hits the player with lightning, but will not cause damage to the surroundings");
        sender.sendMessage(ChatColor.AQUA + "Hounds:" + ChatColor.DARK_AQUA + " Releases a wolf at the players location");
        sender.sendMessage(ChatColor.AQUA + "End:" + ChatColor.DARK_AQUA + " Kills the player");
        sender.sendMessage(ChatColor.AQUA + "Creeper:" + ChatColor.DARK_AQUA + " Spawns a creeper next to them");
        sender.sendMessage(ChatColor.AQUA + "Void:" + ChatColor.DARK_AQUA + " Transports the player to the void");
        sender.sendMessage(ChatColor.AQUA + "InVoid:" + ChatColor.DARK_AQUA + " Spawns and respawns in the void until you END them");
        sender.sendMessage(ChatColor.AQUA + "Lavablock:" + ChatColor.DARK_AQUA + " The next block the player breaks will turn into lava");
        sender.sendMessage(ChatColor.AQUA + "Hole:" + ChatColor.DARK_AQUA + " The player falls down a freshly dug pit");
        sender.sendMessage(ChatColor.AQUA + "Teleport:" + ChatColor.DARK_AQUA + " Teleports the player to a random location");
        sender.sendMessage(ChatColor.AQUA + "Freeze:" + ChatColor.DARK_AQUA + " Freezes the player still until /end or /stop");
        sender.sendMessage(ChatColor.AQUA + "IronGolem:" + ChatColor.DARK_AQUA + " Spawns an IronGolem that is targeted at the player");
        sender.sendMessage(ChatColor.AQUA + "Hailfire:" + ChatColor.DARK_AQUA + " Send a fireball from the sky to hit the player");
        sender.sendMessage(ChatColor.AQUA + "Paranoia:" + ChatColor.DARK_AQUA + " Plays weird sound effects to only the player");
        sender.sendMessage(ChatColor.AQUA + "Blind:" + ChatColor.DARK_AQUA + " Blinds the player, making them unable to see");
        sender.sendMessage(ChatColor.AQUA + "Drunk:" + ChatColor.DARK_AQUA + " Makes the players vision wobbely and unusable (goes well with paranoia)");
        sender.sendMessage(ChatColor.AQUA + "Starve:" + ChatColor.DARK_AQUA + " Rapidly depleets the players hunger bar");
        sender.sendMessage(ChatColor.AQUA + "Slow:" + ChatColor.DARK_AQUA + " Slows the players movment speed");
        sender.sendMessage(ChatColor.AQUA + "Poison:" + ChatColor.DARK_AQUA + " Poisons the player, depleting their health");
        sender.sendMessage(ChatColor.AQUA + "Strip:" + ChatColor.DARK_AQUA + " Removes all the players possesions");
        sender.sendMessage(ChatColor.AQUA + "FloorPortal:" + ChatColor.DARK_AQUA + " Turns the block beneath the player into a portal, and lets them fall through");
        sender.sendMessage(ChatColor.AQUA + "Nether:" + ChatColor.DARK_AQUA + " Teleports the player to the nether");
        sender.sendMessage(ChatColor.AQUA + "Chatroll:" + ChatColor.DARK_AQUA + " Makes the player say the text you put, multiple times >ADD TEXT TO SAY AFTER DELAY<");
        sender.sendMessage(ChatColor.AQUA + "Glass:" + ChatColor.DARK_AQUA + " Puts the player on a platform of glass in the air");
        sender.sendMessage(ChatColor.AQUA + "Shoot:" + ChatColor.DARK_AQUA + " Fires the player off into the sky");
        sender.sendMessage(ChatColor.AQUA + "Babble:" + ChatColor.DARK_AQUA + " Turnes all the players words into meaningless jibberish");
        sender.sendMessage(ChatColor.AQUA + "Lag:" + ChatColor.DARK_AQUA + " Gives the player the visual effect that they are lagging");
        sender.sendMessage(ChatColor.AQUA + "Unaware:" + ChatColor.DARK_AQUA + " Makes the player no longer see other players");
        sender.sendMessage(ChatColor.AQUA + "Leveldown:" + ChatColor.DARK_AQUA + " Puts the player back to level zero.");
        sender.sendMessage(ChatColor.AQUA + "Annoy:" + ChatColor.DARK_AQUA + " Sends out a villager targeted at them. Specify the amount of villagers by adding ... 0 [number of villagers] to the end of the punishment");
        sender.sendMessage(ChatColor.AQUA + "Surround:" + ChatColor.DARK_AQUA + " Moves all the entities within 50 blocks to the player");
        sender.sendMessage(ChatColor.AQUA + "FlamingArrow:" + ChatColor.DARK_AQUA + " Fires a flaming arrow at the target. Specify the amount of arrows by adding ... 0 [number of arrows] to the end of the punishment");
        sender.sendMessage(ChatColor.AQUA + "Rename:" + ChatColor.DARK_AQUA + " Changes the players display name. Specify the new name by adding ... [Newname] to the end of the punishment");
        sender.sendMessage(ChatColor.AQUA + "Kick:" + ChatColor.DARK_AQUA + " Kicks the player with a custom message. Specify the kick message by adding ... [Msg] to the end of the punishment");
        sender.sendMessage(ChatColor.AQUA + "Block:" + ChatColor.DARK_AQUA + " Prevents the player from placing blocks");
        sender.sendMessage(ChatColor.AQUA + "Chat:" + ChatColor.DARK_AQUA + " Make the player say something");
        sender.sendMessage(ChatColor.AQUA + "Night:" + ChatColor.DARK_AQUA + " Sets the players Local time to night. Does not affect server");
        sender.sendMessage(ChatColor.AQUA + "Rewind:" + ChatColor.DARK_AQUA + " Makes the player's day run 20x backwards. Does not affect server");
        sender.sendMessage(ChatColor.AQUA + "Slap:" + ChatColor.DARK_AQUA + " Makes the player fly backwards");
        sender.sendMessage(ChatColor.AQUA + "Rotate:" + ChatColor.DARK_AQUA + " Makes the player's view turn upsidedown");
        sender.sendMessage(ChatColor.AQUA + "Exblock:" + ChatColor.DARK_AQUA + " Makes the next block the player destroys explode");
        sender.sendMessage(ChatColor.AQUA + "Hostile:" + ChatColor.DARK_AQUA + " Makes the mobs around the player turn hostile and target the player");
        sender.sendMessage(ChatColor.AQUA + "Mute:" + ChatColor.DARK_AQUA + " Makes the player unable to chat");
        sender.sendMessage(ChatColor.AQUA + "Slender:" + ChatColor.DARK_AQUA + " Nuff' Said.");
        sender.sendMessage(ChatColor.AQUA + "Drop:" + ChatColor.DARK_AQUA + " Makes the player disconnect with the impression they lost connection");
        sender.sendMessage(ChatColor.AQUA + "Spam:" + ChatColor.DARK_AQUA + " Sends the player constant annoying messages");
        sender.sendMessage(ChatColor.AQUA + "Anvil:" + ChatColor.DARK_AQUA + " Drops an Anvil on the player. Set damage to 20 to freeze the player while the anvil is falling.");
        sender.sendMessage(ChatColor.AQUA + "Cage:" + ChatColor.DARK_AQUA + " Builds a cage made of anvils around the player");
        sender.sendMessage(ChatColor.AQUA + "FakeOP:" + ChatColor.DARK_AQUA + " Sends the player a fake 'You have been Opped' message");
        sender.sendMessage(ChatColor.AQUA + "White:" + ChatColor.DARK_AQUA + " Turns the players world white");
        sender.sendMessage(ChatColor.AQUA + "Popup:" + ChatColor.DARK_AQUA + " Pops up the players inventory for no reason");
        sender.sendMessage(ChatColor.AQUA + "Booty:" + ChatColor.DARK_AQUA + " Opens the player's inventory to everyone else on the server");
        sender.sendMessage(ChatColor.AQUA + "Popular:" + ChatColor.DARK_AQUA + " Teleports every player on the server to this player");
        sender.sendMessage(ChatColor.AQUA + "Pumpkin:" + ChatColor.DARK_AQUA + " Makes the player wear a pumpkin on their head");
        sender.sendMessage(ChatColor.AQUA + "Armour:" + ChatColor.DARK_AQUA + " Removes the players armour in favor for empty buckets");
        sender.sendMessage(ChatColor.AQUA + "Display:" + ChatColor.DARK_AQUA + " Freezes a player in a glass cage");
        sender.sendMessage(ChatColor.AQUA + "Potato:" + ChatColor.DARK_AQUA + " Potato");
        sender.sendMessage(ChatColor.AQUA + "Tree:" + ChatColor.DARK_AQUA + " Surrounds the player with trees");
        sender.sendMessage(ChatColor.AQUA + "Hiss:" + ChatColor.DARK_AQUA + " Makes the player hear a creeper hissing");
        sender.sendMessage(ChatColor.AQUA + "Ambiance:" + ChatColor.DARK_AQUA + " Plays ambiance noise to the player");
        sender.sendMessage(ChatColor.AQUA + "Maim:" + ChatColor.DARK_AQUA + " Prevents the player from picking up items");
        sender.sendMessage(ChatColor.AQUA + "Brittle:" + ChatColor.DARK_AQUA + " The next time the player falls/jumps, they'll die.");
        sender.sendMessage(ChatColor.AQUA + "TNT:" + ChatColor.DARK_AQUA + " Surrounds the player with TNT and ignites it");
        sender.sendMessage(ChatColor.AQUA + "Tnttrick:" + ChatColor.DARK_AQUA + " Surrounds the player with TNT, then makes a hissing noise, but does not explode it");
        sender.sendMessage(ChatColor.AQUA + "Squid:" + ChatColor.DARK_AQUA + " Makes a squid fall from the sky. Works well with repeating arguments.");
        sender.sendMessage(ChatColor.AQUA + "Crash:" + ChatColor.DARK_AQUA + " Crashes the player's client, requiring a minecraft restart");
        sender.sendMessage(ChatColor.AQUA + "Useless:" + ChatColor.DARK_AQUA + " Renames the player's inventory to 'Useless'");
        sender.sendMessage(ChatColor.AQUA + "IDTheft:" + ChatColor.DARK_AQUA + " Changes the player's name everytime they move");
        sender.sendMessage(ChatColor.AQUA + "Wrong:" + ChatColor.DARK_AQUA + " Changes the player's inentory to incorrect names");
        sender.sendMessage(ChatColor.AQUA + "Potate:" + ChatColor.DARK_AQUA + " -verb, as in 'Jacob was potating'");
        sender.sendMessage(ChatColor.AQUA + "Scream:" + ChatColor.DARK_AQUA + " Plays a ghast scream at full volume to the player");
        sender.sendMessage(ChatColor.AQUA + "Trip:" + ChatColor.DARK_AQUA + " Makes the player drop all their items");
        sender.sendMessage(ChatColor.AQUA + "Arab:" + ChatColor.DARK_AQUA + " Translates all the player's chats into arabic");
        sender.sendMessage(ChatColor.AQUA + "Metro:" + ChatColor.DARK_AQUA + " Translates all the player's chats into several different languages");
        sender.sendMessage(ChatColor.AQUA + "Bomb:" + ChatColor.DARK_AQUA + " Gradually carpet-bombs the player's location");
        sender.sendMessage(ChatColor.AQUA + "Nuke:" + ChatColor.DARK_AQUA + " Explodes a player with the power of 30 blocks of TNT (may slow down small servers)");
        sender.sendMessage(ChatColor.RED + "/Punish [Punishment] [Username/All] [Opt. damage/x/y] [amount of times] [delay] [Other arguments]");
        sender.sendMessage(ChatColor.GRAY + "The above list is Scrollable");
    }

    public boolean SecurityCheck(Player player) {
        return player.hasPermission("punish.slap") || player.hasPermission("punish.nuke") || player.hasPermission("punish.bomb") || player.hasPermission("punish.scream") || player.hasPermission("punish.wrong") || player.hasPermission("punish.trip") || player.hasPermission("punish.arab") || player.hasPermission("punish.metro") || player.hasPermission("punish.wrong") || player.hasPermission("punish.idtheft") || player.hasPermission("punish.useless") || player.hasPermission("punish.crash") || player.hasPermission("punish.squid") || player.hasPermission("punish.exblock") || player.hasPermission("punish.hostile") || player.hasPermission("punish.rewind") || player.hasPermission("punish.night") || player.hasPermission("punish.chat") || player.hasPermission("punish.block") || player.hasPermission("punish.fragile") || player.hasPermission("punish.kick") || player.hasPermission("punish.rename") || player.hasPermission("punish.flamingarrow") || player.hasPermission("punish.surround") || player.hasPermission("punish.annoy") || player.hasPermission("punish.leveldown") || player.hasPermission("punish.unaware") || player.hasPermission("punish.lag") || player.hasPermission("punish.choke") || player.hasPermission("punish.stop") || player.hasPermission("punish.shoot") || player.hasPermission("punish.glass") || player.hasPermission("punish.chatroll") || player.hasPermission("punish.floorportal") || player.hasPermission("punish.strip") || player.hasPermission("punish.blind") || player.hasPermission("punish.drunk") || player.hasPermission("punish.starve") || player.hasPermission("punish.slow") || player.hasPermission("punish.poison") || player.hasPermission("punish.paranoia") || player.hasPermission("punish.hailfire") || player.hasPermission("punish.restore") || player.hasPermission("punish.irongolem") || player.hasPermission("punish.freeze") || player.hasPermission("punish.teleport") || player.hasPermission("punish.end") || player.hasPermission("punish.teleport") || player.hasPermission("punish.lavablock") || player.hasPermission("punish.fall") || player.hasPermission("punish.hole") || player.hasPermission("punish.creeper") || player.hasPermission("punish.strike") || player.hasPermission("punish.explode") ||  player.hasPermission("punish.infall") || player.hasPermission("punish.void") || player.hasPermission("punish.invoid") || player.hasPermission("punish.hounds") || player.hasPermission("punish.mute") || player.hasPermission("punish.drop") || player.hasPermission("punish.anvil") || player.hasPermission("punish.cage") || player.hasPermission("punish.fakeop") || player.hasPermission("punish.popup") || player.hasPermission("punish.white") || player.hasPermission("punish.booty") || player.hasPermission("punish.display") || player.hasPermission("punish.potato") || player.hasPermission("punish.pumpkin") || player.hasPermission("punish.armour") || player.hasPermission("punish.popular") || player.hasPermission("punish.trees") || player.hasPermission("punish.hiss") || player.hasPermission("punish.ambiance") || player.hasPermission("punish.maim") || player.hasPermission("punish.brittle") || player.hasPermission("punish.burn") || player.hasPermission("punish.tnttrick") || player.hasPermission("punish.tnt") || player.hasPermission("punish.fire");
    }

    class ExceptionHandler
    implements Thread.UncaughtExceptionHandler {
        ExceptionHandler() {
        }

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            this.handle(e);
        }

        public void handle(Throwable throwable) {
            try {
                StackTraceElement ste;
                Fall.this.log.info(" ------------------------ PUNISHMENTAL ERROR REPORT:");
                Fall.this.log.info(throwable.getMessage());
                Fall.this.log.info(throwable.getLocalizedMessage());
                Fall.this.log.info("STACK TRACE:");
                StackTraceElement[] stackTraceElementArray = throwable.getStackTrace();
                int n = stackTraceElementArray.length;
                int n2 = 0;
                while (n2 < n) {
                    ste = stackTraceElementArray[n2];
                    Fall.this.log.info(ste.toString());
                    ++n2;
                }
                Fall.this.log.info("END STACK TRACE:");
                Fall.this.log.info("Caused by: " + throwable.getCause().getMessage());
                Fall.this.log.info("@ ");
                Fall.this.log.info("STACK TRACE:");
                stackTraceElementArray = throwable.getCause().getStackTrace();
                n = stackTraceElementArray.length;
                n2 = 0;
                while (n2 < n) {
                    ste = stackTraceElementArray[n2];
                    Fall.this.log.info(ste.toString());
                    ++n2;
                }
                Fall.this.log.info("END STACK TRACE:");
                Fall.this.log.info(" ------------------------ ERROR REPORT ENDS ----------");
            }
            catch (Throwable throwable2) {
                // empty catch block
            }
        }

        public void registerExceptionHandler() {
            Fall.this.log.info("Registered Error Handler");
            Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
            System.setProperty("sun.awt.exception.handler", ExceptionHandler.class.getName());
        }
    }

    public static enum Punishments {
        FALL(1),
        EXPLODE(2),
        END(3),
        INFALL(4),
        INVOID(5),
        IRONGOLEM(6),
        CREEPER(7),
        HOUNDS(8),
        WEB(9),
        VOID(10),
        HOLE(11),
        LAVABLOCK(12),
        BURN(13),
        STRIKE(14),
        FREEZE(15),
        RESTORE(16),
        HAILFIRE(17),
        FEXPLODE(18),
        FSTRIKE(19),
        PARANOIA(20),
        SLOW(21),
        DRUNK(22),
        BLIND(23),
        STARVE(24),
        POISON(25),
        NETHER(26),
        STRIP(27),
        FLOORPORTAL(28),
        CHATROLL(29),
        STOP(30),
        GLASS(31),
        SHOOT(32),
        ARROW(33),
        BABBLE(34),
        LAG(35),
        CHOKE(36),
        UNAWARE(37),
        DOWNLEVEL(38),
        ANNOY(39),
        SURROUND(40),
        ROTATE(41),
        FLAMINGARROW(42),
        REVERSE(43),
        RENAME(44),
        KICK(45),
        FRAGILE(46),
        BLOCK(47),
        CHAT(48),
        NIGHT(49),
        REWIND(50),
        DRAGON(51),
        SLAP(52),
        EXBLOCK(53),
        HOSTILE(54),
        SLENDER(55),
        MUTE(56),
        DROP(57),
        SPAM(58),
        ANVIL(59),
        CAGE(60),
        FAKEOP(61),
        WHITE(62),
        POPUP(63),
        BOOTY(64),
        DEAF(64),
        POPULAR(65),
        TEST(66),
        PUMPKIN(67),
        ARMOUR(68),
        POTATO(69),
        DISPLAY(70),
        TREE(71),
        HISS(72),
        AMBIANCE(73),
        MAIM(74),
        BRITTLE(75),
        TNTTRICK(76),
        TNT(77),
        FIRE(78),
        SQUID(79),
        CRASH(80),
        USELESS(81),
        IDTHEFT(82),
        WRONG(83),
        TELEPORT(84),
        POTATE(85),
        SCREAM(86),
        TRIP(87),
        NUKE(88),
        BOMB(89),
        ARAB(90),
        METRO(91);

        private int value;

        private Punishments(int value) {
            this.value = value;
        }
    }

    public static class autopunishData {
        public static int ExplodeBlock;
        public static int CreeperBlock;
        public static int FallBlock;
        public static boolean Enabled;
        public static boolean LavaBucket;
        public static boolean WaterBucket;
        public static boolean FlintAndSteel;
        public static int AnvilBlock;
    }

    public static class configSettings {
        public static int ParanoiaProbability;
        public static int HoleDepth;
        public static int SurroundDistance;
        public static int HostileRange;
        public static boolean RemoveCreative;
        public static boolean ErrorReport;
        public static boolean Failsafe;
        public static int FailsafeTrigger;
        public static int FailsafeLagMargin;
        public static boolean UsePunishmentLevels;
    }
}

