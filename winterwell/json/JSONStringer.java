// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.json;

import java.io.Writer;
import java.io.StringWriter;

public class JSONStringer extends JSONWriter
{
    public JSONStringer() {
        super(new StringWriter());
    }
    
    @Override
    public String toString() {
        return (this.mode == 'd') ? this.writer.toString() : null;
    }
}
