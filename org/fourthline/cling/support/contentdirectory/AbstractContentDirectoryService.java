// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.contentdirectory;

import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.support.model.SortCriterion;
import org.fourthline.cling.support.model.BrowseResult;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpOutputArgument;
import org.fourthline.cling.binding.annotations.UpnpAction;
import java.util.Collection;
import org.fourthline.cling.model.types.csv.CSVString;
import java.util.List;
import java.util.ArrayList;
import java.beans.PropertyChangeSupport;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.types.csv.CSV;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;
import org.fourthline.cling.binding.annotations.UpnpStateVariables;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpService;

@UpnpService(serviceId = @UpnpServiceId("ContentDirectory"), serviceType = @UpnpServiceType(value = "ContentDirectory", version = 1))
@UpnpStateVariables({ @UpnpStateVariable(name = "A_ARG_TYPE_ObjectID", sendEvents = false, datatype = "string"), @UpnpStateVariable(name = "A_ARG_TYPE_Result", sendEvents = false, datatype = "string"), @UpnpStateVariable(name = "A_ARG_TYPE_BrowseFlag", sendEvents = false, datatype = "string", allowedValuesEnum = BrowseFlag.class), @UpnpStateVariable(name = "A_ARG_TYPE_Filter", sendEvents = false, datatype = "string"), @UpnpStateVariable(name = "A_ARG_TYPE_SortCriteria", sendEvents = false, datatype = "string"), @UpnpStateVariable(name = "A_ARG_TYPE_Index", sendEvents = false, datatype = "ui4"), @UpnpStateVariable(name = "A_ARG_TYPE_Count", sendEvents = false, datatype = "ui4"), @UpnpStateVariable(name = "A_ARG_TYPE_UpdateID", sendEvents = false, datatype = "ui4"), @UpnpStateVariable(name = "A_ARG_TYPE_URI", sendEvents = false, datatype = "uri"), @UpnpStateVariable(name = "A_ARG_TYPE_SearchCriteria", sendEvents = false, datatype = "string") })
public abstract class AbstractContentDirectoryService
{
    public static final String CAPS_WILDCARD = "*";
    @UpnpStateVariable(sendEvents = false)
    private final CSV<String> searchCapabilities;
    @UpnpStateVariable(sendEvents = false)
    private final CSV<String> sortCapabilities;
    @UpnpStateVariable(sendEvents = true, defaultValue = "0", eventMaximumRateMilliseconds = 200)
    private UnsignedIntegerFourBytes systemUpdateID;
    protected final PropertyChangeSupport propertyChangeSupport;
    
    protected AbstractContentDirectoryService() {
        this(new ArrayList<String>(), new ArrayList<String>(), null);
    }
    
    protected AbstractContentDirectoryService(final List<String> searchCapabilities, final List<String> sortCapabilities) {
        this(searchCapabilities, sortCapabilities, null);
    }
    
    protected AbstractContentDirectoryService(final List<String> searchCapabilities, final List<String> sortCapabilities, final PropertyChangeSupport propertyChangeSupport) {
        this.systemUpdateID = new UnsignedIntegerFourBytes(0L);
        this.propertyChangeSupport = ((propertyChangeSupport != null) ? propertyChangeSupport : new PropertyChangeSupport(this));
        (this.searchCapabilities = new CSVString()).addAll((Collection<?>)searchCapabilities);
        (this.sortCapabilities = new CSVString()).addAll((Collection<?>)sortCapabilities);
    }
    
    @UpnpAction(out = { @UpnpOutputArgument(name = "SearchCaps") })
    public CSV<String> getSearchCapabilities() {
        return this.searchCapabilities;
    }
    
    @UpnpAction(out = { @UpnpOutputArgument(name = "SortCaps") })
    public CSV<String> getSortCapabilities() {
        return this.sortCapabilities;
    }
    
    @UpnpAction(out = { @UpnpOutputArgument(name = "Id") })
    public synchronized UnsignedIntegerFourBytes getSystemUpdateID() {
        return this.systemUpdateID;
    }
    
    public PropertyChangeSupport getPropertyChangeSupport() {
        return this.propertyChangeSupport;
    }
    
    protected synchronized void changeSystemUpdateID() {
        final Long oldUpdateID = this.getSystemUpdateID().getValue();
        this.systemUpdateID.increment(true);
        this.getPropertyChangeSupport().firePropertyChange("SystemUpdateID", oldUpdateID, this.getSystemUpdateID().getValue());
    }
    
    @UpnpAction(out = { @UpnpOutputArgument(name = "Result", stateVariable = "A_ARG_TYPE_Result", getterName = "getResult"), @UpnpOutputArgument(name = "NumberReturned", stateVariable = "A_ARG_TYPE_Count", getterName = "getCount"), @UpnpOutputArgument(name = "TotalMatches", stateVariable = "A_ARG_TYPE_Count", getterName = "getTotalMatches"), @UpnpOutputArgument(name = "UpdateID", stateVariable = "A_ARG_TYPE_UpdateID", getterName = "getContainerUpdateID") })
    public BrowseResult browse(@UpnpInputArgument(name = "ObjectID", aliases = { "ContainerID" }) final String objectId, @UpnpInputArgument(name = "BrowseFlag") final String browseFlag, @UpnpInputArgument(name = "Filter") final String filter, @UpnpInputArgument(name = "StartingIndex", stateVariable = "A_ARG_TYPE_Index") final UnsignedIntegerFourBytes firstResult, @UpnpInputArgument(name = "RequestedCount", stateVariable = "A_ARG_TYPE_Count") final UnsignedIntegerFourBytes maxResults, @UpnpInputArgument(name = "SortCriteria") final String orderBy) throws ContentDirectoryException {
        SortCriterion[] orderByCriteria;
        try {
            orderByCriteria = SortCriterion.valueOf(orderBy);
        }
        catch (Exception ex) {
            throw new ContentDirectoryException(ContentDirectoryErrorCode.UNSUPPORTED_SORT_CRITERIA, ex.toString());
        }
        try {
            return this.browse(objectId, BrowseFlag.valueOrNullOf(browseFlag), filter, firstResult.getValue(), maxResults.getValue(), orderByCriteria);
        }
        catch (ContentDirectoryException ex2) {
            throw ex2;
        }
        catch (Exception ex) {
            throw new ContentDirectoryException(ErrorCode.ACTION_FAILED, ex.toString());
        }
    }
    
    public abstract BrowseResult browse(final String p0, final BrowseFlag p1, final String p2, final long p3, final long p4, final SortCriterion[] p5) throws ContentDirectoryException;
    
    @UpnpAction(out = { @UpnpOutputArgument(name = "Result", stateVariable = "A_ARG_TYPE_Result", getterName = "getResult"), @UpnpOutputArgument(name = "NumberReturned", stateVariable = "A_ARG_TYPE_Count", getterName = "getCount"), @UpnpOutputArgument(name = "TotalMatches", stateVariable = "A_ARG_TYPE_Count", getterName = "getTotalMatches"), @UpnpOutputArgument(name = "UpdateID", stateVariable = "A_ARG_TYPE_UpdateID", getterName = "getContainerUpdateID") })
    public BrowseResult search(@UpnpInputArgument(name = "ContainerID", stateVariable = "A_ARG_TYPE_ObjectID") final String containerId, @UpnpInputArgument(name = "SearchCriteria") final String searchCriteria, @UpnpInputArgument(name = "Filter") final String filter, @UpnpInputArgument(name = "StartingIndex", stateVariable = "A_ARG_TYPE_Index") final UnsignedIntegerFourBytes firstResult, @UpnpInputArgument(name = "RequestedCount", stateVariable = "A_ARG_TYPE_Count") final UnsignedIntegerFourBytes maxResults, @UpnpInputArgument(name = "SortCriteria") final String orderBy) throws ContentDirectoryException {
        SortCriterion[] orderByCriteria;
        try {
            orderByCriteria = SortCriterion.valueOf(orderBy);
        }
        catch (Exception ex) {
            throw new ContentDirectoryException(ContentDirectoryErrorCode.UNSUPPORTED_SORT_CRITERIA, ex.toString());
        }
        try {
            return this.search(containerId, searchCriteria, filter, firstResult.getValue(), maxResults.getValue(), orderByCriteria);
        }
        catch (ContentDirectoryException ex2) {
            throw ex2;
        }
        catch (Exception ex) {
            throw new ContentDirectoryException(ErrorCode.ACTION_FAILED, ex.toString());
        }
    }
    
    public BrowseResult search(final String containerId, final String searchCriteria, final String filter, final long firstResult, final long maxResults, final SortCriterion[] orderBy) throws ContentDirectoryException {
        try {
            return new BrowseResult(new DIDLParser().generate(new DIDLContent()), 0L, 0L);
        }
        catch (Exception ex) {
            throw new ContentDirectoryException(ErrorCode.ACTION_FAILED, ex.toString());
        }
    }
}
