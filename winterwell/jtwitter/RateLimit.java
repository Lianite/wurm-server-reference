// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.jtwitter;

import java.util.Date;
import winterwell.json.JSONObject;

public final class RateLimit
{
    public static final String RES_STREAM_USER = "/stream/user";
    public static final String RES_STREAM_KEYWORD = "/stream/keyword";
    public static final String RES_USERS_BULK_SHOW = "/users/lookup";
    public static final String RES_USERS_SHOW1 = "/users/show";
    public static final String RES_USER_TIMELINE = "/statuses/user_timeline";
    public static final String RES_SEARCH = "/search/tweets";
    public static final String RES_STATUS_SHOW = "/statuses/show";
    public static final String RES_USERS_SEARCH = "/users/search";
    public static final String RES_FRIENDSHIPS_SHOW = "/friendships/show";
    public static final String RES_TRENDS = "/trends/place";
    public static final String RES_LISTS_SHOW = "/lists/show";
    private String limit;
    private String remaining;
    private String reset;
    
    public RateLimit(final String limit, final String remaining, final String reset) {
        this.limit = limit;
        this.remaining = remaining;
        this.reset = reset;
    }
    
    RateLimit(final JSONObject jrl) {
        this(jrl.getString("limit"), jrl.getString("remaining"), jrl.getString("reset"));
    }
    
    public int getLimit() {
        return Integer.valueOf(this.limit);
    }
    
    public int getRemaining() {
        return Integer.valueOf(this.remaining);
    }
    
    public Date getReset() {
        return InternalUtils.parseDate(this.reset);
    }
    
    public boolean isOutOfDate() {
        return this.getReset().getTime() < System.currentTimeMillis();
    }
    
    @Override
    public String toString() {
        return this.remaining;
    }
    
    public void waitForReset() {
        final Long r = Long.valueOf(this.reset);
        final long now = System.currentTimeMillis();
        final long wait = r - now;
        if (wait < 0L) {
            return;
        }
        try {
            Thread.sleep(wait);
        }
        catch (InterruptedException e) {
            throw new TwitterException(e);
        }
    }
    
    public static String getResource(final String url) {
        if (!url.startsWith("https://api.twitter.com/1.1")) {
            return null;
        }
        final int s = "https://api.twitter.com/1.1".length();
        final int e = url.indexOf(".json", s);
        if (e == -1) {
            return null;
        }
        final int e2 = url.indexOf("/", s + 1);
        if (e2 == -1 || e2 > e) {
            return url.substring(s, e);
        }
        final int e3 = url.indexOf("/", e2 + 1);
        if (e3 == -1 || e3 > e) {
            return url.substring(s, e);
        }
        return url.substring(s, e3);
    }
}
