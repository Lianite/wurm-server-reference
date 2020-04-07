// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.spi;

import org.fourthline.cling.model.message.OutgoingDatagramMessage;
import org.fourthline.cling.model.UnsupportedDataException;
import org.fourthline.cling.model.message.IncomingDatagramMessage;
import java.net.DatagramPacket;
import java.net.InetAddress;

public interface DatagramProcessor
{
    IncomingDatagramMessage read(final InetAddress p0, final DatagramPacket p1) throws UnsupportedDataException;
    
    DatagramPacket write(final OutgoingDatagramMessage p0) throws UnsupportedDataException;
}
