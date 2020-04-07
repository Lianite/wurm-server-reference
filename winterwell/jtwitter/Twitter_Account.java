// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.jtwitter;

import java.util.Date;
import java.util.ArrayList;
import winterwell.json.JSONArray;
import java.util.List;
import winterwell.json.JSONException;
import winterwell.json.JSONObject;
import java.util.Map;

public class Twitter_Account
{
    public static String COLOR_BG;
    public static String COLOR_LINK;
    public static String COLOR_SIDEBAR_BORDER;
    public static String COLOR_SIDEBAR_FILL;
    public static String COLOR_TEXT;
    private KAccessLevel accessLevel;
    final Twitter jtwit;
    
    static {
        Twitter_Account.COLOR_BG = "profile_background_color";
        Twitter_Account.COLOR_LINK = "profile_link_color";
        Twitter_Account.COLOR_SIDEBAR_BORDER = "profile_sidebar_border_color";
        Twitter_Account.COLOR_SIDEBAR_FILL = "profile_sidebar_fill_color";
        Twitter_Account.COLOR_TEXT = "profile_text_color";
    }
    
    public Twitter_Account(final Twitter jtwit) {
        assert jtwit.getHttpClient().canAuthenticate() : jtwit;
        this.jtwit = jtwit;
    }
    
    public Map<String, RateLimit> getRateLimits() {
        return ((URLConnectionHttpClient)this.jtwit.getHttpClient()).updateRateLimits();
    }
    
    public Search createSavedSearch(final String query) {
        final String url = String.valueOf(this.jtwit.TWITTER_URL) + "saved_searches/create.json";
        final Map vars = InternalUtils.asMap("query", query);
        final String json = this.jtwit.getHttpClient().post(url, vars, true);
        try {
            return this.makeSearch(new JSONObject(json));
        }
        catch (JSONException e) {
            throw new TwitterException.Parsing(json, e);
        }
    }
    
    public Search destroySavedSearch(final Long id) {
        final String url = String.valueOf(this.jtwit.TWITTER_URL) + "saved_searches/destroy/" + id + ".json";
        final String json = this.jtwit.getHttpClient().post(url, null, true);
        try {
            return this.makeSearch(new JSONObject(json));
        }
        catch (JSONException e) {
            throw new TwitterException.Parsing(json, e);
        }
    }
    
    public KAccessLevel getAccessLevel() {
        if (this.accessLevel != null) {
            return this.accessLevel;
        }
        try {
            this.verifyCredentials();
            return this.accessLevel;
        }
        catch (TwitterException.E401 e) {
            return KAccessLevel.NONE;
        }
    }
    
    public List<Search> getSavedSearches() {
        final String url = String.valueOf(this.jtwit.TWITTER_URL) + "saved_searches.json";
        final String json = this.jtwit.getHttpClient().getPage(url, null, true);
        try {
            final JSONArray ja = new JSONArray(json);
            final List<Search> searches = new ArrayList<Search>();
            for (int i = 0; i < ja.length(); ++i) {
                final JSONObject jo = ja.getJSONObject(i);
                final Search search = this.makeSearch(jo);
                searches.add(search);
            }
            return searches;
        }
        catch (JSONException e) {
            throw new TwitterException.Parsing(json, e);
        }
    }
    
    private Search makeSearch(final JSONObject jo) throws JSONException {
        final Date createdAt = InternalUtils.parseDate(jo.getString("created_at"));
        final Long id = jo.getLong("id");
        final String query = jo.getString("query");
        final Search search = new Search(id, createdAt, query);
        return search;
    }
    
    public User setProfile(final String name, final String url, final String location, final String description) {
        final Map<String, String> vars = (Map<String, String>)InternalUtils.asMap("name", name, "url", url, "location", location, "description", description);
        final String apiUrl = String.valueOf(this.jtwit.TWITTER_URL) + "/account/update_profile.json";
        final String json = this.jtwit.getHttpClient().post(apiUrl, vars, true);
        return InternalUtils.user(json);
    }
    
    public User setProfileColors(final Map<String, String> colorName2hexCode) {
        assert colorName2hexCode.size() != 0;
        final String url = String.valueOf(this.jtwit.TWITTER_URL) + "/account/update_profile_colors.json";
        final String json = this.jtwit.getHttpClient().post(url, colorName2hexCode, true);
        return InternalUtils.user(json);
    }
    
    @Override
    public String toString() {
        return "TwitterAccount[" + this.jtwit.getScreenName() + "]";
    }
    
    public User verifyCredentials() throws TwitterException.E401 {
        final String url = String.valueOf(this.jtwit.TWITTER_URL) + "/account/verify_credentials.json";
        final String json = this.jtwit.getHttpClient().getPage(url, null, true);
        final Twitter.IHttpClient client = this.jtwit.getHttpClient();
        final String al = client.getHeader("X-Access-Level");
        if (al != null) {
            if ("read".equals(al)) {
                this.accessLevel = KAccessLevel.READ_ONLY;
            }
            if ("read-write".equals(al)) {
                this.accessLevel = KAccessLevel.READ_WRITE;
            }
            if ("read-write-directmessages".equals(al)) {
                this.accessLevel = KAccessLevel.READ_WRITE_DM;
            }
        }
        final User self = InternalUtils.user(json);
        return this.jtwit.self = self;
    }
    
    public enum KAccessLevel
    {
        NONE("NONE", 0), 
        READ_ONLY("READ_ONLY", 1), 
        READ_WRITE("READ_WRITE", 2), 
        READ_WRITE_DM("READ_WRITE_DM", 3);
        
        private KAccessLevel(final String s, final int n) {
        }
    }
    
    public static class Search
    {
        private Date createdAt;
        private Long id;
        private String query;
        
        public Search(final Long id, final Date createdAt, final String query) {
            this.id = id;
            this.createdAt = createdAt;
            this.query = query;
        }
        
        public Date getCreatedAt() {
            return this.createdAt;
        }
        
        public Long getId() {
            return this.id;
        }
        
        public String getText() {
            return this.query;
        }
    }
}
