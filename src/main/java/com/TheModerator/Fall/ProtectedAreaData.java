/*
 * Decompiled with CFR 0.151.
 */
package com.TheModerator.Fall;

import com.TheModerator.Fall.SimpleXZ;
import java.io.Serializable;
import java.util.ArrayList;

public class ProtectedAreaData
implements Serializable {
    public static final long serialVersionUID = -2558584604174254824L;
    public int version;
    public String punishment;
    public String worldname;
    public int minY;
    public boolean active;
    public int maxY;
    public ArrayList<SimpleXZ> points;
    public String ID;
    public ArrayList<String> AllowedPlayers = new ArrayList();
    public String Owner;
    boolean LOCKED;
    boolean Deadly;

    ProtectedAreaData() {
        this.AllowedPlayers.add("");
        this.points = new ArrayList();
    }
}

