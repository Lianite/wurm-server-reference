// 
// Decompiled by Procyon v0.5.30
// 

package javax.activation;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

public class DataHandler implements Transferable
{
    private DataSource dataSource;
    private DataSource objDataSource;
    private Object object;
    private String objectMimeType;
    private CommandMap currentCommandMap;
    private static final DataFlavor[] emptyFlavors;
    private DataFlavor[] transferFlavors;
    private DataContentHandler dataContentHandler;
    private DataContentHandler factoryDCH;
    private static DataContentHandlerFactory factory;
    private DataContentHandlerFactory oldFactory;
    private String shortType;
    static /* synthetic */ Class class$javax$activation$DataHandler;
    
    public DataHandler(final DataSource ds) {
        this.dataSource = null;
        this.objDataSource = null;
        this.object = null;
        this.objectMimeType = null;
        this.currentCommandMap = null;
        this.transferFlavors = DataHandler.emptyFlavors;
        this.dataContentHandler = null;
        this.factoryDCH = null;
        this.oldFactory = null;
        this.shortType = null;
        this.dataSource = ds;
        this.oldFactory = DataHandler.factory;
    }
    
    public DataHandler(final Object obj, final String mimeType) {
        this.dataSource = null;
        this.objDataSource = null;
        this.object = null;
        this.objectMimeType = null;
        this.currentCommandMap = null;
        this.transferFlavors = DataHandler.emptyFlavors;
        this.dataContentHandler = null;
        this.factoryDCH = null;
        this.oldFactory = null;
        this.shortType = null;
        this.object = obj;
        this.objectMimeType = mimeType;
        this.oldFactory = DataHandler.factory;
    }
    
    public DataHandler(final URL url) {
        this.dataSource = null;
        this.objDataSource = null;
        this.object = null;
        this.objectMimeType = null;
        this.currentCommandMap = null;
        this.transferFlavors = DataHandler.emptyFlavors;
        this.dataContentHandler = null;
        this.factoryDCH = null;
        this.oldFactory = null;
        this.shortType = null;
        this.dataSource = new URLDataSource(url);
        this.oldFactory = DataHandler.factory;
    }
    
    private synchronized CommandMap getCommandMap() {
        if (this.currentCommandMap != null) {
            return this.currentCommandMap;
        }
        return CommandMap.getDefaultCommandMap();
    }
    
    public DataSource getDataSource() {
        if (this.dataSource == null) {
            if (this.objDataSource == null) {
                this.objDataSource = new DataHandlerDataSource(this);
            }
            return this.objDataSource;
        }
        return this.dataSource;
    }
    
    public String getName() {
        if (this.dataSource != null) {
            return this.dataSource.getName();
        }
        return null;
    }
    
    public String getContentType() {
        if (this.dataSource != null) {
            return this.dataSource.getContentType();
        }
        return this.objectMimeType;
    }
    
    public InputStream getInputStream() throws IOException {
        InputStream ins = null;
        if (this.dataSource != null) {
            ins = this.dataSource.getInputStream();
        }
        else {
            final DataContentHandler dch = this.getDataContentHandler();
            if (dch == null) {
                throw new UnsupportedDataTypeException("no DCH for MIME type " + this.getBaseType());
            }
            if (dch instanceof ObjectDataContentHandler && ((ObjectDataContentHandler)dch).getDCH() == null) {
                throw new UnsupportedDataTypeException("no object DCH for MIME type " + this.getBaseType());
            }
            final DataContentHandler fdch = dch;
            final PipedOutputStream pos = new PipedOutputStream();
            final PipedInputStream pin = new PipedInputStream(pos);
            new Thread(new Runnable() {
                public void run() {
                    try {
                        fdch.writeTo(DataHandler.this.object, DataHandler.this.objectMimeType, pos);
                    }
                    catch (IOException e) {}
                    finally {
                        try {
                            pos.close();
                        }
                        catch (IOException ex) {}
                    }
                }
            }, "DataHandler.getInputStream").start();
            ins = pin;
        }
        return ins;
    }
    
    public void writeTo(final OutputStream os) throws IOException {
        if (this.dataSource != null) {
            InputStream is = null;
            final byte[] data = new byte[8192];
            is = this.dataSource.getInputStream();
            try {
                int bytes_read;
                while ((bytes_read = is.read(data)) > 0) {
                    os.write(data, 0, bytes_read);
                }
            }
            finally {
                is.close();
                is = null;
            }
        }
        else {
            final DataContentHandler dch = this.getDataContentHandler();
            dch.writeTo(this.object, this.objectMimeType, os);
        }
    }
    
    public OutputStream getOutputStream() throws IOException {
        if (this.dataSource != null) {
            return this.dataSource.getOutputStream();
        }
        return null;
    }
    
    public synchronized DataFlavor[] getTransferDataFlavors() {
        if (DataHandler.factory != this.oldFactory) {
            this.transferFlavors = DataHandler.emptyFlavors;
        }
        if (this.transferFlavors == DataHandler.emptyFlavors) {
            this.transferFlavors = this.getDataContentHandler().getTransferDataFlavors();
        }
        return this.transferFlavors;
    }
    
    public boolean isDataFlavorSupported(final DataFlavor flavor) {
        final DataFlavor[] lFlavors = this.getTransferDataFlavors();
        for (int i = 0; i < lFlavors.length; ++i) {
            if (lFlavors[i].equals(flavor)) {
                return true;
            }
        }
        return false;
    }
    
    public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return this.getDataContentHandler().getTransferData(flavor, this.dataSource);
    }
    
    public synchronized void setCommandMap(final CommandMap commandMap) {
        if (commandMap != this.currentCommandMap || commandMap == null) {
            this.transferFlavors = DataHandler.emptyFlavors;
            this.dataContentHandler = null;
            this.currentCommandMap = commandMap;
        }
    }
    
    public CommandInfo[] getPreferredCommands() {
        if (this.dataSource != null) {
            return this.getCommandMap().getPreferredCommands(this.getBaseType(), this.dataSource);
        }
        return this.getCommandMap().getPreferredCommands(this.getBaseType());
    }
    
    public CommandInfo[] getAllCommands() {
        if (this.dataSource != null) {
            return this.getCommandMap().getAllCommands(this.getBaseType(), this.dataSource);
        }
        return this.getCommandMap().getAllCommands(this.getBaseType());
    }
    
    public CommandInfo getCommand(final String cmdName) {
        if (this.dataSource != null) {
            return this.getCommandMap().getCommand(this.getBaseType(), cmdName, this.dataSource);
        }
        return this.getCommandMap().getCommand(this.getBaseType(), cmdName);
    }
    
    public Object getContent() throws IOException {
        if (this.object != null) {
            return this.object;
        }
        return this.getDataContentHandler().getContent(this.getDataSource());
    }
    
    public Object getBean(final CommandInfo cmdinfo) {
        Object bean = null;
        try {
            ClassLoader cld = null;
            cld = SecuritySupport.getContextClassLoader();
            if (cld == null) {
                cld = this.getClass().getClassLoader();
            }
            bean = cmdinfo.getCommandObject(this, cld);
        }
        catch (IOException e) {}
        catch (ClassNotFoundException ex) {}
        return bean;
    }
    
    private synchronized DataContentHandler getDataContentHandler() {
        if (DataHandler.factory != this.oldFactory) {
            this.oldFactory = DataHandler.factory;
            this.factoryDCH = null;
            this.dataContentHandler = null;
            this.transferFlavors = DataHandler.emptyFlavors;
        }
        if (this.dataContentHandler != null) {
            return this.dataContentHandler;
        }
        final String simpleMT = this.getBaseType();
        if (this.factoryDCH == null && DataHandler.factory != null) {
            this.factoryDCH = DataHandler.factory.createDataContentHandler(simpleMT);
        }
        if (this.factoryDCH != null) {
            this.dataContentHandler = this.factoryDCH;
        }
        if (this.dataContentHandler == null) {
            if (this.dataSource != null) {
                this.dataContentHandler = this.getCommandMap().createDataContentHandler(simpleMT, this.dataSource);
            }
            else {
                this.dataContentHandler = this.getCommandMap().createDataContentHandler(simpleMT);
            }
        }
        if (this.dataSource != null) {
            this.dataContentHandler = new DataSourceDataContentHandler(this.dataContentHandler, this.dataSource);
        }
        else {
            this.dataContentHandler = new ObjectDataContentHandler(this.dataContentHandler, this.object, this.objectMimeType);
        }
        return this.dataContentHandler;
    }
    
    private synchronized String getBaseType() {
        if (this.shortType == null) {
            final String ct = this.getContentType();
            try {
                final MimeType mt = new MimeType(ct);
                this.shortType = mt.getBaseType();
            }
            catch (MimeTypeParseException e) {
                this.shortType = ct;
            }
        }
        return this.shortType;
    }
    
    public static synchronized void setDataContentHandlerFactory(final DataContentHandlerFactory newFactory) {
        if (DataHandler.factory != null) {
            throw new Error("DataContentHandlerFactory already defined");
        }
        final SecurityManager security = System.getSecurityManager();
        if (security != null) {
            try {
                security.checkSetFactory();
            }
            catch (SecurityException ex) {
                if (((DataHandler.class$javax$activation$DataHandler == null) ? (DataHandler.class$javax$activation$DataHandler = class$("javax.activation.DataHandler")) : DataHandler.class$javax$activation$DataHandler).getClassLoader() != newFactory.getClass().getClassLoader()) {
                    throw ex;
                }
            }
        }
        DataHandler.factory = newFactory;
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError().initCause(x);
        }
    }
    
    static {
        emptyFlavors = new DataFlavor[0];
        DataHandler.factory = null;
    }
}
