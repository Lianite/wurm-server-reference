// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.xml.xsom.XSWildcard;

final class GWildcardElement extends GElement
{
    private boolean strict;
    
    GWildcardElement() {
        this.strict = true;
    }
    
    public String toString() {
        return "#any";
    }
    
    String getPropertyNameSeed() {
        return "any";
    }
    
    public void merge(final XSWildcard wc) {
        switch (wc.getMode()) {
            case 1:
            case 3: {
                this.strict = false;
                break;
            }
        }
    }
    
    public boolean isStrict() {
        return this.strict;
    }
}
