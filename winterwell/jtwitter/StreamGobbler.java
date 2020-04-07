// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.jtwitter;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.Reader;
import java.io.InputStreamReader;
import java.util.Iterator;
import winterwell.json.JSONObject;
import java.util.List;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.ArrayList;

final class StreamGobbler extends Thread
{
    Exception ex;
    int forgotten;
    private ArrayList<String> jsons;
    volatile boolean stopFlag;
    final AStream stream;
    
    public StreamGobbler(final AStream stream) {
        this.jsons = new ArrayList<String>();
        this.setDaemon(true);
        this.stream = stream;
    }
    
    @Override
    protected void finalize() throws Throwable {
        if (this.stream != null) {
            InternalUtils.close(this.stream.stream);
        }
    }
    
    public void pleaseStop() {
        if (this.stream != null) {
            InternalUtils.close(this.stream.stream);
        }
        this.stopFlag = true;
    }
    
    public synchronized String[] popJsons() {
        final String[] arr = this.jsons.toArray(new String[this.jsons.size()]);
        this.jsons = new ArrayList<String>();
        return arr;
    }
    
    private void readJson(final BufferedReader br, int len) throws IOException {
        assert len > 0;
        final char[] sb = new char[len];
        int cnt = 0;
        while (len > 0) {
            final int rd = br.read(sb, cnt, len);
            if (rd == -1) {
                throw new IOException("end of stream");
            }
            cnt += rd;
            len -= rd;
        }
        final String json = new String(sb);
        if (!this.stream.listenersOnly) {
            synchronized (this) {
                this.jsons.add(json);
                this.forgotten += AStream.forgetIfFull(this.jsons);
            }
        }
        this.readJson2_notifyListeners(json);
    }
    
    private void readJson2_notifyListeners(final String json) {
        if (this.stream.listeners.size() == 0) {
            return;
        }
        synchronized (this.stream.listeners) {
            try {
                final JSONObject jo = new JSONObject(json);
                final Object obj = AStream.read3_parse(jo, this.stream.jtwit);
                for (final AStream.IListen listener : this.stream.listeners) {
                    boolean carryOn;
                    if (obj instanceof Twitter.ITweet) {
                        carryOn = listener.processTweet((Twitter.ITweet)obj);
                    }
                    else if (obj instanceof TwitterEvent) {
                        carryOn = listener.processEvent((TwitterEvent)obj);
                    }
                    else {
                        carryOn = listener.processSystemEvent((Object[])obj);
                    }
                    if (!carryOn) {
                        break;
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        // monitorexit(this.stream.listeners)
    }
    
    private int readLength(final BufferedReader br) throws IOException {
        final StringBuilder numSb = new StringBuilder();
        while (true) {
            final int ich = br.read();
            if (ich == -1) {
                throw new IOException("end of stream " + this);
            }
            final char ch = (char)ich;
            if (ch == '\n' || ch == '\r') {
                if (numSb.length() == 0) {
                    continue;
                }
                return Integer.valueOf(numSb.toString());
            }
            else {
                assert Character.isDigit(ch) : ch;
                assert numSb.length() < 10 : numSb;
                numSb.append(ch);
            }
        }
    }
    
    @Override
    public void run() {
        while (!this.stopFlag) {
            assert this.stream.stream != null : this.stream;
            try {
                final InputStreamReader isr = new InputStreamReader(this.stream.stream);
                final BufferedReader br = new BufferedReader(isr);
                while (!this.stopFlag) {
                    final int len = this.readLength(br);
                    this.readJson(br, len);
                }
            }
            catch (Exception ioe) {
                if (this.stopFlag) {
                    return;
                }
                this.ex = ioe;
                this.stream.addSysEvent(new Object[] { "exception", this.ex });
                if (!this.stream.autoReconnect) {
                    return;
                }
                try {
                    this.stream.reconnectFromGobblerThread();
                    assert this.stream.stream != null : this.stream;
                    continue;
                }
                catch (Exception e) {
                    this.ex = e;
                }
            }
        }
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.getName()) + "[" + this.jsons.size() + "]";
    }
}
