// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader;

import java.util.Map;
import java.util.List;
import com.sun.tools.xjc.model.CPropertyInfo;
import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Iterator;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.ErrorReceiver;
import com.sun.tools.xjc.model.Model;

public final class ModelChecker
{
    private final Model model;
    private final ErrorReceiver errorReceiver;
    
    public ModelChecker() {
        this.model = Ring.get(Model.class);
        this.errorReceiver = Ring.get(ErrorReceiver.class);
    }
    
    public void check() {
        for (final CClassInfo ci : this.model.beans().values()) {
            this.check(ci);
        }
    }
    
    private void check(final CClassInfo ci) {
        final List<CPropertyInfo> props = ci.getProperties();
        final Map<QName, CPropertyInfo> collisionTable = new HashMap<QName, CPropertyInfo>();
    Label_0272:
        for (int i = 0; i < props.size(); ++i) {
            final CPropertyInfo p1 = props.get(i);
            if (p1.getName(true).equals("Class")) {
                this.errorReceiver.error(p1.locator, Messages.PROPERTY_CLASS_IS_RESERVED.format(new Object[0]));
            }
            else {
                final QName n = p1.collectElementNames(collisionTable);
                if (n != null) {
                    final CPropertyInfo p2 = collisionTable.get(n);
                    this.errorReceiver.error(p1.locator, Messages.DUPLICATE_ELEMENT.format(n));
                    this.errorReceiver.error(p2.locator, Messages.ERR_RELEVANT_LOCATION.format(new Object[0]));
                }
                for (int j = i + 1; j < props.size(); ++j) {
                    if (this.checkPropertyCollision(p1, props.get(j))) {
                        continue Label_0272;
                    }
                }
                for (CClassInfo c = ci.getBaseClass(); c != null; c = c.getBaseClass()) {
                    for (final CPropertyInfo p3 : c.getProperties()) {
                        if (this.checkPropertyCollision(p1, p3)) {
                            continue Label_0272;
                        }
                    }
                }
            }
        }
    }
    
    private boolean checkPropertyCollision(final CPropertyInfo p1, final CPropertyInfo p2) {
        if (!p1.getName(true).equals(p2.getName(true))) {
            return false;
        }
        this.errorReceiver.error(p1.locator, Messages.DUPLICATE_PROPERTY.format(p1.getName(true)));
        this.errorReceiver.error(p2.locator, Messages.ERR_RELEVANT_LOCATION.format(new Object[0]));
        return true;
    }
}
