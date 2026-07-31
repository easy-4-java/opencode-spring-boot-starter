package io.github.easy4j.opencode.spring.boot;

import io.github.easy4j.opencode.OpenCodeCliConfig;
import io.github.easy4j.opencode.OpenCodeHttpClientConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * OpenCode Spring Boot 配置属性。
 * <p>
 * HTTP/CLI 子系统的启动检查（{@code startupCheckEnabled}、{@code failFastOnUnavailable}）
 * 已下沉到 SDK 子配置中。
 * </p>
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
    private final OpenCodeCliConfig cli = new OpenCodeCliConfig();
}
