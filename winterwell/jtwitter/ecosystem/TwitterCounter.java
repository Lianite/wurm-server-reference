// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.jtwitter.ecosystem;

import java.util.Map;
import java.text.ParseException;
import winterwell.json.JSONException;
import winterwell.jtwitter.TwitterException;
import winterwell.json.JSONObject;
import winterwell.jtwitter.InternalUtils;
import winterwell.jtwitter.URLConnectionHttpClient;
import winterwell.jtwitter.Twitter;

public class TwitterCounter
{
    final String apiKey;
    Twitter.IHttpClient client;
    
    public Twitter.IHttpClient getClient() {
        return this.client;
    }
    
    public TwitterCounter(final String twitterCounterApiKey) {
        this.client = new URLConnectionHttpClient();
        this.apiKey = twitterCounterApiKey;
    }
    
    public TwitterCounterStats getStats(final Number twitterUserId) {
        final Map<String, String> vars = (Map<String, String>)InternalUtils.asMap("twitter_id", twitterUserId, "apikey", this.apiKey);
        final String json = this.client.getPage("http://api.twittercounter.com/", vars, false);
        try {
            final JSONObject jo = new JSONObject(json);
            return new TwitterCounterStats(jo);
        }
        catch (JSONException e) {
            throw new TwitterException.Parsing(json, e);
        }
        catch (ParseException e2) {
            throw new TwitterException.Parsing(json, e2);
        }
    }
}
