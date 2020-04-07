// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control;

import java.util.Locale;
import java.util.Objects;
import javafx.scene.shape.SVGPath;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.collections.ObservableList;
import impl.org.controlsfx.worldmap.WorldMapViewSkin;
import javafx.scene.control.Skin;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.util.Callback;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Tooltip;

public class WorldMapView extends ControlsFXControl
{
    private static final String DEFAULT_STYLE_CLASS = "world-map";
    private Tooltip tooltip;
    private final ObjectProperty<SelectionMode> countrySelectionMode;
    private final ObjectProperty<SelectionMode> locationSelectionMode;
    private final DoubleProperty zoomFactor;
    private final ListProperty<Country> selectedCountries;
    private final ListProperty<Location> selectedLocations;
    private final ListProperty<Country> countries;
    private final ListProperty<Location> locations;
    private final BooleanProperty showLocations;
    private final ObjectProperty<Callback<Location, Node>> locationViewFactory;
    private final ObjectProperty<Callback<Country, CountryView>> countryViewFactory;
    
    public WorldMapView() {
        this.tooltip = new Tooltip();
        this.countrySelectionMode = (ObjectProperty<SelectionMode>)new SimpleObjectProperty((Object)this, "countrySelectionMode", (Object)SelectionMode.MULTIPLE);
        this.locationSelectionMode = (ObjectProperty<SelectionMode>)new SimpleObjectProperty((Object)this, "locationSelectionMode", (Object)SelectionMode.MULTIPLE);
        this.zoomFactor = (DoubleProperty)new SimpleDoubleProperty((Object)this, "zoomFactor", 1.0) {
            public void set(final double newValue) {
                super.set(Math.max(1.0, Math.min(10.0, newValue)));
            }
        };
        this.selectedCountries = (ListProperty<Country>)new SimpleListProperty((Object)this, "selectedCountries", FXCollections.observableArrayList());
        this.selectedLocations = (ListProperty<Location>)new SimpleListProperty((Object)this, "selectedLocations", FXCollections.observableArrayList());
        this.countries = (ListProperty<Country>)new SimpleListProperty((Object)this, "countries", FXCollections.observableArrayList());
        this.locations = (ListProperty<Location>)new SimpleListProperty((Object)this, "locations", FXCollections.observableArrayList());
        this.showLocations = (BooleanProperty)new SimpleBooleanProperty((Object)this, "showLocations", true);
        this.locationViewFactory = (ObjectProperty<Callback<Location, Node>>)new SimpleObjectProperty((Object)this, "locationViewFactory");
        this.countryViewFactory = (ObjectProperty<Callback<Country, CountryView>>)new SimpleObjectProperty((Object)this, "countryViewFactory");
        this.getStyleClass().add((Object)"world-map");
        this.setCountryViewFactory((Callback<Country, CountryView>)(country -> {
            final CountryView view = new CountryView(country);
            view.setOnMouseEntered(evt -> this.tooltip.setText(country.getLocale().getDisplayCountry()));
            Tooltip.install((Node)view, this.tooltip);
            return view;
        }));
        this.setLocationViewFactory((Callback<Location, Node>)(location -> {
            final Circle circle = new Circle();
            circle.setRadius(4.0);
            circle.setTranslateX(-4.0);
            circle.setTranslateY(-4.0);
            circle.setOnMouseEntered(evt -> this.tooltip.setText(location.getName()));
            Tooltip.install((Node)circle, this.tooltip);
            return circle;
        }));
    }
    
    protected Skin<?> createDefaultSkin() {
        return (Skin<?>)new WorldMapViewSkin(this);
    }
    
    public String getUserAgentStylesheet() {
        return this.getUserAgentStylesheet(WorldMapView.class, "world.css");
    }
    
    public final ObjectProperty<SelectionMode> countrySelectionModeProperty() {
        return this.countrySelectionMode;
    }
    
    public final SelectionMode getCountrySelectionMode() {
        return (SelectionMode)this.countrySelectionMode.get();
    }
    
    public final void setCountrySelectionMode(final SelectionMode mode) {
        this.countrySelectionMode.set((Object)mode);
    }
    
    public final ObjectProperty<SelectionMode> locationSelectionModeProperty() {
        return this.locationSelectionMode;
    }
    
    public final SelectionMode getLocationSelectionMode() {
        return (SelectionMode)this.locationSelectionMode.get();
    }
    
    public final void setLocationSelectionMode(final SelectionMode mode) {
        this.locationSelectionMode.set((Object)mode);
    }
    
    public final DoubleProperty zoomFactorProperty() {
        return this.zoomFactor;
    }
    
    public final double getZoomFactor() {
        return this.zoomFactor.get();
    }
    
    public final void setZoomFactor(final double factor) {
        this.zoomFactor.set(factor);
    }
    
    public final ListProperty<Country> selectedCountriesProperty() {
        return this.selectedCountries;
    }
    
    public final ObservableList<Country> getSelectedCountries() {
        return (ObservableList<Country>)this.selectedCountries.get();
    }
    
    public final void setSelectedCountries(final ObservableList<Country> countries) {
        this.selectedCountries.set((Object)countries);
    }
    
    public final ListProperty<Location> selectedLocationsProperty() {
        return this.selectedLocations;
    }
    
    public final ObservableList<Location> getSelectedLocations() {
        return (ObservableList<Location>)this.selectedLocations.get();
    }
    
    public final void setSelectedLocations(final ObservableList<Location> locations) {
        this.selectedLocations.set((Object)locations);
    }
    
    public final ListProperty<Country> countriesProperty() {
        return this.countries;
    }
    
    public final ObservableList<Country> getCountries() {
        return (ObservableList<Country>)this.countries.get();
    }
    
    public final void setCountries(final ObservableList<Country> countries) {
        this.countries.set((Object)countries);
    }
    
    public final ListProperty<Location> locationsProperty() {
        return this.locations;
    }
    
    public final ObservableList<Location> getLocations() {
        return (ObservableList<Location>)this.locations.get();
    }
    
    public final void setLocations(final ObservableList<Location> locations) {
        this.locations.set((Object)locations);
    }
    
    public final BooleanProperty showLocationsProperty() {
        return this.showLocations;
    }
    
    public final boolean isShowLocations() {
        return this.showLocations.get();
    }
    
    public final void setShowLocations(final boolean show) {
        this.showLocations.set(show);
    }
    
    public final ObjectProperty<Callback<Location, Node>> locationViewFactoryProperty() {
        return this.locationViewFactory;
    }
    
    public final Callback<Location, Node> getLocationViewFactory() {
        return (Callback<Location, Node>)this.locationViewFactory.get();
    }
    
    public final void setLocationViewFactory(final Callback<Location, Node> factory) {
        this.locationViewFactory.set((Object)factory);
    }
    
    public final ObjectProperty<Callback<Country, CountryView>> countryViewFactoryProperty() {
        return this.countryViewFactory;
    }
    
    public final Callback<Country, CountryView> getCountryViewFactory() {
        return (Callback<Country, CountryView>)this.countryViewFactory.get();
    }
    
    public final void setCountryViewFactory(final Callback<Country, CountryView> factory) {
        this.countryViewFactory.set((Object)factory);
    }
    
    public enum SelectionMode
    {
        SINGLE, 
        MULTIPLE;
    }
    
    public static class CountryView extends SVGPath
    {
        private final Country country;
        
        public CountryView(final Country country) {
            this.country = Objects.requireNonNull(country);
        }
        
        public final Country getCountry() {
            return this.country;
        }
        
        public String getName() {
            return this.country.name();
        }
    }
    
    public enum Country
    {
        AE(new String[0]), 
        AO(new String[0]), 
        AR(new String[0]), 
        AT(new String[0]), 
        AU(new String[0]), 
        AZ(new String[0]), 
        BA(new String[0]), 
        BD(new String[0]), 
        BE(new String[0]), 
        BF(new String[0]), 
        BG(new String[0]), 
        BI(new String[0]), 
        BJ(new String[0]), 
        BN(new String[0]), 
        BO(new String[0]), 
        BR(new String[0]), 
        BS(new String[0]), 
        BT(new String[0]), 
        BW(new String[0]), 
        BY(new String[0]), 
        BZ(new String[0]), 
        CA(new String[0]), 
        CD(new String[0]), 
        CF(new String[0]), 
        CG(new String[0]), 
        CH(new String[0]), 
        CI(new String[0]), 
        CL(new String[0]), 
        CM(new String[0]), 
        CN(new String[0]), 
        CO(new String[0]), 
        CR(new String[0]), 
        CU(new String[0]), 
        CY(new String[0]), 
        CZ(new String[0]), 
        DE(new String[0]), 
        DJ(new String[0]), 
        DK(new String[0]), 
        DO(new String[0]), 
        DZ(new String[0]), 
        EC(new String[0]), 
        EE(new String[0]), 
        EG(new String[0]), 
        EH(new String[0]), 
        ER(new String[0]), 
        ES(new String[0]), 
        ET(new String[0]), 
        FK(new String[0]), 
        FI(new String[0]), 
        FJ(new String[0]), 
        FR(new String[0]), 
        GA(new String[0]), 
        GB(new String[0]), 
        GE(new String[0]), 
        GF(new String[0]), 
        GH(new String[0]), 
        GL(new String[0]), 
        GM(new String[0]), 
        GN(new String[0]), 
        GQ(new String[0]), 
        GR(new String[0]), 
        GT(new String[0]), 
        GW(new String[0]), 
        GY(new String[0]), 
        HN(new String[0]), 
        HR(new String[0]), 
        HT(new String[0]), 
        HU(new String[0]), 
        ID(new String[0]), 
        IE(new String[0]), 
        IL(new String[0]), 
        IN(new String[0]), 
        IQ(new String[0]), 
        IR(new String[0]), 
        IS(new String[0]), 
        IT(new String[0]), 
        JM(new String[0]), 
        JO(new String[0]), 
        JP(new String[0]), 
        KE(new String[0]), 
        KG(new String[0]), 
        KH(new String[0]), 
        KP(new String[0]), 
        KR(new String[0]), 
        XK(new String[0]), 
        KW(new String[0]), 
        KZ(new String[0]), 
        LA(new String[0]), 
        LB(new String[0]), 
        LK(new String[0]), 
        LR(new String[0]), 
        LS(new String[0]), 
        LT(new String[0]), 
        LU(new String[0]), 
        LV(new String[0]), 
        LY(new String[0]), 
        MA(new String[0]), 
        MD(new String[0]), 
        ME(new String[0]), 
        MG(new String[0]), 
        MK(new String[0]), 
        ML(new String[0]), 
        MM(new String[0]), 
        MN(new String[0]), 
        MR(new String[0]), 
        MW(new String[0]), 
        MX(new String[0]), 
        MY(new String[0]), 
        MZ(new String[0]), 
        NA(new String[0]), 
        NC(new String[0]), 
        NE(new String[0]), 
        NG(new String[0]), 
        NI(new String[0]), 
        NL(new String[0]), 
        NO(new String[0]), 
        NP(new String[0]), 
        NZ(new String[0]), 
        OM(new String[0]), 
        PA(new String[0]), 
        PE(new String[0]), 
        PG(new String[0]), 
        PH(new String[0]), 
        PL(new String[0]), 
        PK(new String[0]), 
        PR(new String[0]), 
        PS(new String[0]), 
        PT(new String[0]), 
        PY(new String[0]), 
        QA(new String[0]), 
        RO(new String[0]), 
        RS(new String[0]), 
        RU(new String[0]), 
        RW(new String[0]), 
        SA(new String[0]), 
        SB(new String[0]), 
        SD(new String[0]), 
        SE(new String[0]), 
        SI(new String[0]), 
        SJ(new String[0]), 
        SK(new String[0]), 
        SL(new String[0]), 
        SN(new String[0]), 
        SO(new String[0]), 
        SR(new String[0]), 
        SS(new String[0]), 
        SV(new String[0]), 
        SY(new String[0]), 
        SZ(new String[0]), 
        TD(new String[0]), 
        TF(new String[0]), 
        TG(new String[0]), 
        TH(new String[0]), 
        TJ(new String[0]), 
        TL(new String[0]), 
        TM(new String[0]), 
        TN(new String[0]), 
        TR(new String[0]), 
        TT(new String[0]), 
        TW(new String[0]), 
        TZ(new String[0]), 
        UA(new String[0]), 
        UG(new String[0]), 
        US(new String[0]), 
        UY(new String[0]), 
        UZ(new String[0]), 
        VE(new String[0]), 
        VN(new String[0]), 
        VU(new String[0]), 
        YE(new String[0]), 
        ZA(new String[0]), 
        ZM(new String[0]), 
        ZW(new String[0]);
        
        private final Locale locale;
        
        private Country(final String[] p) {
            this.locale = new Locale("", this.name());
        }
        
        public Locale getLocale() {
            return this.locale;
        }
    }
    
    public static class Location
    {
        private String name;
        private double latitude;
        private double longitude;
        
        public Location(final double latitude, final double longitude) {
            this("", latitude, longitude);
        }
        
        public Location(final String name, final double latitude, final double longitude) {
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
        }
        
        public final String getName() {
            return this.name;
        }
        
        public final double getLatitude() {
            return this.latitude;
        }
        
        public final double getLongitude() {
            return this.longitude;
        }
    }
}
