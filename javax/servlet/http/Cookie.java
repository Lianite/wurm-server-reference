// 
// Decompiled by Procyon v0.5.30
// 

package javax.servlet.http;

import java.util.Locale;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.io.Serializable;

public class Cookie implements Cloneable, Serializable
{
    private static final long serialVersionUID = -6454587001725327448L;
    private static final String TSPECIALS;
    private static final String LSTRING_FILE = "javax.servlet.http.LocalStrings";
    private static ResourceBundle lStrings;
    private String name;
    private String value;
    private String comment;
    private String domain;
    private int maxAge;
    private String path;
    private boolean secure;
    private int version;
    private boolean isHttpOnly;
    
    public Cookie(final String name, final String value) {
        this.maxAge = -1;
        this.version = 0;
        this.isHttpOnly = false;
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException(Cookie.lStrings.getString("err.cookie_name_blank"));
        }
        if (!this.isToken(name) || name.equalsIgnoreCase("Comment") || name.equalsIgnoreCase("Discard") || name.equalsIgnoreCase("Domain") || name.equalsIgnoreCase("Expires") || name.equalsIgnoreCase("Max-Age") || name.equalsIgnoreCase("Path") || name.equalsIgnoreCase("Secure") || name.equalsIgnoreCase("Version") || name.startsWith("$")) {
            String errMsg = Cookie.lStrings.getString("err.cookie_name_is_token");
            final Object[] errArgs = { name };
            errMsg = MessageFormat.format(errMsg, errArgs);
            throw new IllegalArgumentException(errMsg);
        }
        this.name = name;
        this.value = value;
    }
    
    public void setComment(final String purpose) {
        this.comment = purpose;
    }
    
    public String getComment() {
        return this.comment;
    }
    
    public void setDomain(final String domain) {
        this.domain = domain.toLowerCase(Locale.ENGLISH);
    }
    
    public String getDomain() {
        return this.domain;
    }
    
    public void setMaxAge(final int expiry) {
        this.maxAge = expiry;
    }
    
    public int getMaxAge() {
        return this.maxAge;
    }
    
    public void setPath(final String uri) {
        this.path = uri;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public void setSecure(final boolean flag) {
        this.secure = flag;
    }
    
    public boolean getSecure() {
        return this.secure;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setValue(final String newValue) {
        this.value = newValue;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public void setVersion(final int v) {
        this.version = v;
    }
    
    private boolean isToken(final String value) {
        for (int len = value.length(), i = 0; i < len; ++i) {
            final char c = value.charAt(i);
            if (c < ' ' || c >= '\u007f' || Cookie.TSPECIALS.indexOf(c) != -1) {
                return false;
            }
        }
        return true;
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    public void setHttpOnly(final boolean isHttpOnly) {
        this.isHttpOnly = isHttpOnly;
    }
    
    public boolean isHttpOnly() {
        return this.isHttpOnly;
    }
    
    static {
        Cookie.lStrings = ResourceBundle.getBundle("javax.servlet.http.LocalStrings");
        if (Boolean.valueOf(System.getProperty("org.glassfish.web.rfc2109_cookie_names_enforced", "true"))) {
            TSPECIALS = "/()<>@,;:\\\"[]?={} \t";
        }
        else {
            TSPECIALS = ",; ";
        }
    }
}
