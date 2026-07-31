package io.github.hiwepy.opencode.spring.boot;

import io.github.hiwepy.opencode.OpenCodeClientConfig;
import io.github.hiwepy.opencode.cli.OpenCodeCliExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * 启动时探测 OpenCode CLI 是否可用。
 */
public class OpenCodeCliStartupChecker implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(OpenCodeCliStartupChecker.class);

    private final OpenCodeClientConfig config;
    private final boolean failFast;

    public OpenCodeCliStartupChecker(OpenCodeClientConfig config, boolean failFast) {
        this.config = config;
        this.failFast = failFast;
    }

    @Override
    public void run(ApplicationArguments args) {
        OpenCodeCliExecutor executor = new OpenCodeCliExecutor(config);
        boolean available = executor.probe();

        if (available) {
            log.info("OpenCode CLI is available: {}", config.getLocalExecutable());
        } else {
            String message = "OpenCode CLI is NOT available: " + config.getLocalExecutable();
            if (failFast) {
                throw new OpenCodeCliUnavailableException(message);
            } else {
                log.warn(message + " (continuing without CLI support)");
            }
        }
    }

    public static class OpenCodeCliUnavailableException extends RuntimeException {
        public OpenCodeCliUnavailableException(String message) {
            super(message);
        }
    }
}
