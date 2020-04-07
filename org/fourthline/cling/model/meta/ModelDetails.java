// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.meta;

import java.net.URI;

public class ModelDetails
{
    private String modelName;
    private String modelDescription;
    private String modelNumber;
    private URI modelURI;
    
    ModelDetails() {
    }
    
    public ModelDetails(final String modelName) {
        this.modelName = modelName;
    }
    
    public ModelDetails(final String modelName, final String modelDescription) {
        this.modelName = modelName;
        this.modelDescription = modelDescription;
    }
    
    public ModelDetails(final String modelName, final String modelDescription, final String modelNumber) {
        this.modelName = modelName;
        this.modelDescription = modelDescription;
        this.modelNumber = modelNumber;
    }
    
    public ModelDetails(final String modelName, final String modelDescription, final String modelNumber, final URI modelURI) {
        this.modelName = modelName;
        this.modelDescription = modelDescription;
        this.modelNumber = modelNumber;
        this.modelURI = modelURI;
    }
    
    public ModelDetails(final String modelName, final String modelDescription, final String modelNumber, final String modelURI) throws IllegalArgumentException {
        this.modelName = modelName;
        this.modelDescription = modelDescription;
        this.modelNumber = modelNumber;
        this.modelURI = URI.create(modelURI);
    }
    
    public String getModelName() {
        return this.modelName;
    }
    
    public String getModelDescription() {
        return this.modelDescription;
    }
    
    public String getModelNumber() {
        return this.modelNumber;
    }
    
    public URI getModelURI() {
        return this.modelURI;
    }
}
