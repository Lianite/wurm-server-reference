// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

import com.wurmonline.server.items.ItemTemplateFactory;
import java.util.HashMap;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Items;
import java.util.concurrent.ConcurrentHashMap;
import com.wurmonline.server.items.TempItem;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.items.Item;
import java.util.Map;
import java.util.logging.Logger;

public class WaterGenerator implements Comparable<WaterGenerator>
{
    private static final Logger logger;
    public final int x;
    public final int y;
    public final int layer;
    private int height;
    private int lastHeight;
    private int resetHeight;
    private boolean changed;
    private boolean spring;
    private Map<Integer, WaterGenerator> waterPointsY;
    private boolean createItem;
    private static final Map<Integer, WaterGenerator> waterPointsX;
    private Item waterMarker;
    private boolean isReset;
    private static ItemTemplate template;
    
    public WaterGenerator(final int tx, final int ty, final boolean isSpring, final int tlayer, final int waterHeight) {
        this.changed = true;
        this.spring = false;
        this.createItem = true;
        this.isReset = false;
        this.x = tx;
        this.y = ty;
        this.layer = tlayer;
        this.spring = isSpring;
        this.height = waterHeight;
        if (!this.spring) {
            this.putInMatrix(this);
        }
    }
    
    public final void setSpring(final boolean isSpring) {
        this.spring = isSpring;
    }
    
    public WaterGenerator(final int tx, final int ty, final int tlayer, final int waterHeight) {
        this.changed = true;
        this.spring = false;
        this.createItem = true;
        this.isReset = false;
        this.x = tx;
        this.y = ty;
        this.layer = tlayer;
        this.height = waterHeight;
        this.lastHeight = this.height;
        this.putInMatrix(this);
    }
    
    public final boolean shouldCreateItem() {
        return this.createItem;
    }
    
    public final void createItem() {
        try {
            this.waterMarker = new TempItem("" + this.height, WaterGenerator.template, 99.0f, this.x * 4 + 2, this.y * 4 + 2, Zones.calculateHeight(this.x * 4 + 2, this.y * 4 + 2, true), 1.0f, -10L, "");
            Zones.getZone(this.waterMarker.getTileX(), this.waterMarker.getTileY(), true).addItem(this.waterMarker);
        }
        catch (Exception ex) {}
        this.createItem = false;
    }
    
    private final void putInMatrix(final WaterGenerator wg) {
        if (getXGeneral(wg.x) == null) {
            addXGeneral(wg);
            this.addY(wg);
        }
        else {
            final WaterGenerator general = getXGeneral(wg.x);
            general.addY(wg);
        }
    }
    
    public static final WaterGenerator getXGeneral(final int aX) {
        return WaterGenerator.waterPointsX.get(aX);
    }
    
    public final WaterGenerator getY(final int aY) {
        return this.waterPointsY.get(aY);
    }
    
    public static final void addXGeneral(final WaterGenerator wg) {
        WaterGenerator.waterPointsX.put(wg.x, wg);
        wg.generateXMap();
    }
    
    public final void generateXMap() {
        this.waterPointsY = new ConcurrentHashMap<Integer, WaterGenerator>();
    }
    
    public final void addY(final WaterGenerator wg) {
        this.waterPointsY.put(wg.y, wg);
    }
    
    public static final WaterGenerator getWG(final int x, final int y) {
        final WaterGenerator xgeneral = getXGeneral(x);
        if (xgeneral == null) {
            return null;
        }
        return xgeneral.getY(y);
    }
    
    public final int getHeight() {
        return this.height;
    }
    
    public final boolean changed() {
        return this.changed;
    }
    
    public final boolean changedSinceReset() {
        return this.changed && this.height != this.resetHeight;
    }
    
    public final void setHeight(final int aHeight) {
        if (this.lastHeight != aHeight) {
            this.changed = true;
            this.isReset = false;
            this.lastHeight = aHeight;
            this.height = aHeight;
        }
    }
    
    public final void updateItem() {
        if (this.changed) {
            if (this.shouldCreateItem()) {
                this.createItem();
            }
            else if (this.height == 0 && !this.spring) {
                this.deleteItem();
                if (this.waterPointsY != null) {
                    this.waterPointsY.remove(this.y);
                    WaterGenerator.waterPointsX.remove(this.x);
                    if (this.waterPointsY.size() > 0) {
                        final WaterGenerator[] gens = this.waterPointsY.values().toArray(new WaterGenerator[this.waterPointsY.size()]);
                        addXGeneral(gens[0]);
                        gens[0].addWaterPointsY(this.waterPointsY);
                    }
                }
            }
            else {
                this.waterMarker.setName("" + this.height);
                this.waterMarker.updateIfGroundItem();
            }
        }
    }
    
    public final void addWaterPointsY(final Map<Integer, WaterGenerator> wpy) {
        this.waterPointsY = wpy;
    }
    
    public final void deleteItem() {
        try {
            Items.destroyItem(this.waterMarker.getWurmId());
        }
        catch (Exception ex) {}
    }
    
    long getTileId() {
        return Tiles.getTileId(this.x, this.y, 0, this.layer >= 0);
    }
    
    @Override
    public int compareTo(final WaterGenerator o) {
        return o.x + o.y + o.layer + o.height - this.x + this.y + this.layer + this.height;
    }
    
    public boolean isReset() {
        return this.isReset;
    }
    
    public void setReset(final boolean aIsReset) {
        this.isReset = aIsReset;
        this.resetHeight = this.height;
    }
    
    static {
        logger = Logger.getLogger(WaterGenerator.class.getName());
        waterPointsX = new HashMap<Integer, WaterGenerator>();
        try {
            WaterGenerator.template = ItemTemplateFactory.getInstance().getTemplate(845);
        }
        catch (Exception ex) {}
    }
}
