// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.worldmap;

import java.util.Hashtable;
import javafx.collections.MapChangeListener;
import javafx.event.EventTarget;
import javafx.scene.input.MouseButton;
import javafx.beans.Observable;
import java.io.IOException;
import java.util.Iterator;
import javafx.util.Callback;
import javafx.geometry.Point2D;
import java.util.Properties;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.input.ScrollEvent;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javafx.scene.layout.BorderPane;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import java.util.HashMap;
import javafx.scene.control.Control;
import javafx.collections.WeakListChangeListener;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.collections.ObservableMap;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import java.util.List;
import java.util.Map;
import javafx.css.PseudoClass;
import org.controlsfx.control.WorldMapView;
import javafx.scene.control.SkinBase;

public class WorldMapViewSkin extends SkinBase<WorldMapView>
{
    private static final PseudoClass SELECTED_PSEUDO_CLASS;
    private static final String DEFAULT_STYLE_LOCATION = "location";
    private static final String DEFAULT_STYLE_COUNTRY = "country";
    private static final double PREFERRED_WIDTH = 1009.0;
    private static final double PREFERRED_HEIGHT = 665.0;
    private static double MAP_OFFSET_X;
    private static double MAP_OFFSET_Y;
    private final Map<WorldMapView.Country, List<? extends String>> countryPathMap;
    private final Map<WorldMapView.Country, List<? extends WorldMapView.CountryView>> countryViewMap;
    private Pane countryPane;
    private Group group;
    private Group locationsGroup;
    protected ObservableMap<WorldMapView.Location, Node> locationMap;
    private double dragX;
    private double dragY;
    private final ListChangeListener<? super WorldMapView.Location> locationsListener;
    private final WeakListChangeListener weakLocationsListener;
    private final ListChangeListener<? super WorldMapView.Country> countrySelectionListener;
    private final WeakListChangeListener weakCountrySelectionListener;
    private final ListChangeListener<? super WorldMapView.Location> locationSelectionListener;
    private final WeakListChangeListener weakLocationSelectionListener;
    
    public WorldMapViewSkin(final WorldMapView view) {
        super((Control)view);
        this.countryPathMap = new HashMap<WorldMapView.Country, List<? extends String>>();
        this.countryViewMap = new HashMap<WorldMapView.Country, List<? extends WorldMapView.CountryView>>();
        this.locationsListener = (ListChangeListener<? super WorldMapView.Location>)(change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    change.getAddedSubList().forEach(location -> this.addLocation(location));
                }
                else {
                    if (!change.wasRemoved()) {
                        continue;
                    }
                    change.getRemoved().forEach(location -> this.removeLocation(location));
                }
            }
        });
        this.weakLocationsListener = new WeakListChangeListener((ListChangeListener)this.locationsListener);
        this.countrySelectionListener = (ListChangeListener<? super WorldMapView.Country>)(change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    change.getAddedSubList().forEach(country -> this.countryViewMap.get(country).forEach(path -> path.pseudoClassStateChanged(WorldMapViewSkin.SELECTED_PSEUDO_CLASS, true)));
                }
                else {
                    if (!change.wasRemoved()) {
                        continue;
                    }
                    change.getRemoved().forEach(country -> this.countryViewMap.get(country).forEach(path -> path.pseudoClassStateChanged(WorldMapViewSkin.SELECTED_PSEUDO_CLASS, false)));
                }
            }
        });
        this.weakCountrySelectionListener = new WeakListChangeListener((ListChangeListener)this.countrySelectionListener);
        this.locationSelectionListener = (ListChangeListener<? super WorldMapView.Location>)(change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    change.getAddedSubList().forEach(location -> ((Node)this.locationMap.get((Object)location)).pseudoClassStateChanged(WorldMapViewSkin.SELECTED_PSEUDO_CLASS, true));
                }
                else {
                    if (!change.wasRemoved()) {
                        continue;
                    }
                    change.getRemoved().forEach(location -> ((Node)this.locationMap.get((Object)location)).pseudoClassStateChanged(WorldMapViewSkin.SELECTED_PSEUDO_CLASS, false));
                }
            }
        });
        this.weakLocationSelectionListener = new WeakListChangeListener((ListChangeListener)this.locationSelectionListener);
        this.locationMap = (ObservableMap<WorldMapView.Location, Node>)FXCollections.observableHashMap();
        (this.group = new Group()).setManaged(false);
        this.group.setAutoSizeChildren(false);
        (this.locationsGroup = new Group()).setManaged(false);
        this.locationsGroup.visibleProperty().bind((ObservableValue)view.showLocationsProperty());
        this.locationsGroup.setAutoSizeChildren(false);
        this.countryPane = new Pane();
        this.countryPane.getChildren().add((Object)this.group);
        view.getLocations().addListener((ListChangeListener)this.locationsListener);
        final ListChangeListener<? super WorldMapView.Country> countriesListener = (ListChangeListener<? super WorldMapView.Country>)(change -> this.buildView());
        view.getCountries().addListener((ListChangeListener)countriesListener);
        this.locationMap.addListener(change -> {
            if (change.wasRemoved()) {
                this.locationsGroup.getChildren().remove(change.getValueRemoved());
            }
        });
        final BorderPane borderPane = new BorderPane();
        borderPane.setCenter((Node)this.countryPane);
        this.getChildren().add((Object)borderPane);
        view.zoomFactorProperty().addListener(it -> view.requestLayout());
        final Properties mapData = this.loadData();
        for (final WorldMapView.Country country : WorldMapView.Country.values()) {
            final String countryData = ((Hashtable<K, String>)mapData).get(country.name());
            if (countryData == null) {
                System.out.println("Missing SVG path for country " + country.getLocale().getDisplayCountry() + " (" + country + ")");
            }
            else {
                final StringTokenizer st = new StringTokenizer(countryData, ";");
                final List<String> paths = new ArrayList<String>();
                while (st.hasMoreTokens()) {
                    paths.add(st.nextToken());
                }
                this.countryPathMap.put(country, paths);
            }
        }
        this.buildView();
        view.getSelectedCountries().addListener((ListChangeListener)this.weakCountrySelectionListener);
        view.selectedCountriesProperty().addListener(it -> view.getSelectedCountries().addListener((ListChangeListener)this.weakCountrySelectionListener));
        view.getSelectedLocations().addListener((ListChangeListener)this.weakLocationSelectionListener);
        view.selectedLocationsProperty().addListener(it -> view.getSelectedLocations().addListener((ListChangeListener)this.weakLocationSelectionListener));
        view.getLocations().addListener((ListChangeListener)this.weakLocationsListener);
        view.locationsProperty().addListener(it -> view.getLocations().addListener((ListChangeListener)this.weakLocationsListener));
        view.getLocations().forEach(location -> this.addLocation(location));
        view.addEventHandler(ScrollEvent.SCROLL, evt -> evt.consume());
        view.addEventHandler(ZoomEvent.ZOOM, evt -> {
            final double factor = evt.getZoomFactor();
            view.setZoomFactor(view.getZoomFactor() * factor);
            evt.consume();
        });
        view.addEventHandler(MouseEvent.MOUSE_PRESSED, evt -> {
            this.dragX = evt.getX();
            this.dragY = evt.getY();
        });
        view.addEventHandler(MouseEvent.MOUSE_DRAGGED, evt -> {
            final double deltaX = evt.getX() - this.dragX;
            final double deltaY = evt.getY() - this.dragY;
            this.group.setTranslateX(this.group.getTranslateX() + deltaX);
            this.group.setTranslateY(this.group.getTranslateY() + deltaY);
            this.dragX = evt.getX();
            this.dragY = evt.getY();
        });
        view.addEventHandler(MouseEvent.MOUSE_CLICKED, evt -> {
            if (evt.getClickCount() == 2) {
                view.setZoomFactor(1.0);
                this.group.setTranslateX(0.0);
                this.group.setTranslateY(0.0);
            }
            else if (evt.getButton().equals((Object)MouseButton.PRIMARY)) {
                final EventTarget target = evt.getTarget();
                if (target instanceof WorldMapView.CountryView) {
                    final WorldMapView.CountryView path = (WorldMapView.CountryView)target;
                    final WorldMapView.Country country = path.getCountry();
                    final boolean wasSelected = view.getSelectedCountries().contains((Object)country);
                    if (view.getCountrySelectionMode().equals(WorldMapView.SelectionMode.SINGLE) || (!evt.isShortcutDown() && !evt.isShiftDown())) {
                        view.getSelectedCountries().clear();
                    }
                    if (wasSelected) {
                        view.getSelectedCountries().remove((Object)country);
                    }
                    else {
                        view.getSelectedCountries().add((Object)country);
                    }
                }
                else if (target.equals(this.countryPane)) {
                    view.getSelectedCountries().clear();
                }
                else {
                    for (final WorldMapView.Location location : this.locationMap.keySet()) {
                        final Node node = (Node)this.locationMap.get((Object)location);
                        if (target.equals(node)) {
                            final boolean wasSelected2 = view.getSelectedLocations().contains((Object)location);
                            if (view.getLocationSelectionMode().equals(WorldMapView.SelectionMode.SINGLE) || (!evt.isShortcutDown() && !evt.isShiftDown())) {
                                view.getSelectedLocations().clear();
                            }
                            if (wasSelected2) {
                                view.getSelectedLocations().remove((Object)location);
                                break;
                            }
                            view.getSelectedLocations().add((Object)location);
                            break;
                        }
                    }
                }
            }
        });
        final Rectangle clip = new Rectangle();
        clip.widthProperty().bind((ObservableValue)view.widthProperty());
        clip.heightProperty().bind((ObservableValue)view.heightProperty());
        view.setClip((Node)clip);
        view.countryViewFactoryProperty().addListener(it -> this.buildView());
        view.locationViewFactoryProperty().addListener(it -> this.buildView());
    }
    
    private Point2D getLocationCoordinates(final WorldMapView.Location location) {
        final double x = (location.getLongitude() + 180.0) * 2.8027777777777776 + WorldMapViewSkin.MAP_OFFSET_X;
        final double y = 332.5 - 1009.0 * Math.log(Math.tan(0.7853981633974483 + Math.toRadians(location.getLatitude()) / 2.0)) / 6.283185307179586 + WorldMapViewSkin.MAP_OFFSET_Y;
        return new Point2D(x, y);
    }
    
    private void addLocation(final WorldMapView.Location location) {
        final Point2D coordinates = this.getLocationCoordinates(location);
        final Callback<WorldMapView.Location, Node> locationViewFactory = ((WorldMapView)this.getSkinnable()).getLocationViewFactory();
        final Node view = (Node)locationViewFactory.call((Object)location);
        if (view == null) {
            throw new IllegalArgumentException("location view factory returned NULL");
        }
        view.getStyleClass().add((Object)"location");
        view.setManaged(false);
        this.locationsGroup.getChildren().add((Object)view);
        view.applyCss();
        view.resizeRelocate(coordinates.getX(), coordinates.getY(), view.prefWidth(-1.0), view.prefHeight(-1.0));
        this.locationMap.put((Object)location, (Object)view);
    }
    
    private void removeLocation(final WorldMapView.Location location) {
        this.locationMap.remove((Object)location);
    }
    
    private void buildView() {
        this.group.getChildren().clear();
        this.locationsGroup.getChildren().clear();
        if (Double.compare(((WorldMapView)this.getSkinnable()).getPrefWidth(), 0.0) <= 0 || Double.compare(((WorldMapView)this.getSkinnable()).getPrefHeight(), 0.0) <= 0 || Double.compare(((WorldMapView)this.getSkinnable()).getWidth(), 0.0) <= 0 || Double.compare(((WorldMapView)this.getSkinnable()).getHeight(), 0.0) <= 0) {
            if (((WorldMapView)this.getSkinnable()).getPrefWidth() > 0.0 && ((WorldMapView)this.getSkinnable()).getPrefHeight() > 0.0) {
                ((WorldMapView)this.getSkinnable()).setPrefSize(((WorldMapView)this.getSkinnable()).getPrefWidth(), ((WorldMapView)this.getSkinnable()).getPrefHeight());
            }
            else {
                ((WorldMapView)this.getSkinnable()).setPrefSize(1009.0, 665.0);
            }
        }
        final Callback<WorldMapView.Country, WorldMapView.CountryView> factory = ((WorldMapView)this.getSkinnable()).getCountryViewFactory();
        for (final WorldMapView.Country country : WorldMapView.Country.values()) {
            if (((WorldMapView)this.getSkinnable()).getCountries().isEmpty() || ((WorldMapView)this.getSkinnable()).getCountries().contains((Object)country)) {
                final List<WorldMapView.CountryView> countryViews = new ArrayList<WorldMapView.CountryView>();
                for (final String svgPath : this.countryPathMap.get(country)) {
                    final WorldMapView.CountryView view = (WorldMapView.CountryView)factory.call((Object)country);
                    if (view != null) {
                        view.setContent(svgPath);
                        view.getStyleClass().add(0, (Object)"country");
                        this.group.getChildren().addAll((Object[])new Node[] { view });
                        countryViews.add(view);
                    }
                }
                this.countryViewMap.put(country, countryViews);
            }
        }
        for (final WorldMapView.Location location : this.locationMap.keySet()) {
            final Point2D coordinates = this.getLocationCoordinates(location);
            if (this.group.getLayoutBounds().contains(coordinates)) {
                this.locationsGroup.getChildren().add(this.locationMap.get((Object)location));
            }
        }
        this.group.getChildren().add((Object)this.locationsGroup);
        ((WorldMapView)this.getSkinnable()).requestLayout();
    }
    
    protected void layoutChildren(final double contentX, final double contentY, final double contentWidth, final double contentHeight) {
        super.layoutChildren(contentX, contentY, contentWidth, contentHeight);
        final double prefWidth = this.group.prefWidth(-1.0);
        final double prefHeight = this.group.prefHeight(-1.0);
        final double scaleX = contentWidth / prefWidth;
        final double scaleY = contentHeight / prefHeight;
        final double scale = Math.min(scaleX, scaleY) * ((WorldMapView)this.getSkinnable()).getZoomFactor();
        this.group.setTranslateX(-this.group.getLayoutBounds().getMinX());
        this.group.setTranslateY(-this.group.getLayoutBounds().getMinY());
        this.group.setScaleX(scale);
        this.group.setScaleY(scale);
        this.group.setLayoutX((contentWidth - prefWidth) / 2.0);
        this.group.setLayoutY((contentHeight - prefHeight) / 2.0);
    }
    
    protected Properties loadData() {
        final Properties mapData = new Properties();
        try {
            mapData.load(WorldMapView.class.getResourceAsStream("worldmap-small.properties"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return mapData;
    }
    
    static {
        SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
        WorldMapViewSkin.MAP_OFFSET_X = -28.756500000000003;
        WorldMapViewSkin.MAP_OFFSET_Y = 129.675;
    }
}
