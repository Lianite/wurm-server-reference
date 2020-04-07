// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.shared;

import java.awt.Window;
import org.seamless.swing.Application;
import org.fourthline.cling.model.ModelUtil;
import org.seamless.xml.DOM;
import org.w3c.dom.Document;
import org.seamless.xml.DOMParser;
import java.awt.Dimension;
import java.awt.Component;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Frame;
import java.util.logging.Logger;
import javax.swing.JDialog;

public class TextExpandDialog extends JDialog
{
    private static Logger log;
    
    public TextExpandDialog(final Frame frame, final String text) {
        super(frame);
        this.setResizable(true);
        final JTextArea textArea = new JTextArea();
        final JScrollPane textPane = new JScrollPane(textArea);
        textPane.setPreferredSize(new Dimension(500, 400));
        this.add(textPane);
        String pretty;
        if (text.startsWith("<") && text.endsWith(">")) {
            try {
                pretty = new DOMParser() {
                    @Override
                    protected DOM createDOM(final Document document) {
                        return null;
                    }
                }.print(text, 2, false);
            }
            catch (Exception ex) {
                TextExpandDialog.log.severe("Error pretty printing XML: " + ex.toString());
                pretty = text;
            }
        }
        else if (text.startsWith("http-get")) {
            pretty = ModelUtil.commaToNewline(text);
        }
        else {
            pretty = text;
        }
        textArea.setEditable(false);
        textArea.setText(pretty);
        this.pack();
        Application.center(this, this.getOwner());
        this.setVisible(true);
    }
    
    static {
        TextExpandDialog.log = Logger.getLogger(TextExpandDialog.class.getName());
    }
}
