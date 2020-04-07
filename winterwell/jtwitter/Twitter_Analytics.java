// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.jtwitter;

import winterwell.json.JSONObject;
import java.util.Map;

public class Twitter_Analytics
{
    private Twitter.IHttpClient http;
    
    Twitter_Analytics(final Twitter.IHttpClient http) {
        this.http = http;
    }
    
    public int getUrlCount(final String url) {
        final Map vars = InternalUtils.asMap("url", url);
        final String json = this.http.getPage("http://urls.api.twitter.com/1/urls/count.json", vars, false);
        final JSONObject jo = new JSONObject(json);
        return jo.getInt("count");
    }
}
