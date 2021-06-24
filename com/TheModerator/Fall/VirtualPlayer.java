/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 */
package com.TheModerator.Fall;

import org.bukkit.inventory.ItemStack;

public class VirtualPlayer {
    int Level;
    int XP;
    double Health;
    ItemStack[] inv;
    float exh;
    int Hunger;

    public VirtualPlayer(int lv, int xp, double d, ItemStack[] inventory, float exhaustion, int hunger) {
        this.Level = lv;
        this.XP = xp;
        this.Health = d;
        this.inv = inventory;
        this.exh = exhaustion;
        this.Hunger = hunger;
    }
}

