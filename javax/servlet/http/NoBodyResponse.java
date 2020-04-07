// 
// Decompiled by Procyon v0.5.30
// 

package javax.servlet.http;

import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import javax.servlet.ServletOutputStream;
import java.io.PrintWriter;
import java.util.ResourceBundle;

class NoBodyResponse extends HttpServletResponseWrapper
{
    private static final ResourceBundle lStrings;
    private NoBodyOutputStream noBody;
    private PrintWriter writer;
    private boolean didSetContentLength;
    private boolean usingOutputStream;
    
    NoBodyResponse(final HttpServletResponse r) {
        super(r);
        this.noBody = new NoBodyOutputStream();
    }
    
    void setContentLength() {
        if (!this.didSetContentLength) {
            if (this.writer != null) {
                this.writer.flush();
            }
            this.setContentLength(this.noBody.getContentLength());
        }
    }
    
    public void setContentLength(final int len) {
        super.setContentLength(len);
        this.didSetContentLength = true;
    }
    
    public ServletOutputStream getOutputStream() throws IOException {
        if (this.writer != null) {
            throw new IllegalStateException(NoBodyResponse.lStrings.getString("err.ise.getOutputStream"));
        }
        this.usingOutputStream = true;
        return this.noBody;
    }
    
    public PrintWriter getWriter() throws UnsupportedEncodingException {
        if (this.usingOutputStream) {
            throw new IllegalStateException(NoBodyResponse.lStrings.getString("err.ise.getWriter"));
        }
        if (this.writer == null) {
            final OutputStreamWriter w = new OutputStreamWriter(this.noBody, this.getCharacterEncoding());
            this.writer = new PrintWriter(w);
        }
        return this.writer;
    }
    
    static {
        lStrings = ResourceBundle.getBundle("javax.servlet.http.LocalStrings");
    }
}
