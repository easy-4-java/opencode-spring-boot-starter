package io.github.easy4j.opencode.spring.boot;

import io.github.easy4j.opencode.OpenCodeCliConfig;
import io.github.easy4j.opencode.OpenCodeHttpClientConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * OpenCode Spring Boot 配置属性。
 */
@ConfigurationProperties(prefix = OpenCodeProperties.PREFIX)
@Data
public class OpenCodeProperties {

    public static final String PREFIX = "opencode";

    /** 是否启用本 Starter 提供的 Bean */
    private boolean enabled = true;

    /** HTTP/Server 相关配置 */
    @NestedConfigurationProperty
    private final OpenCodeHttpClientConfig http = new OpenCodeHttpClientConfig();

    /** 本地 CLI 相关配置 */
    @NestedConfigurationProperty
    private final OpenCodeCli cli = new OpenCodeCli();

    /**
     * Starter 扩展的 CLI 配置，包含启动探测开关。
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class OpenCodeCli extends OpenCodeCliConfig {

        /** 是否在应用启动时执行本机 {@code opencode --version} 探测 */
        private boolean startupCheckEnabled = true;

        /** 启动探测失败时是否中断应用启动 */
        private boolean failFastOnUnavailable = false;
    }
}
