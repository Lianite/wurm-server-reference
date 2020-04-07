// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures.ai;

import java.util.LinkedList;

public final class Path
{
    private LinkedList<PathTile> path;
    
    Path() {
        this.path = new LinkedList<PathTile>();
    }
    
    public Path(final LinkedList<PathTile> pathlist) {
        this.path = pathlist;
    }
    
    public PathTile getFirst() {
        return this.path.getFirst();
    }
    
    public PathTile getTargetTile() {
        return this.path.getLast();
    }
    
    public int getSize() {
        return this.path.size();
    }
    
    public void removeFirst() {
        this.path.removeFirst();
    }
    
    public boolean isEmpty() {
        return this.path == null || this.path.isEmpty();
    }
    
    public LinkedList<PathTile> getPathTiles() {
        return this.path;
    }
    
    public void clear() {
        if (this.path != null) {
            this.path.clear();
        }
        this.path = null;
    }
}
