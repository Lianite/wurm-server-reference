// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.dialog;

import javafx.scene.control.DialogPane;

public class WizardPane extends DialogPane
{
    public WizardPane() {
        this.getStylesheets().add((Object)Wizard.class.getResource("wizard.css").toExternalForm());
        this.getStyleClass().add((Object)"wizard-pane");
    }
    
    public void onEnteringPage(final Wizard wizard) {
    }
    
    public void onExitingPage(final Wizard wizard) {
    }
}
