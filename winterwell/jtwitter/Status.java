// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.jtwitter;

import java.util.Iterator;
import java.util.regex.Matcher;
import winterwell.json.JSONException;
import winterwell.json.JSONObject;
import winterwell.json.JSONArray;
import java.util.ArrayList;
import java.util.Collections;
import java.math.BigInteger;
import java.util.List;
import java.util.EnumMap;
import java.util.Date;
import java.util.regex.Pattern;

public final class Status implements Twitter.ITweet
{
    static final Pattern AT_YOU_SIR;
    private static final String FAKE = "fake";
    private static final long serialVersionUID = 1L;
    public final Date createdAt;
    private EnumMap<Twitter.KEntityType, List<Twitter.TweetEntity>> entities;
    private boolean favorited;
    public final BigInteger id;
    public final BigInteger inReplyToStatusId;
    private String location;
    private Status original;
    private Place place;
    public final int retweetCount;
    boolean sensitive;
    public final String source;
    public final String text;
    public final User user;
    private String lang;
    
    static {
        AT_YOU_SIR = Pattern.compile("@(\\w+)");
    }
    
    static List<Status> getStatuses(final String json) throws TwitterException {
        if (json.trim().equals("")) {
            return Collections.emptyList();
        }
        try {
            final List<Status> tweets = new ArrayList<Status>();
            final JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); ++i) {
                final Object ai = arr.get(i);
                if (!JSONObject.NULL.equals(ai)) {
                    final JSONObject obj = (JSONObject)ai;
                    final Status tweet = new Status(obj, null);
                    tweets.add(tweet);
                }
            }
            return tweets;
        }
        catch (JSONException e) {
            if (json.startsWith("<")) {
                throw new TwitterException.E50X(InternalUtils.stripTags(json));
            }
            throw new TwitterException.Parsing(json, e);
        }
    }
    
    static List<Status> getStatusesFromSearch(final Twitter tw, final String json) {
        try {
            final JSONObject searchResults = new JSONObject(json);
            final List<Status> users = new ArrayList<Status>();
            final JSONArray arr = searchResults.getJSONArray("statuses");
            for (int i = 0; i < arr.length(); ++i) {
                final JSONObject obj = arr.getJSONObject(i);
                final Status s = new Status(obj, null);
                users.add(s);
            }
            return users;
        }
        catch (JSONException e) {
            throw new TwitterException.Parsing(json, e);
        }
    }
    
    static Object jsonGetLocn(final JSONObject object) throws JSONException {
        String _location = InternalUtils.jsonGet("location", object);
        if (_location != null && _location.length() == 0) {
            _location = null;
        }
        final JSONObject _place = object.optJSONObject("place");
        if (_location != null) {
            final Matcher m = InternalUtils.latLongLocn.matcher(_location);
            if (m.matches()) {
                _location = String.valueOf(m.group(2)) + "," + m.group(3);
            }
            return _location;
        }
        if (_place != null) {
            final Place place = new Place(_place);
            return place;
        }
        final JSONObject geo = object.optJSONObject("geo");
        if (geo != null && geo != JSONObject.NULL) {
            final JSONArray latLong = geo.getJSONArray("coordinates");
            _location = latLong.get(0) + "," + latLong.get(1);
        }
        return _location;
    }
    
    public String getLang() {
        return this.lang;
    }
    
    Status(final JSONObject object, final User user) throws TwitterException {
        try {
            final String _id = object.optString("id_str");
            this.id = new BigInteger((_id == "") ? object.get("id").toString() : _id);
            final JSONObject retweeted = object.optJSONObject("retweeted_status");
            if (retweeted != null) {
                this.original = new Status(retweeted, null);
            }
            String _text;
            final String _rawtext = _text = InternalUtils.jsonGet("text", object);
            boolean truncated = object.optBoolean("truncated");
            if (!truncated && this.original != null) {
                truncated = (_text.endsWith("\u2026") || _text.endsWith("..."));
            }
            String rtStart = null;
            if (truncated && this.original != null && _text.startsWith("RT ")) {
                rtStart = "RT @" + this.original.getUser() + ": ";
                _text = String.valueOf(rtStart) + this.original.getText();
            }
            else {
                _text = InternalUtils.unencode(_text);
            }
            this.text = _text;
            final String c = InternalUtils.jsonGet("created_at", object);
            this.createdAt = InternalUtils.parseDate(c);
            final String src = InternalUtils.jsonGet("source", object);
            this.source = ((src != null && src.contains("&lt;")) ? InternalUtils.unencode(src) : src);
            final String irt = InternalUtils.jsonGet("in_reply_to_status_id", object);
            if (irt == null || irt.length() == 0) {
                this.inReplyToStatusId = ((this.original == null) ? null : this.original.getId());
            }
            else {
                this.inReplyToStatusId = new BigInteger(irt);
            }
            this.favorited = object.optBoolean("favorited");
            if (user != null) {
                this.user = user;
            }
            else {
                final JSONObject jsonUser = object.optJSONObject("user");
                if (jsonUser == null) {
                    this.user = null;
                }
                else if (jsonUser.opt("screen_name") == null) {
                    final String _uid = jsonUser.optString("id_str");
                    final BigInteger userId = new BigInteger((_uid == "") ? object.get("id").toString() : _uid);
                    this.user = new User(null, userId);
                }
                else {
                    this.user = new User(jsonUser, this);
                }
            }
            final Object _locn = jsonGetLocn(object);
            this.location = ((_locn == null) ? null : _locn.toString());
            if (_locn instanceof Place) {
                this.place = (Place)_locn;
            }
            final String _lang = object.optString("lang");
            this.lang = ("und".equals(_lang) ? null : _lang);
            this.retweetCount = object.optInt("retweet_count", -1);
            final JSONObject jsonEntities = object.optJSONObject("entities");
            if (jsonEntities != null) {
                this.entities = new EnumMap<Twitter.KEntityType, List<Twitter.TweetEntity>>(Twitter.KEntityType.class);
                this.setupEntities(_rawtext, rtStart, jsonEntities);
            }
            this.sensitive = object.optBoolean("possibly_sensitive");
        }
        catch (JSONException e) {
            throw new TwitterException.Parsing(null, e);
        }
    }
    
    private void setupEntities(final String _rawtext, final String rtStart, final JSONObject jsonEntities) {
        if (rtStart != null) {
            final int rt = rtStart.length();
            Twitter.KEntityType[] values;
            for (int length = (values = Twitter.KEntityType.values()).length, i = 0; i < length; ++i) {
                final Twitter.KEntityType type = values[i];
                final List<Twitter.TweetEntity> es = this.original.getTweetEntities(type);
                if (es != null) {
                    final ArrayList rtEs = new ArrayList(es.size());
                    for (final Twitter.TweetEntity e : es) {
                        final Twitter.TweetEntity rte = new Twitter.TweetEntity(this, e.type, Math.min(rt + e.start, this.text.length()), Math.min(rt + e.end, this.text.length()), e.display);
                        rtEs.add(rte);
                    }
                    this.entities.put(type, rtEs);
                }
            }
            return;
        }
        Twitter.KEntityType[] values2;
        for (int length2 = (values2 = Twitter.KEntityType.values()).length, j = 0; j < length2; ++j) {
            final Twitter.KEntityType type2 = values2[j];
            final List<Twitter.TweetEntity> es2 = Twitter.TweetEntity.parse(this, _rawtext, type2, jsonEntities);
            this.entities.put(type2, es2);
        }
    }
    
    public Status(final User user, final String text, final Number id, final Date createdAt) {
        this.text = text;
        this.user = user;
        this.createdAt = createdAt;
        this.id = (BigInteger)((id == null) ? null : ((id instanceof BigInteger) ? id : new BigInteger(id.toString())));
        this.inReplyToStatusId = null;
        this.source = "fake";
        this.retweetCount = -1;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Status other = (Status)obj;
        return this.id.equals(other.id);
    }
    
    @Override
    public Date getCreatedAt() {
        return this.createdAt;
    }
    
    @Override
    public BigInteger getId() {
        return this.id;
    }
    
    @Override
    public String getLocation() {
        return this.location;
    }
    
    @Override
    public List<String> getMentions() {
        final Matcher m = Status.AT_YOU_SIR.matcher(this.text);
        final List<String> list = new ArrayList<String>(2);
        while (m.find()) {
            if (m.start() != 0 && Character.isLetterOrDigit(this.text.charAt(m.start() - 1))) {
                continue;
            }
            String mention = m.group(1);
            if (!Twitter.CASE_SENSITIVE_SCREENNAMES) {
                mention = mention.toLowerCase();
            }
            list.add(mention);
        }
        return list;
    }
    
    public Status getOriginal() {
        return this.original;
    }
    
    @Override
    public Place getPlace() {
        return this.place;
    }
    
    public String getSource() {
        return InternalUtils.stripTags(this.source);
    }
    
    @Override
    public String getText() {
        return this.text;
    }
    
    @Override
    public List<Twitter.TweetEntity> getTweetEntities(final Twitter.KEntityType type) {
        return (this.entities == null) ? null : this.entities.get(type);
    }
    
    @Override
    public User getUser() {
        return this.user;
    }
    
    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
    
    public boolean isFavorite() {
        return this.favorited;
    }
    
    public boolean isSensitive() {
        return this.sensitive;
    }
    
    @Override
    public String toString() {
        return this.text;
    }
    
    @Override
    public String getDisplayText() {
        return getDisplayText2(this);
    }
    
    static String getDisplayText2(final Twitter.ITweet tweet) {
        final List<Twitter.TweetEntity> es = tweet.getTweetEntities(Twitter.KEntityType.urls);
        final String _text = tweet.getText();
        if (es == null || es.size() == 0) {
            return _text;
        }
        final StringBuilder sb = new StringBuilder(200);
        int i = 0;
        for (final Twitter.TweetEntity entity : es) {
            sb.append(_text.substring(i, entity.start));
            sb.append(entity.displayVersion());
            i = entity.end;
        }
        if (i < _text.length()) {
            sb.append(_text.substring(i));
        }
        return sb.toString();
    }
}
