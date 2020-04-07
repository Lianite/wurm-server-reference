// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.jtwitter;

import java.text.ParseException;
import winterwell.json.JSONException;
import java.io.IOException;

public class TwitterException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    private String additionalInfo;
    
    TwitterException(final Exception e) {
        super(e);
        this.additionalInfo = "";
        assert !(e instanceof TwitterException) : e;
    }
    
    public TwitterException(final String string) {
        super(string);
        this.additionalInfo = "";
    }
    
    TwitterException(final String msg, final Exception e) {
        super(msg, e);
        this.additionalInfo = "";
        assert !(e instanceof TwitterException) : e;
    }
    
    public TwitterException(final String string, final String additionalInfo) {
        this(string);
        this.setAdditionalInfo(additionalInfo);
    }
    
    public String getAdditionalInfo() {
        return this.additionalInfo;
    }
    
    public void setAdditionalInfo(final String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
    
    public static class IO extends TwitterException
    {
        private static final long serialVersionUID = 1L;
        
        public IO(final IOException e) {
            super(e);
        }
        
        @Override
        public IOException getCause() {
            return (IOException)super.getCause();
        }
    }
    
    public static class AccessLevel extends E401
    {
        private static final long serialVersionUID = 1L;
        
        public AccessLevel(final String msg) {
            super(msg);
        }
    }
    
    public static class BadParameter extends E403
    {
        private static final long serialVersionUID = 1L;
        
        public BadParameter(final String msg) {
            super(msg);
        }
    }
    
    public static class E401 extends E40X
    {
        private static final long serialVersionUID = 1L;
        
        public E401(final String string) {
            super(string);
        }
    }
    
    public static class E403 extends E40X
    {
        private static final long serialVersionUID = 1L;
        
        public E403(final String string) {
            super(string);
        }
    }
    
    public static class E404 extends E40X
    {
        private static final long serialVersionUID = 1L;
        
        public E404(final String string) {
            super(string);
        }
    }
    
    public static class E406 extends E40X
    {
        private static final long serialVersionUID = 1L;
        
        public E406(final String string) {
            super(string);
        }
    }
    
    public static class E40X extends TwitterException
    {
        private static final long serialVersionUID = 1L;
        
        public E40X(final String string) {
            super(string);
        }
    }
    
    public static class E413 extends E40X
    {
        private static final long serialVersionUID = 1L;
        
        public E413(final String string) {
            super(string);
        }
    }
    
    public static class E416 extends E40X
    {
        private static final long serialVersionUID = 1L;
        
        public E416(final String string) {
            super(string);
        }
    }
    
    public static class E50X extends TwitterException
    {
        private static final long serialVersionUID = 1L;
        
        public E50X(final String string) {
            super(msg(string));
        }
        
        static String msg(String msg) {
            if (msg == null) {
                return null;
            }
            msg = InternalUtils.TAG_REGEX.matcher(msg).replaceAll("");
            msg = msg.replaceAll("\\s+", " ");
            if (msg.length() > 280) {
                msg = String.valueOf(msg.substring(0, 280)) + "...";
            }
            return msg;
        }
    }
    
    public static class FollowerLimit extends E403
    {
        private static final long serialVersionUID = 1L;
        
        public FollowerLimit(final String msg) {
            super(msg);
        }
    }
    
    public static class Parsing extends TwitterException
    {
        private static final long serialVersionUID = 1L;
        
        private static String clip(final String json, final int len) {
            return (json == null) ? null : ((json.length() <= len) ? json : (String.valueOf(json.substring(0, len)) + "..."));
        }
        
        public Parsing(final String json, final JSONException e) {
            super(String.valueOf((json == null) ? String.valueOf(e) : clip(json, 280)) + causeLine(e), e);
        }
        
        private static String causeLine(final JSONException e) {
            if (e == null) {
                return "";
            }
            final StackTraceElement[] st = e.getStackTrace();
            StackTraceElement[] array;
            for (int length = (array = st).length, i = 0; i < length; ++i) {
                final StackTraceElement ste = array[i];
                if (!ste.getClassName().contains("JSON")) {
                    return " caused by " + ste;
                }
            }
            return "";
        }
        
        public Parsing(final String date, final ParseException e) {
            super(date, e);
        }
    }
    
    public static class RateLimit extends TwitterException
    {
        private static final long serialVersionUID = 1L;
        
        public RateLimit(final String string) {
            super(string);
        }
    }
    
    public static class Repetition extends E403
    {
        private static final long serialVersionUID = 1L;
        
        public Repetition(final String tweet) {
            super("Already tweeted! " + tweet);
        }
    }
    
    public static class SuspendedUser extends E403
    {
        private static final long serialVersionUID = 1L;
        
        SuspendedUser(final String msg) {
            super(msg);
        }
    }
    
    public static class Timeout extends E50X
    {
        private static final long serialVersionUID = 1L;
        
        public Timeout(final String string) {
            super(string);
        }
    }
    
    public static class TooManyLogins extends E40X
    {
        private static final long serialVersionUID = 1L;
        
        public TooManyLogins(final String string) {
            super(string);
        }
    }
    
    public static class TooRecent extends E403
    {
        private static final long serialVersionUID = 1L;
        
        TooRecent(final String msg) {
            super(msg);
        }
    }
    
    public static class TwitLongerException extends TwitterException
    {
        private static final long serialVersionUID = 1L;
        
        public TwitLongerException(final String string, final String details) {
            super(string, details);
        }
    }
    
    public static class Unexplained extends TwitterException
    {
        private static final long serialVersionUID = 1L;
        
        public Unexplained(final String msg) {
            super(msg);
        }
    }
    
    public static class UpdateToOAuth extends E401
    {
        private static final long serialVersionUID = 1L;
        
        public UpdateToOAuth() {
            super("You need to switch to OAuth. Twitter no longer support basic authentication.");
        }
    }
}
