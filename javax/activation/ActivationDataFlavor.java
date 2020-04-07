// 
// Decompiled by Procyon v0.5.30
// 

package javax.activation;

import java.awt.datatransfer.DataFlavor;

public class ActivationDataFlavor extends DataFlavor
{
    private String mimeType;
    private MimeType mimeObject;
    private String humanPresentableName;
    private Class representationClass;
    
    public ActivationDataFlavor(final Class representationClass, final String mimeType, final String humanPresentableName) {
        super(mimeType, humanPresentableName);
        this.mimeType = null;
        this.mimeObject = null;
        this.humanPresentableName = null;
        this.representationClass = null;
        this.mimeType = mimeType;
        this.humanPresentableName = humanPresentableName;
        this.representationClass = representationClass;
    }
    
    public ActivationDataFlavor(final Class representationClass, final String humanPresentableName) {
        super(representationClass, humanPresentableName);
        this.mimeType = null;
        this.mimeObject = null;
        this.humanPresentableName = null;
        this.representationClass = null;
        this.mimeType = super.getMimeType();
        this.representationClass = representationClass;
        this.humanPresentableName = humanPresentableName;
    }
    
    public ActivationDataFlavor(final String mimeType, final String humanPresentableName) {
        super(mimeType, humanPresentableName);
        this.mimeType = null;
        this.mimeObject = null;
        this.humanPresentableName = null;
        this.representationClass = null;
        this.mimeType = mimeType;
        try {
            this.representationClass = Class.forName("java.io.InputStream");
        }
        catch (ClassNotFoundException ex) {}
        this.humanPresentableName = humanPresentableName;
    }
    
    public String getMimeType() {
        return this.mimeType;
    }
    
    public Class getRepresentationClass() {
        return this.representationClass;
    }
    
    public String getHumanPresentableName() {
        return this.humanPresentableName;
    }
    
    public void setHumanPresentableName(final String humanPresentableName) {
        this.humanPresentableName = humanPresentableName;
    }
    
    public boolean equals(final DataFlavor dataFlavor) {
        return this.isMimeTypeEqual(dataFlavor) && dataFlavor.getRepresentationClass() == this.representationClass;
    }
    
    public boolean isMimeTypeEqual(final String mimeType) {
        MimeType mt = null;
        try {
            if (this.mimeObject == null) {
                this.mimeObject = new MimeType(this.mimeType);
            }
            mt = new MimeType(mimeType);
        }
        catch (MimeTypeParseException e) {
            return this.mimeType.equalsIgnoreCase(mimeType);
        }
        return this.mimeObject.match(mt);
    }
    
    protected String normalizeMimeTypeParameter(final String parameterName, final String parameterValue) {
        return parameterValue;
    }
    
    protected String normalizeMimeType(final String mimeType) {
        return mimeType;
    }
}
