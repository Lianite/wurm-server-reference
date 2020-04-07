// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

public class Friend implements Comparable<Friend>
{
    private final long id;
    private final Category cat;
    private final String note;
    
    public Friend(final long aId, final byte catId, final String note) {
        this(aId, Category.catFromInt(catId), note);
    }
    
    public Friend(final long aId, final Category category, final String note) {
        this.id = aId;
        this.cat = category;
        this.note = note;
    }
    
    public long getFriendId() {
        return this.id;
    }
    
    public Category getCategory() {
        return this.cat;
    }
    
    public byte getCatId() {
        return this.cat.getCatId();
    }
    
    public String getName() {
        return PlayerInfoFactory.getPlayerName(this.id);
    }
    
    public String getNote() {
        return this.note;
    }
    
    @Override
    public int compareTo(final Friend otherFriend) {
        if (this.getCatId() < otherFriend.getCatId()) {
            return 1;
        }
        if (this.getCatId() > otherFriend.getCatId()) {
            return -1;
        }
        return this.getName().compareTo(otherFriend.getName());
    }
    
    public enum Category
    {
        Other(0), 
        Contacts(1), 
        Friends(2), 
        Trusted(3);
        
        private final byte cat;
        private static final Category[] cats;
        
        private Category(final int numb) {
            this.cat = (byte)numb;
        }
        
        public byte getCatId() {
            return this.cat;
        }
        
        public static final int getCatLength() {
            return Category.cats.length;
        }
        
        public static final Category[] getCategories() {
            return Category.cats;
        }
        
        public static Category catFromInt(final int typeId) {
            if (typeId >= getCatLength()) {
                return Category.cats[0];
            }
            return Category.cats[typeId & 0xFF];
        }
        
        public static Category catFromName(final String catName) {
            for (final Category c : Category.cats) {
                if (c.name().toLowerCase().startsWith(catName.toLowerCase())) {
                    return c;
                }
            }
            return null;
        }
        
        static {
            cats = values();
        }
    }
}
