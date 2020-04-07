// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.contentdirectory.callback;

import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.BrowseResult;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.support.model.SortCriterion;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.model.meta.Service;
import java.util.logging.Logger;
import org.fourthline.cling.controlpoint.ActionCallback;

public abstract class Browse extends ActionCallback
{
    public static final String CAPS_WILDCARD = "*";
    private static Logger log;
    
    public Browse(final Service service, final String containerId, final BrowseFlag flag) {
        this(service, containerId, flag, "*", 0L, null, new SortCriterion[0]);
    }
    
    public Browse(final Service service, final String objectID, final BrowseFlag flag, final String filter, final long firstResult, final Long maxResults, final SortCriterion... orderBy) {
        super(new ActionInvocation(service.getAction("Browse")));
        Browse.log.fine("Creating browse action for object ID: " + objectID);
        this.getActionInvocation().setInput("ObjectID", objectID);
        this.getActionInvocation().setInput("BrowseFlag", flag.toString());
        this.getActionInvocation().setInput("Filter", filter);
        this.getActionInvocation().setInput("StartingIndex", new UnsignedIntegerFourBytes(firstResult));
        this.getActionInvocation().setInput("RequestedCount", new UnsignedIntegerFourBytes((maxResults == null) ? this.getDefaultMaxResults() : maxResults));
        this.getActionInvocation().setInput("SortCriteria", SortCriterion.toString(orderBy));
    }
    
    @Override
    public void run() {
        this.updateStatus(Status.LOADING);
        super.run();
    }
    
    @Override
    public void success(final ActionInvocation invocation) {
        Browse.log.fine("Successful browse action, reading output argument values");
        final BrowseResult result = new BrowseResult(invocation.getOutput("Result").getValue().toString(), (UnsignedIntegerFourBytes)invocation.getOutput("NumberReturned").getValue(), (UnsignedIntegerFourBytes)invocation.getOutput("TotalMatches").getValue(), (UnsignedIntegerFourBytes)invocation.getOutput("UpdateID").getValue());
        final boolean proceed = this.receivedRaw(invocation, result);
        if (proceed && result.getCountLong() > 0L && result.getResult().length() > 0) {
            try {
                final DIDLParser didlParser = new DIDLParser();
                final DIDLContent didl = didlParser.parse(result.getResult());
                this.received(invocation, didl);
                this.updateStatus(Status.OK);
            }
            catch (Exception ex) {
                invocation.setFailure(new ActionException(ErrorCode.ACTION_FAILED, "Can't parse DIDL XML response: " + ex, ex));
                this.failure(invocation, null);
            }
        }
        else {
            this.received(invocation, new DIDLContent());
            this.updateStatus(Status.NO_CONTENT);
        }
    }
    
    public long getDefaultMaxResults() {
        return 999L;
    }
    
    public boolean receivedRaw(final ActionInvocation actionInvocation, final BrowseResult browseResult) {
        return true;
    }
    
    public abstract void received(final ActionInvocation p0, final DIDLContent p1);
    
    public abstract void updateStatus(final Status p0);
    
    static {
        Browse.log = Logger.getLogger(Browse.class.getName());
    }
    
    public enum Status
    {
        NO_CONTENT("No Content"), 
        LOADING("Loading..."), 
        OK("OK");
        
        private String defaultMessage;
        
        private Status(final String defaultMessage) {
            this.defaultMessage = defaultMessage;
        }
        
        public String getDefaultMessage() {
            return this.defaultMessage;
        }
    }
}
