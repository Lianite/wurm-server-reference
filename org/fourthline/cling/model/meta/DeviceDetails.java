// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.meta;

import java.util.ArrayList;
import org.fourthline.cling.model.ValidationError;
import java.util.List;
import org.fourthline.cling.model.types.DLNACaps;
import org.fourthline.cling.model.types.DLNADoc;
import java.net.URI;
import java.net.URL;
import java.util.logging.Logger;
import org.fourthline.cling.model.Validatable;

public class DeviceDetails implements Validatable
{
    private static final Logger log;
    private final URL baseURL;
    private final String friendlyName;
    private final ManufacturerDetails manufacturerDetails;
    private final ModelDetails modelDetails;
    private final String serialNumber;
    private final String upc;
    private final URI presentationURI;
    private final DLNADoc[] dlnaDocs;
    private final DLNACaps dlnaCaps;
    private final DLNACaps secProductCaps;
    
    public DeviceDetails(final String friendlyName) {
        this(null, friendlyName, null, null, null, null, null);
    }
    
    public DeviceDetails(final String friendlyName, final DLNADoc[] dlnaDocs, final DLNACaps dlnaCaps) {
        this(null, friendlyName, null, null, null, null, null, dlnaDocs, dlnaCaps);
    }
    
    public DeviceDetails(final String friendlyName, final ManufacturerDetails manufacturerDetails) {
        this(null, friendlyName, manufacturerDetails, null, null, null, null);
    }
    
    public DeviceDetails(final String friendlyName, final ManufacturerDetails manufacturerDetails, final DLNADoc[] dlnaDocs, final DLNACaps dlnaCaps) {
        this(null, friendlyName, manufacturerDetails, null, null, null, null, dlnaDocs, dlnaCaps);
    }
    
    public DeviceDetails(final String friendlyName, final ManufacturerDetails manufacturerDetails, final ModelDetails modelDetails) {
        this(null, friendlyName, manufacturerDetails, modelDetails, null, null, null);
    }
    
    public DeviceDetails(final String friendlyName, final ManufacturerDetails manufacturerDetails, final ModelDetails modelDetails, final DLNADoc[] dlnaDocs, final DLNACaps dlnaCaps) {
        this(null, friendlyName, manufacturerDetails, modelDetails, null, null, null, dlnaDocs, dlnaCaps);
    }
    
    public DeviceDetails(final String friendlyName, final ManufacturerDetails manufacturerDetails, final ModelDetails modelDetails, final DLNADoc[] dlnaDocs, final DLNACaps dlnaCaps, final DLNACaps secProductCaps) {
        this(null, friendlyName, manufacturerDetails, modelDetails, null, null, null, dlnaDocs, dlnaCaps, secProductCaps);
    }
    
    public DeviceDetails(final String friendlyName, final ManufacturerDetails manufacturerDetails, final ModelDetails modelDetails, final String serialNumber, final String upc) {
        this(null, friendlyName, manufacturerDetails, modelDetails, serialNumber, upc, null);
    }
    
    public DeviceDetails(final String friendlyName, final ManufacturerDetails manufacturerDetails, final ModelDetails modelDetails, final String serialNumber, final String upc, final DLNADoc[] dlnaDocs, final DLNACaps dlnaCaps) {
        this(null, friendlyName, manufacturerDetails, modelDetails, serialNumber, upc, null, dlnaDocs, dlnaCaps);
    }
    
    public DeviceDetails(final String friendlyName, final URI presentationURI) {
        this(null, friendlyName, null, null, null, null, presentationURI);
    }
    
    public DeviceDetails(final String friendlyName, final URI presentationURI, final DLNADoc[] dlnaDocs, final DLNACaps dlnaCaps) {
        this(null, friendlyName, null, null, null, null, presentationURI, dlnaDocs, dlnaCaps);
    }
    
    public DeviceDetails(final String friendlyName, final ManufacturerDetails manufacturerDetails, final ModelDetails modelDetails, final URI presentationURI) {
        this(null, friendlyName, manufacturerDetails, modelDetails, null, null, presentationURI);
    }
    
    public DeviceDetails(final String friendlyName, final ManufacturerDetails manufacturerDetails, final ModelDetails modelDetails, final URI presentationURI, final DLNADoc[] dlnaDocs, final DLNACaps dlnaCaps) {
        this(null, friendlyName, manufacturerDetails, modelDetails, null, null, presentationURI, dlnaDocs, dlnaCaps);
    }
    
    public DeviceDetails(final String friendlyName, final ManufacturerDetails manufacturerDetails, final ModelDetails modelDetails, final String serialNumber, final String upc, final URI presentationURI) {
        this(null, friendlyName, manufacturerDetails, modelDetails, serialNumber, upc, presentationURI);
    }
    
    public DeviceDetails(final String friendlyName, final ManufacturerDetails manufacturerDetails, final ModelDetails modelDetails, final String serialNumber, final String upc, final URI presentationURI, final DLNADoc[] dlnaDocs, final DLNACaps dlnaCaps) {
        this(null, friendlyName, manufacturerDetails, modelDetails, serialNumber, upc, presentationURI, dlnaDocs, dlnaCaps);
    }
    
    public DeviceDetails(final String friendlyName, final ManufacturerDetails manufacturerDetails, final ModelDetails modelDetails, final String serialNumber, final String upc, final String presentationURI) throws IllegalArgumentException {
        this(null, friendlyName, manufacturerDetails, modelDetails, serialNumber, upc, URI.create(presentationURI));
    }
    
    public DeviceDetails(final String friendlyName, final ManufacturerDetails manufacturerDetails, final ModelDetails modelDetails, final String serialNumber, final String upc, final String presentationURI, final DLNADoc[] dlnaDocs, final DLNACaps dlnaCaps) throws IllegalArgumentException {
        this(null, friendlyName, manufacturerDetails, modelDetails, serialNumber, upc, URI.create(presentationURI), dlnaDocs, dlnaCaps);
    }
    
    public DeviceDetails(final URL baseURL, final String friendlyName, final ManufacturerDetails manufacturerDetails, final ModelDetails modelDetails, final String serialNumber, final String upc, final URI presentationURI) {
        this(baseURL, friendlyName, manufacturerDetails, modelDetails, serialNumber, upc, presentationURI, null, null);
    }
    
    public DeviceDetails(final URL baseURL, final String friendlyName, final ManufacturerDetails manufacturerDetails, final ModelDetails modelDetails, final String serialNumber, final String upc, final URI presentationURI, final DLNADoc[] dlnaDocs, final DLNACaps dlnaCaps) {
        this(baseURL, friendlyName, manufacturerDetails, modelDetails, serialNumber, upc, presentationURI, dlnaDocs, dlnaCaps, null);
    }
    
    public DeviceDetails(final URL baseURL, final String friendlyName, final ManufacturerDetails manufacturerDetails, final ModelDetails modelDetails, final String serialNumber, final String upc, final URI presentationURI, final DLNADoc[] dlnaDocs, final DLNACaps dlnaCaps, final DLNACaps secProductCaps) {
        this.baseURL = baseURL;
        this.friendlyName = friendlyName;
        this.manufacturerDetails = ((manufacturerDetails == null) ? new ManufacturerDetails() : manufacturerDetails);
        this.modelDetails = ((modelDetails == null) ? new ModelDetails() : modelDetails);
        this.serialNumber = serialNumber;
        this.upc = upc;
        this.presentationURI = presentationURI;
        this.dlnaDocs = ((dlnaDocs != null) ? dlnaDocs : new DLNADoc[0]);
        this.dlnaCaps = dlnaCaps;
        this.secProductCaps = secProductCaps;
    }
    
    public URL getBaseURL() {
        return this.baseURL;
    }
    
    public String getFriendlyName() {
        return this.friendlyName;
    }
    
    public ManufacturerDetails getManufacturerDetails() {
        return this.manufacturerDetails;
    }
    
    public ModelDetails getModelDetails() {
        return this.modelDetails;
    }
    
    public String getSerialNumber() {
        return this.serialNumber;
    }
    
    public String getUpc() {
        return this.upc;
    }
    
    public URI getPresentationURI() {
        return this.presentationURI;
    }
    
    public DLNADoc[] getDlnaDocs() {
        return this.dlnaDocs;
    }
    
    public DLNACaps getDlnaCaps() {
        return this.dlnaCaps;
    }
    
    public DLNACaps getSecProductCaps() {
        return this.secProductCaps;
    }
    
    @Override
    public List<ValidationError> validate() {
        final List<ValidationError> errors = new ArrayList<ValidationError>();
        if (this.getUpc() != null) {
            if (this.getUpc().length() != 12) {
                DeviceDetails.log.fine("UPnP specification violation, UPC must be 12 digits: " + this.getUpc());
            }
            else {
                try {
                    Long.parseLong(this.getUpc());
                }
                catch (NumberFormatException ex) {
                    DeviceDetails.log.fine("UPnP specification violation, UPC must be 12 digits all-numeric: " + this.getUpc());
                }
            }
        }
        return errors;
    }
    
    static {
        log = Logger.getLogger(DeviceDetails.class.getName());
    }
}
