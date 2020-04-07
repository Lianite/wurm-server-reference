// 
// Decompiled by Procyon v0.5.30
// 

package javax.activation;

import java.io.OutputStream;
import java.io.IOException;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.datatransfer.DataFlavor;

class DataSourceDataContentHandler implements DataContentHandler
{
    private DataSource ds;
    private DataFlavor[] transferFlavors;
    private DataContentHandler dch;
    
    public DataSourceDataContentHandler(final DataContentHandler dch, final DataSource ds) {
        this.ds = null;
        this.transferFlavors = null;
        this.dch = null;
        this.ds = ds;
        this.dch = dch;
    }
    
    public DataFlavor[] getTransferDataFlavors() {
        if (this.transferFlavors == null) {
            if (this.dch != null) {
                this.transferFlavors = this.dch.getTransferDataFlavors();
            }
            else {
                (this.transferFlavors = new DataFlavor[1])[0] = new ActivationDataFlavor(this.ds.getContentType(), this.ds.getContentType());
            }
        }
        return this.transferFlavors;
    }
    
    public Object getTransferData(final DataFlavor df, final DataSource ds) throws UnsupportedFlavorException, IOException {
        if (this.dch != null) {
            return this.dch.getTransferData(df, ds);
        }
        if (df.equals(this.getTransferDataFlavors()[0])) {
            return ds.getInputStream();
        }
        throw new UnsupportedFlavorException(df);
    }
    
    public Object getContent(final DataSource ds) throws IOException {
        if (this.dch != null) {
            return this.dch.getContent(ds);
        }
        return ds.getInputStream();
    }
    
    public void writeTo(final Object obj, final String mimeType, final OutputStream os) throws IOException {
        if (this.dch != null) {
            this.dch.writeTo(obj, mimeType, os);
            return;
        }
        throw new UnsupportedDataTypeException("no DCH for content type " + this.ds.getContentType());
    }
}
