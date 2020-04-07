// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.io.IOException;
import java.nio.ByteBuffer;
import com.google.common.base.Preconditions;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;

final class DowngradedSeekableByteChannel implements SeekableByteChannel
{
    private final FileChannel channel;
    
    DowngradedSeekableByteChannel(final FileChannel channel) {
        this.channel = Preconditions.checkNotNull(channel);
    }
    
    @Override
    public int read(final ByteBuffer dst) throws IOException {
        return this.channel.read(dst);
    }
    
    @Override
    public int write(final ByteBuffer src) throws IOException {
        return this.channel.write(src);
    }
    
    @Override
    public long position() throws IOException {
        return this.channel.position();
    }
    
    @Override
    public SeekableByteChannel position(final long newPosition) throws IOException {
        this.channel.position(newPosition);
        return this;
    }
    
    @Override
    public long size() throws IOException {
        return this.channel.size();
    }
    
    @Override
    public SeekableByteChannel truncate(final long size) throws IOException {
        this.channel.truncate(size);
        return this;
    }
    
    @Override
    public boolean isOpen() {
        return this.channel.isOpen();
    }
    
    @Override
    public void close() throws IOException {
        this.channel.close();
    }
}
