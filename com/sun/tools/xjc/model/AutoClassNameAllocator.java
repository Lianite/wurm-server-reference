// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import com.sun.tools.xjc.api.ClassNameAllocator;

public class AutoClassNameAllocator implements ClassNameAllocator
{
    private final ClassNameAllocator core;
    private final Map<String, Set<String>> names;
    
    public AutoClassNameAllocator(final ClassNameAllocator core) {
        this.names = new HashMap<String, Set<String>>();
        this.core = core;
    }
    
    public String assignClassName(final String packageName, String className) {
        className = this.determineName(packageName, className);
        if (this.core != null) {
            className = this.core.assignClassName(packageName, className);
        }
        return className;
    }
    
    private String determineName(final String packageName, final String className) {
        Set<String> s = this.names.get(packageName);
        if (s == null) {
            s = new HashSet<String>();
            this.names.put(packageName, s);
        }
        if (s.add(className)) {
            return className;
        }
        int i;
        for (i = 2; !s.add(className + i); ++i) {}
        return className + i;
    }
}
