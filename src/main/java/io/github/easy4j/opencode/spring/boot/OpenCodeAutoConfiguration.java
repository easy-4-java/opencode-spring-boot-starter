package io.github.easy4j.opencode.spring.boot;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.easy4j.opencode.OpenCodeClient;
import io.github.easy4j.opencode.cli.availability.OpenCodeCliAvailabilityChecker;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * OpenCode 自动配置。
 */
@Configuration
@ConditionalOnClass(OpenCodeClient.class)
@ConditionalOnProperty(prefix = OpenCodeProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(OpenCodeProperties.class)
public class OpenCodeAutoConfiguration {

    /**
     * 注册 OpenCode 客户端门面 Bean。
     */
    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public OpenCodeClient openCodeClient(OpenCodeProperties properties,
                                         ObjectProvider<ObjectMapper> objectMapperProvider,
                                         ObjectProvider<OkHttpClient> httpClientProvider) {
        return new OpenCodeClient(properties.getHttp(), properties.getCli(),
                objectMapperProvider.getIfAvailable(), httpClientProvider.getIfAvailable());
    }

    /**
     * 注册 CLI 可用性探测器。
     */
    @Bean
    @ConditionalOnMissingBean
    public OpenCodeCliAvailabilityChecker openCodeCliAvailabilityChecker() {
        return new OpenCodeCliAvailabilityChecker();
    }

    /**
     * 启动时可选执行 CLI 探测。
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = OpenCodeProperties.PREFIX + ".cli", name = "startup-check-enabled",
            havingValue = "true", matchIfMissing = true)
    public OpenCodeCliStartupChecker openCodeCliStartupChecker(OpenCodeProperties properties,
                                                               OpenCodeCliAvailabilityChecker checker,
                                                               Environment environment) {
        return new OpenCodeCliStartupChecker(properties.getCli(), properties, checker, environment);
    }
}
