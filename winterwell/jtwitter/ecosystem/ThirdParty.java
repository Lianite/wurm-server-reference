// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.jtwitter.ecosystem;

import winterwell.json.JSONException;
import winterwell.jtwitter.TwitterException;
import winterwell.json.JSONObject;
import java.util.Map;
import winterwell.jtwitter.InternalUtils;
import winterwell.jtwitter.User;
import winterwell.jtwitter.URLConnectionHttpClient;
import winterwell.jtwitter.Twitter;

public class ThirdParty
{
    private Twitter.IHttpClient client;
    
    public ThirdParty() {
        this(new URLConnectionHttpClient());
    }
    
    public ThirdParty(final Twitter.IHttpClient client) {
        this.client = client;
    }
    
    public double getInfochimpTrustRank(final User user, final String apiKey) {
        final String json = this.client.getPage("http://api.infochimps.com/soc/net/tw/trstrank.json", InternalUtils.asMap("screen_name", user.screenName, "apikey", apiKey), false);
        try {
            final JSONObject results = new JSONObject(json);
            final Double score = results.getDouble("trstrank");
            return score;
        }
        catch (JSONException e) {
            throw new TwitterException.Parsing(json, e);
        }
    }
}
