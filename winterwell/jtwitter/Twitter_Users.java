// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.jtwitter;

import java.util.Collections;
import winterwell.json.JSONArray;
import winterwell.json.JSONException;
import winterwell.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

public class Twitter_Users
{
    private final Twitter.IHttpClient http;
    private final Twitter jtwit;
    
    Twitter_Users(final Twitter jtwit) {
        this.jtwit = jtwit;
        this.http = jtwit.getHttpClient();
    }
    
    public User block(final String screenName) {
        final HashMap vars = new HashMap();
        vars.put("screen_name", screenName);
        final String json = this.http.post(String.valueOf(this.jtwit.TWITTER_URL) + "/blocks/create.json", vars, true);
        return InternalUtils.user(json);
    }
    
    List<User> bulkShow2(final String apiMethod, final Class stringOrNumber, final Collection screenNamesOrIds) {
        final boolean auth = InternalUtils.authoriseIn11(this.jtwit);
        final int batchSize = 100;
        final ArrayList<User> users = new ArrayList<User>(screenNamesOrIds.size());
        final List _screenNamesOrIds = (screenNamesOrIds instanceof List) ? ((List)screenNamesOrIds) : new ArrayList(screenNamesOrIds);
        for (int i = 0; i < _screenNamesOrIds.size(); i += batchSize) {
            final int last = i + batchSize;
            final String names = InternalUtils.join(_screenNamesOrIds, i, last);
            final String var = (stringOrNumber == String.class) ? "screen_name" : "user_id";
            final Map<String, String> vars = (Map<String, String>)InternalUtils.asMap(var, names);
            try {
                final String json = this.http.getPage(String.valueOf(this.jtwit.TWITTER_URL) + apiMethod, vars, auth);
                final List<User> usersi = User.getUsers(json);
                users.addAll(usersi);
            }
            catch (TwitterException.E404 e2) {}
            catch (TwitterException e) {
                if (users.size() == 0) {
                    throw e;
                }
                e.printStackTrace();
                break;
            }
        }
        return users;
    }
    
    public User follow(final String username) throws TwitterException {
        if (username == null) {
            throw new NullPointerException();
        }
        if (username.equals(this.jtwit.getScreenName())) {
            throw new IllegalArgumentException("follow yourself makes no sense");
        }
        String page = null;
        try {
            final Map<String, String> vars = (Map<String, String>)InternalUtils.asMap("screen_name", username);
            page = this.http.post(String.valueOf(this.jtwit.TWITTER_URL) + "/friendships/create.json", vars, true);
            return new User(new JSONObject(page), null);
        }
        catch (TwitterException.SuspendedUser e) {
            throw e;
        }
        catch (TwitterException.Repetition e4) {
            return null;
        }
        catch (TwitterException.E403 e2) {
            try {
                if (this.isFollowing(username)) {
                    return null;
                }
            }
            catch (TwitterException ex) {}
            throw e2;
        }
        catch (JSONException e3) {
            throw new TwitterException.Parsing(page, e3);
        }
    }
    
    public User follow(final User user) {
        return this.follow(user.screenName);
    }
    
    public List<Number> getBlockedIds() {
        final String json = this.http.getPage(String.valueOf(this.jtwit.TWITTER_URL) + "/blocks/ids.json", null, true);
        try {
            final JSONArray arr = json.startsWith("[") ? new JSONArray(json) : new JSONObject(json).getJSONArray("ids");
            final List<Number> ids = new ArrayList<Number>(arr.length());
            for (int i = 0, n = arr.length(); i < n; ++i) {
                ids.add(arr.getLong(i));
            }
            return ids;
        }
        catch (JSONException e) {
            throw new TwitterException.Parsing(json, e);
        }
    }
    
    public List<Number> getFollowerIDs() throws TwitterException {
        return this.getUserIDs(String.valueOf(this.jtwit.TWITTER_URL) + "/followers/ids.json", null, null);
    }
    
    public List<Number> getFollowerIDs(final String screenName) throws TwitterException {
        return this.getUserIDs(String.valueOf(this.jtwit.TWITTER_URL) + "/followers/ids.json", screenName, null);
    }
    
    public List<Number> getFollowerIDs(final long userId) throws TwitterException {
        return this.getUserIDs(String.valueOf(this.jtwit.TWITTER_URL) + "/followers/ids.json", null, userId);
    }
    
    @Deprecated
    public List<User> getFollowers() throws TwitterException {
        final List<Number> ids = this.getFollowerIDs();
        return this.getTweeps2(ids);
    }
    
    public List<User> getFollowers(final String username) throws TwitterException {
        final List<Number> ids = this.getFollowerIDs(username);
        return this.getTweeps2(ids);
    }
    
    public List<Number> getFriendIDs() throws TwitterException {
        return this.getUserIDs(String.valueOf(this.jtwit.TWITTER_URL) + "/friends/ids.json", null, null);
    }
    
    public List<Number> getFriendIDs(final String screenName) throws TwitterException {
        return this.getUserIDs(String.valueOf(this.jtwit.TWITTER_URL) + "/friends/ids.json", screenName, null);
    }
    
    public List<Number> getFriendIDs(final long userId) throws TwitterException {
        return this.getUserIDs(String.valueOf(this.jtwit.TWITTER_URL) + "/friends/ids.json", null, userId);
    }
    
    @Deprecated
    public List<User> getFriends() throws TwitterException {
        final List<Number> ids = this.getFriendIDs();
        return this.getTweeps2(ids);
    }
    
    public List<User> getFriends(final String username) throws TwitterException {
        final List<Number> ids = this.getFriendIDs(username);
        return this.getTweeps2(ids);
    }
    
    private List<User> getTweeps2(List<Number> ids) {
        if (ids.size() > 100) {
            ids = ids.subList(0, 100);
        }
        final List<User> users = this.showById(ids);
        return users;
    }
    
    public List<User> getRelationshipInfo(final List<String> screenNames) {
        if (screenNames.size() == 0) {
            return (List<User>)Collections.EMPTY_LIST;
        }
        final List<User> users = this.bulkShow2("/friendships/lookup.json", String.class, screenNames);
        return users;
    }
    
    public List<User> getRelationshipInfoById(final List<? extends Number> userIDs) {
        if (userIDs.size() == 0) {
            return (List<User>)Collections.EMPTY_LIST;
        }
        final List<User> users = this.bulkShow2("/friendships/lookup.json", Number.class, userIDs);
        return users;
    }
    
    public User getUser(final long userId) {
        return this.show(userId);
    }
    
    public User getUser(final String screenName) {
        return this.show(screenName);
    }
    
    private List<Number> getUserIDs(final String url, final String screenName, final Long userId) {
        Long cursor = -1L;
        final List<Number> ids = new ArrayList<Number>();
        if (screenName != null && userId != null) {
            throw new IllegalArgumentException("cannot use both screen_name and user_id when fetching user_ids");
        }
        final Map<String, String> vars = (Map<String, String>)InternalUtils.asMap("screen_name", screenName, "user_id", userId);
        while (cursor != 0L && !this.jtwit.enoughResults(ids)) {
            vars.put("cursor", String.valueOf(cursor));
            final String json = this.http.getPage(url, vars, this.http.canAuthenticate());
            try {
                JSONArray jarr;
                if (json.charAt(0) == '[') {
                    jarr = new JSONArray(json);
                    cursor = 0L;
                }
                else {
                    final JSONObject jobj = new JSONObject(json);
                    jarr = (JSONArray)jobj.get("ids");
                    cursor = new Long(jobj.getString("next_cursor"));
                }
                for (int i = 0; i < jarr.length(); ++i) {
                    ids.add(jarr.getLong(i));
                }
                if (jarr.length() == 0) {
                    break;
                }
                continue;
            }
            catch (JSONException e) {
                throw new TwitterException.Parsing(json, e);
            }
        }
        return ids;
    }
    
    private List<User> getUsers(final String url, final String screenName) {
        final Map<String, String> vars = (Map<String, String>)InternalUtils.asMap("screen_name", screenName);
        final List<User> users = new ArrayList<User>();
        Long cursor = -1L;
        while (cursor != 0L && !this.jtwit.enoughResults(users)) {
            vars.put("cursor", cursor.toString());
            try {
                final JSONObject jobj = new JSONObject(this.http.getPage(url, vars, this.http.canAuthenticate()));
                users.addAll(User.getUsers(jobj.getString("users")));
                cursor = new Long(jobj.getString("next_cursor"));
            }
            catch (JSONException e) {
                throw new TwitterException.Parsing(null, e);
            }
        }
        return users;
    }
    
    public boolean isBlocked(final Long userId) {
        try {
            final HashMap vars = new HashMap();
            vars.put("user_id", Long.toString(userId));
            final String json = this.http.getPage(String.valueOf(this.jtwit.TWITTER_URL) + "/blocks/exists.json", vars, true);
            return true;
        }
        catch (TwitterException.E404 e) {
            return false;
        }
    }
    
    public boolean isBlocked(final String screenName) {
        try {
            final HashMap vars = new HashMap();
            vars.put("screen_name", screenName);
            final String json = this.http.getPage(String.valueOf(this.jtwit.TWITTER_URL) + "/blocks/exists.json", vars, true);
            return true;
        }
        catch (TwitterException.E404 e) {
            return false;
        }
    }
    
    public boolean isFollower(final String userB) {
        return this.isFollower(userB, this.jtwit.getScreenName());
    }
    
    public boolean isFollower(final String followerScreenName, final String followedScreenName) {
        assert followerScreenName != null && followedScreenName != null;
        try {
            final Map vars = InternalUtils.asMap("source_screen_name", followerScreenName, "target_screen_name", followedScreenName);
            final String page = this.http.getPage(String.valueOf(this.jtwit.TWITTER_URL) + "/friendships/show.json", vars, this.http.canAuthenticate());
            final JSONObject jo = new JSONObject(page);
            final JSONObject trgt = jo.getJSONObject("relationship").getJSONObject("target");
            final boolean fby = trgt.getBoolean("followed_by");
            return fby;
        }
        catch (TwitterException.E403 e) {
            if (e instanceof TwitterException.SuspendedUser) {
                throw e;
            }
            final String whoFirst = followedScreenName.equals(this.jtwit.getScreenName()) ? followerScreenName : followedScreenName;
            try {
                this.show(whoFirst);
                final String whoSecond = whoFirst.equals(followedScreenName) ? followerScreenName : followedScreenName;
                if (whoSecond.equals(this.jtwit.getScreenName())) {
                    throw e;
                }
                this.show(whoSecond);
            }
            catch (TwitterException.RateLimit rateLimit) {}
            throw e;
        }
        catch (TwitterException e2) {
            if (e2.getMessage() != null && e2.getMessage().contains("Two user ids or screen_names must be supplied")) {
                throw new TwitterException("WTF? inputs: follower=" + followerScreenName + ", followed=" + followedScreenName + ", call-by=" + this.jtwit.getScreenName() + "; " + e2.getMessage());
            }
            throw e2;
        }
    }
    
    public boolean isFollowing(final String userB) {
        return this.isFollower(this.jtwit.getScreenName(), userB);
    }
    
    public boolean isFollowing(final User user) {
        return this.isFollowing(user.screenName);
    }
    
    public User leaveNotifications(final String screenName) {
        return this.setNotifications(screenName, false, null);
    }
    
    public User setNotifications(final String screenName, final Boolean device, final Boolean retweets) {
        if (device == null && retweets == null) {
            return null;
        }
        final Map<String, String> vars = (Map<String, String>)InternalUtils.asMap("screen_name", screenName, "device", device, "retweets", retweets);
        final String page = this.http.post(String.valueOf(this.jtwit.TWITTER_URL) + "/friendships/update.json", vars, true);
        try {
            final JSONObject jo = new JSONObject(page).getJSONObject("relationship").getJSONObject("target");
            return new User(jo, null);
        }
        catch (JSONException e) {
            throw new TwitterException.Parsing(page, e);
        }
    }
    
    public User notify(final String username) {
        final Map<String, String> vars = (Map<String, String>)InternalUtils.asMap("screen_name", username);
        final String page = this.http.getPage(String.valueOf(this.jtwit.TWITTER_URL) + "/notifications/follow.json", vars, true);
        try {
            return new User(new JSONObject(page), null);
        }
        catch (JSONException e) {
            throw new TwitterException.Parsing(page, e);
        }
    }
    
    public User reportSpammer(final String screenName) {
        final HashMap vars = new HashMap();
        vars.put("screen_name", screenName);
        final String json = this.http.post(String.valueOf(this.jtwit.TWITTER_URL) + "/report_spam.json", vars, true);
        return InternalUtils.user(json);
    }
    
    public List<User> searchUsers(final String searchTerm) {
        return this.searchUsers(searchTerm, 0);
    }
    
    public List<User> searchUsers(final String searchTerm, final int page) {
        assert searchTerm != null;
        final Map<String, String> vars = (Map<String, String>)InternalUtils.asMap("q", searchTerm);
        if (page > 1) {
            vars.put("page", Integer.toString(page));
        }
        if (this.jtwit.count != null && this.jtwit.count < 20) {
            vars.put("per_page", String.valueOf(this.jtwit.count));
        }
        final String json = this.http.getPage(String.valueOf(this.jtwit.TWITTER_URL) + "/users/search.json", vars, true);
        final List<User> users = User.getUsers(json);
        return users;
    }
    
    public List<User> show(final Collection<String> screenNames) {
        if (screenNames.size() == 0) {
            return (List<User>)Collections.EMPTY_LIST;
        }
        return this.bulkShow2("/users/lookup.json", String.class, screenNames);
    }
    
    public User show(final Number userId) {
        final Map<String, String> vars = (Map<String, String>)InternalUtils.asMap("user_id", userId.toString());
        final String json = this.http.getPage(String.valueOf(this.jtwit.TWITTER_URL) + "/users/show.json", vars, this.http.canAuthenticate());
        try {
            final User user = new User(new JSONObject(json), null);
            return user;
        }
        catch (JSONException e) {
            throw new TwitterException.Parsing(json, e);
        }
    }
    
    public User show(final String screenName) throws TwitterException, TwitterException.SuspendedUser {
        final Map vars = InternalUtils.asMap("screen_name", screenName);
        String json = "";
        try {
            json = this.http.getPage(String.valueOf(this.jtwit.TWITTER_URL) + "/users/show.json", vars, this.http.canAuthenticate());
        }
        catch (Exception e2) {
            throw new TwitterException.E404("User " + screenName + " does not seem to exist, their user account may have been removed from the service");
        }
        if (json.length() == 0) {
            throw new TwitterException.E404(String.valueOf(screenName) + " does not seem to exist");
        }
        try {
            final User user = new User(new JSONObject(json), null);
            return user;
        }
        catch (JSONException e) {
            throw new TwitterException.Parsing(json, e);
        }
    }
    
    public List<User> showById(final Collection<? extends Number> userIds) {
        if (userIds.size() == 0) {
            return (List<User>)Collections.EMPTY_LIST;
        }
        return this.bulkShow2("/users/lookup.json", Number.class, userIds);
    }
    
    public User stopFollowing(final String username) {
        String page;
        try {
            final Map<String, String> vars = (Map<String, String>)InternalUtils.asMap("screen_name", username);
            page = this.jtwit.http.post(String.valueOf(this.jtwit.TWITTER_URL) + "/friendships/destroy.json", vars, true);
        }
        catch (TwitterException e) {
            if (e.getMessage() != null && e.getMessage().contains("not friends")) {
                return null;
            }
            throw e;
        }
        try {
            final User user = new User(new JSONObject(page), null);
            return user;
        }
        catch (JSONException e2) {
            throw new TwitterException.Parsing(page, e2);
        }
    }
    
    public User stopFollowing(final User user) {
        return this.stopFollowing(user.screenName);
    }
    
    public User unblock(final String screenName) {
        final HashMap vars = new HashMap();
        vars.put("screen_name", screenName);
        final String json = this.http.post(String.valueOf(this.jtwit.TWITTER_URL) + "/blocks/destroy.json", vars, true);
        return InternalUtils.user(json);
    }
    
    public boolean userExists(final String screenName) {
        try {
            this.show(screenName);
        }
        catch (TwitterException.SuspendedUser e) {
            return false;
        }
        catch (TwitterException.E404 e2) {
            return false;
        }
        return true;
    }
}
