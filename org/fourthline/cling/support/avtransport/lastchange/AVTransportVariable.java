// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.avtransport.lastchange;

import org.fourthline.cling.support.model.TransportAction;
import java.net.URI;
import org.fourthline.cling.support.lastchange.EventedValueURI;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.lastchange.EventedValueUnsignedIntegerFourBytes;
import org.fourthline.cling.support.model.RecordQualityMode;
import org.fourthline.cling.support.model.RecordMediumWriteStatus;
import org.fourthline.cling.model.types.InvalidValueException;
import java.util.regex.Pattern;
import org.fourthline.cling.support.lastchange.EventedValueString;
import org.fourthline.cling.support.model.PlayMode;
import java.util.List;
import java.util.ArrayList;
import org.fourthline.cling.support.lastchange.EventedValueEnumArray;
import org.fourthline.cling.support.model.StorageMedium;
import org.fourthline.cling.support.model.TransportStatus;
import java.util.Map;
import org.fourthline.cling.support.model.TransportState;
import org.fourthline.cling.support.lastchange.EventedValueEnum;
import java.util.HashSet;
import org.fourthline.cling.support.lastchange.EventedValue;
import java.util.Set;

public class AVTransportVariable
{
    public static Set<Class<? extends EventedValue>> ALL;
    
    static {
        AVTransportVariable.ALL = new HashSet<Class<? extends EventedValue>>() {
            {
                ((HashSet<Class<TransportState>>)this).add(TransportState.class);
                ((HashSet<Class<TransportStatus>>)this).add(TransportStatus.class);
                ((HashSet<Class<RecordStorageMedium>>)this).add(RecordStorageMedium.class);
                ((HashSet<Class<PossibleRecordStorageMedia>>)this).add(PossibleRecordStorageMedia.class);
                ((HashSet<Class<PossiblePlaybackStorageMedia>>)this).add(PossiblePlaybackStorageMedia.class);
                ((HashSet<Class<CurrentPlayMode>>)this).add(CurrentPlayMode.class);
                ((HashSet<Class<TransportPlaySpeed>>)this).add(TransportPlaySpeed.class);
                ((HashSet<Class<RecordMediumWriteStatus>>)this).add(RecordMediumWriteStatus.class);
                ((HashSet<Class<CurrentRecordQualityMode>>)this).add(CurrentRecordQualityMode.class);
                ((HashSet<Class<PossibleRecordQualityModes>>)this).add(PossibleRecordQualityModes.class);
                ((HashSet<Class<NumberOfTracks>>)this).add(NumberOfTracks.class);
                ((HashSet<Class<CurrentTrack>>)this).add(CurrentTrack.class);
                ((HashSet<Class<CurrentTrackDuration>>)this).add(CurrentTrackDuration.class);
                ((HashSet<Class<CurrentMediaDuration>>)this).add(CurrentMediaDuration.class);
                ((HashSet<Class<CurrentTrackMetaData>>)this).add(CurrentTrackMetaData.class);
                ((HashSet<Class<CurrentTrackURI>>)this).add(CurrentTrackURI.class);
                ((HashSet<Class<AVTransportURI>>)this).add(AVTransportURI.class);
                ((HashSet<Class<NextAVTransportURI>>)this).add(NextAVTransportURI.class);
                ((HashSet<Class<AVTransportURIMetaData>>)this).add(AVTransportURIMetaData.class);
                ((HashSet<Class<NextAVTransportURIMetaData>>)this).add(NextAVTransportURIMetaData.class);
                ((HashSet<Class<CurrentTransportActions>>)this).add(CurrentTransportActions.class);
                ((HashSet<Class<RelativeTimePosition>>)this).add(RelativeTimePosition.class);
                ((HashSet<Class<AbsoluteTimePosition>>)this).add(AbsoluteTimePosition.class);
                ((HashSet<Class<RelativeCounterPosition>>)this).add(RelativeCounterPosition.class);
                ((HashSet<Class<AbsoluteCounterPosition>>)this).add(AbsoluteCounterPosition.class);
            }
        };
    }
    
    public static class TransportState extends EventedValueEnum<org.fourthline.cling.support.model.TransportState>
    {
        public TransportState(final org.fourthline.cling.support.model.TransportState avTransportState) {
            super(avTransportState);
        }
        
        public TransportState(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
        
        @Override
        protected org.fourthline.cling.support.model.TransportState enumValueOf(final String s) {
            return org.fourthline.cling.support.model.TransportState.valueOf(s);
        }
    }
    
    public static class TransportStatus extends EventedValueEnum<org.fourthline.cling.support.model.TransportStatus>
    {
        public TransportStatus(final org.fourthline.cling.support.model.TransportStatus transportStatus) {
            super(transportStatus);
        }
        
        public TransportStatus(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
        
        @Override
        protected org.fourthline.cling.support.model.TransportStatus enumValueOf(final String s) {
            return org.fourthline.cling.support.model.TransportStatus.valueOf(s);
        }
    }
    
    public static class RecordStorageMedium extends EventedValueEnum<StorageMedium>
    {
        public RecordStorageMedium(final StorageMedium storageMedium) {
            super(storageMedium);
        }
        
        public RecordStorageMedium(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
        
        @Override
        protected StorageMedium enumValueOf(final String s) {
            return StorageMedium.valueOf(s);
        }
    }
    
    public static class PossibleRecordStorageMedia extends EventedValueEnumArray<StorageMedium>
    {
        public PossibleRecordStorageMedia(final StorageMedium[] e) {
            super(e);
        }
        
        public PossibleRecordStorageMedia(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
        
        @Override
        protected StorageMedium[] enumValueOf(final String[] names) {
            final List<StorageMedium> list = new ArrayList<StorageMedium>();
            for (final String s : names) {
                list.add(StorageMedium.valueOf(s));
            }
            return list.toArray(new StorageMedium[list.size()]);
        }
    }
    
    public static class PossiblePlaybackStorageMedia extends PossibleRecordStorageMedia
    {
        public PossiblePlaybackStorageMedia(final StorageMedium[] e) {
            super(e);
        }
        
        public PossiblePlaybackStorageMedia(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
    
    public static class CurrentPlayMode extends EventedValueEnum<PlayMode>
    {
        public CurrentPlayMode(final PlayMode playMode) {
            super(playMode);
        }
        
        public CurrentPlayMode(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
        
        @Override
        protected PlayMode enumValueOf(final String s) {
            return PlayMode.valueOf(s);
        }
    }
    
    public static class TransportPlaySpeed extends EventedValueString
    {
        static final Pattern pattern;
        
        public TransportPlaySpeed(final String value) {
            super(value);
            if (!TransportPlaySpeed.pattern.matcher(value).matches()) {
                throw new InvalidValueException("Can't parse TransportPlaySpeed speeds.");
            }
        }
        
        public TransportPlaySpeed(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
        
        static {
            pattern = Pattern.compile("^-?\\d+(/\\d+)?$", 2);
        }
    }
    
    public static class RecordMediumWriteStatus extends EventedValueEnum<org.fourthline.cling.support.model.RecordMediumWriteStatus>
    {
        public RecordMediumWriteStatus(final org.fourthline.cling.support.model.RecordMediumWriteStatus recordMediumWriteStatus) {
            super(recordMediumWriteStatus);
        }
        
        public RecordMediumWriteStatus(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
        
        @Override
        protected org.fourthline.cling.support.model.RecordMediumWriteStatus enumValueOf(final String s) {
            return org.fourthline.cling.support.model.RecordMediumWriteStatus.valueOf(s);
        }
    }
    
    public static class CurrentRecordQualityMode extends EventedValueEnum<RecordQualityMode>
    {
        public CurrentRecordQualityMode(final RecordQualityMode recordQualityMode) {
            super(recordQualityMode);
        }
        
        public CurrentRecordQualityMode(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
        
        @Override
        protected RecordQualityMode enumValueOf(final String s) {
            return RecordQualityMode.valueOf(s);
        }
    }
    
    public static class PossibleRecordQualityModes extends EventedValueEnumArray<RecordQualityMode>
    {
        public PossibleRecordQualityModes(final RecordQualityMode[] e) {
            super(e);
        }
        
        public PossibleRecordQualityModes(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
        
        @Override
        protected RecordQualityMode[] enumValueOf(final String[] names) {
            final List<RecordQualityMode> list = new ArrayList<RecordQualityMode>();
            for (final String s : names) {
                list.add(RecordQualityMode.valueOf(s));
            }
            return list.toArray(new RecordQualityMode[list.size()]);
        }
    }
    
    public static class NumberOfTracks extends EventedValueUnsignedIntegerFourBytes
    {
        public NumberOfTracks(final UnsignedIntegerFourBytes value) {
            super(value);
        }
        
        public NumberOfTracks(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
    
    public static class CurrentTrack extends EventedValueUnsignedIntegerFourBytes
    {
        public CurrentTrack(final UnsignedIntegerFourBytes value) {
            super(value);
        }
        
        public CurrentTrack(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
    
    public static class CurrentTrackDuration extends EventedValueString
    {
        public CurrentTrackDuration(final String value) {
            super(value);
        }
        
        public CurrentTrackDuration(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
    
    public static class CurrentMediaDuration extends EventedValueString
    {
        public CurrentMediaDuration(final String value) {
            super(value);
        }
        
        public CurrentMediaDuration(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
    
    public static class CurrentTrackMetaData extends EventedValueString
    {
        public CurrentTrackMetaData(final String value) {
            super(value);
        }
        
        public CurrentTrackMetaData(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
    
    public static class CurrentTrackURI extends EventedValueURI
    {
        public CurrentTrackURI(final URI value) {
            super(value);
        }
        
        public CurrentTrackURI(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
    
    public static class AVTransportURI extends EventedValueURI
    {
        public AVTransportURI(final URI value) {
            super(value);
        }
        
        public AVTransportURI(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
    
    public static class NextAVTransportURI extends EventedValueURI
    {
        public NextAVTransportURI(final URI value) {
            super(value);
        }
        
        public NextAVTransportURI(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
    
    public static class AVTransportURIMetaData extends EventedValueString
    {
        public AVTransportURIMetaData(final String value) {
            super(value);
        }
        
        public AVTransportURIMetaData(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
    
    public static class NextAVTransportURIMetaData extends EventedValueString
    {
        public NextAVTransportURIMetaData(final String value) {
            super(value);
        }
        
        public NextAVTransportURIMetaData(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
    
    public static class CurrentTransportActions extends EventedValueEnumArray<TransportAction>
    {
        public CurrentTransportActions(final TransportAction[] e) {
            super(e);
        }
        
        public CurrentTransportActions(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
        
        @Override
        protected TransportAction[] enumValueOf(final String[] names) {
            if (names == null) {
                return new TransportAction[0];
            }
            final List<TransportAction> list = new ArrayList<TransportAction>();
            for (final String s : names) {
                list.add(TransportAction.valueOf(s));
            }
            return list.toArray(new TransportAction[list.size()]);
        }
    }
    
    public static class RelativeTimePosition extends EventedValueString
    {
        public RelativeTimePosition(final String value) {
            super(value);
        }
        
        public RelativeTimePosition(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
    
    public static class AbsoluteTimePosition extends EventedValueString
    {
        public AbsoluteTimePosition(final String value) {
            super(value);
        }
        
        public AbsoluteTimePosition(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
    
    public static class RelativeCounterPosition extends EventedValueString
    {
        public RelativeCounterPosition(final String value) {
            super(value);
        }
        
        public RelativeCounterPosition(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
    
    public static class AbsoluteCounterPosition extends EventedValueString
    {
        public AbsoluteCounterPosition(final String value) {
            super(value);
        }
        
        public AbsoluteCounterPosition(final Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
}
