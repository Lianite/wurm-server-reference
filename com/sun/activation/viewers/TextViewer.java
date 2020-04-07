// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.activation.viewers;

import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.GridLayout;
import javax.activation.DataHandler;
import java.io.File;
import java.awt.TextArea;
import javax.activation.CommandObject;
import java.awt.Panel;

public class TextViewer extends Panel implements CommandObject
{
    private TextArea text_area;
    private File text_file;
    private String text_buffer;
    private DataHandler _dh;
    private boolean DEBUG;
    
    public TextViewer() {
        this.text_area = null;
        this.text_file = null;
        this.text_buffer = null;
        this._dh = null;
        this.DEBUG = false;
        this.setLayout(new GridLayout(1, 1));
        (this.text_area = new TextArea("", 24, 80, 1)).setEditable(false);
        this.add(this.text_area);
    }
    
    public void setCommandContext(final String verb, final DataHandler dh) throws IOException {
        this._dh = dh;
        this.setInputStream(this._dh.getInputStream());
    }
    
    public void setInputStream(final InputStream ins) throws IOException {
        int bytes_read = 0;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final byte[] data = new byte[1024];
        while ((bytes_read = ins.read(data)) > 0) {
            baos.write(data, 0, bytes_read);
        }
        ins.close();
        this.text_buffer = baos.toString();
        this.text_area.setText(this.text_buffer);
    }
    
    public void addNotify() {
        super.addNotify();
        this.invalidate();
    }
    
    public Dimension getPreferredSize() {
        return this.text_area.getMinimumSize(24, 80);
    }
}
