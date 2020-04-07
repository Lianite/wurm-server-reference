// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import java.io.IOException;
import com.wurmonline.server.Items;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.FailedException;
import java.util.logging.Logger;
import edu.umd.cs.findbugs.annotations.Nullable;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.shared.constants.CounterTypes;

public final class Possessions implements CounterTypes
{
    private final Item inventory;
    @Nullable
    private Creature owner;
    private static final Logger logger;
    
    public Possessions(final Creature aOwner) throws NoSuchTemplateException, FailedException, NoSuchPlayerException, NoSuchCreatureException {
        this.owner = aOwner;
        if (this.owner.isPlayer()) {
            this.inventory = ItemFactory.createItem(0, 100.0f, null);
        }
        else {
            this.inventory = ItemFactory.createInventory(this.owner.getWurmId(), (short)48, 100.0f);
        }
        assert this.inventory != null;
        this.inventory.setOwner(aOwner.getWurmId(), true);
    }
    
    public Possessions(final Creature aOwner, final long aInventoryId) throws Exception {
        this.owner = aOwner;
        if (!this.owner.isPlayer()) {
            if (WurmId.getType(aInventoryId) == 19) {
                (this.inventory = ItemFactory.createInventory(this.owner.getWurmId(), (short)48, 100.0f)).setOwner(this.owner.getWurmId(), true);
                this.inventory.getContainedItems();
            }
            else {
                final Item invent = Items.getItem(aInventoryId);
                (this.inventory = ItemFactory.createInventory(this.owner.getWurmId(), (short)48, 100.0f)).setOwner(this.owner.getWurmId(), true);
                Items.destroyItem(invent.getWurmId());
                aOwner.getStatus().setInventoryId(this.inventory.getWurmId());
            }
        }
        else {
            this.inventory = Items.getItem(aInventoryId);
        }
    }
    
    public Item getInventory() {
        return this.inventory;
    }
    
    public void clearOwner() {
        this.owner = null;
    }
    
    public void save() throws IOException {
    }
    
    public void sleep(final boolean epicServer) throws IOException {
        this.inventory.sleep(this.owner, epicServer);
    }
    
    static {
        logger = Logger.getLogger(Possessions.class.getName());
    }
}
