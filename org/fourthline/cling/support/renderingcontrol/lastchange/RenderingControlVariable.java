// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.renderingcontrol.lastchange;

import org.fourthline.cling.support.lastchange.EventedValueShort;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;
import org.fourthline.cling.support.lastchange.EventedValueUnsignedIntegerTwoBytes;
import java.util.Map;
import org.fourthline.cling.support.lastchange.EventedValueString;
import java.util.HashSet;
import org.fourthline.cling.support.lastchange.EventedValue;
import java.util.Set;

public class RenderingControlVariable
{
    public static Set<Class<? extends EventedValue>> ALL;
    
    static {
        RenderingControlVariable.ALL = new HashSet<Class<? extends EventedValue>>() {
            {
                ((HashSet<Class<PresetNameList>>)this).add(PresetNameList.class);
                ((HashSet<Class<Brightness>>)this).add(Brightness.class);
                ((HashSet<Class<Contrast>>)this).add(Contrast.class);
                ((HashSet<Class<Sharpness>>)this).add(Sharpness.class);
                ((HashSet<Class<RedVideoGain>>)this).add(RedVideoGain.class);
                ((HashSet<Class<BlueVideoGain>>)this).add(BlueVideoGain.class);
                ((HashSet<Class<GreenVideoGain>>)this).add(GreenVideoGain.class);
                ((HashSet<Class<RedVideoBlackLevel>>)this).add(RedVideoBlackLevel.class);
                ((HashSet<Class<BlueVideoBlackLevel>>)this).add(BlueVideoBlackLevel.class);
                ((HashSet<Class<GreenVideoBlackLevel>>)this).add(GreenVideoBlackLevel.class);
                ((HashSet<Class<ColorTemperature>>)this).add(ColorTemperature.class);
                ((HashSet<Class<HorizontalKeystone>>)this).add(HorizontalKeystone.class);
                ((HashSet<Class<VerticalKeystone>>)this).add(VerticalKeystone.class);
                ((HashSet<Class<Mute>>)this).add(Mute.class);
                ((HashSet<Class<VolumeDB>>)this).add(VolumeDB.class);
                ((HashSet<Class<Volume>>)this).add(Volume.class);
                ((HashSet<Class<Loudness>>)this).add(Loudness.class);
            }
        };
    }
    
    public static class PresetNameList extends EventedValueString
    {
        public PresetNameList(final String s) {
            super(s);
        }
        
        public PresetNameList(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
    
    public static class Brightness extends EventedValueUnsignedIntegerTwoBytes
    {
        public Brightness(final UnsignedIntegerTwoBytes value) {
            super(value);
        }
        
        public Brightness(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
    
    public static class Contrast extends EventedValueUnsignedIntegerTwoBytes
    {
        public Contrast(final UnsignedIntegerTwoBytes value) {
            super(value);
        }
        
        public Contrast(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
    
    public static class Sharpness extends EventedValueUnsignedIntegerTwoBytes
    {
        public Sharpness(final UnsignedIntegerTwoBytes value) {
            super(value);
        }
        
        public Sharpness(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
    
    public static class RedVideoGain extends EventedValueUnsignedIntegerTwoBytes
    {
        public RedVideoGain(final UnsignedIntegerTwoBytes value) {
            super(value);
        }
        
        public RedVideoGain(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
    
    public static class BlueVideoGain extends EventedValueUnsignedIntegerTwoBytes
    {
        public BlueVideoGain(final UnsignedIntegerTwoBytes value) {
            super(value);
        }
        
        public BlueVideoGain(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
    
    public static class GreenVideoGain extends EventedValueUnsignedIntegerTwoBytes
    {
        public GreenVideoGain(final UnsignedIntegerTwoBytes value) {
            super(value);
        }
        
        public GreenVideoGain(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
    
    public static class RedVideoBlackLevel extends EventedValueUnsignedIntegerTwoBytes
    {
        public RedVideoBlackLevel(final UnsignedIntegerTwoBytes value) {
            super(value);
        }
        
        public RedVideoBlackLevel(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
    
    public static class BlueVideoBlackLevel extends EventedValueUnsignedIntegerTwoBytes
    {
        public BlueVideoBlackLevel(final UnsignedIntegerTwoBytes value) {
            super(value);
        }
        
        public BlueVideoBlackLevel(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
    
    public static class GreenVideoBlackLevel extends EventedValueUnsignedIntegerTwoBytes
    {
        public GreenVideoBlackLevel(final UnsignedIntegerTwoBytes value) {
            super(value);
        }
        
        public GreenVideoBlackLevel(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
    
    public static class ColorTemperature extends EventedValueUnsignedIntegerTwoBytes
    {
        public ColorTemperature(final UnsignedIntegerTwoBytes value) {
            super(value);
        }
        
        public ColorTemperature(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
    
    public static class HorizontalKeystone extends EventedValueShort
    {
        public HorizontalKeystone(final Short value) {
            super(value);
        }
        
        public HorizontalKeystone(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
    
    public static class VerticalKeystone extends EventedValueShort
    {
        public VerticalKeystone(final Short value) {
            super(value);
        }
        
        public VerticalKeystone(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
    
    public static class Mute extends EventedValueChannelMute
    {
        public Mute(final ChannelMute value) {
            super(value);
        }
        
        public Mute(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
    
    public static class VolumeDB extends EventedValueChannelVolumeDB
    {
        public VolumeDB(final ChannelVolumeDB value) {
            super(value);
        }
        
        public VolumeDB(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
    
    public static class Volume extends EventedValueChannelVolume
    {
        public Volume(final ChannelVolume value) {
            super(value);
        }
        
        public Volume(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
    
    public static class Loudness extends EventedValueChannelLoudness
    {
        public Loudness(final ChannelLoudness value) {
            super(value);
        }
        
        public Loudness(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
}
