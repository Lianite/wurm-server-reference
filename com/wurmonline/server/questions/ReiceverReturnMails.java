// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.util.HashSet;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.WurmMail;
import java.util.Set;

final class ReiceverReturnMails
{
    private final Set<WurmMail> returnWurmMailSet;
    private final Set<Item> returnItemSet;
    private int serverId;
    private long receiverId;
    
    ReiceverReturnMails() {
        this.returnWurmMailSet = new HashSet<WurmMail>();
        this.returnItemSet = new HashSet<Item>();
    }
    
    void addMail(final WurmMail mail, final Item item) {
        if (!this.returnWurmMailSet.contains(mail)) {
            this.returnWurmMailSet.add(mail);
        }
        this.returnItemSet.add(item);
    }
    
    int getServerId() {
        return this.serverId;
    }
    
    void setServerId(final int aServerId) {
        this.serverId = aServerId;
    }
    
    void setReceiverId(final long aReceiverId) {
        this.receiverId = aReceiverId;
    }
    
    Set<WurmMail> getReturnWurmMailSet() {
        return this.returnWurmMailSet;
    }
    
    Item[] getReturnItemSetAsArray() {
        return this.returnItemSet.toArray(new Item[this.returnItemSet.size()]);
    }
}
