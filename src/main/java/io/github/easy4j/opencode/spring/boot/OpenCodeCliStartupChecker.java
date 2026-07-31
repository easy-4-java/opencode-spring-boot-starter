package io.github.easy4j.opencode.spring.boot;

import io.github.easy4j.opencode.OpenCodeCliConfig;
import io.github.easy4j.opencode.cli.availability.OpenCodeCliAvailabilityChecker;
import io.github.easy4j.opencode.cli.availability.OpenCodeCliAvailabilityReport;
import io.github.easy4j.opencode.exception.OpenCodeCliStartupException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;

/**
 * 应用启动时探测本机 {@code opencode} CLI 是否可用。
 */
@Slf4j
@RequiredArgsConstructor
public class OpenCodeCliStartupChecker implements ApplicationRunner {

    private final OpenCodeCliConfig cliConfig;
    private final OpenCodeProperties openCodeProperties;
    private final OpenCodeCliAvailabilityChecker availabilityChecker;
    private final Environment environment;

    /**
     * 启动阶段执行 {@code opencode --version} 探测。
     */
    @Override
    public void run(ApplicationArguments args) {
        OpenCodeCliAvailabilityReport report = availabilityChecker.check(cliConfig);
        String configSnapshot = buildEffectiveConfigSnapshot();

        if (report.isAvailable()) {
            log.info(
                    "OpenCode CLI ready: {} effectiveConfig={}",
                    report.toDiagnosticMessage(),
                    configSnapshot);
            return;
        }

        String message = report.toDiagnosticMessage()
                + "。请确认 opencode.cli.executable 指向可执行的 opencode（如 /usr/local/bin/opencode）。"
                + " effectiveConfig={" + configSnapshot + "}";
        if (openCodeProperties.getCli().isFailFastOnUnavailable()) {
            throw new OpenCodeCliStartupException(message, report);
        }
        log.warn("OpenCode CLI startup check failed (fail-fast disabled): {}", message);
    }

    private String buildEffectiveConfigSnapshot() {
        String profiles = environment.getProperty("spring.profiles.active", "(unset)");
        OpenCodeProperties.OpenCodeCli cli = openCodeProperties.getCli();
        return "profiles=" + profiles
                + ", opencode.enabled=" + openCodeProperties.isEnabled()
                + ", opencode.cli.executable=" + cli.getExecutable()
                + ", opencode.cli.startup-check-enabled=" + cli.isStartupCheckEnabled()
                + ", opencode.cli.fail-fast-on-unavailable=" + cli.isFailFastOnUnavailable()
                + ", opencodeStarterOnClasspath=" + isOpenCodeStarterOnClasspath();
    }

    private static boolean isOpenCodeStarterOnClasspath() {
        try {
            Class.forName("io.github.easy4j.opencode.spring.boot.OpenCodeAutoConfiguration");
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }
}
