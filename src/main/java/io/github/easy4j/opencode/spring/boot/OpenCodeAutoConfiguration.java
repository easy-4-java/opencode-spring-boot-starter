package io.github.hiwepy.opencode.spring.boot;

import io.github.hiwepy.opencode.OpenCodeClient;
import io.github.hiwepy.opencode.OpenCodeClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenCode 自动配置。
 */
@Configuration
@ConditionalOnClass(OpenCodeClient.class)
@ConditionalOnProperty(prefix = OpenCodeProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(OpenCodeProperties.class)
public class OpenCodeAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(OpenCodeAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public OpenCodeClientConfig openCodeClientConfig(OpenCodeProperties properties) {
        OpenCodeClientConfig config = new OpenCodeClientConfig();
        config.setServerUrl(properties.getServerUrl());
        config.setUsername(properties.getUsername());
        config.setPassword(properties.getPassword());
        config.setConnectTimeoutMillis(properties.getConnectTimeoutMillis());
        config.setReadTimeoutMillis(properties.getReadTimeoutMillis());
        config.setVerifySsl(properties.isVerifySsl());
        config.setLocalExecutable(properties.getCliExecutable());
        config.setLocalTimeoutSeconds(properties.getCliTimeoutSeconds());
        config.setLocalProbeTimeoutSeconds(5);
        config.setDefaultModel(properties.getDefaultModel());
        config.setDefaultAgent(properties.getDefaultAgent());
        log.info("OpenCode client configured: serverUrl={}, username={}", config.getServerUrl(), config.getUsername());
        return config;
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public OpenCodeClient openCodeClient(OpenCodeClientConfig config) {
        return new OpenCodeClient(config);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = OpenCodeProperties.PREFIX, name = "startup-check-enabled", havingValue = "true", matchIfMissing = true)
    public OpenCodeCliStartupChecker openCodeCliStartupChecker(OpenCodeClientConfig config, OpenCodeProperties properties) {
        return new OpenCodeCliStartupChecker(config, properties.isFailFastOnUnavailable());
    }
}
