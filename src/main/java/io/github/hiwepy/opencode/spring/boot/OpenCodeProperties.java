package io.github.hiwepy.opencode.spring.boot;

import io.github.hiwepy.opencode.OpenCodeClientConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OpenCode Spring Boot 配置属性。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ConfigurationProperties(prefix = OpenCodeProperties.PREFIX)
public class OpenCodeProperties extends OpenCodeClientConfig {

    public static final String PREFIX = "opencode";

    /**
     * 启用/禁用 OpenCode starter。
     */
    private boolean enabled = true;

    /**
     * 启动时是否探测 CLI 可用性。
     */
    private boolean startupCheckEnabled = true;

    /**
     * CLI 不可用时是否快速失败（启动失败）。
     */
    private boolean failFastOnUnavailable = false;

    /**
     * 默认模型，格式 {@code provider/model}。
     */
    private String defaultModel;

    /**
     * 默认 agent 名称。
     */
    private String defaultAgent;
}
