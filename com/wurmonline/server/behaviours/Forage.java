// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.Server;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.mesh.GrassData;

public enum Forage
{
    GSHORT_BLUEBERRY(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short)571, 364, (byte)0, 5, 5, 10, 10, ModifiedBy.NOTHING, 0), 
    GSHORT_CORN(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short)569, 32, (byte)0, 8, 8, -5, -5, ModifiedBy.NOTHING, 0), 
    GSHORT_COTTON(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short)570, 144, (byte)0, 5, 5, -5, -5, ModifiedBy.WOUNDED, 15), 
    GSHORT_LINGONBERRY(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short)571, 367, (byte)0, 6, 6, -5, -5, ModifiedBy.NEAR_BUSH, 12), 
    GSHORT_ONION(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short)569, 355, (byte)0, 1, 20, -5, -5, ModifiedBy.NOTHING, 0), 
    GSHORT_POTATO(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short)569, 35, (byte)0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0), 
    GSHORT_PUMPKIN(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short)569, 33, (byte)0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0), 
    GSHORT_STRAWBERRY(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short)571, 362, (byte)0, 5, 5, -5, -5, ModifiedBy.HUNGER, 24), 
    GSHORT_WEMP_PLANT(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short)570, 316, (byte)0, 10, 10, -5, -5, ModifiedBy.NO_TREES, 10), 
    GSHORT_EASTER_EGG(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short)570, 466, (byte)0, 0, 0, -5, -5, ModifiedBy.EASTER, 20), 
    GSHORT_RICE(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short)569, 746, (byte)0, 1, 1, -10, -10, ModifiedBy.NEAR_WATER, 40), 
    GSHORT_IVY(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short)570, 917, (byte)0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0), 
    GSHORT_HOPS(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short)570, 1275, (byte)0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0), 
    GSHORT_ROCK(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short)570, 684, (byte)0, 4, 0, 0, 4, ModifiedBy.NOTHING, 0), 
    GSHORT_SPROUT_HAZELNUT(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short)570, 266, (byte)71, 5, 1, -10, -10, ModifiedBy.NEAR_BUSH, 10), 
    GSHORT_SPROUT_ORANGE(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short)570, 266, (byte)88, 5, 1, -10, -10, ModifiedBy.NEAR_BUSH, 10), 
    GSHORT_SPROUT_RASPBERRY(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short)570, 266, (byte)90, 10, 10, -10, -10, ModifiedBy.NEAR_BUSH, 10), 
    GSHORT_SPROUT_BLUEBERRY(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short)570, 266, (byte)91, 10, 10, -10, -10, ModifiedBy.NEAR_BUSH, 10), 
    GSHORT_CAROT(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short)569, 1133, (byte)0, 3, 3, -5, -5, ModifiedBy.NOTHING, 0), 
    GSHORT_CABBAGE(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short)569, 1134, (byte)0, 4, 4, -5, -5, ModifiedBy.NOTHING, 0), 
    GSHORT_TOMATO(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short)569, 1135, (byte)0, 5, 5, -5, -5, ModifiedBy.NOTHING, 0), 
    GSHORT_SUGARBEET(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short)569, 1136, (byte)0, 3, 3, -5, -5, ModifiedBy.NOTHING, 0), 
    GSHORT_LETTUCE(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short)569, 1137, (byte)0, 2, 5, -5, -5, ModifiedBy.NOTHING, 0), 
    GSHORT_PEAPOD(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short)569, 1138, (byte)0, 5, 2, -5, -5, ModifiedBy.NOTHING, 0), 
    GSHORT_COCOABEAN(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short)570, 1155, (byte)0, 3, 3, -5, -5, ModifiedBy.NOTHING, 0), 
    GSHORT_RASPBERRY(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short)571, 1196, (byte)0, 5, 5, -5, -5, ModifiedBy.HUNGER, 24), 
    GSHORT_CUCUMBER(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short)569, 1247, (byte)0, 5, 5, -5, -5, ModifiedBy.HUNGER, 24), 
    GSHORT_EGG(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short)570, 464, (byte)0, 5, 5, -5, -5, ModifiedBy.NOTHING, 0), 
    GMED_BLUEBERRY(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short)571, 364, (byte)0, 5, 5, 10, 10, ModifiedBy.NOTHING, 0), 
    GMED_CORN(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short)569, 32, (byte)0, 8, 8, -5, -5, ModifiedBy.NOTHING, 0), 
    GMED_COTTON(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short)570, 144, (byte)0, 5, 5, -5, -5, ModifiedBy.WOUNDED, 12), 
    GMED_LINGONBERRY(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short)571, 367, (byte)0, 6, 6, -5, -5, ModifiedBy.NEAR_BUSH, 12), 
    GMED_ONION(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short)569, 355, (byte)0, 1, 20, -5, -5, ModifiedBy.NOTHING, 0), 
    GMED_POTATO(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short)569, 35, (byte)0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0), 
    GMED_PUMPKIN(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short)569, 33, (byte)0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0), 
    GMED_STRAWBERRY(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short)571, 362, (byte)0, 5, 5, -5, -5, ModifiedBy.HUNGER, 24), 
    GMED_WEMP_PLANT(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short)570, 316, (byte)0, 10, 10, -5, -5, ModifiedBy.NO_TREES, 10), 
    GMED_EASTER_EGG(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short)571, 466, (byte)0, 0, 0, -5, -5, ModifiedBy.EASTER, 20), 
    GMED_RICE(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short)569, 746, (byte)0, 1, 1, -10, -10, ModifiedBy.NEAR_WATER, 30), 
    GMED_IVY(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short)570, 917, (byte)0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0), 
    GMED_HOPS(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short)570, 1275, (byte)0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0), 
    GMED_ROCK(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short)570, 684, (byte)0, 4, 0, 0, 4, ModifiedBy.NOTHING, 0), 
    GMED_CAROT(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short)569, 1133, (byte)0, 3, 2, -5, -5, ModifiedBy.NOTHING, 0), 
    GMED_CABBAGE(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short)569, 1134, (byte)0, 2, 2, -5, -5, ModifiedBy.NOTHING, 0), 
    GMED_TOMATO(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short)569, 1135, (byte)0, 4, 4, -5, -5, ModifiedBy.NOTHING, 0), 
    GMED_SUGARBEET(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short)569, 1136, (byte)0, 3, 3, -5, -5, ModifiedBy.NOTHING, 0), 
    GMED_LETTUCE(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short)569, 1137, (byte)0, 3, 3, -5, -5, ModifiedBy.NOTHING, 0), 
    GMED_PEAPOD(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short)569, 1138, (byte)0, 3, 3, -5, -5, ModifiedBy.NOTHING, 0), 
    GMAD_COCOABEAN(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short)570, 1155, (byte)0, 4, 4, -5, -5, ModifiedBy.NOTHING, 0), 
    GMED_RASPBERRY(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short)571, 1196, (byte)0, 5, 5, -5, -5, ModifiedBy.HUNGER, 24), 
    GMED_CUCUMBER(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short)569, 1247, (byte)0, 5, 5, -5, -5, ModifiedBy.HUNGER, 24), 
    GMED_EGG(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short)570, 464, (byte)0, 5, 5, -5, -5, ModifiedBy.NOTHING, 0), 
    GMED_SPROUT_HAZELNUT(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short)570, 266, (byte)71, 5, 1, -10, -10, ModifiedBy.NEAR_BUSH, 10), 
    GMED_SPROUT_ORANGE(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short)570, 266, (byte)88, 5, 1, -10, -10, ModifiedBy.NEAR_BUSH, 10), 
    GMED_SPROUT_RASPBERRY(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short)570, 266, (byte)90, 10, 10, -10, -10, ModifiedBy.NEAR_BUSH, 10), 
    GMED_SPROUT_BLUEBERRY(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short)570, 266, (byte)91, 10, 10, -10, -10, ModifiedBy.NEAR_BUSH, 10), 
    GTALL_BLUEBERRY(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short)571, 364, (byte)0, 5, 5, 10, 10, ModifiedBy.NOTHING, 0), 
    GTALL_CORN(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short)569, 32, (byte)0, 8, 8, -5, -5, ModifiedBy.NOTHING, 0), 
    GTALL_COTTON(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short)570, 144, (byte)0, 5, 5, -5, -5, ModifiedBy.WOUNDED, 12), 
    GTALL_LINGONBERRY(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short)571, 367, (byte)0, 6, 6, -5, -5, ModifiedBy.NEAR_BUSH, 12), 
    GTALL_ONION(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short)569, 355, (byte)0, 1, 20, -5, -5, ModifiedBy.NOTHING, 0), 
    GTALL_POTATO(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short)569, 35, (byte)0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0), 
    GTALL_PUMPKIN(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short)569, 33, (byte)0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0), 
    GTALL_STRAWBERRY(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short)571, 362, (byte)0, 5, 5, -5, -5, ModifiedBy.HUNGER, 24), 
    GTALL_WEMP_PLANT(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short)570, 316, (byte)0, 10, 10, -5, -5, ModifiedBy.NO_TREES, 10), 
    GTALL_EASTER_EGG(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short)571, 466, (byte)0, 0, 0, -5, -5, ModifiedBy.EASTER, 20), 
    GTALL_RICE(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short)569, 746, (byte)0, 1, 1, -10, -10, ModifiedBy.NEAR_WATER, 20), 
    GTALL_IVY(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short)570, 917, (byte)0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0), 
    GTALL_HOPS(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short)570, 1275, (byte)0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0), 
    GTALL_ROCK(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short)570, 684, (byte)0, 4, 0, 0, 4, ModifiedBy.NOTHING, 0), 
    GTALL_CAROT(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short)569, 1133, (byte)0, 4, 4, -5, -5, ModifiedBy.NOTHING, 0), 
    GTALL_CABBAGE(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short)569, 1134, (byte)0, 4, 4, -5, -5, ModifiedBy.NOTHING, 0), 
    GTALL_TOMATO(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short)569, 1135, (byte)0, 4, 4, -5, -5, ModifiedBy.NOTHING, 0), 
    GTALL_SUGARBEET(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short)569, 1136, (byte)0, 4, 4, -5, -5, ModifiedBy.NOTHING, 0), 
    GTALL_LETTUCE(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short)569, 1137, (byte)0, 4, 4, -5, -5, ModifiedBy.NOTHING, 0), 
    GTALL_PEAPOD(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short)569, 1138, (byte)0, 4, 4, -5, -5, ModifiedBy.NOTHING, 0), 
    GTALL_COCOABEAN(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short)570, 1155, (byte)0, 4, 4, -5, -5, ModifiedBy.NOTHING, 0), 
    GTALL_RASPBERRY(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short)571, 1196, (byte)0, 5, 5, -5, -5, ModifiedBy.HUNGER, 24), 
    GTALL_CUCUMBER(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short)569, 1247, (byte)0, 5, 5, -5, -5, ModifiedBy.HUNGER, 24), 
    GTALL_EGG(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short)570, 464, (byte)0, 5, 5, -5, -5, ModifiedBy.NOTHING, 0), 
    GTALL_SPROUT_HAZELNUT(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short)570, 266, (byte)71, 5, 1, -10, -10, ModifiedBy.NEAR_BUSH, 10), 
    GTALL_SPROUT_ORANGE(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short)570, 266, (byte)88, 5, 1, -10, -10, ModifiedBy.NEAR_BUSH, 10), 
    GTALL_SPROUT_RASPBERRY(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short)570, 266, (byte)90, 10, 10, -10, -10, ModifiedBy.NEAR_BUSH, 10), 
    GTALL_SPROUT_BLUEBERRY(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short)570, 266, (byte)91, 10, 10, -10, -10, ModifiedBy.NEAR_BUSH, 10), 
    GWILD_BLUEBERRY(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short)571, 364, (byte)0, 5, 5, 10, 10, ModifiedBy.NOTHING, 0), 
    GWILD_CORN(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short)569, 32, (byte)0, 8, 8, -5, -5, ModifiedBy.NOTHING, 0), 
    GWILD_COTTON(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short)570, 144, (byte)0, 5, 5, -5, -5, ModifiedBy.WOUNDED, 12), 
    GWILD_LINGONBERRY(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short)571, 367, (byte)0, 6, 6, -5, -5, ModifiedBy.NEAR_BUSH, 12), 
    GWILD_ONION(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short)569, 355, (byte)0, 1, 20, -5, -5, ModifiedBy.NOTHING, 0), 
    GWILD_POTATO(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short)569, 35, (byte)0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0), 
    GWILD_PUMPKIN(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short)569, 33, (byte)0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0), 
    GWILD_STRAWBERRY(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short)571, 362, (byte)0, 5, 5, -5, -5, ModifiedBy.HUNGER, 24), 
    GWILD_WEMP_PLANT(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short)570, 316, (byte)0, 10, 10, -5, -5, ModifiedBy.NO_TREES, 10), 
    GWILD_EASTER_EGG(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short)571, 466, (byte)0, 0, 0, -5, -5, ModifiedBy.EASTER, 20), 
    GWILD_RICE(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short)569, 746, (byte)0, 1, 1, -10, -10, ModifiedBy.NEAR_WATER, 10), 
    GWILD_IVY(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short)570, 917, (byte)0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0), 
    GWILD_HOPS(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short)570, 1275, (byte)0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0), 
    GWILD_ROCK(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short)570, 684, (byte)0, 4, 0, 0, 4, ModifiedBy.NOTHING, 0), 
    GWILD_CAROT(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short)569, 1133, (byte)0, 3, 3, -5, -5, ModifiedBy.NOTHING, 0), 
    GWILD_CABBAGE(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short)569, 1134, (byte)0, 3, 3, -5, -5, ModifiedBy.NOTHING, 0), 
    GWILD_TOMATO(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short)569, 1135, (byte)0, 3, 3, -5, -5, ModifiedBy.NOTHING, 0), 
    GWILD_SUGARBEET(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short)569, 1136, (byte)0, 3, 3, -5, -5, ModifiedBy.NOTHING, 0), 
    GWILD_LETTUCE(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short)569, 1137, (byte)0, 3, 3, -5, -5, ModifiedBy.NOTHING, 0), 
    GWILD_PEAPOD(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short)569, 1138, (byte)0, 3, 3, -5, -5, ModifiedBy.NOTHING, 0), 
    GWILD_COCOABEAN(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short)570, 1155, (byte)0, 4, 4, -5, -5, ModifiedBy.NOTHING, 0), 
    GWILD_RASPBERRY(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short)571, 1196, (byte)0, 5, 5, -5, -5, ModifiedBy.HUNGER, 24), 
    GWILD_CUCUMBER(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short)569, 1247, (byte)0, 5, 5, -5, -5, ModifiedBy.HUNGER, 24), 
    GWILD_EGG(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short)570, 464, (byte)0, 5, 5, -5, -5, ModifiedBy.NOTHING, 0), 
    GWILD_SPROUT_HAZELNUT(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short)570, 266, (byte)71, 5, 1, -10, -10, ModifiedBy.NEAR_BUSH, 10), 
    GWILD_SPROUT_ORANGE(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short)570, 266, (byte)88, 5, 1, -10, -10, ModifiedBy.NEAR_BUSH, 10), 
    GWILD_SPROUT_RASPBERRY(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short)570, 266, (byte)90, 10, 10, -10, -10, ModifiedBy.NEAR_BUSH, 10), 
    GWILD_SPROUT_BLUEBERRY(Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short)570, 266, (byte)91, 10, 10, -10, -10, ModifiedBy.NEAR_BUSH, 10), 
    STEPPE_BLUEBERRY(Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short)571, 364, (byte)0, 5, 5, 10, 10, ModifiedBy.NOTHING, 0), 
    STEPPE_CORN(Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short)569, 32, (byte)0, 8, 8, -5, -5, ModifiedBy.NOTHING, 0), 
    STEPPE_COTTON(Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short)570, 144, (byte)0, 5, 5, -5, -5, ModifiedBy.WOUNDED, 12), 
    STEPPE_LINGONBERRY(Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short)571, 367, (byte)0, 6, 6, -5, -5, ModifiedBy.NEAR_BUSH, 12), 
    STEPPE_ONION(Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short)569, 355, (byte)0, 1, 20, -5, -5, ModifiedBy.NOTHING, 0), 
    STEPPE_POTATO(Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short)569, 35, (byte)0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0), 
    STEPPE_PUMPKIN(Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short)569, 33, (byte)0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0), 
    STEPPE_STRAWBERRY(Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short)571, 362, (byte)0, 5, 5, -5, -5, ModifiedBy.HUNGER, 24), 
    STEPPE_WEMP_PLANT(Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short)570, 316, (byte)0, 10, 10, -5, -5, ModifiedBy.NO_TREES, 10), 
    STEPPE_RICE(Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short)569, 746, (byte)0, 1, 1, -10, -10, ModifiedBy.NEAR_WATER, 20), 
    STEPPE_CAROT(Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short)569, 1133, (byte)0, 2, 2, -5, -5, ModifiedBy.NOTHING, 0), 
    STEPPE_CABBAGE(Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short)569, 1134, (byte)0, 2, 2, -5, -5, ModifiedBy.NOTHING, 0), 
    STEPPE_TOMATO(Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short)569, 1135, (byte)0, 2, 2, -5, -5, ModifiedBy.NOTHING, 0), 
    STEPPE_SUGARBEET(Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short)569, 1136, (byte)0, 2, 2, -5, -5, ModifiedBy.NOTHING, 0), 
    STEPPE_LETTUCE(Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short)569, 1137, (byte)0, 2, 2, -5, -5, ModifiedBy.NOTHING, 0), 
    STEPPE_PEAPOD(Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short)569, 1138, (byte)0, 2, 2, -5, -5, ModifiedBy.NOTHING, 0), 
    STEPPE_COCOABEAN(Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short)570, 1155, (byte)0, 4, 4, -5, -5, ModifiedBy.NOTHING, 0), 
    STEPPE_RASPBERRY(Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short)571, 1196, (byte)0, 5, 5, -5, -5, ModifiedBy.HUNGER, 24), 
    STEPPE_CUCUMBER(Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short)569, 1247, (byte)0, 5, 5, -5, -5, ModifiedBy.HUNGER, 24), 
    TUNDRA_BLUEBERRY(Tiles.Tile.TILE_TUNDRA.id, GrassData.GrowthStage.SHORT, (short)571, 364, (byte)0, 25, 25, 10, 10, ModifiedBy.NO_TREES, 10), 
    TUNDRA_COTTON(Tiles.Tile.TILE_TUNDRA.id, GrassData.GrowthStage.SHORT, (short)570, 144, (byte)0, 5, 5, -5, -5, ModifiedBy.WOUNDED, 12), 
    TUNDRA_LINGONBERRY(Tiles.Tile.TILE_TUNDRA.id, GrassData.GrowthStage.SHORT, (short)571, 367, (byte)0, 20, 20, -5, -5, ModifiedBy.NEAR_BUSH, 12), 
    TUNDRA_STRAWBERRY(Tiles.Tile.TILE_TUNDRA.id, GrassData.GrowthStage.SHORT, (short)571, 362, (byte)0, 15, 15, -5, -5, ModifiedBy.HUNGER, 10), 
    TUNDRA_COCOABEAN(Tiles.Tile.TILE_TUNDRA.id, GrassData.GrowthStage.SHORT, (short)570, 1155, (byte)0, 1, 1, -5, -5, ModifiedBy.NOTHING, 0), 
    TUNDRA_RASPBERRY(Tiles.Tile.TILE_TUNDRA.id, GrassData.GrowthStage.SHORT, (short)571, 1196, (byte)0, 5, 5, -5, -5, ModifiedBy.HUNGER, 24), 
    MARSH_BLUEBERRY(Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short)571, 364, (byte)0, 5, 5, 10, 10, ModifiedBy.NOTHING, 0), 
    MARSH_CORN(Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short)569, 32, (byte)0, 8, 8, -5, -5, ModifiedBy.NOTHING, 0), 
    MARSH_COTTON(Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short)570, 144, (byte)0, 5, 5, -5, -5, ModifiedBy.WOUNDED, 12), 
    MARSH_MUSHROOM_RED(Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short)569, 251, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    MARSH_ONION(Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short)569, 355, (byte)0, 1, 20, -5, -5, ModifiedBy.NOTHING, 0), 
    MARSH_POTATO(Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short)569, 35, (byte)0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0), 
    MARSH_PUMPKIN(Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short)569, 33, (byte)0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0), 
    MARSH_STRAWBERRY(Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short)571, 362, (byte)0, 5, 5, -5, -5, ModifiedBy.HUNGER, 20), 
    MARSH_WEMP_PLANT(Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short)570, 316, (byte)0, 10, 10, -5, -5, ModifiedBy.NO_TREES, 10), 
    MARSH_RICE(Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short)569, 746, (byte)0, 1, 1, -10, -10, ModifiedBy.NEAR_WATER, 50), 
    MARSH_SUGARBEET(Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short)569, 1136, (byte)0, 2, 2, -5, -5, ModifiedBy.NOTHING, 0), 
    MARSH_COCOABEAN(Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short)570, 1155, (byte)0, 3, 3, -5, -5, ModifiedBy.NOTHING, 0), 
    MARSH_RASPBERRY(Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short)571, 1196, (byte)0, 5, 5, -5, -5, ModifiedBy.HUNGER, 24), 
    MARSH_CUCUMBER(Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short)569, 1247, (byte)0, 5, 5, -5, -5, ModifiedBy.HUNGER, 24), 
    TSHORT_BLUEBERRY(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short)571, 364, (byte)0, 5, 5, 10, 10, ModifiedBy.NOTHING, 0), 
    TSHORT_BRANCH(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short)570, 688, (byte)0, 18, 0, 0, 18, ModifiedBy.NOTHING, 0), 
    TSHORT_CORN(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short)569, 32, (byte)0, 12, 12, -5, -5, ModifiedBy.NOTHING, 0), 
    TSHORT_COTTON(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short)570, 144, (byte)0, 5, 5, -5, -5, ModifiedBy.WOUNDED, 12), 
    TSHORT_LINGONBERRY(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short)571, 367, (byte)0, 6, 6, -5, -5, ModifiedBy.NEAR_BUSH, 12), 
    TSHORT_MUSHROOM_BLACK(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short)569, 247, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    TSHORT_MUSHROOM_BLUE(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short)569, 250, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    TSHORT_MUSHROOM_BROWN(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short)569, 248, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    TSHORT_MUSHROOM_GREEN(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short)569, 246, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    TSHORT_MUSHROOM_RED(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short)569, 251, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    TSHORT_MUSHROOM_YELLOW(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short)569, 249, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    TSHORT_ONION(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short)569, 355, (byte)0, 1, 20, -5, -5, ModifiedBy.NOTHING, 0), 
    TSHORT_POTATO(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short)569, 35, (byte)0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0), 
    TSHORT_RICE(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short)569, 746, (byte)0, 1, 1, -10, -10, ModifiedBy.NEAR_WATER, 20), 
    TSHORT_COCOABEAN(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short)570, 1155, (byte)0, 2, 2, -5, -5, ModifiedBy.NOTHING, 0), 
    TSHORT_RASPBERRY(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short)571, 1196, (byte)0, 5, 5, -5, -5, ModifiedBy.HUNGER, 24), 
    TMED_BLUEBERRY(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short)571, 364, (byte)0, 5, 5, 10, 10, ModifiedBy.NOTHING, 0), 
    TMED_BRANCH(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short)570, 688, (byte)0, 18, 0, 0, 18, ModifiedBy.NOTHING, 0), 
    TMED_CORN(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short)569, 32, (byte)0, 12, 12, -5, -5, ModifiedBy.NOTHING, 0), 
    TMED_COTTON(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short)570, 144, (byte)0, 5, 5, -5, -5, ModifiedBy.WOUNDED, 12), 
    TMED_LINGONBERRY(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short)571, 367, (byte)0, 6, 6, -5, -5, ModifiedBy.NEAR_BUSH, 12), 
    TMED_MUSHROOM_BLACK(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short)569, 247, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    TMED_MUSHROOM_BLUE(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short)569, 250, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    TMED_MUSHROOM_BROWN(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short)569, 248, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    TMED_MUSHROOM_GREEN(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short)569, 246, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    TMED_MUSHROOM_RED(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short)569, 251, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    TMED_MUSHROOM_YELLOW(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short)569, 249, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    TMED_ONION(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short)569, 355, (byte)0, 1, 20, -5, -5, ModifiedBy.NOTHING, 0), 
    TMED_POTATO(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short)569, 35, (byte)0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0), 
    TMED_RICE(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short)569, 746, (byte)0, 1, 1, -10, -10, ModifiedBy.NEAR_WATER, 20), 
    TMED_COCOABEAN(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short)570, 1155, (byte)0, 3, 3, -5, -5, ModifiedBy.NOTHING, 0), 
    TMED_RASPBERRY(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short)571, 1196, (byte)0, 5, 5, -5, -5, ModifiedBy.HUNGER, 24), 
    TTALL_BLUEBERRY(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short)571, 364, (byte)0, 5, 5, 10, 10, ModifiedBy.NOTHING, 0), 
    TTALL_BRANCH(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short)570, 688, (byte)0, 18, 0, 0, 18, ModifiedBy.NOTHING, 0), 
    TTALL_CORN(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short)569, 32, (byte)0, 12, 12, -5, -5, ModifiedBy.NOTHING, 0), 
    TTALL_COTTON(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short)570, 144, (byte)0, 5, 5, -5, -5, ModifiedBy.WOUNDED, 12), 
    TTALL_LINGONBERRY(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short)571, 367, (byte)0, 6, 6, -5, -5, ModifiedBy.NEAR_BUSH, 12), 
    TTALL_MUSHROOM_BLACK(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short)569, 247, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    TTALL_MUSHROOM_BLUE(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short)569, 250, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    TTALL_MUSHROOM_BROWN(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short)569, 248, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    TTALL_MUSHROOM_GREEN(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short)569, 246, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    TTALL_MUSHROOM_RED(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short)569, 251, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    TTALL_MUSHROOM_YELLOW(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short)569, 249, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    TTALL_ONION(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short)569, 355, (byte)0, 1, 20, -5, -5, ModifiedBy.NOTHING, 0), 
    TTALL_POTATO(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short)569, 35, (byte)0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0), 
    TTALL_RICE(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short)569, 746, (byte)0, 1, 1, -10, -10, ModifiedBy.NEAR_WATER, 20), 
    TTALL_COCOABEAN(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short)570, 1155, (byte)0, 3, 3, -5, -5, ModifiedBy.NOTHING, 0), 
    TTALL_RASPBERRY(Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short)571, 1196, (byte)0, 5, 5, -5, -5, ModifiedBy.HUNGER, 24), 
    BSHORT_BLUEBERRY(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short)571, 364, (byte)0, 5, 5, 10, 10, ModifiedBy.NOTHING, 0), 
    BSHORT_CORN(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short)569, 32, (byte)0, 12, 12, -5, -5, ModifiedBy.NOTHING, 0), 
    BSHORT_COTTON(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short)570, 144, (byte)0, 5, 5, -5, -5, ModifiedBy.WOUNDED, 12), 
    BSHORT_LINGONBERRY(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short)571, 367, (byte)0, 6, 6, -5, -5, ModifiedBy.NEAR_BUSH, 12), 
    BSHORT_MUSHROOM_BLACK(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short)569, 247, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    BSHORT_MUSHROOM_BLUE(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short)569, 250, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    BSHORT_MUSHROOM_BROWN(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short)569, 248, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    BSHORT_MUSHROOM_GREEN(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short)569, 246, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    BSHORT_MUSHROOM_RED(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short)569, 251, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    BSHORT_MUSHROOM_YELLOW(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short)569, 249, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    BSHORT_ONION(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short)569, 355, (byte)0, 1, 20, -5, -5, ModifiedBy.NOTHING, 0), 
    BSHORT_POTATO(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short)569, 35, (byte)0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0), 
    BSHORT_RICE(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short)569, 746, (byte)0, 1, 1, -10, -10, ModifiedBy.NEAR_WATER, 20), 
    BSHORT_TOMATO(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short)569, 1135, (byte)0, 4, 4, -5, -5, ModifiedBy.NOTHING, 0), 
    BSHORT_SUGARBEET(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short)569, 1136, (byte)0, 3, 3, -5, -5, ModifiedBy.NOTHING, 0), 
    BSHORT_PEAPOD(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short)569, 1138, (byte)0, 2, 2, -5, -5, ModifiedBy.NOTHING, 0), 
    BSHORT_COCOABEAN(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short)570, 1155, (byte)0, 3, 3, -5, -5, ModifiedBy.NOTHING, 0), 
    BSHORT_RASPBERRY(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short)571, 1196, (byte)0, 5, 5, -5, -5, ModifiedBy.HUNGER, 24), 
    BMED_BLUEBERRY(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short)571, 364, (byte)0, 5, 5, 10, 10, ModifiedBy.NOTHING, 0), 
    BMED_CORN(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short)569, 32, (byte)0, 12, 12, -5, -5, ModifiedBy.NOTHING, 0), 
    BMED_COTTON(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short)570, 144, (byte)0, 5, 5, -5, -5, ModifiedBy.WOUNDED, 12), 
    BMED_LINGONBERRY(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short)571, 367, (byte)0, 6, 6, -5, -5, ModifiedBy.NEAR_BUSH, 12), 
    BMED_MUSHROOM_BLACK(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short)569, 247, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    BMED_MUSHROOM_BLUE(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short)569, 250, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    BMED_MUSHROOM_BROWN(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short)569, 248, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    BMED_MUSHROOM_GREEN(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short)569, 246, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    BMED_MUSHROOM_RED(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short)569, 251, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    BMED_MUSHROOM_YELLOW(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short)569, 249, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    BMED_ONION(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short)569, 355, (byte)0, 1, 20, -5, -5, ModifiedBy.NOTHING, 0), 
    BMED_POTATO(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short)569, 35, (byte)0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0), 
    BMED_RICE(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short)569, 746, (byte)0, 1, 1, -10, -10, ModifiedBy.NEAR_WATER, 20), 
    BMED_TOMATO(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short)569, 1135, (byte)0, 3, 3, -5, -5, ModifiedBy.NOTHING, 0), 
    BMED_SUGARBEET(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short)569, 1136, (byte)0, 3, 3, -5, -5, ModifiedBy.NOTHING, 0), 
    BMED_PEAPOD(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short)569, 1138, (byte)0, 3, 3, -5, -5, ModifiedBy.NOTHING, 0), 
    BMED_COCOABEAN(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short)570, 1155, (byte)0, 4, 4, -5, -5, ModifiedBy.NOTHING, 0), 
    BMED_RASPBERRY(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short)571, 1196, (byte)0, 5, 5, -5, -5, ModifiedBy.HUNGER, 24), 
    BTALL_BLUEBERRY(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short)571, 364, (byte)0, 5, 5, 10, 10, ModifiedBy.NOTHING, 0), 
    BTALL_CORN(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short)569, 32, (byte)0, 12, 12, -5, -5, ModifiedBy.NOTHING, 0), 
    BTALL_COTTON(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short)570, 144, (byte)0, 5, 5, -5, -5, ModifiedBy.WOUNDED, 12), 
    BTALL_LINGONBERRY(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short)571, 367, (byte)0, 6, 6, -5, -5, ModifiedBy.NEAR_BUSH, 12), 
    BTALL_MUSHROOM_BLACK(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short)569, 247, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    BTALL_MUSHROOM_BLUE(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short)569, 250, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    BTALL_MUSHROOM_BROWN(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short)569, 248, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    BTALL_MUSHROOM_GREEN(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short)569, 246, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    BTALL_MUSHROOM_RED(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short)569, 251, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    BTALL_MUSHROOM_YELLOW(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short)569, 249, (byte)0, 0, 0, -5, -5, ModifiedBy.NEAR_TREE, 12), 
    BTALL_ONION(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short)569, 355, (byte)0, 1, 20, -5, -5, ModifiedBy.NOTHING, 0), 
    BTALL_POTATO(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short)569, 35, (byte)0, 10, 10, -5, -5, ModifiedBy.NOTHING, 0), 
    BTALL_RICE(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short)569, 746, (byte)0, 1, 1, -10, -10, ModifiedBy.NEAR_WATER, 20), 
    BTALL_TOMATO(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short)569, 1135, (byte)0, 4, 4, -5, -5, ModifiedBy.NOTHING, 0), 
    BTALL_SUGARBEET(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short)569, 1136, (byte)0, 2, 2, -5, -5, ModifiedBy.NOTHING, 0), 
    BTALL_PEAPOD(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short)569, 1138, (byte)0, 3, 3, -5, -5, ModifiedBy.NOTHING, 0), 
    BTALL_COCOABEAN(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short)570, 1155, (byte)0, 4, 4, -5, -5, ModifiedBy.NOTHING, 0), 
    BTALL_RASPBERRY(Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short)571, 1196, (byte)0, 5, 5, -5, -5, ModifiedBy.HUNGER, 24);
    
    private final byte tileType;
    private final GrassData.GrowthStage grassLength;
    private final short category;
    private final int itemType;
    private final byte material;
    private final int chanceAt1;
    private final int chanceAt100;
    private final int difficultyAt1;
    private final int difficultyAt100;
    private final ModifiedBy modifiedBy;
    private final int chanceModifier;
    
    private Forage(final byte aTileType, final GrassData.GrowthStage aGrassLength, final short aCategory, final int aItemType, final byte aMaterial, final int aChanceAt1, final int aChanceAt100, final int aDifficultyAt1, final int aDifficultyAt100, final ModifiedBy aModifiedBy, final int aChanceModifier) {
        this.tileType = aTileType;
        this.grassLength = aGrassLength;
        this.category = aCategory;
        this.itemType = aItemType;
        this.material = aMaterial;
        this.chanceAt1 = aChanceAt1;
        this.chanceAt100 = aChanceAt100;
        this.difficultyAt1 = aDifficultyAt1;
        this.difficultyAt100 = aDifficultyAt100;
        this.modifiedBy = aModifiedBy;
        this.chanceModifier = aChanceModifier;
    }
    
    public int getItem() {
        return this.itemType;
    }
    
    public float getDifficultyAt(final int knowledge) {
        final float diff = this.difficultyAt1 + (this.difficultyAt100 - this.difficultyAt1) / 100 * knowledge;
        if (diff < 0.0f) {
            return knowledge + diff;
        }
        return diff;
    }
    
    public static float getQL(final double power, final int knowledge) {
        return Math.min(100.0f, knowledge + (100 - knowledge) * ((float)power / 500.0f));
    }
    
    private float getChanceAt(final Creature performer, final int knowledge, final int tilex, final int tiley) {
        final float chance = this.chanceAt1 + (this.chanceAt100 - this.chanceAt1) / 100 * knowledge;
        return chance + this.modifiedBy.chanceModifier(performer, this.chanceModifier, tilex, tiley);
    }
    
    public byte getMaterial() {
        return this.material;
    }
    
    public static Forage getRandomForage(final Creature performer, final byte aTileType, final GrassData.GrowthStage aGrassLength, final short aCategory, final int knowledge, final int tilex, final int tiley) {
        byte checkType = aTileType;
        final Tiles.Tile theTile = Tiles.getTile(aTileType);
        if (theTile.isMycelium()) {
            checkType = Tiles.Tile.TILE_GRASS.id;
        }
        if (theTile.isNormalTree() || theTile.isMyceliumTree()) {
            checkType = Tiles.Tile.TILE_TREE.id;
        }
        if (theTile.isNormalBush() || theTile.isMyceliumBush()) {
            checkType = Tiles.Tile.TILE_BUSH.id;
        }
        float totalChance = 0.0f;
        for (final Forage f : values()) {
            if (f.tileType == checkType && f.grassLength == aGrassLength) {
                final float chance = f.getChanceAt(performer, knowledge, tilex, tiley);
                if (chance >= 0.0f) {
                    totalChance += chance;
                }
            }
        }
        if (totalChance == 0.0f) {
            return null;
        }
        final int rndChance = Server.rand.nextInt((int)totalChance);
        float runningChance = 0.0f;
        for (final Forage f2 : values()) {
            if (f2.tileType == checkType && (checkType != Tiles.Tile.TILE_GRASS.id || f2.grassLength == aGrassLength)) {
                final float chance2 = f2.getChanceAt(performer, knowledge, tilex, tiley);
                if (chance2 >= 0.0f) {
                    runningChance += chance2;
                    if (rndChance < runningChance) {
                        if (aCategory == 223 || aCategory == f2.category) {
                            return f2;
                        }
                        return null;
                    }
                }
            }
        }
        return null;
    }
    
    public static float getDifficulty(final int templateId, final int knowledge) {
        for (final Forage f : values()) {
            if (f.tileType == Tiles.Tile.TILE_GRASS.id && f.grassLength == GrassData.GrowthStage.SHORT && f.itemType == templateId) {
                return f.getDifficultyAt(knowledge);
            }
        }
        return -1.0f;
    }
}
