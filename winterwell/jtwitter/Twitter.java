// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.jtwitter;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.io.File;
import java.util.regex.Matcher;
import java.util.Comparator;
import java.util.Collections;
import winterwell.jtwitter.ecosystem.TwitLonger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import winterwell.json.JSONArray;
import winterwell.json.JSONException;
import winterwell.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.math.BigInteger;
import java.util.Date;
import java.io.Serializable;

public class Twitter implements Serializable
{
    public static boolean CASE_SENSITIVE_SCREENNAMES;
    public static boolean CHECK_TWEET_LENGTH;
    public static int LINK_LENGTH;
    public static int MEDIA_LENGTH;
    public static long PHOTO_SIZE_LIMIT;
    public static final String SEARCH_MIXED = "mixed";
    public static final String SEARCH_POPULAR = "popular";
    public static final String SEARCH_RECENT = "recent";
    private static final long serialVersionUID = 1L;
    public static final String version = "2.8.7";
    public static final int MAX_CHARS = 140;
    static final String API_VERSION = "1.1";
    static final String DEFAULT_TWITTER_URL = "https://api.twitter.com/1.1";
    public static boolean WORRIED_ABOUT_TWITTER;
    Integer count;
    private String geocode;
    final IHttpClient http;
    boolean includeRTs;
    private String lang;
    private int maxResults;
    private double[] myLatLong;
    private String name;
    private String resultType;
    User self;
    private Date sinceDate;
    private Number sinceId;
    private String sourceApp;
    boolean tweetEntities;
    @Deprecated
    private transient String twitlongerApiKey;
    @Deprecated
    private transient String twitlongerAppName;
    String TWITTER_URL;
    private Date untilDate;
    private BigInteger untilId;
    private Long placeId;
    
    static {
        Twitter.CHECK_TWEET_LENGTH = true;
        Twitter.LINK_LENGTH = 22;
        Twitter.MEDIA_LENGTH = 23;
        Twitter.PHOTO_SIZE_LIMIT = 3145728L;
        Twitter.WORRIED_ABOUT_TWITTER = false;
    }
    
    public static Map<String, Double> getAPIStatus() throws Exception {
        final HashMap<String, Double> map = new HashMap<String, Double>();
        String json = null;
        try {
            final URLConnectionHttpClient client = new URLConnectionHttpClient();
            json = client.getPage("https://api.io.watchmouse.com/synth/current/39657/folder/7617/?fields=info;cur;24h.uptime", null, false);
            final JSONObject jobj = new JSONObject(json);
            final JSONArray jarr = jobj.getJSONArray("result");
            for (int i = 0; i < jarr.length(); ++i) {
                final JSONObject jo = jarr.getJSONObject(i);
                final String name = jo.getJSONObject("info").getString("name");
                final JSONObject h24 = jo.getJSONObject("24h");
                final double value = h24.getDouble("uptime");
                map.put(name, value);
            }
            return map;
        }
        catch (JSONException e) {
            throw new TwitterException.Parsing(json, e);
        }
        catch (Exception e2) {
            return map;
        }
    }
    
    public static User getUser(final String screenName, final List<User> users) {
        assert screenName != null && users != null;
        for (final User user : users) {
            if (screenName.equals(user.screenName)) {
                return user;
            }
        }
        return null;
    }
    
    public static void main(final String[] args) {
        if (args.length == 3) {
            final Twitter tw = new Twitter(args[0], args[1]);
            final Status s = tw.setStatus(args[2]);
            System.out.println(s);
            return;
        }
        System.out.println("Java interface for Twitter");
        System.out.println("--------------------------");
        System.out.println("Version 2.8.7");
        System.out.println("Released under LGPL by Winterwell Associates Ltd.");
        System.out.println("See source code, JavaDoc, or http://winterwell.com for details on how to use.");
    }
    
    public void setMyPlace(final Long placeId) {
        this.placeId = placeId;
    }
    
    public Twitter() {
        this(null, new URLConnectionHttpClient());
    }
    
    public Twitter(final String name, final IHttpClient client) {
        this.includeRTs = true;
        this.maxResults = -1;
        this.sourceApp = "jtwitterlib";
        this.tweetEntities = true;
        this.TWITTER_URL = "https://api.twitter.com/1.1";
        this.name = name;
        this.http = client;
        assert client != null;
    }
    
    public Twitter(final String screenName, final String password) {
        this(screenName, new URLConnectionHttpClient(screenName, password));
    }
    
    public Twitter(final Twitter jtwit) {
        this(jtwit.getScreenName(), jtwit.http.copy());
    }
    
    public Twitter_Account account() {
        return new Twitter_Account(this);
    }
    
    public Twitter_Analytics analytics() {
        return new Twitter_Analytics(this.http);
    }
    
    Map<String, String> addStandardishParameters(final Map<String, String> vars) {
        if (this.sinceId != null) {
            vars.put("since_id", this.sinceId.toString());
        }
        if (this.untilId != null) {
            vars.put("max_id", this.untilId.toString());
        }
        if (this.count != null) {
            vars.put("count", this.count.toString());
        }
        if (this.tweetEntities) {
            vars.put("include_entities", "1");
        }
        else {
            vars.put("include_entities", "0");
        }
        if (!this.includeRTs) {
            vars.put("include_rts", "0");
        }
        return vars;
    }
    
    @Deprecated
    public User befriend(final String username) throws TwitterException {
        return this.follow(username);
    }
    
    @Deprecated
    public User breakFriendship(final String username) {
        return this.stopFollowing(username);
    }
    
    public List<User> bulkShow(final List<String> screenNames) {
        return this.users().show(screenNames);
    }
    
    public List<User> bulkShowById(final List<? extends Number> userIds) {
        return this.users().showById(userIds);
    }
    
    private <T extends ITweet> List<T> dateFilter(final List<T> list) {
        if (this.sinceDate == null && this.untilDate == null) {
            return list;
        }
        final ArrayList<T> filtered = new ArrayList<T>(list.size());
        for (final T message : list) {
            if (message.getCreatedAt() == null) {
                filtered.add(message);
            }
            else {
                if (this.untilDate != null && this.untilDate.before(message.getCreatedAt())) {
                    continue;
                }
                if (this.sinceDate != null && this.sinceDate.after(message.getCreatedAt())) {
                    continue;
                }
                filtered.add(message);
            }
        }
        return filtered;
    }
    
    public void destroy(final ITweet tweet) throws TwitterException {
        if (tweet instanceof Status) {
            this.destroyStatus(tweet.getId());
        }
        else {
            this.destroyMessage((Message)tweet);
        }
    }
    
    private void destroyMessage(final Message dm) {
        final String page = this.post(String.valueOf(this.TWITTER_URL) + "/direct_messages/destroy/" + dm.id + ".json", null, true);
        assert page != null;
    }
    
    public void destroyMessage(final Number id) {
        final String page = this.post(String.valueOf(this.TWITTER_URL) + "/direct_messages/destroy/" + id + ".json", null, true);
        assert page != null;
    }
    
    public void destroyStatus(final Number id) throws TwitterException {
        final String page = this.post(String.valueOf(this.TWITTER_URL) + "/statuses/destroy/" + id + ".json", null, true);
        this.flush();
        assert page != null;
    }
    
    @Deprecated
    public void destroyStatus(final Status status) throws TwitterException {
        this.destroyStatus(status.getId());
    }
    
    boolean enoughResults(final List list) {
        return this.maxResults != -1 && list.size() >= this.maxResults;
    }
    
    void flush() {
        this.http.getPage("https://twitter.com/" + this.name, null, true);
    }
    
    @Deprecated
    public User follow(final String username) throws TwitterException {
        return this.users().follow(username);
    }
    
    @Override
    public String toString() {
        return (this.name == null) ? "Twitter" : ("Twitter[" + this.name + "]");
    }
    
    @Deprecated
    public User follow(final User user) {
        return this.follow(user.screenName);
    }
    
    public Twitter_Geo geo() {
        return new Twitter_Geo(this);
    }
    
    public List<Message> getDirectMessages() {
        return this.getMessages(String.valueOf(this.TWITTER_URL) + "/direct_messages.json", this.standardishParameters());
    }
    
    public List<Message> getDirectMessagesSent() {
        return this.getMessages(String.valueOf(this.TWITTER_URL) + "/direct_messages/sent.json", this.standardishParameters());
    }
    
    public List<Status> getFavorites() {
        return this.getFavorites(null);
    }
    
    public List<Status> getFavorites(final String screenName) {
        final Map<String, String> vars = (Map<String, String>)InternalUtils.asMap("screen_name", screenName);
        return this.getStatuses(String.valueOf(this.TWITTER_URL) + "/favorites/list.json", this.addStandardishParameters(vars), this.http.canAuthenticate());
    }
    
    @Deprecated
    public List<Number> getFollowerIDs() throws TwitterException {
        return this.users().getFollowerIDs();
    }
    
    @Deprecated
    public List<Number> getFollowerIDs(final String screenName) throws TwitterException {
        return this.users().getFollowerIDs(screenName);
    }
    
    @Deprecated
    public List<User> getFollowers() throws TwitterException {
        return this.users().getFollowers();
    }
    
    @Deprecated
    public List<User> getFollowers(final String username) throws TwitterException {
        return this.users().getFollowers(username);
    }
    
    @Deprecated
    public List<Number> getFriendIDs() throws TwitterException {
        return this.users().getFriendIDs();
    }
    
    @Deprecated
    public List<Number> getFriendIDs(final String screenName) throws TwitterException {
        return this.users().getFriendIDs(screenName);
    }
    
    @Deprecated
    public List<User> getFriends() throws TwitterException {
        return this.users().getFriends();
    }
    
    @Deprecated
    public List<User> getFriends(final String username) throws TwitterException {
        return this.users().getFriends(username);
    }
    
    @Deprecated
    public List<Status> getFriendsTimeline() throws TwitterException {
        return this.getHomeTimeline();
    }
    
    public List<Status> getHomeTimeline() throws TwitterException {
        assert this.http.canAuthenticate();
        return this.getStatuses(String.valueOf(this.TWITTER_URL) + "/statuses/home_timeline.json", this.standardishParameters(), true);
    }
    
    public IHttpClient getHttpClient() {
        return this.http;
    }
    
    public List<TwitterList> getLists() {
        return this.getLists(this.name);
    }
    
    public List<TwitterList> getListsAll(final User user) {
        assert !(!this.http.canAuthenticate()) : "No authenticating user";
        try {
            final String url = String.valueOf(this.TWITTER_URL) + "/lists/all.json";
            final Map<String, String> vars = (Map<String, String>)((user.screenName == null) ? InternalUtils.asMap("user_id", user.id) : InternalUtils.asMap("screen_name", user.screenName));
            final String listsJson = this.http.getPage(url, vars, this.http.canAuthenticate());
            final JSONObject wrapper = new JSONObject(listsJson);
            final JSONArray jarr = (JSONArray)wrapper.get("lists");
            final List<TwitterList> lists = new ArrayList<TwitterList>();
            for (int i = 0; i < jarr.length(); ++i) {
                final JSONObject li = jarr.getJSONObject(i);
                final TwitterList twList = new TwitterList(li, this);
                lists.add(twList);
            }
            return lists;
        }
        catch (JSONException e) {
            throw new TwitterException.Parsing(null, e);
        }
    }
    
    public List<TwitterList> getLists(final String screenName) {
        assert screenName != null;
        try {
            final String url = String.valueOf(this.TWITTER_URL) + "/lists/list.json";
            final Map<String, String> vars = (Map<String, String>)InternalUtils.asMap("screen_name", screenName);
            final String listsJson = this.http.getPage(url, vars, true);
            final JSONArray jarr = new JSONArray(listsJson);
            final List<TwitterList> lists = new ArrayList<TwitterList>();
            for (int i = 0; i < jarr.length(); ++i) {
                final JSONObject li = jarr.getJSONObject(i);
                final TwitterList twList = new TwitterList(li, this);
                lists.add(twList);
            }
            return lists;
        }
        catch (JSONException e) {
            throw new TwitterException.Parsing(null, e);
        }
    }
    
    public List<TwitterList> getListsContaining(final String screenName, final boolean filterToOwned) {
        assert screenName != null;
        try {
            final String url = String.valueOf(this.TWITTER_URL) + "/lists/memberships.json";
            final Map<String, String> vars = (Map<String, String>)InternalUtils.asMap("screen_name", screenName);
            if (filterToOwned) {
                assert this.http.canAuthenticate();
                vars.put("filter_to_owned_lists", "1");
            }
            final String listsJson = this.http.getPage(url, vars, this.http.canAuthenticate());
            final JSONObject wrapper = new JSONObject(listsJson);
            final JSONArray jarr = (JSONArray)wrapper.get("lists");
            final List<TwitterList> lists = new ArrayList<TwitterList>();
            for (int i = 0; i < jarr.length(); ++i) {
                final JSONObject li = jarr.getJSONObject(i);
                final TwitterList twList = new TwitterList(li, this);
                lists.add(twList);
            }
            return lists;
        }
        catch (JSONException e) {
            throw new TwitterException.Parsing(null, e);
        }
    }
    
    public List<TwitterList> getListsContainingMe() {
        return this.getListsContaining(this.name, false);
    }
    
    public String getLongStatus(final Status truncatedStatus) {
        final TwitLonger tl = new TwitLonger();
        return tl.getLongStatus(truncatedStatus);
    }
    
    public int getMaxResults() {
        return this.maxResults;
    }
    
    public List<Status> getMentions() {
        return this.getStatuses(String.valueOf(this.TWITTER_URL) + "/statuses/mentions_timeline.json", this.standardishParameters(), true);
    }
    
    private List<Message> getMessages(final String url, final Map<String, String> var) {
        if (this.maxResults < 1) {
            List<Message> msgs = Message.getMessages(this.http.getPage(url, var, true));
            msgs = this.dateFilter(msgs);
            return msgs;
        }
        BigInteger maxId = this.untilId;
        final List<Message> msgs2 = new ArrayList<Message>();
        while (msgs2.size() <= this.maxResults) {
            final String p = this.http.getPage(url, var, true);
            List<Message> nextpage = Message.getMessages(p);
            maxId = InternalUtils.getMinId(maxId, nextpage);
            nextpage = this.dateFilter(nextpage);
            msgs2.addAll(nextpage);
            if (nextpage.size() < 20) {
                break;
            }
            var.put("max_id", maxId.toString());
        }
        return msgs2;
    }
    
    public RateLimit getRateLimit(final KRequestType reqType) {
        return this.http.getRateLimit(reqType);
    }
    
    public int getRateLimitStatus() {
        final RateLimit rl = ((URLConnectionHttpClient)this.http).updateRateLimits().get(KRequestType.NORMAL.rateLimit);
        return (rl == null) ? 90 : rl.getRemaining();
    }
    
    public List<Status> getReplies() throws TwitterException {
        return this.getMentions();
    }
    
    public List<User> getRetweeters(final Status tweet) {
        final String url = String.valueOf(this.TWITTER_URL) + "/statuses/retweets/" + tweet.id + ".json";
        final Map<String, String> vars = this.addStandardishParameters(new HashMap<String, String>());
        final String json = this.http.getPage(url, vars, this.http.canAuthenticate());
        final List<Status> ss = Status.getStatuses(json);
        final List<User> users = new ArrayList<User>(ss.size());
        for (final Status status : ss) {
            users.add(status.getUser());
        }
        return users;
    }
    
    public List<Status> getRetweets(final Status tweet) {
        final String url = String.valueOf(this.TWITTER_URL) + "/statuses/retweets/" + tweet.id + ".json";
        final Map<String, String> vars = this.addStandardishParameters(new HashMap<String, String>());
        final String json = this.http.getPage(url, vars, true);
        final List<Status> newStyle = Status.getStatuses(json);
        try {
            final StringBuilder sq = new StringBuilder();
            sq.append("\"RT @" + tweet.getUser().getScreenName() + ": ");
            if (sq.length() + tweet.text.length() + 1 > 140) {
                final int i = tweet.text.lastIndexOf(32, 140 - sq.length() - 1);
                final String words = tweet.text.substring(0, i);
                sq.append(words);
            }
            else {
                sq.append(tweet.text);
            }
            sq.append('\"');
            final List<Status> oldStyle = this.search(sq.toString());
            newStyle.addAll(oldStyle);
            Collections.sort(newStyle, InternalUtils.NEWEST_FIRST);
            return newStyle;
        }
        catch (TwitterException e) {
            return newStyle;
        }
    }
    
    public List<Status> getRetweetsByMe() {
        final List<Status> myTweets = this.getUserTimeline();
        final List<Status> retweets = new ArrayList<Status>();
        for (final Status status : myTweets) {
            if (status.getOriginal() != null && status.getText().startsWith("RT")) {
                retweets.add(status);
            }
        }
        return retweets;
    }
    
    public List<Status> getRetweetsOfMe() {
        final String url = String.valueOf(this.TWITTER_URL) + "/statuses/retweets_of_me.json";
        final Map<String, String> vars = this.addStandardishParameters(new HashMap<String, String>());
        final String json = this.http.getPage(url, vars, true);
        return Status.getStatuses(json);
    }
    
    public String getScreenName() {
        if (this.name != null) {
            return this.name;
        }
        this.getSelf();
        return this.name;
    }
    
    public String getScreenNameIfKnown() {
        return this.name;
    }
    
    private Map<String, String> getSearchParams(final String searchTerm, final Integer rpp) {
        final Map vars = InternalUtils.asMap("count", rpp, "q", searchTerm);
        if (this.sinceId != null) {
            vars.put("since_id", this.sinceId.toString());
        }
        if (this.untilId != null) {
            vars.put("max_id", this.untilId.toString());
        }
        if (this.untilDate != null) {
            vars.put("until", InternalUtils.df.format(this.untilDate));
        }
        if (this.lang != null) {
            vars.put("lang", this.lang);
        }
        if (this.geocode != null) {
            vars.put("geocode", this.geocode);
        }
        if (this.resultType != null) {
            vars.put("result_type", this.resultType);
        }
        this.addStandardishParameters(vars);
        return (Map<String, String>)vars;
    }
    
    public User getSelf() {
        if (this.self != null) {
            return this.self;
        }
        if (this.http.canAuthenticate()) {
            this.account().verifyCredentials();
            this.name = this.self.getScreenName();
            return this.self;
        }
        if (this.name != null) {
            return this.self = new User(this.name);
        }
        return null;
    }
    
    public Status getStatus() throws TwitterException {
        final Map<String, String> vars = (Map<String, String>)InternalUtils.asMap("count", 6);
        final String json = this.http.getPage(String.valueOf(this.TWITTER_URL) + "/statuses/user_timeline.json", vars, true);
        final List<Status> statuses = Status.getStatuses(json);
        if (statuses.size() == 0) {
            return null;
        }
        return statuses.get(0);
    }
    
    public Status getStatus(final Number id) throws TwitterException {
        final boolean auth = InternalUtils.authoriseIn11(this);
        final Map vars = this.tweetEntities ? InternalUtils.asMap("include_entities", "1") : null;
        final String json = this.http.getPage(String.valueOf(this.TWITTER_URL) + "/statuses/show/" + id + ".json", vars, auth);
        try {
            return new Status(new JSONObject(json), null);
        }
        catch (JSONException e) {
            throw new TwitterException.Parsing(json, e);
        }
    }
    
    public Status getStatus(final String username) throws TwitterException {
        assert username != null;
        final Map<String, String> vars = (Map<String, String>)InternalUtils.asMap("id", username, "count", 6);
        final String json = this.http.getPage(String.valueOf(this.TWITTER_URL) + "/statuses/user_timeline.json", vars, this.http.canAuthenticate());
        final List<Status> statuses = Status.getStatuses(json);
        if (statuses.size() == 0) {
            return null;
        }
        return statuses.get(0);
    }
    
    List<Status> getStatuses(final String url, final Map<String, String> var, final boolean authenticate) {
        if (this.maxResults < 1) {
            List<Status> msgs;
            try {
                msgs = Status.getStatuses(this.http.getPage(url, var, authenticate));
            }
            catch (TwitterException.Parsing pex) {
                if (!this.http.isRetryOnError()) {
                    throw pex;
                }
                InternalUtils.sleep(250L);
                final String json = this.http.getPage(url, var, authenticate);
                msgs = Status.getStatuses(json);
            }
            msgs = this.dateFilter(msgs);
            return msgs;
        }
        BigInteger maxId = this.untilId;
        final List<Status> msgs2 = new ArrayList<Status>();
        while (msgs2.size() <= this.maxResults) {
            List<Status> nextpage;
            try {
                final String json2 = this.http.getPage(url, var, authenticate);
                nextpage = Status.getStatuses(json2);
            }
            catch (TwitterException.Parsing pex2) {
                if (!this.http.isRetryOnError()) {
                    throw pex2;
                }
                InternalUtils.sleep(250L);
                final String json3 = this.http.getPage(url, var, authenticate);
                nextpage = Status.getStatuses(json3);
            }
            if (nextpage.size() == 0) {
                break;
            }
            maxId = InternalUtils.getMinId(maxId, nextpage);
            msgs2.addAll(this.dateFilter(nextpage));
            var.put("max_id", maxId.toString());
        }
        return msgs2;
    }
    
    public List<String> getTrends() {
        return this.getTrends(1);
    }
    
    public List<String> getTrends(final Number woeid) {
        final String jsonTrends = this.http.getPage(String.valueOf(this.TWITTER_URL) + "/trends/place.json", InternalUtils.asMap("id", woeid), true);
        try {
            final JSONArray jarr = new JSONArray(jsonTrends);
            final JSONObject json1 = jarr.getJSONObject(0);
            final JSONArray json2 = json1.getJSONArray("trends");
            final List<String> trends = new ArrayList<String>();
            for (int i = 0; i < json2.length(); ++i) {
                final JSONObject ti = json2.getJSONObject(i);
                final String t = ti.getString("name");
                trends.add(t);
            }
            return trends;
        }
        catch (JSONException e) {
            throw new TwitterException.Parsing(jsonTrends, e);
        }
    }
    
    public Date getUntilDate() {
        return this.untilDate;
    }
    
    @Deprecated
    public User getUser(final long userId) {
        return this.show(userId);
    }
    
    @Deprecated
    public User getUser(final String screenName) {
        return this.show(screenName);
    }
    
    public List<Status> getUserTimeline() throws TwitterException {
        return this.getStatuses(String.valueOf(this.TWITTER_URL) + "/statuses/user_timeline.json", this.standardishParameters(), true);
    }
    
    public List<Status> getUserTimeline(final Long userId) throws TwitterException {
        final Map<String, String> vars = (Map<String, String>)InternalUtils.asMap("user_id", userId);
        this.addStandardishParameters(vars);
        final boolean authenticate = this.http.canAuthenticate();
        try {
            return this.getStatuses(String.valueOf(this.TWITTER_URL) + "/statuses/user_timeline.json", vars, authenticate);
        }
        catch (TwitterException.E401 e) {
            throw e;
        }
    }
    
    public List<Status> getUserTimeline(final String screenName) throws TwitterException {
        final Map<String, String> vars = (Map<String, String>)InternalUtils.asMap("screen_name", screenName);
        this.addStandardishParameters(vars);
        final boolean authenticate = this.http.canAuthenticate();
        try {
            return this.getStatuses(String.valueOf(this.TWITTER_URL) + "/statuses/user_timeline.json", vars, authenticate);
        }
        catch (TwitterException.E404 e2) {
            throw new TwitterException.E404("Twitter does not return any information for " + screenName + ". They may have been deleted long ago.");
        }
        catch (TwitterException.E401 e) {
            this.isSuspended(screenName);
            throw e;
        }
    }
    
    public List<Status> getUserTimelineWithRetweets(final String screenName) throws TwitterException {
        final Map<String, String> vars = (Map<String, String>)InternalUtils.asMap("screen_name", screenName, "include_rts", "1");
        this.addStandardishParameters(vars);
        final boolean authenticate = this.http.canAuthenticate();
        try {
            return this.getStatuses(String.valueOf(this.TWITTER_URL) + "/statuses/user_timeline.json", vars, authenticate);
        }
        catch (TwitterException.E401 e) {
            this.isSuspended(screenName);
            throw e;
        }
    }
    
    @Deprecated
    public boolean isFollower(final String userB) {
        return this.isFollower(userB, this.name);
    }
    
    public boolean isFollower(final String followerScreenName, final String followedScreenName) {
        return this.users().isFollower(followerScreenName, followedScreenName);
    }
    
    @Deprecated
    public boolean isFollowing(final String userB) {
        return this.isFollower(this.name, userB);
    }
    
    @Deprecated
    public boolean isFollowing(final User user) {
        return this.isFollowing(user.screenName);
    }
    
    public boolean isRateLimited(final KRequestType reqType, final int minCalls) {
        final RateLimit rl = this.getRateLimit(reqType);
        return rl != null && rl.getRemaining() < minCalls && !rl.isOutOfDate();
    }
    
    private void isSuspended(final String screenName) throws TwitterException.SuspendedUser {
        this.show(screenName);
    }
    
    public boolean isTwitlongerSetup() {
        return this.twitlongerApiKey != null && this.twitlongerAppName != null;
    }
    
    public boolean isValidLogin() {
        if (!this.http.canAuthenticate()) {
            return false;
        }
        try {
            final Twitter_Account ta = new Twitter_Account(this);
            final User u = ta.verifyCredentials();
            return true;
        }
        catch (TwitterException.E403 e2) {
            return false;
        }
        catch (TwitterException.E401 e3) {
            return false;
        }
        catch (TwitterException e) {
            throw e;
        }
    }
    
    private String post(final String uri, final Map<String, String> vars, final boolean authenticate) throws TwitterException {
        final String page = this.http.post(uri, vars, authenticate);
        return page;
    }
    
    public void reportSpam(final String screenName) {
        this.http.getPage(String.valueOf(this.TWITTER_URL) + "/version/report_spam.json", InternalUtils.asMap("screen_name", screenName), true);
    }
    
    public Status retweet(final Status tweet) {
        try {
            final String result = this.post(String.valueOf(this.TWITTER_URL) + "/statuses/retweet/" + tweet.getId() + ".json", null, true);
            return new Status(new JSONObject(result), null);
        }
        catch (TwitterException.E403 e) {
            final List<Status> rts = this.getRetweetsByMe();
            for (final Status rt : rts) {
                if (tweet.equals(rt.getOriginal())) {
                    throw new TwitterException.Repetition(rt.getText());
                }
            }
            throw e;
        }
        catch (JSONException e2) {
            throw new TwitterException.Parsing(null, e2);
        }
    }
    
    public List<Status> search(final String searchTerm) {
        return this.search(searchTerm, null, 100);
    }
    
    public List<Status> search(final String searchTerm, final ICallback callback, final int rpp) {
        if (rpp > 100 && this.maxResults < rpp) {
            throw new IllegalArgumentException("You need to switch on paging to fetch more than 100 search results. First call setMaxResults() to raise the limit above " + rpp);
        }
        if (searchTerm.length() > 1000) {
            throw new TwitterException.E406("Search query too long: " + searchTerm);
        }
        Map vars;
        if (this.maxResults < 100 && this.maxResults > 0) {
            vars = this.getSearchParams(searchTerm, this.maxResults);
        }
        else {
            vars = this.getSearchParams(searchTerm, rpp);
        }
        final List<Status> allResults = new ArrayList<Status>(Math.max(this.maxResults, rpp));
        final String url = String.valueOf(this.TWITTER_URL) + "/search/tweets.json";
        BigInteger maxId = this.untilId;
        do {
            vars.put("max_id", maxId);
            List<Status> stati;
            try {
                final String json = this.http.getPage(url, vars, true);
                stati = Status.getStatusesFromSearch(this, json);
            }
            catch (TwitterException.Parsing pex) {
                if (!this.http.isRetryOnError()) {
                    throw pex;
                }
                InternalUtils.sleep(250L);
                final String json2 = this.http.getPage(url, vars, true);
                stati = Status.getStatusesFromSearch(this, json2);
            }
            catch (TwitterException.E403 ex) {
                if (ex.getMessage() != null && ex.getMessage().startsWith("code 195:")) {
                    throw new TwitterException.E406("Search too long/complex: " + ex.getMessage());
                }
                throw ex;
            }
            final int numResults = stati.size();
            maxId = InternalUtils.getMinId(maxId, stati);
            stati = this.dateFilter(stati);
            allResults.addAll(stati);
            if (callback != null && callback.process(stati)) {
                break;
            }
            if (rpp == 100 && numResults < 70) {
                break;
            }
            if (numResults < rpp) {
                break;
            }
        } while (allResults.size() < this.maxResults);
        return allResults;
    }
    
    @Deprecated
    public List<User> searchUsers(final String searchTerm) {
        return this.users().searchUsers(searchTerm);
    }
    
    public Message sendMessage(final String recipient, final String text) throws TwitterException {
        assert recipient != null && text != null : String.valueOf(recipient) + " " + text;
        assert !text.startsWith("d " + recipient) : String.valueOf(recipient) + " " + text;
        assert !recipient.startsWith("@") : String.valueOf(recipient) + " " + text;
        if (text.length() > 140) {
            throw new IllegalArgumentException("Message is too long.");
        }
        final Map<String, String> vars = (Map<String, String>)InternalUtils.asMap("user", recipient, "text", text);
        if (this.tweetEntities) {
            vars.put("include_entities", "1");
        }
        String result = null;
        try {
            result = this.post(String.valueOf(this.TWITTER_URL) + "/direct_messages/new.json", vars, true);
            return new Message(new JSONObject(result));
        }
        catch (JSONException e) {
            throw new TwitterException.Parsing(result, e);
        }
        catch (TwitterException.E404 e2) {
            throw new TwitterException.E404(String.valueOf(e2.getMessage()) + " with recipient=" + recipient + ", text=" + text);
        }
    }
    
    public void setAPIRootUrl(final String url) {
        assert url.startsWith("http://") || url.startsWith("https://") : url;
        assert !url.endsWith("/") : "Please remove the trailing / from " + url;
        this.TWITTER_URL = url;
    }
    
    public void setCount(final Integer count) {
        this.count = count;
    }
    
    public Status setFavorite(final Status status, final boolean isFavorite) {
        try {
            final String uri = isFavorite ? (String.valueOf(this.TWITTER_URL) + "/favorites/create.json") : (String.valueOf(this.TWITTER_URL) + "/favorites/destroy.json");
            final String json = this.http.post(uri, InternalUtils.asMap("id", status.id), true);
            return new Status(new JSONObject(json), null);
        }
        catch (TwitterException.E403 e) {
            if (e.getMessage() != null && e.getMessage().contains("already favorited")) {
                return null;
            }
            throw e;
        }
    }
    
    public void setIncludeRTs(final boolean includeRTs) {
        this.includeRTs = includeRTs;
    }
    
    public void setIncludeTweetEntities(final boolean tweetEntities) {
        this.tweetEntities = tweetEntities;
    }
    
    public void setLanguage(final String language) {
        this.lang = language;
    }
    
    public void setMaxResults(final int maxResults) {
        assert maxResults != 0;
        this.maxResults = maxResults;
    }
    
    public void setMyLocation(final double[] latitudeLongitude) {
        this.myLatLong = latitudeLongitude;
        if (this.myLatLong == null) {
            return;
        }
        if (Math.abs(this.myLatLong[0]) > 90.0) {
            throw new IllegalArgumentException(String.valueOf(this.myLatLong[0]) + " is not within +/- 90");
        }
        if (Math.abs(this.myLatLong[1]) > 180.0) {
            throw new IllegalArgumentException(String.valueOf(this.myLatLong[1]) + " is not within +/- 180");
        }
    }
    
    public void setSearchLocation(final double latitude, final double longitude, final String radius) {
        assert radius.endsWith("mi") || radius.endsWith("km") : radius;
        this.geocode = String.valueOf((float)latitude) + "," + (float)longitude + "," + radius;
    }
    
    public String getSearchLocation() {
        return this.geocode;
    }
    
    public void setSearchResultType(final String resultType) {
        this.resultType = resultType;
    }
    
    @Deprecated
    public void setSinceDate(final Date sinceDate) {
        this.sinceDate = sinceDate;
    }
    
    public void setSinceId(final Number statusId) {
        this.sinceId = statusId;
    }
    
    public void setSource(final String sourceApp) {
        this.sourceApp = sourceApp;
    }
    
    public Status setStatus(final String statusText) throws TwitterException {
        return this.updateStatus(statusText);
    }
    
    @Deprecated
    public void setUntilDate(final Date untilDate) {
        this.untilDate = untilDate;
    }
    
    public void setUntilId(final Number untilId) {
        if (untilId == null) {
            this.untilId = null;
            return;
        }
        if (untilId instanceof BigInteger) {
            this.untilId = (BigInteger)untilId;
            return;
        }
        this.untilId = BigInteger.valueOf(untilId.longValue());
    }
    
    public BigInteger getUntilId() {
        return this.untilId;
    }
    
    public Number getSinceId() {
        return this.sinceId;
    }
    
    public void setupTwitlonger(final String twitlongerAppName, final String twitlongerApiKey) {
        this.twitlongerAppName = twitlongerAppName;
        this.twitlongerApiKey = twitlongerApiKey;
    }
    
    @Deprecated
    public User show(final Number userId) {
        return this.users().show(userId);
    }
    
    @Deprecated
    public User show(final String screenName) throws TwitterException, TwitterException.SuspendedUser {
        return this.users().show(screenName);
    }
    
    public List<String> splitMessage(final String longStatus) {
        if (longStatus.length() <= 140) {
            return Collections.singletonList(longStatus);
        }
        final List<String> sections = new ArrayList<String>(4);
        StringBuilder tweet = new StringBuilder(140);
        final String[] words = longStatus.split("\\s+");
        String[] array;
        for (int length = (array = words).length, i = 0; i < length; ++i) {
            final String w = array[i];
            if (tweet.length() + w.length() + 1 > 140) {
                tweet.append("...");
                sections.add(tweet.toString());
                tweet = new StringBuilder(140);
                tweet.append(w);
            }
            else {
                if (tweet.length() != 0) {
                    tweet.append(" ");
                }
                tweet.append(w);
            }
        }
        if (tweet.length() != 0) {
            sections.add(tweet.toString());
        }
        return sections;
    }
    
    private Map<String, String> standardishParameters() {
        return this.addStandardishParameters(new HashMap<String, String>());
    }
    
    @Deprecated
    public User stopFollowing(final String username) {
        return this.users().stopFollowing(username);
    }
    
    @Deprecated
    public User stopFollowing(final User user) {
        return this.stopFollowing(user.screenName);
    }
    
    public boolean updateConfiguration() {
        final String json = this.http.getPage(String.valueOf(this.TWITTER_URL) + "/help/configuration.json", null, true);
        boolean change = false;
        try {
            final JSONObject jo = new JSONObject(json);
            int len = jo.getInt("short_url_length");
            if (len != Twitter.LINK_LENGTH) {
                change = true;
            }
            Twitter.LINK_LENGTH = len;
            len = jo.getInt("characters_reserved_per_media");
            if (len != Twitter.MEDIA_LENGTH) {
                change = true;
            }
            Twitter.MEDIA_LENGTH = len;
            final long lmt = jo.getLong("photo_size_limit");
            if (lmt != Twitter.PHOTO_SIZE_LIMIT) {
                change = true;
            }
            Twitter.PHOTO_SIZE_LIMIT = lmt;
            return change;
        }
        catch (JSONException e) {
            throw new TwitterException.Parsing(json, e);
        }
    }
    
    public Status updateLongStatus(final String message, final Number inReplyToStatusId) {
        final TwitLonger tl = new TwitLonger(this, this.twitlongerApiKey, this.twitlongerAppName);
        return tl.updateLongStatus(message, inReplyToStatusId);
    }
    
    public Status updateStatus(final String statusText) {
        return this.updateStatus(statusText, null);
    }
    
    public static int countCharacters(final String statusText) {
        int shortLength = statusText.length();
        final Matcher m = Regex.VALID_URL.matcher(statusText);
        while (m.find()) {
            shortLength += Twitter.LINK_LENGTH - m.group().length();
            if (m.group().startsWith("https")) {
                ++shortLength;
            }
        }
        return shortLength;
    }
    
    public Status updateStatus(final String statusText, final Number inReplyToStatusId) throws TwitterException {
        final Map<String, String> vars = this.updateStatus2_vars(statusText, inReplyToStatusId, false);
        final String result = this.http.post(String.valueOf(this.TWITTER_URL) + "/statuses/update.json", vars, true);
        try {
            Status s = new Status(new JSONObject(result), null);
            s = this.updateStatus2_safetyCheck(statusText, s);
            return s;
        }
        catch (JSONException e) {
            throw new TwitterException.Parsing(result, e);
        }
    }
    
    private Map<String, String> updateStatus2_vars(final String statusText, final Number inReplyToStatusId, final boolean withMedia) {
        final int max = withMedia ? (140 - Twitter.MEDIA_LENGTH) : 140;
        if (statusText.length() > max && this.TWITTER_URL.contains("twitter") && Twitter.CHECK_TWEET_LENGTH) {
            final int shortLength = countCharacters(statusText);
            if (shortLength > max) {
                if (statusText.startsWith("RT")) {
                    throw new IllegalArgumentException("Status text must be 140 characters or less -- use Twitter.retweet() to do new-style retweets which can be a bit longer: " + statusText.length() + " " + statusText);
                }
                if (withMedia) {
                    throw new IllegalArgumentException("Status-with-media text must be " + max + " characters or less: " + statusText.length() + " " + statusText);
                }
                throw new IllegalArgumentException("Status text must be 140 characters or less: " + statusText.length() + " " + statusText);
            }
        }
        final Map<String, String> vars = (Map<String, String>)InternalUtils.asMap("status", statusText);
        if (this.tweetEntities) {
            vars.put("include_entities", "1");
        }
        if (this.myLatLong != null) {
            vars.put("lat", Double.toString(this.myLatLong[0]));
            vars.put("long", Double.toString(this.myLatLong[1]));
        }
        if (this.placeId != null) {
            vars.put("place_id", Long.toString(this.placeId));
        }
        if (this.sourceApp != null) {
            vars.put("source", this.sourceApp);
        }
        if (inReplyToStatusId != null) {
            final double v = inReplyToStatusId.doubleValue();
            assert v != 0.0 && v != -1.0;
            vars.put("in_reply_to_status_id", inReplyToStatusId.toString());
        }
        return vars;
    }
    
    private Status updateStatus2_safetyCheck(final String statusText, final Status s) {
        final String st = statusText.toLowerCase();
        if (st.startsWith("dm ") || st.startsWith("d ")) {
            return null;
        }
        if (!Twitter.WORRIED_ABOUT_TWITTER) {
            return s;
        }
        String targetText = statusText.trim();
        String returnedStatusText = s.text.trim();
        targetText = InternalUtils.stripUrls(targetText);
        returnedStatusText = InternalUtils.stripUrls(returnedStatusText);
        if (returnedStatusText.equals(targetText)) {
            return s;
        }
        try {
            Thread.sleep(500L);
        }
        catch (InterruptedException ex) {}
        final Status s2 = this.getStatus();
        if (s2 != null) {
            returnedStatusText = InternalUtils.stripUrls(s2.text.trim());
            if (targetText.equals(returnedStatusText)) {
                return s2;
            }
        }
        throw new TwitterException.Unexplained("Unexplained failure for tweet: expected \"" + statusText + "\" but got " + s2);
    }
    
    public Status updateStatusWithMedia(final String statusText, final BigInteger inReplyToStatusId, final File mediaFile) {
        if (mediaFile == null || !mediaFile.isFile()) {
            throw new IllegalArgumentException("Invalid file: " + mediaFile);
        }
        final Map vars = this.updateStatus2_vars(statusText, inReplyToStatusId, true);
        vars.put("media[]", mediaFile);
        String result = null;
        try {
            final String url = String.valueOf(this.TWITTER_URL) + "/statuses/update_with_media.json";
            result = ((OAuthSignpostClient)this.http).postMultipartForm(url, vars);
            final Status s = new Status(new JSONObject(result), null);
            return s;
        }
        catch (TwitterException.E403 e) {
            final Status s = this.getStatus();
            if (s != null && s.getText().equals(statusText)) {
                throw new TwitterException.Repetition(s.getText());
            }
            throw e;
        }
        catch (JSONException e2) {
            throw new TwitterException.Parsing(result, e2);
        }
    }
    
    public Twitter_Users users() {
        return new Twitter_Users(this);
    }
    
    public enum KRequestType
    {
        NORMAL("NORMAL", 0, "/statuses/user_timeline"), 
        SEARCH("SEARCH", 1, "/search/tweets"), 
        SEARCH_USERS("SEARCH_USERS", 2, "/users/search"), 
        SHOW_USER("SHOW_USER", 3, "/users/show"), 
        UPLOAD_MEDIA("UPLOAD_MEDIA", 4, "Media"), 
        STREAM_KEYWORD("STREAM_KEYWORD", 5, ""), 
        STREAM_USER("STREAM_USER", 6, "");
        
        final String rateLimit;
        
        private KRequestType(final String s, final int n, final String rateLimit) {
            this.rateLimit = rateLimit;
        }
    }
    
    public enum KEntityType
    {
        hashtags("hashtags", 0), 
        urls("urls", 1), 
        user_mentions("user_mentions", 2);
        
        private KEntityType(final String s, final int n) {
        }
    }
    
    public static final class TweetEntity implements Serializable
    {
        private static final long serialVersionUID = 1L;
        final String display;
        public final int end;
        public final int start;
        private final ITweet tweet;
        public final KEntityType type;
        
        static ArrayList<TweetEntity> parse(final ITweet tweet, final String rawText, final KEntityType type, final JSONObject jsonEntities) throws JSONException {
            assert type != null && tweet != null && rawText != null && jsonEntities != null : tweet + "\t" + rawText + "\t" + type + "\t" + jsonEntities;
            try {
                final JSONArray arr = jsonEntities.optJSONArray(type.toString());
                if (arr == null || arr.length() == 0) {
                    return null;
                }
                final ArrayList<TweetEntity> list = new ArrayList<TweetEntity>(arr.length());
                for (int i = 0; i < arr.length(); ++i) {
                    final JSONObject obj = arr.getJSONObject(i);
                    final TweetEntity te = new TweetEntity(tweet, rawText, type, obj, list);
                    list.add(te);
                }
                return list;
            }
            catch (Throwable e) {
                return null;
            }
        }
        
        TweetEntity(final ITweet tweet, final String rawText, final KEntityType type, final JSONObject obj, final ArrayList<TweetEntity> previous) throws JSONException {
            this.tweet = tweet;
            this.type = type;
            switch (type) {
                case urls: {
                    final Object eu = obj.opt("expanded_url");
                    this.display = (JSONObject.NULL.equals(eu) ? null : ((String)eu));
                    break;
                }
                case user_mentions: {
                    this.display = obj.getString("name");
                    break;
                }
                default: {
                    this.display = null;
                    break;
                }
            }
            final JSONArray indices = obj.getJSONArray("indices");
            int _start = indices.getInt(0);
            int _end = indices.getInt(1);
            assert _start >= 0 && _end >= _start : obj;
            final String text = tweet.getText();
            if (rawText.regionMatches(_start, text, _start, _end - _start)) {
                this.start = _start;
                this.end = _end;
                return;
            }
            _end = Math.min(_end, rawText.length());
            _start = Math.min(_start, _end);
            if (_start == _end) {
                switch (type) {
                    case urls: {
                        final Matcher m = Regex.VALID_URL.matcher(text);
                        if (m.find()) {
                            this.start = m.start();
                            this.end = m.end();
                            return;
                        }
                        break;
                    }
                }
                this.end = Math.min(_end, text.length());
                this.start = Math.min(_start, this.end);
                return;
            }
            String entityText = rawText.substring(_start, _end);
            int from = 0;
            for (final TweetEntity prev : previous) {
                if (tweet.getText().regionMatches(prev.start, entityText, 0, entityText.length())) {
                    from = prev.end;
                }
            }
            int i = text.indexOf(entityText, from);
            if (i == -1) {
                entityText = InternalUtils.unencode(entityText);
                i = text.indexOf(entityText);
                if (i == -1) {
                    i = _start;
                }
            }
            this.start = i;
            this.end = this.start + _end - _start;
        }
        
        TweetEntity(final ITweet tweet, final KEntityType type, final int start, final int end, final String display) {
            this.tweet = tweet;
            this.end = end;
            this.start = start;
            this.type = type;
            this.display = display;
        }
        
        public String displayVersion() {
            return (this.display == null) ? this.toString() : this.display;
        }
        
        @Override
        public String toString() {
            final String text = this.tweet.getText();
            final int e = Math.min(this.end, text.length());
            final int s = Math.min(this.start, e);
            return text.substring(s, e);
        }
    }
    
    public interface ICallback
    {
        boolean process(final List<Status> p0);
    }
    
    public interface IHttpClient
    {
        boolean canAuthenticate();
        
        HttpURLConnection connect(final String p0, final Map<String, String> p1, final boolean p2) throws IOException;
        
        IHttpClient copy();
        
        String getHeader(final String p0);
        
        String getPage(final String p0, final Map<String, String> p1, final boolean p2) throws TwitterException;
        
        RateLimit getRateLimit(final KRequestType p0);
        
        Map<String, RateLimit> getRateLimits();
        
        String post(final String p0, final Map<String, String> p1, final boolean p2) throws TwitterException;
        
        HttpURLConnection post2_connect(final String p0, final Map<String, String> p1) throws Exception;
        
        void setTimeout(final int p0);
        
        boolean isRetryOnError();
        
        void setRetryOnError(final boolean p0);
    }
    
    public interface ITweet extends Serializable
    {
        Date getCreatedAt();
        
        BigInteger getId();
        
        String getLocation();
        
        List<String> getMentions();
        
        Place getPlace();
        
        String getText();
        
        List<TweetEntity> getTweetEntities(final KEntityType p0);
        
        User getUser();
        
        String getDisplayText();
    }
}
