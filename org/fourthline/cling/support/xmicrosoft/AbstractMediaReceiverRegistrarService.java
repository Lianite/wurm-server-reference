// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.xmicrosoft;

import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpOutputArgument;
import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import java.beans.PropertyChangeSupport;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;
import org.fourthline.cling.binding.annotations.UpnpStateVariables;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpService;

@UpnpService(serviceId = @UpnpServiceId(namespace = "microsoft.com", value = "X_MS_MediaReceiverRegistrar"), serviceType = @UpnpServiceType(namespace = "microsoft.com", value = "X_MS_MediaReceiverRegistrar", version = 1))
@UpnpStateVariables({ @UpnpStateVariable(name = "A_ARG_TYPE_DeviceID", sendEvents = false, datatype = "string"), @UpnpStateVariable(name = "A_ARG_TYPE_Result", sendEvents = false, datatype = "int"), @UpnpStateVariable(name = "A_ARG_TYPE_RegistrationReqMsg", sendEvents = false, datatype = "bin.base64"), @UpnpStateVariable(name = "A_ARG_TYPE_RegistrationRespMsg", sendEvents = false, datatype = "bin.base64") })
public abstract class AbstractMediaReceiverRegistrarService
{
    protected final PropertyChangeSupport propertyChangeSupport;
    @UpnpStateVariable(eventMinimumDelta = 1)
    private UnsignedIntegerFourBytes authorizationGrantedUpdateID;
    @UpnpStateVariable(eventMinimumDelta = 1)
    private UnsignedIntegerFourBytes authorizationDeniedUpdateID;
    @UpnpStateVariable
    private UnsignedIntegerFourBytes validationSucceededUpdateID;
    @UpnpStateVariable
    private UnsignedIntegerFourBytes validationRevokedUpdateID;
    
    protected AbstractMediaReceiverRegistrarService() {
        this(null);
    }
    
    protected AbstractMediaReceiverRegistrarService(final PropertyChangeSupport propertyChangeSupport) {
        this.authorizationGrantedUpdateID = new UnsignedIntegerFourBytes(0L);
        this.authorizationDeniedUpdateID = new UnsignedIntegerFourBytes(0L);
        this.validationSucceededUpdateID = new UnsignedIntegerFourBytes(0L);
        this.validationRevokedUpdateID = new UnsignedIntegerFourBytes(0L);
        this.propertyChangeSupport = ((propertyChangeSupport != null) ? propertyChangeSupport : new PropertyChangeSupport(this));
    }
    
    public PropertyChangeSupport getPropertyChangeSupport() {
        return this.propertyChangeSupport;
    }
    
    @UpnpAction(out = { @UpnpOutputArgument(name = "AuthorizationGrantedUpdateID") })
    public UnsignedIntegerFourBytes getAuthorizationGrantedUpdateID() {
        return this.authorizationGrantedUpdateID;
    }
    
    @UpnpAction(out = { @UpnpOutputArgument(name = "AuthorizationDeniedUpdateID") })
    public UnsignedIntegerFourBytes getAuthorizationDeniedUpdateID() {
        return this.authorizationDeniedUpdateID;
    }
    
    @UpnpAction(out = { @UpnpOutputArgument(name = "ValidationSucceededUpdateID") })
    public UnsignedIntegerFourBytes getValidationSucceededUpdateID() {
        return this.validationSucceededUpdateID;
    }
    
    @UpnpAction(out = { @UpnpOutputArgument(name = "ValidationRevokedUpdateID") })
    public UnsignedIntegerFourBytes getValidationRevokedUpdateID() {
        return this.validationRevokedUpdateID;
    }
    
    @UpnpAction(out = { @UpnpOutputArgument(name = "Result", stateVariable = "A_ARG_TYPE_Result") })
    public int isAuthorized(@UpnpInputArgument(name = "DeviceID", stateVariable = "A_ARG_TYPE_DeviceID") final String deviceID) {
        return 1;
    }
    
    @UpnpAction(out = { @UpnpOutputArgument(name = "Result", stateVariable = "A_ARG_TYPE_Result") })
    public int isValidated(@UpnpInputArgument(name = "DeviceID", stateVariable = "A_ARG_TYPE_DeviceID") final String deviceID) {
        return 1;
    }
    
    @UpnpAction(out = { @UpnpOutputArgument(name = "RegistrationRespMsg", stateVariable = "A_ARG_TYPE_RegistrationRespMsg") })
    public byte[] registerDevice(@UpnpInputArgument(name = "RegistrationReqMsg", stateVariable = "A_ARG_TYPE_RegistrationReqMsg") final byte[] registrationReqMsg) {
        return new byte[0];
    }
}
