package io.github.easy4j.opencode.spring.boot;

import io.github.easy4j.opencode.OpenCodeClient;
import io.github.easy4j.opencode.OpenCodeHttpClientConfig;
import io.github.easy4j.opencode.HttpResponseMode;
import io.github.easy4j.opencode.cli.availability.OpenCodeCliAvailabilityChecker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Starter 装配冒烟测试。
 */
@SpringBootTest(classes = OpenCodeAutoConfiguration.class)
@TestPropertySource(
        properties = {
                "opencode.http.base-url=http://opencode.example:4096",
                "opencode.http.mode=blocking",
                "opencode.http.stream-core-pool-size=7",
                "opencode.http.password=test-password",
                "opencode.http.default-agent=build",
                "opencode.cli.startup-check-enabled=false",
                "opencode.debug.enabled=true",
                "opencode.debug.level=HEADERS",
                "opencode.debug.max-content-length=4096"
        })
class OpenCodeAutoConfigurationTest {

    @Autowired
    private OpenCodeClient openCodeClient;

    @Autowired
    private OpenCodeProperties openCodeProperties;

    @Autowired
    private OpenCodeCliAvailabilityChecker openCodeCliAvailabilityChecker;

    /**
     * 校验 Bean 创建且 HTTP 配置映射正确。
     */
    @Test
    void beansCreated() {
        assertNotNull(openCodeClient);
        assertNotNull(openCodeProperties);
        assertNotNull(openCodeCliAvailabilityChecker);

        OpenCodeHttpClientConfig httpConfig = openCodeProperties.getHttp();
        assertEquals("http://opencode.example:4096", httpConfig.getBaseUrl());
        assertEquals(HttpResponseMode.BLOCKING, httpConfig.getMode());
        assertEquals(7, httpConfig.getStreamCorePoolSize());
        assertEquals("test-password", httpConfig.getPassword());
        assertEquals("build", httpConfig.getDefaultAgent());
        assertEquals(true, openCodeProperties.getDebug().isEnabled());
        assertEquals("HEADERS", openCodeProperties.getDebug().getLevel().name());
        assertEquals(4096, openCodeProperties.getDebug().getMaxContentLength());
        org.junit.jupiter.api.Assertions.assertSame(openCodeProperties.getDebug(), httpConfig.getDebug());
        org.junit.jupiter.api.Assertions.assertSame(openCodeProperties.getDebug(), openCodeProperties.getCli().getDebug());
    }
}
