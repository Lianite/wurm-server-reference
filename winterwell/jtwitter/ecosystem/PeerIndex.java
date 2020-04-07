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

public class PeerIndex
{
    final String API_KEY;
    Twitter.IHttpClient client;
    
    public PeerIndex(final String apiKey) {
        this.client = new URLConnectionHttpClient();
        this.API_KEY = apiKey;
    }
    
    public PeerIndexProfile getProfile(final User user) {
        final Map vars = InternalUtils.asMap((user.screenName == null) ? "twitter_screen_name" : "twitter_id", (user.screenName == null) ? user.id : user.screenName, "api_key", this.API_KEY);
        final String json = this.client.getPage("https://api.peerindex.com/1/actor/basic.json", vars, false);
        try {
            final JSONObject jo = new JSONObject(json);
            return new PeerIndexProfile(jo);
        }
        catch (JSONException e) {
            throw new TwitterException.Parsing(json, e);
        }
    }
}
