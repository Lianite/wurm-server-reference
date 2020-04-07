// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures.ai.scripts;

import com.wurmonline.math.Vector2f;
import java.awt.Point;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import com.wurmonline.server.creatures.Creature;

public class UtilitiesAOE
{
    public static HashSet<Creature> getLineAreaCreatures(final Creature c, final float distance, final float width) {
        final Rectangle2D.Float r = new Rectangle2D.Float(c.getPosX() - width / 2.0f, c.getPosY(), width, distance);
        final AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(Creature.normalizeAngle(c.getStatus().getRotation() - 180.0f)), c.getPosX(), c.getPosY());
        final Shape rotatedArea = at.createTransformedShape(r);
        final HashSet<Creature> creatureList = new HashSet<Creature>();
        for (int i = c.getTileX() - ((int)(distance / 4.0f) + 1); i < c.getTileX() + ((int)(distance / 4.0f) + 1); ++i) {
            for (int j = c.getTileY() - ((int)(distance / 4.0f) + 1); j < c.getTileY() + ((int)(distance / 4.0f) + 1); ++j) {
                final VolaTile v = Zones.getTileOrNull(i, j, c.isOnSurface());
                if (v != null) {
                    for (final Creature target : v.getCreatures()) {
                        if (rotatedArea.contains(target.getPosX(), target.getPosY())) {
                            creatureList.add(target);
                        }
                    }
                }
            }
        }
        return creatureList;
    }
    
    public static HashSet<Point> getLineArea(final Creature c, final float distance, final float width) {
        final Rectangle2D.Float r = new Rectangle2D.Float(c.getPosX() - width / 2.0f, c.getPosY(), width, distance);
        final AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(Creature.normalizeAngle(c.getStatus().getRotation() - 180.0f)), c.getPosX(), c.getPosY());
        final Shape rotatedArea = at.createTransformedShape(r);
        final HashSet<Point> tileList = new HashSet<Point>();
        for (int i = c.getTileX() - ((int)(distance / 4.0f) + 1); i < c.getTileX() + ((int)(distance / 4.0f) + 1); ++i) {
            for (int j = c.getTileY() - ((int)(distance / 4.0f) + 1); j < c.getTileY() + ((int)(distance / 4.0f) + 1); ++j) {
                if (rotatedArea.contains(i * 4 + 2, j * 4 + 2)) {
                    tileList.add(new Point(i, j));
                }
            }
        }
        return tileList;
    }
    
    public static HashSet<Creature> getRadialAreaCreatures(final Creature c, final float radius) {
        final HashSet<Creature> creatureList = new HashSet<Creature>();
        for (int tileRadius = (int)(radius / 4.0f), i = c.getTileX() - (tileRadius + 1); i < c.getTileX() + (tileRadius + 1); ++i) {
            for (int j = c.getTileY() - (tileRadius + 1); j < c.getTileY() + (tileRadius + 1); ++j) {
                final VolaTile v = Zones.getTileOrNull(i, j, c.isOnSurface());
                if (v != null) {
                    for (final Creature target : v.getCreatures()) {
                        if ((target.getPosX() - c.getPosX()) * (target.getPosX() - c.getPosX()) + (target.getPosY() - c.getPosY()) * (target.getPosY() - c.getPosY()) < radius * radius) {
                            creatureList.add(target);
                        }
                    }
                }
            }
        }
        return creatureList;
    }
    
    public static HashSet<Point> getRadialArea(final Creature c, final int radius) {
        final HashSet<Point> tileList = new HashSet<Point>();
        for (int i = c.getTileX() - (radius + 1); i < c.getTileX() + (radius + 1); ++i) {
            for (int j = c.getTileY() - (radius + 1); j < c.getTileY() + (radius + 1); ++j) {
                if ((i - c.getTileX()) * (i - c.getTileX()) + (j - c.getTileY()) * (j - c.getTileY()) < radius * radius) {
                    tileList.add(new Point(i, j));
                }
            }
        }
        return tileList;
    }
    
    public static HashSet<Creature> getConeAreaCreatures(final Creature c, final float coneDistance, final int coneAngle) {
        final float attAngle = Creature.normalizeAngle(c.getStatus().getRotation() - 90.0f);
        final Vector2f creaturePoint = new Vector2f(c.getPosX(), c.getPosY());
        final Vector2f testPoint = new Vector2f();
        final HashSet<Creature> creatureList = new HashSet<Creature>();
        for (int coneDistTiles = (int)(coneDistance / 4.0f), i = c.getTileX() - (coneDistTiles + 1); i < c.getTileX() + (coneDistTiles + 1); ++i) {
            for (int j = c.getTileY() - (coneDistTiles + 1); j < c.getTileY() + (coneDistTiles + 1); ++j) {
                final VolaTile v = Zones.getTileOrNull(i, j, c.isOnSurface());
                if (v != null) {
                    for (final Creature target : v.getCreatures()) {
                        if ((target.getPosX() - c.getPosX()) * (target.getPosX() - c.getPosX()) + (target.getPosY() - c.getPosY()) * (target.getPosY() - c.getPosY()) < coneDistance * coneDistance) {
                            testPoint.set(target.getPosX(), target.getPosY());
                            if (Math.abs(getAngleDiff(creaturePoint, testPoint) - attAngle) < coneAngle / 2) {
                                creatureList.add(target);
                            }
                        }
                    }
                }
            }
        }
        return creatureList;
    }
    
    public static HashSet<Point> getConeArea(final Creature c, final int coneDistance, final int coneAngle) {
        final float attAngle = Creature.normalizeAngle(c.getStatus().getRotation() - 90.0f);
        final Vector2f creaturePoint = new Vector2f(c.getPosX(), c.getPosY());
        final Vector2f testPoint = new Vector2f();
        final HashSet<Point> tileList = new HashSet<Point>();
        for (int i = c.getTileX() - (coneDistance + 1); i < c.getTileX() + (coneDistance + 1); ++i) {
            for (int j = c.getTileY() - (coneDistance + 1); j < c.getTileY() + (coneDistance + 1); ++j) {
                if ((i - c.getTileX()) * (i - c.getTileX()) + (j - c.getTileY()) * (j - c.getTileY()) < coneDistance * coneDistance) {
                    testPoint.set(i * 4 + 2, j * 4 + 2);
                    if (Math.abs(getAngleDiff(creaturePoint, testPoint) - attAngle) < coneAngle / 2) {
                        tileList.add(new Point(i, j));
                    }
                }
            }
        }
        return tileList;
    }
    
    public static Vector2f getPointInFrontOf(final Creature c, final float distance) {
        final float attAngle = (float)Math.toRadians(Creature.normalizeAngle(c.getStatus().getRotation() - 90.0f));
        Vector2f toReturn = new Vector2f((float)Math.cos(attAngle) * distance, (float)Math.sin(attAngle) * distance);
        toReturn = toReturn.add(new Vector2f(c.getPosX(), c.getPosY()));
        return toReturn;
    }
    
    private static float getAngleDiff(final Vector2f from, final Vector2f to) {
        float angle = (float)Math.toDegrees(Math.atan2(to.y - from.y, to.x - from.x));
        if (angle < 0.0f) {
            angle += 360.0f;
        }
        return angle;
    }
}
