// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.shared.log.impl;

import org.fourthline.cling.support.shared.View;
import javax.annotation.PreDestroy;
import javax.swing.SwingUtilities;
import org.seamless.swing.logging.LogMessage;
import org.fourthline.cling.support.shared.TextExpand;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.enterprise.context.ApplicationScoped;
import org.fourthline.cling.support.shared.log.LogView;

@ApplicationScoped
public class LogPresenter implements LogView.Presenter
{
    @Inject
    protected LogView view;
    @Inject
    protected Event<TextExpand> textExpandEvent;
    
    @Override
    public void init() {
        ((View<LogPresenter>)this.view).setPresenter(this);
    }
    
    @Override
    public void onExpand(final LogMessage logMessage) {
        this.textExpandEvent.fire((Object)new TextExpand(logMessage.getMessage()));
    }
    
    @PreDestroy
    public void destroy() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LogPresenter.this.view.dispose();
            }
        });
    }
    
    @Override
    public void pushMessage(final LogMessage message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LogPresenter.this.view.pushMessage(message);
            }
        });
    }
}
