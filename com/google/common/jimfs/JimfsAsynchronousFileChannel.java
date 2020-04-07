// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.nio.channels.ClosedChannelException;
import com.google.common.util.concurrent.SettableFuture;
import java.nio.ByteBuffer;
import java.util.concurrent.Callable;
import java.nio.channels.FileLock;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import java.nio.channels.CompletionHandler;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.IOException;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.base.Preconditions;
import java.util.concurrent.ExecutorService;
import com.google.common.util.concurrent.ListeningExecutorService;
import java.nio.channels.AsynchronousFileChannel;

final class JimfsAsynchronousFileChannel extends AsynchronousFileChannel
{
    private final JimfsFileChannel channel;
    private final ListeningExecutorService executor;
    
    public JimfsAsynchronousFileChannel(final JimfsFileChannel channel, final ExecutorService executor) {
        this.channel = Preconditions.checkNotNull(channel);
        this.executor = MoreExecutors.listeningDecorator(executor);
    }
    
    @Override
    public long size() throws IOException {
        return this.channel.size();
    }
    
    private <R, A> void addCallback(final ListenableFuture<R> future, final CompletionHandler<R, ? super A> handler, @Nullable final A attachment) {
        future.addListener(new CompletionHandlerCallback<Object, Object>((ListenableFuture)future, (CompletionHandler)handler, (Object)attachment), this.executor);
    }
    
    @Override
    public AsynchronousFileChannel truncate(final long size) throws IOException {
        this.channel.truncate(size);
        return this;
    }
    
    @Override
    public void force(final boolean metaData) throws IOException {
        this.channel.force(metaData);
    }
    
    @Override
    public <A> void lock(final long position, final long size, final boolean shared, @Nullable final A attachment, final CompletionHandler<FileLock, ? super A> handler) {
        Preconditions.checkNotNull(handler);
        this.addCallback(this.lock(position, size, shared), handler, attachment);
    }
    
    @Override
    public ListenableFuture<FileLock> lock(final long position, final long size, final boolean shared) {
        Util.checkNotNegative(position, "position");
        Util.checkNotNegative(size, "size");
        if (!this.isOpen()) {
            return closedChannelFuture();
        }
        if (shared) {
            this.channel.checkReadable();
        }
        else {
            this.channel.checkWritable();
        }
        return this.executor.submit((Callable<FileLock>)new Callable<FileLock>() {
            @Override
            public FileLock call() throws IOException {
                return JimfsAsynchronousFileChannel.this.tryLock(position, size, shared);
            }
        });
    }
    
    @Override
    public FileLock tryLock(final long position, final long size, final boolean shared) throws IOException {
        Util.checkNotNegative(position, "position");
        Util.checkNotNegative(size, "size");
        this.channel.checkOpen();
        if (shared) {
            this.channel.checkReadable();
        }
        else {
            this.channel.checkWritable();
        }
        return new JimfsFileChannel.FakeFileLock(this, position, size, shared);
    }
    
    @Override
    public <A> void read(final ByteBuffer dst, final long position, @Nullable final A attachment, final CompletionHandler<Integer, ? super A> handler) {
        this.addCallback(this.read(dst, position), handler, attachment);
    }
    
    @Override
    public ListenableFuture<Integer> read(final ByteBuffer dst, final long position) {
        Preconditions.checkArgument(!dst.isReadOnly(), (Object)"dst may not be read-only");
        Util.checkNotNegative(position, "position");
        if (!this.isOpen()) {
            return closedChannelFuture();
        }
        this.channel.checkReadable();
        return this.executor.submit((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws IOException {
                return JimfsAsynchronousFileChannel.this.channel.read(dst, position);
            }
        });
    }
    
    @Override
    public <A> void write(final ByteBuffer src, final long position, @Nullable final A attachment, final CompletionHandler<Integer, ? super A> handler) {
        this.addCallback(this.write(src, position), handler, attachment);
    }
    
    @Override
    public ListenableFuture<Integer> write(final ByteBuffer src, final long position) {
        Util.checkNotNegative(position, "position");
        if (!this.isOpen()) {
            return closedChannelFuture();
        }
        this.channel.checkWritable();
        return this.executor.submit((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws IOException {
                return JimfsAsynchronousFileChannel.this.channel.write(src, position);
            }
        });
    }
    
    @Override
    public boolean isOpen() {
        return this.channel.isOpen();
    }
    
    @Override
    public void close() throws IOException {
        this.channel.close();
    }
    
    private static <V> ListenableFuture<V> closedChannelFuture() {
        final SettableFuture<V> future = SettableFuture.create();
        future.setException(new ClosedChannelException());
        return future;
    }
    
    private static final class CompletionHandlerCallback<R, A> implements Runnable
    {
        private final ListenableFuture<R> future;
        private final CompletionHandler<R, ? super A> completionHandler;
        @Nullable
        private final A attachment;
        
        private CompletionHandlerCallback(final ListenableFuture<R> future, final CompletionHandler<R, ? super A> completionHandler, @Nullable final A attachment) {
            this.future = Preconditions.checkNotNull(future);
            this.completionHandler = Preconditions.checkNotNull(completionHandler);
            this.attachment = attachment;
        }
        
        @Override
        public void run() {
            R result;
            try {
                result = this.future.get();
            }
            catch (ExecutionException e) {
                this.onFailure(e.getCause());
                return;
            }
            catch (InterruptedException | RuntimeException | Error ex) {
                final Throwable t;
                final Throwable e2 = t;
                this.onFailure(e2);
                return;
            }
            this.onSuccess(result);
        }
        
        private void onSuccess(final R result) {
            this.completionHandler.completed(result, (Object)this.attachment);
        }
        
        private void onFailure(final Throwable t) {
            this.completionHandler.failed(t, (Object)this.attachment);
        }
    }
}
