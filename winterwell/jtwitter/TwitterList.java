// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.jtwitter;

import java.util.HashMap;
import winterwell.json.JSONArray;
import java.util.Collection;
import java.util.Map;
import winterwell.json.JSONException;
import java.util.ArrayList;
import winterwell.json.JSONObject;
import java.util.List;
import java.util.AbstractList;

public class TwitterList extends AbstractList<User>
{
    private boolean _private;
    private long cursor;
    private String description;
    private final Twitter.IHttpClient http;
    private Number id;
    private final Twitter jtwit;
    private int memberCount;
    private String name;
    private User owner;
    private String slug;
    private int subscriberCount;
    private final List<User> users;
    
    public static TwitterList get(final String ownerScreenName, final String slug, final Twitter jtwit) {
        return new TwitterList(ownerScreenName, slug, jtwit);
    }
    
    public static TwitterList get(final Number id, final Twitter jtwit) {
        return new TwitterList(id, jtwit);
    }
    
    TwitterList(final JSONObject json, final Twitter jtwit) throws JSONException {
        this.cursor = -1L;
        this.memberCount = -1;
        this.users = new ArrayList<User>();
        this.jtwit = jtwit;
        this.http = jtwit.getHttpClient();
        this.init2(json);
    }
    
    public TwitterList(final String ownerScreenName, final String slug, final Twitter jtwit) {
        this.cursor = -1L;
        this.memberCount = -1;
        this.users = new ArrayList<User>();
        assert ownerScreenName != null && slug != null && jtwit != null;
        this.jtwit = jtwit;
        this.owner = new User(ownerScreenName);
        this.name = slug;
        this.slug = slug;
        this.http = jtwit.getHttpClient();
        this.init();
    }
    
    public TwitterList(final String listName, final Twitter jtwit, final boolean isPublic, final String description) {
        this.cursor = -1L;
        this.memberCount = -1;
        this.users = new ArrayList<User>();
        assert listName != null && jtwit != null;
        this.jtwit = jtwit;
        final String ownerScreenName = jtwit.getScreenName();
        assert ownerScreenName != null;
        this.name = listName;
        this.slug = listName;
        this.http = jtwit.getHttpClient();
        final String url = String.valueOf(jtwit.TWITTER_URL) + "/lists/create.json";
        final Map<String, String> vars = (Map<String, String>)InternalUtils.asMap("name", listName, "mode", isPublic ? "public" : "private", "description", description);
        final String json = this.http.post(url, vars, true);
        try {
            final JSONObject jobj = new JSONObject(json);
            this.init2(jobj);
        }
        catch (JSONException e) {
            throw new TwitterException("Could not parse response: " + e);
        }
    }
    
    public TwitterList(final Number id, final Twitter jtwit) {
        this.cursor = -1L;
        this.memberCount = -1;
        this.users = new ArrayList<User>();
        assert id != null && jtwit != null;
        this.jtwit = jtwit;
        this.id = id;
        this.http = jtwit.getHttpClient();
        this.init();
    }
    
    @Override
    public boolean add(final User user) {
        if (this.users.contains(user)) {
            return false;
        }
        final String url = String.valueOf(this.jtwit.TWITTER_URL) + "/lists/members/create.json";
        final Map map = this.getListVars();
        map.put("screen_name", user.screenName);
        final String json = this.http.post(url, map, true);
        try {
            final JSONObject jobj = new JSONObject(json);
            this.memberCount = jobj.getInt("member_count");
            this.users.add(user);
            return true;
        }
        catch (JSONException e) {
            throw new TwitterException("Could not parse response: " + e);
        }
    }
    
    @Override
    public boolean addAll(final Collection<? extends User> newUsers) {
        final List newUsersList = new ArrayList(newUsers);
        newUsersList.removeAll(this.users);
        if (newUsersList.size() == 0) {
            return false;
        }
        final String url = String.valueOf(this.jtwit.TWITTER_URL) + "/lists/members/create_all.json";
        final Map map = this.getListVars();
        for (int batchSize = 100, i = 0; i < this.users.size(); i += batchSize) {
            final int last = i + batchSize;
            final String names = InternalUtils.join(newUsersList, i, last);
            map.put("screen_name", names);
            final String json = this.http.post(url, map, true);
            try {
                final JSONObject jobj = new JSONObject(json);
                this.memberCount = jobj.getInt("member_count");
            }
            catch (JSONException e) {
                throw new TwitterException("Could not parse response: " + e);
            }
        }
        return true;
    }
    
    public void delete() {
        final String URL = String.valueOf(this.jtwit.TWITTER_URL) + "/lists/destroy.json";
        this.http.post(URL, this.getListVars(), true);
    }
    
    @Override
    public User get(final int index) {
        final String url = String.valueOf(this.jtwit.TWITTER_URL) + "/lists/members.json";
        final Map<String, String> vars = this.getListVars();
        while (this.users.size() < index + 1 && this.cursor != 0L) {
            vars.put("cursor", Long.toString(this.cursor));
            final String json = this.http.getPage(url, vars, true);
            try {
                final JSONObject jobj = new JSONObject(json);
                final JSONArray jarr = (JSONArray)jobj.get("users");
                final List<User> users1page = User.getUsers(jarr.toString());
                this.users.addAll(users1page);
                this.cursor = new Long(jobj.getString("next_cursor"));
            }
            catch (JSONException e) {
                throw new TwitterException("Could not parse user list" + e);
            }
        }
        return this.users.get(index);
    }
    
    public String getDescription() {
        this.init();
        return this.description;
    }
    
    private Map<String, String> getListVars() {
        final Map vars = new HashMap();
        if (this.id != null) {
            vars.put("list_id", this.id);
            return (Map<String, String>)vars;
        }
        vars.put("owner_screen_name", this.owner.screenName);
        vars.put("slug", this.slug);
        return (Map<String, String>)vars;
    }
    
    public Number getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public User getOwner() {
        return this.owner;
    }
    
    public List<Status> getStatuses() throws TwitterException {
        final Map vars = this.getListVars();
        this.jtwit.addStandardishParameters(vars);
        final String url = String.valueOf(this.jtwit.TWITTER_URL) + "/lists/statuses.json";
        return this.jtwit.getStatuses(url, vars, true);
    }
    
    public int getSubscriberCount() {
        this.init();
        return this.subscriberCount;
    }
    
    public List<User> getSubscribers() {
        final String url = String.valueOf(this.jtwit.TWITTER_URL) + "/lists/subscribers.json";
        final Map<String, String> vars = this.getListVars();
        final String json = this.http.getPage(url, vars, true);
        try {
            final JSONObject jobj = new JSONObject(json);
            final JSONArray jsonUsers = jobj.getJSONArray("users");
            return User.getUsers2(jsonUsers);
        }
        catch (JSONException e) {
            throw new TwitterException("Could not parse response: " + e);
        }
    }
    
    private void init() {
        if (this.memberCount != -1) {
            return;
        }
        final String url = String.valueOf(this.jtwit.TWITTER_URL) + "/lists/show.json";
        final Map<String, String> vars = this.getListVars();
        final String json = this.http.getPage(url, vars, true);
        try {
            final JSONObject jobj = new JSONObject(json);
            this.init2(jobj);
        }
        catch (JSONException e) {
            throw new TwitterException("Could not parse response: " + e);
        }
    }
    
    private void init2(final JSONObject jobj) throws JSONException {
        this.memberCount = jobj.getInt("member_count");
        this.subscriberCount = jobj.getInt("subscriber_count");
        this.name = jobj.getString("name");
        this.slug = jobj.getString("slug");
        this.id = jobj.getLong("id");
        this._private = "private".equals(jobj.optString("mode"));
        this.description = jobj.optString("description");
        final JSONObject user = jobj.getJSONObject("user");
        this.owner = new User(user, null);
    }
    
    public boolean isPrivate() {
        this.init();
        return this._private;
    }
    
    @Override
    public boolean remove(final Object o) {
        try {
            final User user = (User)o;
            final String url = String.valueOf(this.jtwit.TWITTER_URL) + "/lists/members/destroy.json";
            final Map map = this.getListVars();
            map.put("screen_name", user.screenName);
            final String json = this.http.post(url, map, true);
            final JSONObject jobj = new JSONObject(json);
            this.memberCount = jobj.getInt("member_count");
            this.users.remove(user);
            return true;
        }
        catch (JSONException e) {
            throw new TwitterException("Could not parse response: " + e);
        }
    }
    
    public void setDescription(final String description) {
        final String url = String.valueOf(this.jtwit.TWITTER_URL) + "/lists/update.json";
        final Map<String, String> vars = this.getListVars();
        vars.put("description", description);
        final String json = this.http.getPage(url, vars, true);
        try {
            final JSONObject jobj = new JSONObject(json);
            this.init2(jobj);
        }
        catch (JSONException e) {
            throw new TwitterException("Could not parse response: " + e);
        }
    }
    
    public void setPrivate(final boolean isPrivate) {
        final String url = String.valueOf(this.jtwit.TWITTER_URL) + "/lists/update.json";
        final Map<String, String> vars = this.getListVars();
        vars.put("mode", isPrivate ? "private" : "public");
        final String json = this.http.getPage(url, vars, true);
        try {
            final JSONObject jobj = new JSONObject(json);
            this.init2(jobj);
        }
        catch (JSONException e) {
            throw new TwitterException("Could not parse response: " + e);
        }
    }
    
    @Override
    public int size() {
        this.init();
        return this.memberCount;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.getClass().getSimpleName()) + "[" + this.owner + "." + this.name + "]";
    }
}
