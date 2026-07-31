package io.github.hiwepy.opencode.spring.boot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OpenCode Spring Boot 配置属性。
 */
@Data
@ConfigurationProperties(prefix = OpenCodeProperties.PREFIX)
public class OpenCodeProperties {

    public static final String PREFIX = "opencode.server";

    /**
     * 启用/禁用 OpenCode starter。
     */
    private boolean enabled = true;

    /**
     * OpenCode Server 根地址。
     */
    private String serverUrl = "http://localhost:4096";

    /**
     * HTTP Basic Auth 用户名。
     */
    private String username = "opencode";

    /**
     * HTTP Basic Auth 密码。
     */
    private String password;

    /**
     * 连接超时（毫秒）。
     */
    private int connectTimeoutMillis = 15000;

    /**
     * 读取超时（毫秒）。
     */
    private int readTimeoutMillis = 300000;

    /**
     * 是否校验 HTTPS 证书。
     */
    private boolean verifySsl = true;

    /**
     * 本地 CLI 可执行文件名或绝对路径。
     */
    private String cliExecutable = "opencode";

    /**
     * 本地 CLI 命令超时（秒）。
     */
    private int cliTimeoutSeconds = 300;

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
