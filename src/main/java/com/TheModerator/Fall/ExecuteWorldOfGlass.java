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
        this.log.info(ChatColor.GREEN + "----------- EWOG FINISHED -----------");
    }
}

