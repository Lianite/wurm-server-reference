// 
// Decompiled by Procyon v0.5.30
// 

package coffee.keenan.network.helpers.address;

import java.util.Arrays;
import org.jetbrains.annotations.NotNull;
import java.util.Iterator;
import java.util.Objects;
import java.net.SocketException;
import java.util.Collections;
import java.net.NetworkInterface;
import coffee.keenan.network.helpers.interfaces.InterfaceHelper;
import org.jetbrains.annotations.Nullable;
import java.net.InetAddress;
import coffee.keenan.network.config.DefaultConfiguration;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import coffee.keenan.network.validators.address.InternetValidator;
import coffee.keenan.network.validators.address.IP4Validator;
import java.util.List;
import coffee.keenan.network.validators.address.IAddressValidator;
import java.util.Set;
import coffee.keenan.network.config.IConfiguration;
import coffee.keenan.network.helpers.ErrorTracking;

public class AddressHelper extends ErrorTracking
{
    private final IConfiguration configuration;
    private Set<IAddressValidator> addressValidators;
    
    public AddressHelper() {
        this.addressValidators = new HashSet<IAddressValidator>((Collection<? extends IAddressValidator>)Stream.of(new IAddressValidator[] { new IP4Validator(), new InternetValidator() }).collect((Collector<? super IAddressValidator, ?, List<? super IAddressValidator>>)Collectors.toList()));
        this.configuration = new DefaultConfiguration();
    }
    
    public AddressHelper(final IConfiguration configuration) {
        this.addressValidators = new HashSet<IAddressValidator>((Collection<? extends IAddressValidator>)Stream.of(new IAddressValidator[] { new IP4Validator(), new InternetValidator() }).collect((Collector<? super IAddressValidator, ?, List<? super IAddressValidator>>)Collectors.toList()));
        this.configuration = configuration;
    }
    
    @Nullable
    public static InetAddress getFirstValidAddress() {
        return getFirstValidAddress(new DefaultConfiguration());
    }
    
    @Nullable
    public static InetAddress getFirstValidAddress(final IConfiguration configuration) {
        final InterfaceHelper interfaceHelper = new InterfaceHelper(configuration);
        final AddressHelper addressHelper = new AddressHelper(configuration);
        List<NetworkInterface> interfaces = null;
        try {
            interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
        }
        catch (SocketException e) {
            e.printStackTrace();
        }
        for (final NetworkInterface networkInterface : Objects.requireNonNull(interfaces)) {
            if (!interfaceHelper.validateInterface(networkInterface)) {
                continue;
            }
            for (final InetAddress inetAddress : Collections.list(networkInterface.getInetAddresses())) {
                if (addressHelper.validateAddress(inetAddress)) {
                    return inetAddress;
                }
            }
        }
        return null;
    }
    
    public void addAddressValidators(@NotNull final IAddressValidator... validators) {
        this.addressValidators.addAll(Arrays.asList(validators));
    }
    
    public Collection<IAddressValidator> getAddressValidators() {
        return this.addressValidators;
    }
    
    public void setAddressValidators(@NotNull final IAddressValidator... validators) {
        this.addressValidators = new HashSet<IAddressValidator>(Arrays.asList(validators));
    }
    
    public boolean validateAddress(final InetAddress address) {
        for (final IAddressValidator validator : this.getAddressValidators()) {
            if (!validator.validate(address, this.configuration)) {
                this.addException("address: " + address.toString(), validator.getException());
                return false;
            }
        }
        return true;
    }
}
