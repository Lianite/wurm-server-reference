// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.jtwitter;

import java.io.Serializable;
import java.util.Random;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import winterwell.json.JSONArray;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.ArrayList;
import winterwell.json.JSONException;
import java.util.Date;
import winterwell.json.JSONObject;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;
import java.io.Closeable;

public abstract class AStream implements Closeable
{
    public static int MAX_BUFFER;
    private static final int MAX_WAIT_SECONDS = 900;
    boolean autoReconnect;
    final Twitter.IHttpClient client;
    List<TwitterEvent> events;
    boolean fillInFollows;
    private int forgotten;
    List<Long> friends;
    final Twitter jtwit;
    private BigInteger lastId;
    final List<IListen> listeners;
    final List<Outage> outages;
    int previousCount;
    StreamGobbler readThread;
    InputStream stream;
    List<Object[]> sysEvents;
    List<Twitter.ITweet> tweets;
    boolean listenersOnly;
    
    static {
        AStream.MAX_BUFFER = 10000;
    }
    
    static int forgetIfFull(final List incoming) {
        if (incoming.size() < AStream.MAX_BUFFER) {
            return 0;
        }
        final int chop = AStream.MAX_BUFFER / 10;
        for (int i = 0; i < chop; ++i) {
            incoming.remove(0);
        }
        return chop;
    }
    
    static Object read3_parse(final JSONObject jo, final Twitter jtwitr) throws JSONException {
        if (jo.has("text")) {
            final Status tweet = new Status(jo, null);
            return tweet;
        }
        if (jo.has("direct_message")) {
            final Message dm = new Message(jo.getJSONObject("direct_message"));
            return dm;
        }
        final String eventType = jo.optString("event");
        if (eventType != "") {
            final TwitterEvent event = new TwitterEvent(jo, jtwitr);
            return event;
        }
        final JSONObject del = jo.optJSONObject("delete");
        if (del != null) {
            boolean isDM = false;
            JSONObject s = del.optJSONObject("status");
            if (s == null) {
                s = del.getJSONObject("direct_message");
                isDM = true;
            }
            final BigInteger id = new BigInteger(s.getString("id_str"));
            final BigInteger userId = new BigInteger(s.getString("user_id"));
            final User dummyUser = new User(null, userId);
            Twitter.ITweet deadTweet;
            if (isDM) {
                deadTweet = new Message(dummyUser, id);
            }
            else {
                deadTweet = new Status(dummyUser, null, id, null);
            }
            return new Object[] { "delete", deadTweet, userId };
        }
        final JSONObject limit = jo.optJSONObject("limit");
        if (limit != null) {
            final int cnt = limit.optInt("track");
            if (cnt == 0) {
                System.out.println(jo);
            }
            return new Object[] { "limit", cnt };
        }
        final JSONObject disconnect = jo.optJSONObject("disconnect");
        if (disconnect != null) {
            return new Object[] { "disconnect", disconnect };
        }
        System.out.println(jo);
        return new Object[] { "unknown", jo };
    }
    
    public AStream(final Twitter jtwit) {
        this.events = new ArrayList<TwitterEvent>();
        this.fillInFollows = true;
        this.lastId = BigInteger.ZERO;
        this.listeners = new ArrayList<IListen>(0);
        this.outages = Collections.synchronizedList(new ArrayList<Outage>());
        this.sysEvents = new ArrayList<Object[]>();
        this.tweets = new ArrayList<Twitter.ITweet>();
        this.client = jtwit.getHttpClient();
        this.jtwit = jtwit;
        this.client.setTimeout(91000);
    }
    
    public void addListener(final IListen listener) {
        synchronized (this.listeners) {
            this.listeners.remove(listener);
            this.listeners.add(0, listener);
        }
        // monitorexit(this.listeners)
    }
    
    public void addOutage(final Outage outage) {
        for (int i = 0; i < this.outages.size(); ++i) {
            final Outage o = this.outages.get(i);
            if (o.sinceId.compareTo(outage.sinceId) > 0) {
                this.outages.add(i, outage);
                return;
            }
        }
        this.outages.add(outage);
    }
    
    public void clear() {
        this.outages.clear();
        this.popEvents();
        this.popSystemEvents();
        this.popTweets();
    }
    
    @Override
    public synchronized void close() {
        if (this.readThread != null && Thread.currentThread() != this.readThread) {
            this.readThread.pleaseStop();
            if (this.readThread.isAlive()) {
                try {
                    Thread.sleep(100L);
                }
                catch (InterruptedException ex) {}
                this.readThread.interrupt();
            }
            this.readThread = null;
        }
        InternalUtils.close(this.stream);
        this.stream = null;
    }
    
    public synchronized void connect() throws TwitterException {
        if (this.isConnected()) {
            return;
        }
        this.close();
        assert this.readThread.stream == this : this;
        HttpURLConnection con = null;
        try {
            con = this.connect2();
            this.stream = con.getInputStream();
            if (this.readThread == null) {
                (this.readThread = new StreamGobbler(this)).setName("Gobble:" + this.toString());
                this.readThread.start();
            }
            else {
                assert Thread.currentThread() == this.readThread : this;
                assert this.readThread.stream == this : this.readThread;
            }
            if (this.isConnected()) {
                return;
            }
            Thread.sleep(10L);
            if (!this.isConnected()) {
                throw new TwitterException(this.readThread.ex);
            }
        }
        catch (Exception e) {
            if (e instanceof TwitterException) {
                throw (TwitterException)e;
            }
            throw new TwitterException(e);
        }
    }
    
    abstract HttpURLConnection connect2() throws Exception;
    
    public final Exception fillInOutages() throws UnsupportedOperationException {
        if (this.outages.size() == 0) {
            return null;
        }
        final Outage[] outs = this.outages.toArray(new Outage[0]);
        final Twitter jtwit2 = new Twitter(this.jtwit);
        Exception ex = null;
        Outage[] array;
        for (int length = (array = outs).length, i = 0; i < length; ++i) {
            final Outage outage = array[i];
            if (System.currentTimeMillis() - outage.untilTime >= 60000L) {
                final boolean ok = this.outages.remove(outage);
                if (ok) {
                    try {
                        jtwit2.setSinceId(outage.sinceId);
                        jtwit2.setUntilDate(new Date(outage.untilTime));
                        jtwit2.setMaxResults(100000);
                        this.fillInOutages2(jtwit2, outage);
                    }
                    catch (Throwable e) {
                        this.outages.add(outage);
                        if (e instanceof Exception) {
                            ex = (Exception)e;
                        }
                    }
                }
            }
        }
        return ex;
    }
    
    abstract void fillInOutages2(final Twitter p0, final Outage p1);
    
    @Override
    protected void finalize() throws Throwable {
        this.close();
    }
    
    public final List<TwitterEvent> getEvents() {
        this.read();
        return this.events;
    }
    
    public final int getForgotten() {
        return this.forgotten;
    }
    
    public final List<Outage> getOutages() {
        return this.outages;
    }
    
    public final List<Object[]> getSystemEvents() {
        this.read();
        return this.sysEvents;
    }
    
    public final List<Twitter.ITweet> getTweets() {
        this.read();
        return this.tweets;
    }
    
    public final boolean isAlive() {
        return this.isConnected() || (this.autoReconnect && (this.readThread != null && this.readThread.isAlive() && !this.readThread.stopFlag));
    }
    
    public final boolean isConnected() {
        return this.readThread != null && this.readThread.isAlive() && this.readThread.ex == null && !this.readThread.stopFlag;
    }
    
    public final List<TwitterEvent> popEvents() {
        final List evs = this.getEvents();
        this.events = new ArrayList<TwitterEvent>();
        return (List<TwitterEvent>)evs;
    }
    
    public final List<Object[]> popSystemEvents() {
        final List<Object[]> evs = this.getSystemEvents();
        this.sysEvents = new ArrayList<Object[]>();
        return evs;
    }
    
    public final List<Twitter.ITweet> popTweets() {
        final List<Twitter.ITweet> ts = this.getTweets();
        this.tweets = new ArrayList<Twitter.ITweet>();
        return ts;
    }
    
    private final void read() {
        if (this.readThread != null) {
            final String[] jsons = this.readThread.popJsons();
            String[] array;
            for (int length = (array = jsons).length, i = 0; i < length; ++i) {
                final String json = array[i];
                try {
                    this.read2(json);
                }
                catch (JSONException e) {
                    throw new TwitterException.Parsing(json, e);
                }
            }
        }
        if (this.isConnected()) {
            return;
        }
        if (this.readThread != null && this.readThread.stopFlag) {
            return;
        }
        final Exception ex = (this.readThread == null) ? null : this.readThread.ex;
        this.close();
        if (this.autoReconnect) {
            this.reconnect();
            return;
        }
        if (ex instanceof TwitterException) {
            throw (TwitterException)ex;
        }
        throw new TwitterException(ex);
    }
    
    private void read2(final String json) throws JSONException {
        final JSONObject jobj = new JSONObject(json);
        final JSONArray _friends = jobj.optJSONArray("friends");
        if (_friends != null) {
            this.read3_friends(_friends);
            return;
        }
        final Object object = read3_parse(jobj, this.jtwit);
        if (object instanceof Twitter.ITweet) {
            final Twitter.ITweet tweet = (Twitter.ITweet)object;
            if (this.tweets.contains(tweet)) {
                return;
            }
            this.tweets.add(tweet);
            if (tweet instanceof Status) {
                final BigInteger id = ((Status)tweet).id;
                if (id.compareTo(this.lastId) > 0) {
                    this.lastId = id;
                }
            }
            this.forgotten += forgetIfFull(this.tweets);
        }
        else {
            if (object instanceof TwitterEvent) {
                final TwitterEvent event = (TwitterEvent)object;
                this.events.add(event);
                this.forgotten += forgetIfFull(this.events);
                return;
            }
            if (object instanceof Object[]) {
                final Object[] sysEvent = (Object[])object;
                if ("delete".equals(sysEvent[0])) {
                    final Twitter.ITweet deadTweet = (Twitter.ITweet)sysEvent[1];
                    final boolean pruned = this.tweets.remove(deadTweet);
                    if (pruned) {
                        return;
                    }
                }
                else if ("limit".equals(sysEvent[0])) {
                    final Integer cnt = (Integer)sysEvent[1];
                    this.forgotten += cnt;
                }
                this.sysEvents.add(sysEvent);
                this.forgotten += forgetIfFull(this.sysEvents);
                return;
            }
            System.out.println(jobj);
        }
    }
    
    private void read3_friends(final JSONArray _friends) throws JSONException {
        final List<Long> oldFriends = this.friends;
        this.friends = new ArrayList<Long>(_friends.length());
        for (int i = 0, n = _friends.length(); i < n; ++i) {
            final long fi = _friends.getLong(i);
            this.friends.add(fi);
        }
        if (oldFriends == null || !this.fillInFollows) {
            return;
        }
        final HashSet<Long> friends2 = new HashSet<Long>(this.friends);
        friends2.removeAll(oldFriends);
        if (friends2.size() == 0) {
            return;
        }
        final Twitter_Users tu = new Twitter_Users(this.jtwit);
        final List<User> newFriends = tu.showById(friends2);
        final User you = this.jtwit.getSelf();
        for (final User nf : newFriends) {
            final TwitterEvent e = new TwitterEvent(new Date(), you, "follow", nf, null);
            this.events.add(e);
        }
        this.forgotten += forgetIfFull(this.events);
    }
    
    synchronized void reconnect() {
        final long now = System.currentTimeMillis();
        this.reconnect2();
        final long dt = System.currentTimeMillis() - now;
        this.addSysEvent(new Object[] { "reconnect", dt });
        if (this.lastId != BigInteger.ZERO) {
            this.outages.add(new Outage(this.lastId, System.currentTimeMillis()));
            if (this.outages.size() > 100000) {
                for (int i = 0; i < 1000; ++i) {
                    this.outages.remove(0);
                }
                this.forgotten += 10000;
            }
        }
    }
    
    void addSysEvent(final Object[] sysEvent) {
        this.sysEvents.add(sysEvent);
        if (this.listeners.size() == 0) {
            return;
        }
        synchronized (this.listeners) {
            try {
                for (final IListen listener : this.listeners) {
                    final boolean carryOn = listener.processSystemEvent(sysEvent);
                    if (!carryOn) {
                        break;
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        // monitorexit(this.listeners)
    }
    
    private void reconnect2() {
        try {
            this.connect();
        }
        catch (TwitterException.E40X e) {
            throw e;
        }
        catch (Exception e2) {
            System.out.println(e2);
            int wait = 20 + new Random().nextInt(40);
            int waited = 0;
            while (waited < 900) {
                try {
                    Thread.sleep(wait * 1000);
                    waited += wait;
                    if (wait < 300) {
                        wait *= 2;
                    }
                    this.connect();
                    return;
                }
                catch (TwitterException.E40X e3) {
                    throw e3;
                }
                catch (Exception e4) {
                    System.out.println(e4);
                }
            }
            throw new TwitterException.E50X("Could not connect to streaming server");
        }
    }
    
    synchronized void reconnectFromGobblerThread() {
        assert this.readThread == null : this;
        if (this.isConnected()) {
            return;
        }
        this.reconnect();
    }
    
    public boolean removeListener(final IListen listener) {
        synchronized (this.listeners) {
            // monitorexit(this.listeners)
            return this.listeners.remove(listener);
        }
    }
    
    public void setAutoReconnect(final boolean yes) {
        this.autoReconnect = yes;
    }
    
    @Deprecated
    public void setPreviousCount(final int previousCount) {
        this.previousCount = previousCount;
    }
    
    public static final class Outage implements Serializable
    {
        private static final long serialVersionUID = 1L;
        public final BigInteger sinceId;
        public final long untilTime;
        
        public Outage(final BigInteger sinceId, final long untilTime) {
            this.sinceId = sinceId;
            this.untilTime = untilTime;
        }
        
        @Override
        public String toString() {
            return "Outage[id:" + this.sinceId + " to time:" + this.untilTime + "]";
        }
    }
    
    public interface IListen
    {
        boolean processEvent(final TwitterEvent p0);
        
        boolean processSystemEvent(final Object[] p0);
        
        boolean processTweet(final Twitter.ITweet p0);
    }
}
