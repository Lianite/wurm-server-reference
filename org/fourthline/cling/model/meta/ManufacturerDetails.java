// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.meta;

import java.net.URI;

public class ManufacturerDetails
{
    private String manufacturer;
    private URI manufacturerURI;
    
    ManufacturerDetails() {
    }
    
    public ManufacturerDetails(final String manufacturer) {
        this.manufacturer = manufacturer;
    }
    
    public ManufacturerDetails(final URI manufacturerURI) {
        this.manufacturerURI = manufacturerURI;
    }
    
    public ManufacturerDetails(final String manufacturer, final URI manufacturerURI) {
        this.manufacturer = manufacturer;
        this.manufacturerURI = manufacturerURI;
    }
    
    public ManufacturerDetails(final String manufacturer, final String manufacturerURI) throws IllegalArgumentException {
        this.manufacturer = manufacturer;
        this.manufacturerURI = URI.create(manufacturerURI);
    }
    
    public String getManufacturer() {
        return this.manufacturer;
    }
    
    public URI getManufacturerURI() {
        return this.manufacturerURI;
    }
}
