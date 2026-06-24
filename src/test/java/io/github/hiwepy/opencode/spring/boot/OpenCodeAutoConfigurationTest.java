package io.github.hiwepy.opencode.spring.boot;

import io.github.hiwepy.opencode.OpenCodeClient;
import io.github.hiwepy.opencode.OpenCodeHttpClientConfig;
import io.github.hiwepy.opencode.cli.availability.OpenCodeCliAvailabilityChecker;
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
                "opencode.http.server-url=http://opencode.example:4096",
                "opencode.http.password=test-password",
                "opencode.http.default-agent=build",
                "opencode.cli.startup-check-enabled=false"
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
        assertEquals("http://opencode.example:4096", httpConfig.getServerUrl());
        assertEquals("test-password", httpConfig.getPassword());
        assertEquals("build", httpConfig.getDefaultAgent());
    }
}
