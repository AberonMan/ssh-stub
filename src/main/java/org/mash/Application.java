package org.mash;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.runAsync;

@Slf4j
public class Application {

    public static void main(String[] args) {
        int sshPort = Optional.ofNullable(System.getProperty("sshPort")).map(Integer::parseInt).orElse(8989);
        int telnetPort = Optional.ofNullable(System.getProperty("telnetPort")).map(Integer::parseInt).orElse(9989);
        AbstractShellServer simpleSSHServer = new SshShellServer("localhost", sshPort);
        AbstractShellServer telnetServer = new TelnetShellServer("localhost", telnetPort);
        allOf(runAsync(simpleSSHServer::start), runAsync(telnetServer::start)).join();
    }

}

