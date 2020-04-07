// 
// Decompiled by Procyon v0.5.30
// 

package javax.mail.internet;

import javax.mail.Session;

class UniqueValue
{
    private static int id;
    
    public static String getUniqueBoundaryValue() {
        final StringBuffer s = new StringBuffer();
        s.append("----=_Part_").append(getUniqueId()).append("_").append(s.hashCode()).append('.').append(System.currentTimeMillis());
        return s.toString();
    }
    
    public static String getUniqueMessageIDValue(final Session ssn) {
        String suffix = null;
        final InternetAddress addr = InternetAddress.getLocalAddress(ssn);
        if (addr != null) {
            suffix = addr.getAddress();
        }
        else {
            suffix = "javamailuser@localhost";
        }
        final StringBuffer s = new StringBuffer();
        s.append(s.hashCode()).append('.').append(getUniqueId()).append('.').append(System.currentTimeMillis()).append('.').append("JavaMail.").append(suffix);
        return s.toString();
    }
    
    private static synchronized int getUniqueId() {
        return UniqueValue.id++;
    }
    
    static {
        UniqueValue.id = 0;
    }
}
