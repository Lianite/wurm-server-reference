// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.jtwitter.ecosystem;

import winterwell.json.JSONException;
import winterwell.jtwitter.TwitterException;
import winterwell.json.JSONObject;
import java.util.Map;
import winterwell.jtwitter.InternalUtils;
import winterwell.jtwitter.URLConnectionHttpClient;
import winterwell.jtwitter.Twitter;

public class Topsy
{
    private Twitter.IHttpClient client;
    private String apikey;
    
    public Topsy() {
        this.client = new URLConnectionHttpClient();
    }
    
    public Topsy(final String apiKey) {
        this.client = new URLConnectionHttpClient();
        this.apikey = apiKey;
    }
    
    public UrlInfo getUrlInfo(final String url) {
        final Map vars = InternalUtils.asMap("url", url);
        if (this.apikey != null) {
            vars.put("apikey", this.apikey);
        }
        final String json = this.client.getPage("http://otter.topsy.com/urlinfo.json", vars, false);
        try {
            final JSONObject jo = new JSONObject(json);
            final JSONObject resp = jo.getJSONObject("response");
            return new UrlInfo(resp);
        }
        catch (JSONException e) {
            throw new TwitterException.Parsing(json, e);
        }
    }
    
    public static final class UrlInfo
    {
        public final String title;
        public final int linkCount;
        public final String desc;
        public final String url;
        
        public UrlInfo(final JSONObject resp) throws JSONException {
            this.url = resp.getString("url");
            this.title = resp.getString("title");
            this.linkCount = resp.getInt("trackback_total");
            this.desc = resp.getString("description");
        }
        
        @Override
        public String toString() {
            return String.valueOf(this.url) + " " + this.linkCount + " " + this.title;
        }
    }
}
