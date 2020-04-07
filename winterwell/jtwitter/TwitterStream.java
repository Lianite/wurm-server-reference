// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.jtwitter;

import java.util.Iterator;
import java.util.HashMap;
import java.net.HttpURLConnection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;

public class TwitterStream extends AStream
{
    public static int MAX_KEYWORDS;
    public static final int MAX_KEYWORD_LENGTH = 60;
    public static final int MAX_USERS = 5000;
    static Map<String, AStream> user2stream;
    private List<Long> follow;
    private List<double[]> locns;
    KMethod method;
    private List<String> track;
    
    static {
        TwitterStream.MAX_KEYWORDS = 400;
        TwitterStream.user2stream = new ConcurrentHashMap<String, AStream>();
    }
    
    public TwitterStream(final Twitter jtwit) {
        super(jtwit);
        this.method = KMethod.sample;
    }
    
    @Override
    HttpURLConnection connect2() throws Exception {
        this.connect3_rateLimit();
        final String url = "https://stream.twitter.com/1.1/statuses/" + this.method + ".json";
        final Map<String, String> vars = new HashMap<String, String>();
        if (this.follow != null && this.follow.size() != 0) {
            vars.put("follow", InternalUtils.join(this.follow, 0, Integer.MAX_VALUE));
        }
        if (this.track != null && this.track.size() != 0) {
            vars.put("track", InternalUtils.join(this.track, 0, Integer.MAX_VALUE));
        }
        if (vars.isEmpty() && this.method == KMethod.filter) {
            throw new IllegalStateException("No filters set for " + this);
        }
        vars.put("delimited", "length");
        final HttpURLConnection con = this.client.post2_connect(url, vars);
        return con;
    }
    
    private void connect3_rateLimit() {
        if (this.jtwit.getScreenName() == null) {
            return;
        }
        final AStream s = TwitterStream.user2stream.get(this.jtwit.getScreenName());
        if (s != null && s.isConnected()) {
            throw new TwitterException.TooManyLogins("One account, one stream (running: " + s + "; trying to run" + this + ").\n\tBut streams OR their filter parameters, so one stream can do a lot.");
        }
        if (TwitterStream.user2stream.size() > 500) {
            TwitterStream.user2stream = new ConcurrentHashMap<String, AStream>();
        }
        TwitterStream.user2stream.put(this.jtwit.getScreenName(), this);
    }
    
    @Override
    void fillInOutages2(final Twitter jtwit2, final Outage outage) {
        if (this.method != KMethod.filter) {
            throw new UnsupportedOperationException();
        }
        if (this.track != null) {
            for (final String keyword : this.track) {
                final List<Status> msgs = this.jtwit.search(keyword);
                for (final Status status : msgs) {
                    if (this.tweets.contains(status)) {
                        continue;
                    }
                    this.tweets.add(status);
                }
            }
        }
        if (this.follow != null) {
            for (final Long user : this.follow) {
                final List<Status> msgs = this.jtwit.getUserTimeline(user);
                for (final Status status : msgs) {
                    if (this.tweets.contains(status)) {
                        continue;
                    }
                    this.tweets.add(status);
                }
            }
        }
        if (this.locns != null && !this.locns.isEmpty()) {
            throw new UnsupportedOperationException("TODO");
        }
    }
    
    public List<String> getTrackKeywords() {
        return this.track;
    }
    
    public void setFollowUsers(final List<Long> userIds) throws IllegalArgumentException {
        this.method = KMethod.filter;
        if (userIds != null && userIds.size() > 5000) {
            throw new IllegalArgumentException("Track upto 5000 users - not " + userIds.size());
        }
        this.follow = userIds;
    }
    
    public List<Long> getFollowUsers() {
        return this.follow;
    }
    
    @Deprecated
    public void setLocation(final List<double[]> boundingBoxes) {
        this.method = KMethod.filter;
        this.locns = boundingBoxes;
        throw new RuntimeException("TODO! Not implemented yet (sorry)");
    }
    
    void setMethod(final KMethod method) {
        this.method = method;
    }
    
    public void setTrackKeywords(final List<String> keywords) {
        if (keywords.size() > TwitterStream.MAX_KEYWORDS) {
            throw new IllegalArgumentException("Too many tracked terms: " + keywords.size() + " (" + TwitterStream.MAX_KEYWORDS + " limit)");
        }
        for (final String kw : keywords) {
            if (kw.length() > 60) {
                throw new IllegalArgumentException("Track term too long: " + kw + " (60 char limit)");
            }
        }
        this.track = keywords;
        this.method = KMethod.filter;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TwitterStream");
        sb.append("[" + this.method);
        if (this.track != null) {
            sb.append(" track:" + InternalUtils.join(this.track, 0, 5));
        }
        if (this.follow != null && this.follow.size() > 0) {
            sb.append(" follow:" + InternalUtils.join(this.follow, 0, 5));
        }
        if (this.locns != null) {
            sb.append(" in:" + InternalUtils.join(this.locns, 0, 5));
        }
        sb.append(" by:" + this.jtwit.getScreenNameIfKnown());
        sb.append("]");
        return sb.toString();
    }
    
    public void setListenersOnly(final boolean listenersOnly) {
        this.listenersOnly = listenersOnly;
    }
    
    public enum KMethod
    {
        filter("filter", 0), 
        firehose("firehose", 1), 
        links("links", 2), 
        retweet("retweet", 3), 
        sample("sample", 4);
        
        private KMethod(final String s, final int n) {
        }
    }
}
