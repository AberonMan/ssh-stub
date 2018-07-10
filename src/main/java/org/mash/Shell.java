package org.mash;

import io.termd.core.readline.Function;
import io.termd.core.readline.Keymap;
import io.termd.core.readline.Readline;
import io.termd.core.tty.TtyConnection;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static io.termd.core.tty.TtyEvent.INTR;
import static io.termd.core.util.Helper.loadServices;
import static java.lang.Thread.currentThread;

@Slf4j
public class Shell implements Consumer<TtyConnection> {

    private final ExecutorService executorService;


    private final Map<String, String> commandRepository = new HashMap<String, String>() {
        {
            put("command1.1", "response1.1 test1.1");
            put("command1.2", "response1.2 test1.2");
            put("command2.1", "response2.1 test2.1");
            put("command2.2", "response2.2 test2.2");
            put("script1.1", "response-script1.1 test1.1");
            put("script1.2", "response-script1.2 test1.2");
        }
    };
    // for io operation we should have more thread than processor count
    private final int IO_MULTIPLICATOR = 4;
    private final List<Function> functions;
    private final static String PROMPT = "> ";

    public Shell() {
        this.functions = loadServices(currentThread().getContextClassLoader(), Function.class);
        int threadCount = Optional
                .ofNullable(System.getProperty("threadCount"))
                .map(Integer::parseInt)
                .orElse(Runtime.getRuntime().availableProcessors() * IO_MULTIPLICATOR);
        this.executorService = Executors.newFixedThreadPool(threadCount);
    }


    @Override
    public void accept(TtyConnection conn) {
        log.info("Start to process session...");
        Keymap keymap = Keymap.getDefault();
        Readline readline = new Readline(keymap);
        readline.addFunctions(functions);
        conn.write("Welcome to mediation test stub" + "\n");
        read(conn, readline);
    }

    private void read(TtyConnection conn, Readline readline) {

        readline.readline(conn, PROMPT, (line) -> {
            System.out.println("Read line was called");
            log.info("Command was received {}", line);
            /*Ctrl+D recived*/
            if (line == null) {
                conn.write("logout\n").close();
                return;
            }
            CompletableFuture.runAsync(() -> processCommand(conn, line), executorService)
                    .exceptionally(this::handleException)
                    .thenRunAsync(() -> read(conn, readline));
        });

    }

    private void processCommand(TtyConnection conn, String line) {
        try {
            Thread executionThread = Thread.currentThread();
            conn.setEventHandler((ttyEvent, integer) -> {
                if (ttyEvent == INTR) {
                    executionThread.interrupt();
                }
            });
            String answer = commandRepository.getOrDefault(line, "unknown command");
            log.info("Answer was found" + answer);
            conn.write(answer + '\n');
        } finally {
            conn.setEventHandler(null);
        }
    }

    private Void handleException(Throwable throwable) {
        throwable.printStackTrace();
        log.error("Exception occurs in command processing", throwable);
        return null;
    }

}





