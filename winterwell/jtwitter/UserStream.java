// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.jtwitter;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class UserStream extends AStream
{
    boolean withFollowings;
    static Map<String, AStream> user2stream;
    
    static {
        UserStream.user2stream = new ConcurrentHashMap<String, AStream>();
    }
    
    public UserStream(final Twitter jtwit) {
        super(jtwit);
    }
    
    @Override
    HttpURLConnection connect2() throws IOException {
        this.connect3_rateLimit();
        final String url = "https://userstream.twitter.com/2/user.json?delimited=length";
        final Map<String, String> vars = (Map<String, String>)InternalUtils.asMap("with", this.withFollowings ? "followings" : "user");
        final HttpURLConnection con = this.client.connect(url, vars, true);
        return con;
    }
    
    private void connect3_rateLimit() {
        if (this.jtwit.getScreenName() == null) {
            return;
        }
        final AStream s = UserStream.user2stream.get(this.jtwit.getScreenName());
        if (s != null && s.isConnected()) {
            throw new TwitterException.TooManyLogins("One account, one UserStream");
        }
        if (UserStream.user2stream.size() > 500) {
            UserStream.user2stream = new ConcurrentHashMap<String, AStream>();
        }
        UserStream.user2stream.put(this.jtwit.getScreenName(), this);
    }
    
    @Override
    void fillInOutages2(final Twitter jtwit2, final Outage outage) throws UnsupportedOperationException, TwitterException {
        if (this.withFollowings) {
            throw new UnsupportedOperationException("TODO");
        }
        final List<Status> mentions = jtwit2.getMentions();
        for (final Status status : mentions) {
            if (this.tweets.contains(status)) {
                continue;
            }
            this.tweets.add(status);
        }
        final List<Status> updates = jtwit2.getUserTimeline(jtwit2.getScreenName());
        for (final Status status2 : updates) {
            if (this.tweets.contains(status2)) {
                continue;
            }
            this.tweets.add(status2);
        }
        final List<Message> dms = jtwit2.getDirectMessages();
        for (final Twitter.ITweet dm : dms) {
            if (this.tweets.contains(dm)) {
                continue;
            }
            this.tweets.add(dm);
        }
    }
    
    public Collection<Long> getFriends() {
        return this.friends;
    }
    
    public void setWithFollowings(final boolean withFollowings) {
        assert !this.isConnected();
        this.withFollowings = withFollowings;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserStream");
        sb.append("[" + this.jtwit.getScreenNameIfKnown());
        if (this.withFollowings) {
            sb.append(" +followings");
        }
        sb.append("]");
        return sb.toString();
    }
}
