// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.skin;

import javafx.collections.ListChangeListener;
import java.util.List;
import javafx.event.Event;
import javafx.animation.Animation;
import javafx.geometry.Side;
import javafx.event.ActionEvent;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.beans.value.WritableValue;
import javafx.animation.KeyValue;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.geometry.Orientation;
import javafx.beans.value.ObservableValue;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Control;
import javafx.beans.value.ChangeListener;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.animation.Timeline;
import javafx.scene.control.SplitPane;
import org.controlsfx.control.MasterDetailPane;
import javafx.scene.control.SkinBase;

public class MasterDetailPaneSkin extends SkinBase<MasterDetailPane>
{
    private boolean changing;
    private SplitPane splitPane;
    private final Timeline timeline;
    private BooleanProperty showDetailForTimeline;
    private InvalidationListener listenersDivider;
    private ChangeListener<Number> updateDividerPositionListener;
    
    public MasterDetailPaneSkin(final MasterDetailPane pane) {
        super((Control)pane);
        this.changing = false;
        this.timeline = new Timeline();
        this.showDetailForTimeline = (BooleanProperty)new SimpleBooleanProperty();
        this.listenersDivider = (InvalidationListener)new InvalidationListener() {
            public void invalidated(final Observable arg0) {
                MasterDetailPaneSkin.this.changing = true;
                MasterDetailPaneSkin.this.splitPane.setDividerPosition(0, ((MasterDetailPane)MasterDetailPaneSkin.this.getSkinnable()).getDividerPosition());
                MasterDetailPaneSkin.this.changing = false;
            }
        };
        this.updateDividerPositionListener = (ChangeListener<Number>)new ChangeListener<Number>() {
            public void changed(final ObservableValue<? extends Number> ov, final Number t, final Number t1) {
                if (!MasterDetailPaneSkin.this.changing) {
                    ((MasterDetailPane)MasterDetailPaneSkin.this.getSkinnable()).setDividerPosition(t1.doubleValue());
                }
            }
        };
        (this.splitPane = new SplitPane()).setDividerPosition(0, pane.getDividerPosition());
        this.splitPane.getDividers().addListener(change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    change.getAddedSubList().get(0).positionProperty().addListener((ChangeListener)this.updateDividerPositionListener);
                }
                else {
                    if (!change.wasRemoved()) {
                        continue;
                    }
                    change.getRemoved().get(0).positionProperty().removeListener((ChangeListener)this.updateDividerPositionListener);
                }
            }
        });
        SplitPane.setResizableWithParent(((MasterDetailPane)this.getSkinnable()).getDetailNode(), false);
        switch (((MasterDetailPane)this.getSkinnable()).getDetailSide()) {
            case BOTTOM:
            case TOP: {
                this.splitPane.setOrientation(Orientation.VERTICAL);
                break;
            }
            case LEFT:
            case RIGHT: {
                this.splitPane.setOrientation(Orientation.HORIZONTAL);
                break;
            }
        }
        ((MasterDetailPane)this.getSkinnable()).masterNodeProperty().addListener((observable, oldNode, newNode) -> {
            if (oldNode != null) {
                this.splitPane.getItems().remove((Object)oldNode);
            }
            if (newNode != null) {
                this.updateMinAndMaxSizes();
                int masterIndex = 0;
                Label_0283: {
                    switch (this.splitPane.getOrientation()) {
                        case HORIZONTAL: {
                            switch (((MasterDetailPane)this.getSkinnable()).getDetailSide()) {
                                case LEFT: {
                                    masterIndex = 1;
                                    break Label_0283;
                                }
                                case RIGHT: {
                                    masterIndex = 0;
                                    break Label_0283;
                                }
                                default: {
                                    throw new IllegalArgumentException("illegal details position " + ((MasterDetailPane)this.getSkinnable()).getDetailSide() + " for orientation " + this.splitPane.getOrientation());
                                }
                            }
                            break;
                        }
                        case VERTICAL: {
                            switch (((MasterDetailPane)this.getSkinnable()).getDetailSide()) {
                                case TOP: {
                                    masterIndex = 1;
                                    break Label_0283;
                                }
                                case BOTTOM: {
                                    masterIndex = 0;
                                    break Label_0283;
                                }
                                default: {
                                    throw new IllegalArgumentException("illegal details position " + ((MasterDetailPane)this.getSkinnable()).getDetailSide() + " for orientation " + this.splitPane.getOrientation());
                                }
                            }
                            break;
                        }
                    }
                }
                final List<Node> items = (List<Node>)this.splitPane.getItems();
                if (items.isEmpty()) {
                    items.add(newNode);
                }
                else {
                    items.add(masterIndex, newNode);
                }
            }
        });
        ((MasterDetailPane)this.getSkinnable()).detailNodeProperty().addListener((observable, oldNode, newNode) -> {
            if (oldNode != null) {
                this.splitPane.getItems().remove((Object)oldNode);
            }
            if (newNode != null && ((MasterDetailPane)this.getSkinnable()).isShowDetailNode()) {
                this.splitPane.setDividerPositions(new double[] { ((MasterDetailPane)this.getSkinnable()).getDividerPosition() });
                this.updateMinAndMaxSizes();
                SplitPane.setResizableWithParent(newNode, false);
                int detailsIndex = 0;
                Label_0327: {
                    switch (this.splitPane.getOrientation()) {
                        case HORIZONTAL: {
                            switch (((MasterDetailPane)this.getSkinnable()).getDetailSide()) {
                                case LEFT: {
                                    detailsIndex = 0;
                                    break Label_0327;
                                }
                                case RIGHT: {
                                    detailsIndex = 1;
                                    break Label_0327;
                                }
                                default: {
                                    throw new IllegalArgumentException("illegal details position " + ((MasterDetailPane)this.getSkinnable()).getDetailSide() + " for orientation " + this.splitPane.getOrientation());
                                }
                            }
                            break;
                        }
                        case VERTICAL: {
                            switch (((MasterDetailPane)this.getSkinnable()).getDetailSide()) {
                                case TOP: {
                                    detailsIndex = 0;
                                    break Label_0327;
                                }
                                case BOTTOM: {
                                    detailsIndex = 1;
                                    break Label_0327;
                                }
                                default: {
                                    throw new IllegalArgumentException("illegal details position " + ((MasterDetailPane)this.getSkinnable()).getDetailSide() + " for orientation " + this.splitPane.getOrientation());
                                }
                            }
                            break;
                        }
                    }
                }
                final List<Node> items = (List<Node>)this.splitPane.getItems();
                if (items.isEmpty()) {
                    items.add(newNode);
                }
                else {
                    items.add(detailsIndex, newNode);
                }
            }
        });
        ((MasterDetailPane)this.getSkinnable()).showDetailNodeProperty().addListener((observable, oldShow, newShow) -> {
            if (((MasterDetailPane)this.getSkinnable()).isAnimated() && this.timeline.getStatus() == Animation.Status.RUNNING) {
                this.timeline.jumpTo("endAnimation");
                this.timeline.getOnFinished().handle((Event)null);
            }
            if (newShow) {
                this.open();
            }
            else {
                this.close();
            }
        });
        ((MasterDetailPane)this.getSkinnable()).detailSideProperty().addListener((observable, oldPos, newPos) -> {
            final Node detailNode = ((MasterDetailPane)this.getSkinnable()).getDetailNode();
            final Node masterNode = ((MasterDetailPane)this.getSkinnable()).getMasterNode();
            final boolean showDetailNode = ((MasterDetailPane)this.getSkinnable()).isShowDetailNode() && detailNode != null;
            if (showDetailNode) {
                this.splitPane.getItems().clear();
            }
            switch (newPos) {
                case BOTTOM:
                case TOP: {
                    this.splitPane.setOrientation(Orientation.VERTICAL);
                    break;
                }
                case LEFT:
                case RIGHT: {
                    this.splitPane.setOrientation(Orientation.HORIZONTAL);
                    break;
                }
            }
            Label_0353: {
                switch (newPos) {
                    case TOP:
                    case LEFT: {
                        if (showDetailNode) {
                            this.splitPane.getItems().add((Object)detailNode);
                            this.splitPane.getItems().add((Object)masterNode);
                        }
                        switch (oldPos) {
                            case BOTTOM:
                            case RIGHT: {
                                ((MasterDetailPane)this.getSkinnable()).setDividerPosition(1.0 - ((MasterDetailPane)this.getSkinnable()).getDividerPosition());
                                break Label_0353;
                            }
                            default: {
                                break Label_0353;
                            }
                        }
                        break;
                    }
                    case BOTTOM:
                    case RIGHT: {
                        if (showDetailNode) {
                            this.splitPane.getItems().add((Object)masterNode);
                            this.splitPane.getItems().add((Object)detailNode);
                        }
                        switch (oldPos) {
                            case TOP:
                            case LEFT: {
                                ((MasterDetailPane)this.getSkinnable()).setDividerPosition(1.0 - ((MasterDetailPane)this.getSkinnable()).getDividerPosition());
                                break Label_0353;
                            }
                        }
                        break;
                    }
                }
            }
            if (showDetailNode) {
                this.splitPane.setDividerPositions(new double[] { ((MasterDetailPane)this.getSkinnable()).getDividerPosition() });
            }
        });
        this.updateMinAndMaxSizes();
        this.getChildren().add((Object)this.splitPane);
        this.splitPane.getItems().add((Object)((MasterDetailPane)this.getSkinnable()).getMasterNode());
        if (((MasterDetailPane)this.getSkinnable()).isShowDetailNode()) {
            switch (((MasterDetailPane)this.getSkinnable()).getDetailSide()) {
                case TOP:
                case LEFT: {
                    this.splitPane.getItems().add(0, (Object)((MasterDetailPane)this.getSkinnable()).getDetailNode());
                    break;
                }
                case BOTTOM:
                case RIGHT: {
                    this.splitPane.getItems().add((Object)((MasterDetailPane)this.getSkinnable()).getDetailNode());
                    break;
                }
            }
            this.bindDividerPosition();
        }
        this.timeline.setOnFinished(evt -> {
            if (!this.showDetailForTimeline.get()) {
                this.unbindDividerPosition();
                this.splitPane.getItems().remove((Object)((MasterDetailPane)this.getSkinnable()).getDetailNode());
                ((MasterDetailPane)this.getSkinnable()).getDetailNode().setOpacity(1.0);
            }
            this.changing = false;
        });
    }
    
    private void bindDividerPosition() {
        ((MasterDetailPane)this.getSkinnable()).dividerPositionProperty().addListener(this.listenersDivider);
    }
    
    private void unbindDividerPosition() {
        ((MasterDetailPane)this.getSkinnable()).dividerPositionProperty().removeListener(this.listenersDivider);
    }
    
    private void updateMinAndMaxSizes() {
        if (((MasterDetailPane)this.getSkinnable()).getMasterNode() instanceof Region) {
            ((Region)((MasterDetailPane)this.getSkinnable()).getMasterNode()).setMinSize(0.0, 0.0);
            ((Region)((MasterDetailPane)this.getSkinnable()).getMasterNode()).setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        }
        if (((MasterDetailPane)this.getSkinnable()).getDetailNode() instanceof Region) {
            ((Region)((MasterDetailPane)this.getSkinnable()).getDetailNode()).setMinSize(0.0, 0.0);
            ((Region)((MasterDetailPane)this.getSkinnable()).getDetailNode()).setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        }
    }
    
    private void open() {
        this.changing = true;
        final Node node = ((MasterDetailPane)this.getSkinnable()).getDetailNode();
        if (node == null) {
            return;
        }
        switch (((MasterDetailPane)this.getSkinnable()).getDetailSide()) {
            case TOP:
            case LEFT: {
                this.splitPane.getItems().add(0, (Object)node);
                this.splitPane.setDividerPositions(new double[] { 0.0 });
                break;
            }
            case BOTTOM:
            case RIGHT: {
                this.splitPane.getItems().add((Object)node);
                this.splitPane.setDividerPositions(new double[] { 1.0 });
                break;
            }
        }
        this.updateMinAndMaxSizes();
        this.maybeAnimatePositionChange(((MasterDetailPane)this.getSkinnable()).getDividerPosition(), true);
    }
    
    private void close() {
        this.changing = true;
        if (!this.splitPane.getDividers().isEmpty()) {
            double targetLocation = 0.0;
            switch (((MasterDetailPane)this.getSkinnable()).getDetailSide()) {
                case BOTTOM:
                case RIGHT: {
                    targetLocation = 1.0;
                    break;
                }
            }
            this.maybeAnimatePositionChange(targetLocation, false);
        }
    }
    
    private void maybeAnimatePositionChange(final double position, final boolean showDetail) {
        final Node detailNode = ((MasterDetailPane)this.getSkinnable()).getDetailNode();
        if (detailNode == null) {
            return;
        }
        this.showDetailForTimeline.set(showDetail);
        final SplitPane.Divider divider = (SplitPane.Divider)this.splitPane.getDividers().get(0);
        if (this.showDetailForTimeline.get()) {
            this.unbindDividerPosition();
            this.bindDividerPosition();
        }
        if (((MasterDetailPane)this.getSkinnable()).isAnimated() && detailNode != null) {
            final KeyValue positionKeyValue = new KeyValue((WritableValue)divider.positionProperty(), (Object)position);
            final KeyValue opacityKeyValue = new KeyValue((WritableValue)detailNode.opacityProperty(), (Object)(int)(this.showDetailForTimeline.get() ? 1 : 0));
            final KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.1), "endAnimation", new KeyValue[] { positionKeyValue, opacityKeyValue });
            this.timeline.getKeyFrames().clear();
            this.timeline.getKeyFrames().add((Object)keyFrame);
            this.timeline.playFromStart();
        }
        else {
            detailNode.setOpacity(1.0);
            divider.setPosition(position);
            if (!this.showDetailForTimeline.get()) {
                this.unbindDividerPosition();
                this.splitPane.getItems().remove((Object)((MasterDetailPane)this.getSkinnable()).getDetailNode());
            }
            this.changing = false;
        }
    }
}
