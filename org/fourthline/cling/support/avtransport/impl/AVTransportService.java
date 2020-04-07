// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.avtransport.impl;

import org.seamless.statemachine.StateMachine;
import org.fourthline.cling.support.model.StorageMedium;
import org.fourthline.cling.support.lastchange.LastChange;
import org.seamless.statemachine.StateMachineBuilder;
import java.util.Iterator;
import org.fourthline.cling.support.model.TransportAction;
import org.fourthline.cling.support.model.SeekMode;
import org.fourthline.cling.support.model.DeviceCapabilities;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.RecordQualityMode;
import org.fourthline.cling.support.model.TransportSettings;
import org.fourthline.cling.support.model.PlayMode;
import org.seamless.statemachine.TransitionException;
import org.fourthline.cling.support.avtransport.AVTransportErrorCode;
import org.fourthline.cling.support.avtransport.AVTransportException;
import org.fourthline.cling.model.types.ErrorCode;
import java.net.URI;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import java.util.concurrent.ConcurrentHashMap;
import org.fourthline.cling.support.avtransport.impl.state.AbstractState;
import java.util.Map;
import java.util.logging.Logger;
import org.fourthline.cling.support.avtransport.AbstractAVTransportService;
import org.fourthline.cling.support.model.AVTransport;

public class AVTransportService<T extends AVTransport> extends AbstractAVTransportService
{
    private static final Logger log;
    private final Map<Long, AVTransportStateMachine> stateMachines;
    final Class<? extends AVTransportStateMachine> stateMachineDefinition;
    final Class<? extends AbstractState> initialState;
    final Class<? extends AVTransport> transportClass;
    
    public AVTransportService(final Class<? extends AVTransportStateMachine> stateMachineDefinition, final Class<? extends AbstractState> initialState) {
        this(stateMachineDefinition, initialState, AVTransport.class);
    }
    
    public AVTransportService(final Class<? extends AVTransportStateMachine> stateMachineDefinition, final Class<? extends AbstractState> initialState, final Class<T> transportClass) {
        this.stateMachines = new ConcurrentHashMap<Long, AVTransportStateMachine>();
        this.stateMachineDefinition = stateMachineDefinition;
        this.initialState = initialState;
        this.transportClass = transportClass;
    }
    
    @Override
    public void setAVTransportURI(final UnsignedIntegerFourBytes instanceId, final String currentURI, final String currentURIMetaData) throws AVTransportException {
        URI uri;
        try {
            uri = new URI(currentURI);
        }
        catch (Exception ex2) {
            throw new AVTransportException(ErrorCode.INVALID_ARGS, "CurrentURI can not be null or malformed");
        }
        try {
            final AVTransportStateMachine transportStateMachine = this.findStateMachine(instanceId, true);
            transportStateMachine.setTransportURI(uri, currentURIMetaData);
        }
        catch (TransitionException ex) {
            throw new AVTransportException(AVTransportErrorCode.TRANSITION_NOT_AVAILABLE, ex.getMessage());
        }
    }
    
    @Override
    public void setNextAVTransportURI(final UnsignedIntegerFourBytes instanceId, final String nextURI, final String nextURIMetaData) throws AVTransportException {
        URI uri;
        try {
            uri = new URI(nextURI);
        }
        catch (Exception ex2) {
            throw new AVTransportException(ErrorCode.INVALID_ARGS, "NextURI can not be null or malformed");
        }
        try {
            final AVTransportStateMachine transportStateMachine = this.findStateMachine(instanceId, true);
            transportStateMachine.setNextTransportURI(uri, nextURIMetaData);
        }
        catch (TransitionException ex) {
            throw new AVTransportException(AVTransportErrorCode.TRANSITION_NOT_AVAILABLE, ex.getMessage());
        }
    }
    
    @Override
    public void setPlayMode(final UnsignedIntegerFourBytes instanceId, final String newPlayMode) throws AVTransportException {
        final AVTransport transport = ((StateMachine<AbstractState<AVTransport>>)this.findStateMachine(instanceId)).getCurrentState().getTransport();
        try {
            transport.setTransportSettings(new TransportSettings(PlayMode.valueOf(newPlayMode), transport.getTransportSettings().getRecQualityMode()));
        }
        catch (IllegalArgumentException ex) {
            throw new AVTransportException(AVTransportErrorCode.PLAYMODE_NOT_SUPPORTED, "Unsupported play mode: " + newPlayMode);
        }
    }
    
    @Override
    public void setRecordQualityMode(final UnsignedIntegerFourBytes instanceId, final String newRecordQualityMode) throws AVTransportException {
        final AVTransport transport = ((StateMachine<AbstractState<AVTransport>>)this.findStateMachine(instanceId)).getCurrentState().getTransport();
        try {
            transport.setTransportSettings(new TransportSettings(transport.getTransportSettings().getPlayMode(), RecordQualityMode.valueOrExceptionOf(newRecordQualityMode)));
        }
        catch (IllegalArgumentException ex) {
            throw new AVTransportException(AVTransportErrorCode.RECORDQUALITYMODE_NOT_SUPPORTED, "Unsupported record quality mode: " + newRecordQualityMode);
        }
    }
    
    @Override
    public MediaInfo getMediaInfo(final UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        return ((StateMachine<AbstractState<AVTransport>>)this.findStateMachine(instanceId)).getCurrentState().getTransport().getMediaInfo();
    }
    
    @Override
    public TransportInfo getTransportInfo(final UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        return ((StateMachine<AbstractState<AVTransport>>)this.findStateMachine(instanceId)).getCurrentState().getTransport().getTransportInfo();
    }
    
    @Override
    public PositionInfo getPositionInfo(final UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        return ((StateMachine<AbstractState<AVTransport>>)this.findStateMachine(instanceId)).getCurrentState().getTransport().getPositionInfo();
    }
    
    @Override
    public DeviceCapabilities getDeviceCapabilities(final UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        return ((StateMachine<AbstractState<AVTransport>>)this.findStateMachine(instanceId)).getCurrentState().getTransport().getDeviceCapabilities();
    }
    
    @Override
    public TransportSettings getTransportSettings(final UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        return ((StateMachine<AbstractState<AVTransport>>)this.findStateMachine(instanceId)).getCurrentState().getTransport().getTransportSettings();
    }
    
    @Override
    public void stop(final UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        try {
            this.findStateMachine(instanceId).stop();
        }
        catch (TransitionException ex) {
            throw new AVTransportException(AVTransportErrorCode.TRANSITION_NOT_AVAILABLE, ex.getMessage());
        }
    }
    
    @Override
    public void play(final UnsignedIntegerFourBytes instanceId, final String speed) throws AVTransportException {
        try {
            this.findStateMachine(instanceId).play(speed);
        }
        catch (TransitionException ex) {
            throw new AVTransportException(AVTransportErrorCode.TRANSITION_NOT_AVAILABLE, ex.getMessage());
        }
    }
    
    @Override
    public void pause(final UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        try {
            this.findStateMachine(instanceId).pause();
        }
        catch (TransitionException ex) {
            throw new AVTransportException(AVTransportErrorCode.TRANSITION_NOT_AVAILABLE, ex.getMessage());
        }
    }
    
    @Override
    public void record(final UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        try {
            this.findStateMachine(instanceId).record();
        }
        catch (TransitionException ex) {
            throw new AVTransportException(AVTransportErrorCode.TRANSITION_NOT_AVAILABLE, ex.getMessage());
        }
    }
    
    @Override
    public void seek(final UnsignedIntegerFourBytes instanceId, final String unit, final String target) throws AVTransportException {
        SeekMode seekMode;
        try {
            seekMode = SeekMode.valueOrExceptionOf(unit);
        }
        catch (IllegalArgumentException ex2) {
            throw new AVTransportException(AVTransportErrorCode.SEEKMODE_NOT_SUPPORTED, "Unsupported seek mode: " + unit);
        }
        try {
            this.findStateMachine(instanceId).seek(seekMode, target);
        }
        catch (TransitionException ex) {
            throw new AVTransportException(AVTransportErrorCode.TRANSITION_NOT_AVAILABLE, ex.getMessage());
        }
    }
    
    @Override
    public void next(final UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        try {
            this.findStateMachine(instanceId).next();
        }
        catch (TransitionException ex) {
            throw new AVTransportException(AVTransportErrorCode.TRANSITION_NOT_AVAILABLE, ex.getMessage());
        }
    }
    
    @Override
    public void previous(final UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        try {
            this.findStateMachine(instanceId).previous();
        }
        catch (TransitionException ex) {
            throw new AVTransportException(AVTransportErrorCode.TRANSITION_NOT_AVAILABLE, ex.getMessage());
        }
    }
    
    @Override
    protected TransportAction[] getCurrentTransportActions(final UnsignedIntegerFourBytes instanceId) throws Exception {
        final AVTransportStateMachine stateMachine = this.findStateMachine(instanceId);
        try {
            return stateMachine.getCurrentState().getCurrentTransportActions();
        }
        catch (TransitionException ex) {
            return new TransportAction[0];
        }
    }
    
    @Override
    public UnsignedIntegerFourBytes[] getCurrentInstanceIds() {
        synchronized (this.stateMachines) {
            final UnsignedIntegerFourBytes[] ids = new UnsignedIntegerFourBytes[this.stateMachines.size()];
            int i = 0;
            for (final Long id : this.stateMachines.keySet()) {
                ids[i] = new UnsignedIntegerFourBytes(id);
                ++i;
            }
            return ids;
        }
    }
    
    protected AVTransportStateMachine findStateMachine(final UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        return this.findStateMachine(instanceId, true);
    }
    
    protected AVTransportStateMachine findStateMachine(final UnsignedIntegerFourBytes instanceId, final boolean createDefaultTransport) throws AVTransportException {
        synchronized (this.stateMachines) {
            final long id = instanceId.getValue();
            AVTransportStateMachine stateMachine = this.stateMachines.get(id);
            if (stateMachine == null && id == 0L && createDefaultTransport) {
                AVTransportService.log.fine("Creating default transport instance with ID '0'");
                stateMachine = this.createStateMachine(instanceId);
                this.stateMachines.put(id, stateMachine);
            }
            else if (stateMachine == null) {
                throw new AVTransportException(AVTransportErrorCode.INVALID_INSTANCE_ID);
            }
            AVTransportService.log.fine("Found transport control with ID '" + id + "'");
            return stateMachine;
        }
    }
    
    protected AVTransportStateMachine createStateMachine(final UnsignedIntegerFourBytes instanceId) {
        return StateMachineBuilder.build(this.stateMachineDefinition, this.initialState, new Class[] { this.transportClass }, new Object[] { this.createTransport(instanceId, this.getLastChange()) });
    }
    
    protected AVTransport createTransport(final UnsignedIntegerFourBytes instanceId, final LastChange lastChange) {
        return new AVTransport(instanceId, lastChange, StorageMedium.NETWORK);
    }
    
    static {
        log = Logger.getLogger(AVTransportService.class.getName());
    }
}
