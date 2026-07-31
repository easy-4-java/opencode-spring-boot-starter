package io.github.easy4j.opencode.spring.boot;

import io.github.easy4j.opencode.cli.availability.OpenCodeCliAvailabilityChecker;
import io.github.easy4j.opencode.exception.OpenCodeCliStartupException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link OpenCodeCliStartupChecker} 行为测试。
 */
class OpenCodeCliStartupCheckerTest {

    @Test
    void shouldFailFastWhenCliUnavailable() {
        OpenCodeProperties properties = new OpenCodeProperties();
        properties.getCli().setExecutable("/nonexistent/opencode-startup-test");
        properties.getCli().setFailFastOnUnavailable(true);
        OpenCodeCliStartupChecker checker = new OpenCodeCliStartupChecker(
                properties.getCli(), properties, new OpenCodeCliAvailabilityChecker(), new MockEnvironment());

        assertThrows(OpenCodeCliStartupException.class,
                () -> checker.run(new DefaultApplicationArguments(new String[0])));
    }

    @Test
    void shouldWarnOnlyWhenFailFastDisabled() {
        OpenCodeProperties properties = new OpenCodeProperties();
        properties.getCli().setExecutable("/nonexistent/opencode-startup-test");
        properties.getCli().setFailFastOnUnavailable(false);
        OpenCodeCliStartupChecker checker = new OpenCodeCliStartupChecker(
                properties.getCli(), properties, new OpenCodeCliAvailabilityChecker(), new MockEnvironment());

        assertDoesNotThrow(() -> checker.run(new DefaultApplicationArguments(new String[0])));
    }
}
