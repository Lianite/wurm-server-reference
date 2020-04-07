// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.tools;

import com.sun.webkit.WebPage;
import javafx.scene.image.Image;
import javafx.scene.SnapshotResult;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;
import javafx.scene.SnapshotParameters;
import javafx.beans.value.ObservableValue;
import javafx.scene.web.WebEngine;
import javafx.concurrent.Worker;
import javafx.beans.value.ChangeListener;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import com.sun.javafx.webkit.Accessor;
import javafx.scene.web.WebView;
import javafx.scene.image.WritableImage;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import java.net.URL;

class SVGLoader
{
    public static void loadSVGImage(final URL svgImage, final double prefWidth, final double prefHeight, final Callback<ImageView, Void> callback) {
        loadSVGImage(svgImage, prefWidth, prefHeight, callback, null);
    }
    
    public static void loadSVGImage(final URL svgImage, final WritableImage outputImage) {
        if (outputImage == null) {
            throw new NullPointerException("outputImage can not be null");
        }
        final double w = outputImage.getWidth();
        final double h = outputImage.getHeight();
        loadSVGImage(svgImage, w, h, null, outputImage);
    }
    
    public static void loadSVGImage(final URL svgImage, final double prefWidth, final double prefHeight, final Callback<ImageView, Void> callback, final WritableImage outputImage) {
        final WebView view = new WebView();
        final WebEngine eng = view.getEngine();
        final WebPage webPage = Accessor.getPageFor(eng);
        webPage.setBackgroundColor(webPage.getMainFrame(), -256);
        webPage.setOpaque(webPage.getMainFrame(), false);
        final Scene scene = new Scene((Parent)view);
        final Stage stage = new Stage();
        stage.setScene(scene);
        stage.setWidth(0.0);
        stage.setHeight(0.0);
        stage.setOpacity(0.0);
        stage.show();
        final String content = "<html><body style=\"margin-top: 0px; margin-bottom: 30px; margin-left: 0px; margin-right: 0px; padding: 0;\"><img id=\"svgImage\" style=\"display: block;float: top;\" width=\"" + prefWidth + "\" height=\"" + prefHeight + "\" src=\"" + svgImage.toExternalForm() + "\" /></body></head>";
        eng.loadContent(content);
        eng.getLoadWorker().stateProperty().addListener((ChangeListener)new ChangeListener<Worker.State>() {
            public void changed(final ObservableValue<? extends Worker.State> o, final Worker.State oldValue, final Worker.State newValue) {
                if (newValue == Worker.State.SUCCEEDED) {
                    final double svgWidth = (prefWidth >= 0.0) ? prefWidth : getSvgWidth(eng);
                    final double svgHeight = (prefHeight >= 0.0) ? prefWidth : getSvgHeight(eng);
                    final SnapshotParameters params = new SnapshotParameters();
                    params.setFill((Paint)Color.TRANSPARENT);
                    params.setViewport(new Rectangle2D(0.0, 0.0, svgWidth, svgHeight));
                    view.snapshot((Callback)new Callback<SnapshotResult, Void>() {
                        public Void call(final SnapshotResult param) {
                            final WritableImage snapshot = param.getImage();
                            final ImageView image = new ImageView((Image)snapshot);
                            if (callback != null) {
                                callback.call((Object)image);
                            }
                            stage.hide();
                            return null;
                        }
                    }, params, outputImage);
                }
            }
        });
    }
    
    private static double getSvgWidth(final WebEngine webEngine) {
        final Object result = getSvgDomProperty(webEngine, "offsetWidth");
        if (result instanceof Integer) {
            return (int)result;
        }
        return -1.0;
    }
    
    private static double getSvgHeight(final WebEngine webEngine) {
        final Object result = getSvgDomProperty(webEngine, "offsetHeight");
        if (result instanceof Integer) {
            return (int)result;
        }
        return -1.0;
    }
    
    private static Object getSvgDomProperty(final WebEngine webEngine, final String property) {
        return webEngine.executeScript("document.getElementById('svgImage')." + property);
    }
}
