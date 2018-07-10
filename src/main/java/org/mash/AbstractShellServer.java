package org.mash;

import lombok.SneakyThrows;

import java.util.concurrent.CountDownLatch;

public abstract class AbstractShellServer {
    private final CountDownLatch latch = new CountDownLatch(1);

    final String host;

    final int port;

    public AbstractShellServer(String host, int port) {
        this.host = host;
        this.port = port;
    }


    @SneakyThrows
    public void start() {
        startServer();
        latch.await();
    }

    protected abstract void startServer();

    public void stop() {
        latch.countDown();
        stopServer();
    }

    protected abstract void stopServer();
}
