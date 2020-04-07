// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.jtwitter;

import java.math.BigInteger;
import winterwell.json.JSONObject;
import winterwell.json.JSONException;
import winterwell.json.JSONArray;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.EnumMap;
import java.util.Date;

public final class Message implements Twitter.ITweet
{
    private static final long serialVersionUID = 1L;
    private final Date createdAt;
    private EnumMap<Twitter.KEntityType, List<Twitter.TweetEntity>> entities;
    public final Number id;
    public Number inReplyToMessageId;
    private String location;
    private Place place;
    private final User recipient;
    private final User sender;
    public final String text;
    
    @Override
    public String getDisplayText() {
        return Status.getDisplayText2(this);
    }
    
    static List<Message> getMessages(final String json) throws TwitterException {
        if (json.trim().equals("")) {
            return Collections.emptyList();
        }
        try {
            final List<Message> msgs = new ArrayList<Message>();
            final JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); ++i) {
                final JSONObject obj = arr.getJSONObject(i);
                final Message u = new Message(obj);
                msgs.add(u);
            }
            return msgs;
        }
        catch (JSONException e) {
            throw new TwitterException.Parsing(json, e);
        }
    }
    
    Message(final User dummyUser, final Number id) {
        this.sender = dummyUser;
        this.id = id;
        this.recipient = null;
        this.createdAt = null;
        this.text = null;
    }
    
    Message(final JSONObject obj) throws JSONException, TwitterException {
        this.id = obj.getLong("id");
        final String _text = obj.getString("text");
        this.text = InternalUtils.unencode(_text);
        final String c = InternalUtils.jsonGet("created_at", obj);
        this.createdAt = InternalUtils.parseDate(c);
        this.sender = new User(obj.getJSONObject("sender"), null);
        final Object recip = obj.opt("recipient");
        if (recip instanceof JSONObject) {
            this.recipient = new User((JSONObject)recip, null);
        }
        else {
            this.recipient = null;
        }
        final JSONObject jsonEntities = obj.optJSONObject("entities");
        if (jsonEntities != null) {
            this.entities = new EnumMap<Twitter.KEntityType, List<Twitter.TweetEntity>>(Twitter.KEntityType.class);
            Twitter.KEntityType[] values;
            for (int length = (values = Twitter.KEntityType.values()).length, i = 0; i < length; ++i) {
                final Twitter.KEntityType type = values[i];
                final List<Twitter.TweetEntity> es = Twitter.TweetEntity.parse(this, _text, type, jsonEntities);
                this.entities.put(type, es);
            }
        }
        final Object _locn = Status.jsonGetLocn(obj);
        this.location = ((_locn == null) ? null : _locn.toString());
        if (_locn instanceof Place) {
            this.place = (Place)_locn;
        }
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
        final Message other = (Message)obj;
        return this.id.equals(other.id);
    }
    
    @Override
    public Date getCreatedAt() {
        return this.createdAt;
    }
    
    @Override
    public BigInteger getId() {
        if (this.id instanceof Long) {
            return BigInteger.valueOf(this.id.longValue());
        }
        return (BigInteger)this.id;
    }
    
    @Override
    public String getLocation() {
        return this.location;
    }
    
    @Override
    public List<String> getMentions() {
        return Collections.singletonList(this.recipient.screenName);
    }
    
    @Override
    public Place getPlace() {
        return this.place;
    }
    
    public User getRecipient() {
        return this.recipient;
    }
    
    public User getSender() {
        return this.sender;
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
        return this.getSender();
    }
    
    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
    
    @Override
    public String toString() {
        return this.text;
    }
}
