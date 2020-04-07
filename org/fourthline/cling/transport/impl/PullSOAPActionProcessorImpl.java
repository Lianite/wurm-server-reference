// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.impl;

import org.fourthline.cling.transport.spi.SOAPActionProcessor;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Map;
import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.meta.ActionArgument;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.message.control.ActionResponseMessage;
import org.xmlpull.v1.XmlPullParser;
import org.fourthline.cling.model.UnsupportedDataException;
import org.seamless.xml.XmlPullParserUtils;
import org.fourthline.cling.model.message.control.ActionMessage;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.control.ActionRequestMessage;
import java.util.logging.Logger;
import javax.enterprise.inject.Alternative;

@Alternative
public class PullSOAPActionProcessorImpl extends SOAPActionProcessorImpl
{
    protected static Logger log;
    
    @Override
    public void readBody(final ActionRequestMessage requestMessage, final ActionInvocation actionInvocation) throws UnsupportedDataException {
        final String body = this.getMessageBody(requestMessage);
        try {
            final XmlPullParser xpp = XmlPullParserUtils.createParser(body);
            this.readBodyRequest(xpp, requestMessage, actionInvocation);
        }
        catch (Exception ex) {
            throw new UnsupportedDataException("Can't transform message payload: " + ex, ex, body);
        }
    }
    
    @Override
    public void readBody(final ActionResponseMessage responseMsg, final ActionInvocation actionInvocation) throws UnsupportedDataException {
        final String body = this.getMessageBody(responseMsg);
        try {
            final XmlPullParser xpp = XmlPullParserUtils.createParser(body);
            this.readBodyElement(xpp);
            this.readBodyResponse(xpp, actionInvocation);
        }
        catch (Exception ex) {
            throw new UnsupportedDataException("Can't transform message payload: " + ex, ex, body);
        }
    }
    
    protected void readBodyElement(final XmlPullParser xpp) throws Exception {
        XmlPullParserUtils.searchTag(xpp, "Body");
    }
    
    protected void readBodyRequest(final XmlPullParser xpp, final ActionRequestMessage requestMessage, final ActionInvocation actionInvocation) throws Exception {
        XmlPullParserUtils.searchTag(xpp, actionInvocation.getAction().getName());
        this.readActionInputArguments(xpp, actionInvocation);
    }
    
    protected void readBodyResponse(final XmlPullParser xpp, final ActionInvocation actionInvocation) throws Exception {
        int event;
        do {
            event = xpp.next();
            if (event == 2) {
                if (xpp.getName().equals("Fault")) {
                    final ActionException e = this.readFaultElement(xpp);
                    actionInvocation.setFailure(e);
                    return;
                }
                if (xpp.getName().equals(actionInvocation.getAction().getName() + "Response")) {
                    this.readActionOutputArguments(xpp, actionInvocation);
                    return;
                }
                continue;
            }
        } while (event != 1 && (event != 3 || !xpp.getName().equals("Body")));
        throw new ActionException(ErrorCode.ACTION_FAILED, String.format("Action SOAP response do not contain %s element", actionInvocation.getAction().getName() + "Response"));
    }
    
    protected void readActionInputArguments(final XmlPullParser xpp, final ActionInvocation actionInvocation) throws Exception {
        actionInvocation.setInput(this.readArgumentValues(xpp, actionInvocation.getAction().getInputArguments()));
    }
    
    protected void readActionOutputArguments(final XmlPullParser xpp, final ActionInvocation actionInvocation) throws Exception {
        actionInvocation.setOutput(this.readArgumentValues(xpp, actionInvocation.getAction().getOutputArguments()));
    }
    
    protected Map<String, String> getMatchingNodes(final XmlPullParser xpp, final ActionArgument[] args) throws Exception {
        final List<String> names = new ArrayList<String>();
        for (final ActionArgument argument : args) {
            names.add(argument.getName().toUpperCase(Locale.ROOT));
            for (final String alias : Arrays.asList(argument.getAliases())) {
                names.add(alias.toUpperCase(Locale.ROOT));
            }
        }
        final Map<String, String> matches = new HashMap<String, String>();
        final String enclosingTag = xpp.getName();
        int event;
        do {
            event = xpp.next();
            if (event == 2 && names.contains(xpp.getName().toUpperCase(Locale.ROOT))) {
                matches.put(xpp.getName(), xpp.nextText());
            }
        } while (event != 1 && (event != 3 || !xpp.getName().equals(enclosingTag)));
        if (matches.size() < args.length) {
            throw new ActionException(ErrorCode.ARGUMENT_VALUE_INVALID, "Invalid number of input or output arguments in XML message, expected " + args.length + " but found " + matches.size());
        }
        return matches;
    }
    
    protected ActionArgumentValue[] readArgumentValues(final XmlPullParser xpp, final ActionArgument[] args) throws Exception {
        final Map<String, String> matches = this.getMatchingNodes(xpp, args);
        final ActionArgumentValue[] values = new ActionArgumentValue[args.length];
        for (int i = 0; i < args.length; ++i) {
            final ActionArgument arg = args[i];
            final String value = this.findActionArgumentValue(matches, arg);
            if (value == null) {
                throw new ActionException(ErrorCode.ARGUMENT_VALUE_INVALID, "Could not find argument '" + arg.getName() + "' node");
            }
            PullSOAPActionProcessorImpl.log.fine("Reading action argument: " + arg.getName());
            values[i] = this.createValue(arg, value);
        }
        return values;
    }
    
    protected String findActionArgumentValue(final Map<String, String> entries, final ActionArgument arg) {
        for (final Map.Entry<String, String> entry : entries.entrySet()) {
            if (arg.isNameOrAlias(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    protected ActionException readFaultElement(final XmlPullParser xpp) throws Exception {
        String errorCode = null;
        String errorDescription = null;
        XmlPullParserUtils.searchTag(xpp, "UPnPError");
        int event;
        do {
            event = xpp.next();
            if (event == 2) {
                final String tag = xpp.getName();
                if (tag.equals("errorCode")) {
                    errorCode = xpp.nextText();
                }
                else {
                    if (!tag.equals("errorDescription")) {
                        continue;
                    }
                    errorDescription = xpp.nextText();
                }
            }
        } while (event != 1 && (event != 3 || !xpp.getName().equals("UPnPError")));
        if (errorCode != null) {
            try {
                final int numericCode = Integer.valueOf(errorCode);
                final ErrorCode standardErrorCode = ErrorCode.getByCode(numericCode);
                if (standardErrorCode != null) {
                    PullSOAPActionProcessorImpl.log.fine("Reading fault element: " + standardErrorCode.getCode() + " - " + errorDescription);
                    return new ActionException(standardErrorCode, errorDescription, false);
                }
                PullSOAPActionProcessorImpl.log.fine("Reading fault element: " + numericCode + " - " + errorDescription);
                return new ActionException(numericCode, errorDescription);
            }
            catch (NumberFormatException ex) {
                throw new RuntimeException("Error code was not a number");
            }
        }
        throw new RuntimeException("Received fault element but no error code");
    }
    
    static {
        PullSOAPActionProcessorImpl.log = Logger.getLogger(SOAPActionProcessor.class.getName());
    }
}
