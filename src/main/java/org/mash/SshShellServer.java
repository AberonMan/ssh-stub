package org.mash;

import io.termd.core.ssh.netty.NettySshTtyBootstrap;
import lombok.SneakyThrows;
import org.apache.sshd.server.keyprovider.AbstractGeneratorHostKeyProvider;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class SshShellServer extends AbstractShellServer {


    private NettySshTtyBootstrap bootstrap;

    public SshShellServer(String host, int port) {
        super(host, port);
    }

    @Override
    @SneakyThrows
    protected void startServer() {
        final AbstractGeneratorHostKeyProvider provider = new SimpleGeneratorHostKeyProvider(new File("hostkey.ser"));
        provider.setAlgorithm("RSA");

        bootstrap = new NettySshTtyBootstrap().
                setPort(port).
                setKeyPairProvider(provider).
                setHost(host);
        bootstrap.start(new Shell()).get(10, TimeUnit.SECONDS);

    }

    @Override
    @SneakyThrows
    protected void stopServer() {
        bootstrap.stop();
    }
}
