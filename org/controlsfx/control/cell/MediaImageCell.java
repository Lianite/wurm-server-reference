// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.cell;

import javafx.scene.Node;
import javafx.beans.value.ObservableValue;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Media;
import org.controlsfx.control.GridCell;

public class MediaImageCell extends GridCell<Media>
{
    private MediaPlayer mediaPlayer;
    private final MediaView mediaView;
    
    public MediaImageCell() {
        this.getStyleClass().add((Object)"media-grid-cell");
        (this.mediaView = new MediaView()).setMediaPlayer(this.mediaPlayer);
        this.mediaView.fitHeightProperty().bind((ObservableValue)this.heightProperty());
        this.mediaView.fitWidthProperty().bind((ObservableValue)this.widthProperty());
        this.mediaView.setMediaPlayer(this.mediaPlayer);
    }
    
    public void pause() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.pause();
        }
    }
    
    public void play() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.play();
        }
    }
    
    public void stop() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.stop();
        }
    }
    
    protected void updateItem(final Media item, final boolean empty) {
        super.updateItem((Object)item, empty);
        this.getChildren().clear();
        if (this.mediaPlayer != null) {
            this.mediaPlayer.stop();
        }
        if (empty) {
            this.setGraphic((Node)null);
        }
        else {
            this.mediaPlayer = new MediaPlayer(item);
            this.mediaView.setMediaPlayer(this.mediaPlayer);
            this.setGraphic((Node)this.mediaView);
        }
    }
}
