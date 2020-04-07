// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.jtwitter.ecosystem;

import java.util.regex.Matcher;
import java.util.Map;
import winterwell.jtwitter.TwitterException;
import winterwell.jtwitter.InternalUtils;
import winterwell.jtwitter.Status;
import winterwell.jtwitter.URLConnectionHttpClient;
import java.util.regex.Pattern;
import winterwell.jtwitter.Twitter;

public class TwitLonger
{
    Twitter.IHttpClient http;
    private Twitter jtwit;
    private String twitlongerApiKey;
    private String twitlongerAppName;
    static final Pattern contentTag;
    static final Pattern idTag;
    
    static {
        contentTag = Pattern.compile("<content>(.+?)<\\/content>", 32);
        idTag = Pattern.compile("<id>(.+?)<\\/id>", 32);
    }
    
    public TwitLonger() {
        this.http = new URLConnectionHttpClient();
    }
    
    public TwitLonger(final Twitter jtwitter, final String twitlongerApiKey, final String twitlongerAppName) {
        this.twitlongerApiKey = twitlongerApiKey;
        this.twitlongerAppName = twitlongerAppName;
        this.http = jtwitter.getHttpClient();
        this.jtwit = jtwitter;
        if (twitlongerApiKey == null || twitlongerAppName == null) {
            throw new IllegalStateException("Incomplete Twitlonger api details");
        }
    }
    
    public Status updateLongStatus(final String message, final Number inReplyToStatusId) {
        assert this.twitlongerApiKey != null : "Wrong constructor used -- you must supply an api-key to post";
        if (message.length() < 141) {
            throw new IllegalArgumentException("Message too short (" + inReplyToStatusId + " chars). Just post a normal Twitter status. ");
        }
        String url = "http://www.twitlonger.com/api_post";
        final Map<String, String> vars = (Map<String, String>)InternalUtils.asMap("application", this.twitlongerAppName, "api_key", this.twitlongerApiKey, "username", this.jtwit.getScreenName(), "message", message);
        if (inReplyToStatusId != null && inReplyToStatusId.doubleValue() != 0.0) {
            vars.put("in_reply", inReplyToStatusId.toString());
        }
        final String response = this.http.post(url, vars, false);
        Matcher m = TwitLonger.contentTag.matcher(response);
        boolean ok = m.find();
        if (!ok) {
            throw new TwitterException.TwitLongerException("TwitLonger call failed", response);
        }
        final String shortMsg = m.group(1).trim();
        final Status s = this.jtwit.updateStatus(shortMsg, inReplyToStatusId);
        m = TwitLonger.idTag.matcher(response);
        ok = m.find();
        if (!ok) {
            return s;
        }
        final String id = m.group(1);
        try {
            url = "http://www.twitlonger.com/api_set_id";
            vars.remove("message");
            vars.remove("in_reply");
            vars.remove("username");
            vars.put("message_id", new StringBuilder().append(id).toString());
            vars.put("twitter_id", new StringBuilder().append(s.getId()).toString());
            this.http.post(url, vars, false);
        }
        catch (Exception ex) {}
        return s;
    }
    
    public String getLongStatus(final Status truncatedStatus) {
        final int i = truncatedStatus.text.indexOf("http://tl.gd/");
        if (i == -1) {
            return truncatedStatus.text;
        }
        final String id = truncatedStatus.text.substring(i + 13).trim();
        final String response = this.http.getPage("http://www.twitlonger.com/api_read/" + id, null, false);
        final Matcher m = TwitLonger.contentTag.matcher(response);
        final boolean ok = m.find();
        if (!ok) {
            throw new TwitterException.TwitLongerException("TwitLonger call failed", response);
        }
        final String longMsg = m.group(1).trim();
        return longMsg;
    }
}
