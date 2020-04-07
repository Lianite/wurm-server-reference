// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.tools;

import javafx.scene.layout.Border;
import java.util.Collection;
import java.util.Arrays;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Paint;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderStroke;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.paint.Color;

public final class Borders
{
    private static final Color DEFAULT_BORDER_COLOR;
    private final Node node;
    private final List<Border> borders;
    
    public static Borders wrap(final Node n) {
        return new Borders(n);
    }
    
    private Borders(final Node n) {
        this.node = n;
        this.borders = new ArrayList<Border>();
    }
    
    public EmptyBorders emptyBorder() {
        return new EmptyBorders(this);
    }
    
    public EtchedBorders etchedBorder() {
        return new EtchedBorders(this);
    }
    
    public LineBorders lineBorder() {
        return new LineBorders(this);
    }
    
    public Borders addBorder(final Border border) {
        this.borders.add(border);
        return this;
    }
    
    public Node build() {
        Node bundle = this.node;
        for (int i = this.borders.size() - 1; i >= 0; --i) {
            final Border border = this.borders.get(i);
            bundle = border.wrap(bundle);
        }
        return bundle;
    }
    
    static {
        DEFAULT_BORDER_COLOR = Color.DARKGRAY;
    }
    
    public class EmptyBorders
    {
        private final Borders parent;
        private double top;
        private double right;
        private double bottom;
        private double left;
        
        private EmptyBorders(final Borders parent) {
            this.parent = parent;
        }
        
        public EmptyBorders padding(final double padding) {
            return this.padding(padding, padding, padding, padding);
        }
        
        public EmptyBorders padding(final double top, final double right, final double bottom, final double left) {
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            this.left = left;
            return this;
        }
        
        public Borders build() {
            this.parent.addBorder(new StrokeBorder(null, new BorderStroke[] { this.buildStroke() }));
            return this.parent;
        }
        
        public Node buildAll() {
            this.build();
            return this.parent.build();
        }
        
        private BorderStroke buildStroke() {
            return new BorderStroke((Paint)null, BorderStrokeStyle.NONE, (CornerRadii)null, new BorderWidths(this.top, this.right, this.bottom, this.left), Insets.EMPTY);
        }
    }
    
    public class EtchedBorders
    {
        private final Borders parent;
        private String title;
        private boolean raised;
        private double outerTopPadding;
        private double outerRightPadding;
        private double outerBottomPadding;
        private double outerLeftPadding;
        private double innerTopPadding;
        private double innerRightPadding;
        private double innerBottomPadding;
        private double innerLeftPadding;
        private double topLeftRadius;
        private double topRightRadius;
        private double bottomRightRadius;
        private double bottomLeftRadius;
        private Color highlightColor;
        private Color shadowColor;
        
        private EtchedBorders(final Borders parent) {
            this.raised = false;
            this.outerTopPadding = 10.0;
            this.outerRightPadding = 10.0;
            this.outerBottomPadding = 10.0;
            this.outerLeftPadding = 10.0;
            this.innerTopPadding = 15.0;
            this.innerRightPadding = 15.0;
            this.innerBottomPadding = 15.0;
            this.innerLeftPadding = 15.0;
            this.topLeftRadius = 0.0;
            this.topRightRadius = 0.0;
            this.bottomRightRadius = 0.0;
            this.bottomLeftRadius = 0.0;
            this.highlightColor = Borders.DEFAULT_BORDER_COLOR;
            this.shadowColor = Color.WHITE;
            this.parent = parent;
        }
        
        public EtchedBorders highlight(final Color highlight) {
            this.highlightColor = highlight;
            return this;
        }
        
        public EtchedBorders shadow(final Color shadow) {
            this.shadowColor = shadow;
            return this;
        }
        
        public EtchedBorders raised() {
            this.raised = true;
            return this;
        }
        
        public EtchedBorders title(final String title) {
            this.title = title;
            return this;
        }
        
        public EtchedBorders outerPadding(final double padding) {
            return this.outerPadding(padding, padding, padding, padding);
        }
        
        public EtchedBorders outerPadding(final double topPadding, final double rightPadding, final double bottomPadding, final double leftPadding) {
            this.outerTopPadding = topPadding;
            this.outerRightPadding = rightPadding;
            this.outerBottomPadding = bottomPadding;
            this.outerLeftPadding = leftPadding;
            return this;
        }
        
        public EtchedBorders innerPadding(final double padding) {
            return this.innerPadding(padding, padding, padding, padding);
        }
        
        public EtchedBorders innerPadding(final double topPadding, final double rightPadding, final double bottomPadding, final double leftPadding) {
            this.innerTopPadding = topPadding;
            this.innerRightPadding = rightPadding;
            this.innerBottomPadding = bottomPadding;
            this.innerLeftPadding = leftPadding;
            return this;
        }
        
        public EtchedBorders radius(final double radius) {
            return this.radius(radius, radius, radius, radius);
        }
        
        public EtchedBorders radius(final double topLeft, final double topRight, final double bottomRight, final double bottomLeft) {
            this.topLeftRadius = topLeft;
            this.topRightRadius = topRight;
            this.bottomRightRadius = bottomRight;
            this.bottomLeftRadius = bottomLeft;
            return this;
        }
        
        public Borders build() {
            final Color inner = this.raised ? this.shadowColor : this.highlightColor;
            final Color outer = this.raised ? this.highlightColor : this.shadowColor;
            final BorderStroke innerStroke = new BorderStroke((Paint)inner, BorderStrokeStyle.SOLID, new CornerRadii(this.topLeftRadius, this.topRightRadius, this.bottomRightRadius, this.bottomLeftRadius, false), new BorderWidths(1.0));
            final BorderStroke outerStroke = new BorderStroke((Paint)outer, BorderStrokeStyle.SOLID, new CornerRadii(this.topLeftRadius, this.topRightRadius, this.bottomRightRadius, this.bottomLeftRadius, false), new BorderWidths(1.0), new Insets(1.0));
            final BorderStroke outerPadding = this.parent.new EmptyBorders().padding(this.outerTopPadding, this.outerRightPadding, this.outerBottomPadding, this.outerLeftPadding).buildStroke();
            final BorderStroke innerPadding = this.parent.new EmptyBorders().padding(this.innerTopPadding, this.innerRightPadding, this.innerBottomPadding, this.innerLeftPadding).buildStroke();
            this.parent.addBorder(new StrokeBorder(null, new BorderStroke[] { outerPadding }));
            this.parent.addBorder(new StrokeBorder(this.title, new BorderStroke[] { innerStroke, outerStroke }));
            this.parent.addBorder(new StrokeBorder(null, new BorderStroke[] { innerPadding }));
            return this.parent;
        }
        
        public Node buildAll() {
            this.build();
            return this.parent.build();
        }
    }
    
    public class LineBorders
    {
        private final Borders parent;
        private String title;
        private BorderStrokeStyle strokeStyle;
        private Color topColor;
        private Color rightColor;
        private Color bottomColor;
        private Color leftColor;
        private double outerTopPadding;
        private double outerRightPadding;
        private double outerBottomPadding;
        private double outerLeftPadding;
        private double innerTopPadding;
        private double innerRightPadding;
        private double innerBottomPadding;
        private double innerLeftPadding;
        private double topThickness;
        private double rightThickness;
        private double bottomThickness;
        private double leftThickness;
        private double topLeftRadius;
        private double topRightRadius;
        private double bottomRightRadius;
        private double bottomLeftRadius;
        
        private LineBorders(final Borders parent) {
            this.strokeStyle = BorderStrokeStyle.SOLID;
            this.topColor = Borders.DEFAULT_BORDER_COLOR;
            this.rightColor = Borders.DEFAULT_BORDER_COLOR;
            this.bottomColor = Borders.DEFAULT_BORDER_COLOR;
            this.leftColor = Borders.DEFAULT_BORDER_COLOR;
            this.outerTopPadding = 10.0;
            this.outerRightPadding = 10.0;
            this.outerBottomPadding = 10.0;
            this.outerLeftPadding = 10.0;
            this.innerTopPadding = 15.0;
            this.innerRightPadding = 15.0;
            this.innerBottomPadding = 15.0;
            this.innerLeftPadding = 15.0;
            this.topThickness = 1.0;
            this.rightThickness = 1.0;
            this.bottomThickness = 1.0;
            this.leftThickness = 1.0;
            this.topLeftRadius = 0.0;
            this.topRightRadius = 0.0;
            this.bottomRightRadius = 0.0;
            this.bottomLeftRadius = 0.0;
            this.parent = parent;
        }
        
        public LineBorders color(final Color color) {
            return this.color(color, color, color, color);
        }
        
        public LineBorders color(final Color topColor, final Color rightColor, final Color bottomColor, final Color leftColor) {
            this.topColor = topColor;
            this.rightColor = rightColor;
            this.bottomColor = bottomColor;
            this.leftColor = leftColor;
            return this;
        }
        
        public LineBorders strokeStyle(final BorderStrokeStyle strokeStyle) {
            this.strokeStyle = strokeStyle;
            return this;
        }
        
        public LineBorders outerPadding(final double padding) {
            return this.outerPadding(padding, padding, padding, padding);
        }
        
        public LineBorders outerPadding(final double topPadding, final double rightPadding, final double bottomPadding, final double leftPadding) {
            this.outerTopPadding = topPadding;
            this.outerRightPadding = rightPadding;
            this.outerBottomPadding = bottomPadding;
            this.outerLeftPadding = leftPadding;
            return this;
        }
        
        public LineBorders innerPadding(final double padding) {
            return this.innerPadding(padding, padding, padding, padding);
        }
        
        public LineBorders innerPadding(final double topPadding, final double rightPadding, final double bottomPadding, final double leftPadding) {
            this.innerTopPadding = topPadding;
            this.innerRightPadding = rightPadding;
            this.innerBottomPadding = bottomPadding;
            this.innerLeftPadding = leftPadding;
            return this;
        }
        
        public LineBorders thickness(final double thickness) {
            return this.thickness(thickness, thickness, thickness, thickness);
        }
        
        public LineBorders thickness(final double topThickness, final double rightThickness, final double bottomThickness, final double leftThickness) {
            this.topThickness = topThickness;
            this.rightThickness = rightThickness;
            this.bottomThickness = bottomThickness;
            this.leftThickness = leftThickness;
            return this;
        }
        
        public LineBorders radius(final double radius) {
            return this.radius(radius, radius, radius, radius);
        }
        
        public LineBorders radius(final double topLeft, final double topRight, final double bottomRight, final double bottomLeft) {
            this.topLeftRadius = topLeft;
            this.topRightRadius = topRight;
            this.bottomRightRadius = bottomRight;
            this.bottomLeftRadius = bottomLeft;
            return this;
        }
        
        public LineBorders title(final String title) {
            this.title = title;
            return this;
        }
        
        public Borders build() {
            final BorderStroke borderStroke = new BorderStroke((Paint)this.topColor, (Paint)this.rightColor, (Paint)this.bottomColor, (Paint)this.leftColor, this.strokeStyle, this.strokeStyle, this.strokeStyle, this.strokeStyle, new CornerRadii(this.topLeftRadius, this.topRightRadius, this.bottomRightRadius, this.bottomLeftRadius, false), new BorderWidths(this.topThickness, this.rightThickness, this.bottomThickness, this.leftThickness), (Insets)null);
            final BorderStroke outerPadding = this.parent.new EmptyBorders().padding(this.outerTopPadding, this.outerRightPadding, this.outerBottomPadding, this.outerLeftPadding).buildStroke();
            final BorderStroke innerPadding = this.parent.new EmptyBorders().padding(this.innerTopPadding, this.innerRightPadding, this.innerBottomPadding, this.innerLeftPadding).buildStroke();
            this.parent.addBorder(new StrokeBorder(null, new BorderStroke[] { outerPadding }));
            this.parent.addBorder(new StrokeBorder(this.title, new BorderStroke[] { borderStroke }));
            this.parent.addBorder(new StrokeBorder(null, new BorderStroke[] { innerPadding }));
            return this.parent;
        }
        
        public Node buildAll() {
            this.build();
            return this.parent.build();
        }
    }
    
    private static class StrokeBorder implements Border
    {
        private static final int TITLE_PADDING = 3;
        private static final double GAP_PADDING = 5.0;
        private final String title;
        private final BorderStroke[] borderStrokes;
        
        public StrokeBorder(final String title, final BorderStroke... borderStrokes) {
            this.title = title;
            this.borderStrokes = borderStrokes;
        }
        
        @Override
        public Node wrap(final Node n) {
            final StackPane pane = new StackPane() {
                Label titleLabel;
                
                {
                    this.getChildren().add((Object)n);
                    if (StrokeBorder.this.title != null) {
                        (this.titleLabel = new Label(StrokeBorder.this.title)).setPadding(new Insets(0.0, 0.0, 0.0, 3.0));
                        this.getChildren().add((Object)this.titleLabel);
                    }
                }
                
                protected void layoutChildren() {
                    super.layoutChildren();
                    if (this.titleLabel != null) {
                        final double labelHeight = this.titleLabel.prefHeight(-1.0);
                        final double labelWidth = this.titleLabel.prefWidth(labelHeight) + 3.0;
                        this.titleLabel.resize(labelWidth, labelHeight);
                        this.titleLabel.relocate(6.0, -labelHeight / 2.0 - 1.0);
                        final List<BorderStroke> newBorderStrokes = new ArrayList<BorderStroke>(2);
                        for (final BorderStroke bs : StrokeBorder.this.borderStrokes) {
                            final List<Double> dashList = new ArrayList<Double>();
                            if (bs.getTopStyle().getDashArray().isEmpty()) {
                                dashList.addAll(Arrays.asList(5.0, labelWidth, Double.MAX_VALUE));
                            }
                            else {
                                final double origDashWidth = bs.getTopStyle().getDashArray().stream().mapToDouble(d -> d).sum();
                                if (origDashWidth > 5.0) {
                                    dashList.add(5.0);
                                    dashList.add(labelWidth);
                                }
                                else {
                                    final int no = (int)(5.0 / origDashWidth);
                                    for (int i = 0; i < no; ++i) {
                                        dashList.addAll(bs.getTopStyle().getDashArray());
                                    }
                                    if ((dashList.size() & 0x1) == 0x0) {
                                        dashList.add(0.0);
                                    }
                                    dashList.add(labelWidth + 5.0 - no * origDashWidth);
                                }
                                for (int j = 0; j < (this.getWidth() - labelWidth - origDashWidth) / origDashWidth; ++j) {
                                    dashList.addAll(bs.getTopStyle().getDashArray());
                                }
                            }
                            final BorderStrokeStyle topStrokeStyle = new BorderStrokeStyle(bs.getTopStyle().getType(), bs.getTopStyle().getLineJoin(), bs.getTopStyle().getLineCap(), bs.getTopStyle().getMiterLimit(), bs.getTopStyle().getDashOffset(), (List)dashList);
                            newBorderStrokes.add(new BorderStroke(bs.getTopStroke(), bs.getRightStroke(), bs.getBottomStroke(), bs.getLeftStroke(), topStrokeStyle, bs.getRightStyle(), bs.getBottomStyle(), bs.getLeftStyle(), bs.getRadii(), bs.getWidths(), (Insets)null));
                        }
                        this.setBorder(new javafx.scene.layout.Border((BorderStroke[])newBorderStrokes.toArray(new BorderStroke[newBorderStrokes.size()])));
                    }
                }
            };
            pane.setBorder(new javafx.scene.layout.Border(this.borderStrokes));
            return (Node)pane;
        }
    }
    
    @FunctionalInterface
    public interface Border
    {
        Node wrap(final Node p0);
    }
}
