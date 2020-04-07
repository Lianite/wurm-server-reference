// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.website.news;

public class News
{
    private long timestamp;
    private String title;
    private String text;
    private String postedBy;
    private String id;
    
    public News() {
    }
    
    public News(final String aTitle, final String aText, final String aPostedBy) {
        this.timestamp = System.currentTimeMillis();
        this.title = aTitle;
        this.text = aText;
        this.postedBy = aPostedBy;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setId(final String aId) {
        this.id = aId;
    }
    
    public String getPostedBy() {
        return this.postedBy;
    }
    
    public String getText() {
        return this.text;
    }
    
    public long getTimestamp() {
        return this.timestamp;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setPostedBy(final String string) {
        this.postedBy = string;
    }
    
    public void setText(final String string) {
        this.text = string;
    }
    
    public void setTimestamp(final long l) {
        this.timestamp = l;
    }
    
    public void setTitle(final String string) {
        this.title = string;
    }
}
