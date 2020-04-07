// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.contentdirectory.callback;

import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.SearchResult;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.support.model.SortCriterion;
import org.fourthline.cling.model.meta.Service;
import java.util.logging.Logger;
import org.fourthline.cling.controlpoint.ActionCallback;

public abstract class Search extends ActionCallback
{
    public static final String CAPS_WILDCARD = "*";
    private static Logger log;
    
    public Search(final Service service, final String containerId, final String searchCriteria) {
        this(service, containerId, searchCriteria, "*", 0L, null, new SortCriterion[0]);
    }
    
    public Search(final Service service, final String containerId, final String searchCriteria, final String filter, final long firstResult, final Long maxResults, final SortCriterion... orderBy) {
        super(new ActionInvocation(service.getAction("Search")));
        Search.log.fine("Creating browse action for container ID: " + containerId);
        this.getActionInvocation().setInput("ContainerID", containerId);
        this.getActionInvocation().setInput("SearchCriteria", searchCriteria);
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
    public void success(final ActionInvocation actionInvocation) {
        Search.log.fine("Successful search action, reading output argument values");
        final SearchResult result = new SearchResult(actionInvocation.getOutput("Result").getValue().toString(), (UnsignedIntegerFourBytes)actionInvocation.getOutput("NumberReturned").getValue(), (UnsignedIntegerFourBytes)actionInvocation.getOutput("TotalMatches").getValue(), (UnsignedIntegerFourBytes)actionInvocation.getOutput("UpdateID").getValue());
        final boolean proceed = this.receivedRaw(actionInvocation, result);
        if (proceed && result.getCountLong() > 0L && result.getResult().length() > 0) {
            try {
                final DIDLParser didlParser = new DIDLParser();
                final DIDLContent didl = didlParser.parse(result.getResult());
                this.received(actionInvocation, didl);
                this.updateStatus(Status.OK);
            }
            catch (Exception ex) {
                actionInvocation.setFailure(new ActionException(ErrorCode.ACTION_FAILED, "Can't parse DIDL XML response: " + ex, ex));
                this.failure(actionInvocation, null);
            }
        }
        else {
            this.received(actionInvocation, new DIDLContent());
            this.updateStatus(Status.NO_CONTENT);
        }
    }
    
    public Long getDefaultMaxResults() {
        return 999L;
    }
    
    public boolean receivedRaw(final ActionInvocation actionInvocation, final SearchResult searchResult) {
        return true;
    }
    
    public abstract void received(final ActionInvocation p0, final DIDLContent p1);
    
    public abstract void updateStatus(final Status p0);
    
    static {
        Search.log = Logger.getLogger(Search.class.getName());
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
