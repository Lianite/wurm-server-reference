// 
// Decompiled by Procyon v0.5.30
// 

package org.relaxng.datatype;

public interface Datatype
{
    public static final int ID_TYPE_NULL = 0;
    public static final int ID_TYPE_ID = 1;
    public static final int ID_TYPE_IDREF = 2;
    public static final int ID_TYPE_IDREFS = 3;
    
    boolean isValid(final String p0, final ValidationContext p1);
    
    void checkValid(final String p0, final ValidationContext p1) throws DatatypeException;
    
    DatatypeStreamingValidator createStreamingValidator(final ValidationContext p0);
    
    Object createValue(final String p0, final ValidationContext p1);
    
    boolean sameValue(final Object p0, final Object p1);
    
    int valueHashCode(final Object p0);
    
    int getIdType();
    
    boolean isContextDependent();
}
