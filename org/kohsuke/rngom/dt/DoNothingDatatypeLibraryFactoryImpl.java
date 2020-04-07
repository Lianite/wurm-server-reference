// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.dt;

import org.relaxng.datatype.helpers.StreamingValidatorImpl;
import org.relaxng.datatype.DatatypeStreamingValidator;
import org.relaxng.datatype.ValidationContext;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.DatatypeLibraryFactory;

public final class DoNothingDatatypeLibraryFactoryImpl implements DatatypeLibraryFactory
{
    public DatatypeLibrary createDatatypeLibrary(final String s) {
        return new DatatypeLibrary() {
            public Datatype createDatatype(final String s) throws DatatypeException {
                return this.createDatatypeBuilder(s).createDatatype();
            }
            
            public DatatypeBuilder createDatatypeBuilder(final String s) throws DatatypeException {
                return new DatatypeBuilder() {
                    public void addParameter(final String s, final String s1, final ValidationContext validationContext) throws DatatypeException {
                    }
                    
                    public Datatype createDatatype() throws DatatypeException {
                        return new Datatype() {
                            public boolean isValid(final String s, final ValidationContext validationContext) {
                                return false;
                            }
                            
                            public void checkValid(final String s, final ValidationContext validationContext) throws DatatypeException {
                            }
                            
                            public DatatypeStreamingValidator createStreamingValidator(final ValidationContext validationContext) {
                                return new StreamingValidatorImpl(this, validationContext);
                            }
                            
                            public Object createValue(final String s, final ValidationContext validationContext) {
                                return null;
                            }
                            
                            public boolean sameValue(final Object o, final Object o1) {
                                return false;
                            }
                            
                            public int valueHashCode(final Object o) {
                                return 0;
                            }
                            
                            public int getIdType() {
                                return 0;
                            }
                            
                            public boolean isContextDependent() {
                                return false;
                            }
                        };
                    }
                };
            }
        };
    }
}
