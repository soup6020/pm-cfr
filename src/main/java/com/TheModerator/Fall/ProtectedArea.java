/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.BlockVector2D
 *  com.sk89q.worldedit.bukkit.selections.CuboidSelection
 *  com.sk89q.worldedit.bukkit.selections.Polygonal2DSelection
 *  com.sk89q.worldedit.bukkit.selections.Selection
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.entity.Player
 */
package com.TheModerator.Fall;

import com.TheModerator.Fall.Fall;
import com.TheModerator.Fall.ProtectedAreaData;
import com.TheModerator.Fall.SimpleXZ;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Polygonal2DSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ProtectedArea {
    public Polygonal2DSelection area;
    public ProtectedAreaData PAD = new ProtectedAreaData();
    public List<BlockVector2D> points = new Vector<BlockVector2D>();

    public void updateArea(Selection selection, World world) {
        List<BlockVector2D> tpoints = new Vector();
        ArrayList<SimpleXZ> PADpoints = new ArrayList<SimpleXZ>();
        if (selection instanceof CuboidSelection) {
            CuboidSelection cuboid = (CuboidSelection)selection;
            tpoints.clear();
            BlockVector2D pointA = new BlockVector2D(cuboid.getMaximumPoint().getBlockX(), cuboid.getMaximumPoint().getBlockZ());
            BlockVector2D pointB = new BlockVector2D(cuboid.getMaximumPoint().getBlockX(), cuboid.getMinimumPoint().getBlockZ());
            BlockVector2D pointC = new BlockVector2D(cuboid.getMinimumPoint().getBlockX(), cuboid.getMinimumPoint().getBlockZ());
            BlockVector2D pointD = new BlockVector2D(cuboid.getMinimumPoint().getBlockX(), cuboid.getMaximumPoint().getBlockZ());
            tpoints.add(pointA);
            tpoints.add(pointB);
            tpoints.add(pointC);
            tpoints.add(pointD);
            this.points = new Vector<BlockVector2D>(tpoints);
            PADpoints.clear();
            SimpleXZ XZPA = new SimpleXZ();
            XZPA.x = cuboid.getMaximumPoint().getBlockX();
            XZPA.z = cuboid.getMaximumPoint().getBlockZ();
            SimpleXZ XZPB = new SimpleXZ();
            XZPB.x = cuboid.getMaximumPoint().getBlockX();
            XZPB.z = cuboid.getMinimumPoint().getBlockZ();
            SimpleXZ XZPC = new SimpleXZ();
            XZPB.x = cuboid.getMinimumPoint().getBlockX();
            XZPB.z = cuboid.getMinimumPoint().getBlockZ();
            SimpleXZ XZPD = new SimpleXZ();
            XZPB.x = cuboid.getMinimumPoint().getBlockX();
            XZPB.z = cuboid.getMaximumPoint().getBlockZ();
            PADpoints.add(XZPA);
            PADpoints.add(XZPB);
            PADpoints.add(XZPC);
            PADpoints.add(XZPD);
            this.PAD.points = new ArrayList(PADpoints);
            this.PAD.minY = cuboid.getMinimumPoint().getBlockY();
            this.PAD.maxY = cuboid.getMaximumPoint().getBlockY();
        } else if (selection instanceof Polygonal2DSelection) {
            Polygonal2DSelection polygon = (Polygonal2DSelection)selection;
            tpoints.clear();
            tpoints = polygon.getNativePoints();
            for (BlockVector2D point : tpoints) {
                SimpleXZ XZPA = new SimpleXZ();
                XZPA.x = point.getBlockX();
                XZPA.z = point.getBlockZ();
                PADpoints.add(XZPA);
            }
            this.PAD.points = new ArrayList(PADpoints);
            this.points = new Vector<BlockVector2D>(tpoints);
        }
        this.PAD.worldname = world.getName();
        this.area = new Polygonal2DSelection(world, this.points, this.PAD.minY, this.PAD.maxY);
        this.saveArea();
    }

    public ProtectedArea(String ID, Selection selection, World world, String Punishment, String Owner, boolean deadly) {
        if (selection instanceof CuboidSelection) {
            CuboidSelection cuboid = (CuboidSelection)selection;
            this.points.clear();
            BlockVector2D pointA = new BlockVector2D(cuboid.getMaximumPoint().getBlockX(), cuboid.getMaximumPoint().getBlockZ());
            BlockVector2D pointB = new BlockVector2D(cuboid.getMaximumPoint().getBlockX(), cuboid.getMinimumPoint().getBlockZ());
            BlockVector2D pointC = new BlockVector2D(cuboid.getMinimumPoint().getBlockX(), cuboid.getMinimumPoint().getBlockZ());
            BlockVector2D pointD = new BlockVector2D(cuboid.getMinimumPoint().getBlockX(), cuboid.getMaximumPoint().getBlockZ());
            this.points.add(pointA);
            this.points.add(pointB);
            this.points.add(pointC);
            this.points.add(pointD);
            this.PAD.points.clear();
            SimpleXZ XZPA = new SimpleXZ();
            XZPA.x = cuboid.getMaximumPoint().getBlockX();
            XZPA.z = cuboid.getMaximumPoint().getBlockZ();
            SimpleXZ XZPB = new SimpleXZ();
            XZPB.x = cuboid.getMaximumPoint().getBlockX();
            XZPB.z = cuboid.getMinimumPoint().getBlockZ();
            SimpleXZ XZPC = new SimpleXZ();
            XZPB.x = cuboid.getMinimumPoint().getBlockX();
            XZPB.z = cuboid.getMinimumPoint().getBlockZ();
            SimpleXZ XZPD = new SimpleXZ();
            XZPB.x = cuboid.getMinimumPoint().getBlockX();
            XZPB.z = cuboid.getMaximumPoint().getBlockZ();
            this.PAD.points.add(XZPA);
            this.PAD.points.add(XZPB);
            this.PAD.points.add(XZPC);
            this.PAD.points.add(XZPD);
        } else if (selection instanceof Polygonal2DSelection) {
            Polygonal2DSelection polygon = (Polygonal2DSelection)selection;
            this.points.clear();
            this.points = polygon.getNativePoints();
            for (BlockVector2D point : this.points) {
                SimpleXZ XZPA = new SimpleXZ();
                XZPA.x = point.getBlockX();
                XZPA.z = point.getBlockZ();
                this.PAD.points.add(XZPA);
            }
        }
        this.PAD.minY = (int)selection.getMinimumPoint().getY();
        this.PAD.maxY = (int)((double)selection.getHeight() + selection.getMinimumPoint().getY());
        this.PAD.version = 1;
        this.PAD.LOCKED = false;
        this.PAD.ID = ID;
        this.PAD.Deadly = deadly;
        this.PAD.Owner = Owner;
        this.PAD.worldname = world.getName();
        this.area = new Polygonal2DSelection(world, this.points, this.PAD.minY, this.PAD.maxY);
        this.PAD.punishment = Punishment;
        this.PAD.active = true;
        this.saveArea();
    }

    public ProtectedArea(String ID, Fall plugin) {
        try {
            FileInputStream fileIn = new FileInputStream("plugins/Punishmental/ProtectedAreas/" + ID + ".ppa");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            this.PAD = (ProtectedAreaData)in.readObject();
            this.points.clear();
            for (SimpleXZ XZ : this.PAD.points) {
                BlockVector2D pointA = new BlockVector2D(XZ.x, XZ.z);
                this.points.add(pointA);
            }
            this.area = new Polygonal2DSelection(plugin.getServer().getWorld(this.PAD.worldname), this.points, this.PAD.minY, this.PAD.maxY);
            in.close();
            fileIn.close();
        }
        catch (IOException i) {
            i.printStackTrace();
            return;
        }
        catch (ClassNotFoundException c) {
            System.out.println("File " + ID + " is corrupt or from a different version.");
            c.printStackTrace();
            return;
        }
        System.out.println("Deserialized Test File...");
    }

    public boolean saveArea() {
        try {
            FileOutputStream fileOut = new FileOutputStream("plugins/Punishmental/ProtectedAreas/" + this.PAD.ID + ".ppa");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this.PAD);
            out.close();
            fileOut.close();
            return true;
        }
        catch (IOException i) {
            i.printStackTrace();
            return false;
        }
    }

    public boolean deleteArea() {
        File file = new File("plugins/Punishmental/ProtectedAreas/" + this.PAD.ID + ".ppa");
        return file.delete();
    }

    public boolean ExecuteWrath(Player p) {
        if (!this.PAD.active) {
            return false;
        }
        if (this.PAD.LOCKED) {
            return this.CheckIntrusion(p.getLocation());
        }
        if (p.getName().equalsIgnoreCase(this.PAD.Owner)) {
            return false;
        }
        if (this.PAD.AllowedPlayers.contains(p.getName())) {
            return false;
        }
        return this.CheckIntrusion(p.getLocation());
    }

    public boolean CheckIntrusion(Location loc) {
        return this.area.contains(loc);
    }
}

