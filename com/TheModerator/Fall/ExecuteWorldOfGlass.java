/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 */
package com.TheModerator.Fall;

import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ExecuteWorldOfGlass
extends Thread {
    Player subject;
    Logger log = Logger.getLogger("Minecraft");

    ExecuteWorldOfGlass(Player p) {
        this.subject = p;
    }

    @Override
    public void run() {
        this.log.info(ChatColor.GREEN + "Running EWOG");
        int sx = this.subject.getLocation().getBlockX();
        int sy = this.subject.getLocation().getBlockY();
        int sz = this.subject.getLocation().getBlockZ();
        int x = sx;
        int y = sy;
        int z = sz;
        int distance = 0;
        int maxdist = 15;
        while (distance < maxdist) {
            x = sx - distance;
            while (x < sx + distance + 1) {
                y = sy - distance;
                while (y < sy + distance + 1) {
                    z = sz - distance;
                    while (z < sz + distance + 1) {
                        Location lc2 = new Location(this.subject.getWorld(), (double)x, (double)y, (double)z);
                        if (lc2.getBlock().getTypeId() != 0 & lc2.getBlock().getTypeId() != 20) {
                            this.subject.sendBlockChange(lc2, 20, this.subject.getWorld().getBlockAt(lc2).getData());
                            try {
                                Thread.sleep(0L, 1);
                            }
                            catch (InterruptedException e) {
                                this.log.info(ChatColor.RED + "ERROR SLEEPING FOR 1 NS IN EWOG THREAD - INTERRUPTED");
                            }
                        }
                        ++z;
                    }
                    ++y;
                }
                ++x;
            }
            ++distance;
        }
        this.log.info(ChatColor.GREEN + "----------- EWOG FINISHED -----------");
    }
}

