// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.jtwitter.ecosystem;

import winterwell.json.JSONArray;
import java.util.HashMap;
import winterwell.json.JSONObject;
import winterwell.jtwitter.InternalUtils;
import java.util.Map;
import winterwell.jtwitter.URLConnectionHttpClient;
import winterwell.jtwitter.Twitter;

public class Klout
{
    final String API_KEY;
    Twitter.IHttpClient client;
    
    public Klout(final String apiKey) {
        this.client = new URLConnectionHttpClient();
        this.API_KEY = apiKey;
    }
    
    public Map<String, Double> getScore(final String... userNames) {
        final String unames = InternalUtils.join(userNames);
        final Map vars = InternalUtils.asMap("key", this.API_KEY, "users", unames);
        final String json = this.client.getPage("http://api.klout.com/1/klout.json", vars, false);
        final JSONObject jo = new JSONObject(json);
        final JSONArray users = jo.getJSONArray("users");
        final Map<String, Double> scores = new HashMap<String, Double>(users.length());
        for (int i = 0, n = users.length(); i < n; ++i) {
            final JSONObject u = users.getJSONObject(i);
            scores.put(u.getString("twitter_screen_name"), u.getDouble("kscore"));
        }
        return scores;
    }
}
