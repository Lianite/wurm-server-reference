// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader;

import com.sun.tools.xjc.util.NameUtil;

public interface NameConverter
{
    public static final NameConverter standard = new Standard();
    public static final NameConverter jaxrpcCompatible = new NameConverter$1();
    public static final NameConverter smart = new NameConverter$2();
    
    String toClassName(final String p0);
    
    String toInterfaceName(final String p0);
    
    String toPropertyName(final String p0);
    
    String toConstantName(final String p0);
    
    String toVariableName(final String p0);
    
    String toPackageName(final String p0);
    
    public static class Standard extends NameUtil implements NameConverter
    {
        public String toClassName(final String s) {
            return this.toMixedCaseName(this.toWordList(s), true);
        }
        
        public String toVariableName(final String s) {
            return this.toMixedCaseName(this.toWordList(s), false);
        }
        
        public String toInterfaceName(final String token) {
            return this.toClassName(token);
        }
        
        public String toPropertyName(final String s) {
            return this.toClassName(s);
        }
        
        public String toConstantName(final String token) {
            return super.toConstantName(token);
        }
        
        public String toPackageName(final String s) {
            return this.toMixedCaseName(this.toWordList(s), false);
        }
    }
}
