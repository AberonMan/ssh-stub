package org.mash;

import io.termd.core.telnet.netty.NettyTelnetTtyBootstrap;
import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;

public class TelnetShellServer extends AbstractShellServer {

    private NettyTelnetTtyBootstrap bootstrap;

    public TelnetShellServer(String host, int port) {
        super(host, port);
    }

    @Override
    @SneakyThrows
    protected void startServer() {
         bootstrap = new NettyTelnetTtyBootstrap().
                setHost(host).
                setPort(port);
        bootstrap.start(new Shell()).get(10, TimeUnit.SECONDS);
    }

    @Override
    protected void stopServer() {
        bootstrap.stop();
    }
}
