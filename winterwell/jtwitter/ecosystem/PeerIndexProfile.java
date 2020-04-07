// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.jtwitter.ecosystem;

import winterwell.json.JSONException;
import winterwell.json.JSONArray;
import java.util.ArrayList;
import winterwell.json.JSONObject;
import java.util.List;

public class PeerIndexProfile
{
    public final int peerIndex;
    public final int authority;
    public final String twitterScreenName;
    public final List<String> topics;
    public final int audience;
    public final int activity;
    public final String slug;
    public final String url;
    public final String name;
    
    @Override
    public String toString() {
        return "PeerIndexProfile[" + this.twitterScreenName + " " + this.peerIndex + " " + this.authority + ":" + this.audience + ":" + this.activity + " " + this.topics + "]";
    }
    
    PeerIndexProfile(final JSONObject jo) throws JSONException {
        this.peerIndex = jo.getInt("peerindex");
        this.authority = jo.getInt("authority");
        this.audience = jo.getInt("audience");
        this.activity = jo.getInt("activity");
        this.twitterScreenName = jo.getString("twitter");
        this.topics = new ArrayList<String>();
        final JSONArray _topics = jo.getJSONArray("topics");
        for (int i = 0; i < _topics.length(); ++i) {
            this.topics.add((String)_topics.get(i));
        }
        this.slug = jo.getString("slug");
        this.url = jo.getString("url");
        this.name = jo.optString("name");
    }
}
