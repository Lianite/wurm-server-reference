// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.parse.compact;

public class Token
{
    public int kind;
    public int beginLine;
    public int beginColumn;
    public int endLine;
    public int endColumn;
    public String image;
    public Token next;
    public Token specialToken;
    
    public String toString() {
        return this.image;
    }
    
    public static final Token newToken(final int ofKind) {
        return new Token();
    }
}
