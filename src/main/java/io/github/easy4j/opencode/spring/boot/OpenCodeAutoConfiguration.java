package io.github.easy4j.opencode.spring.boot;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.easy4j.opencode.OpenCodeClient;
import io.github.easy4j.opencode.OpenCodeClientConfig;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.ObjectProvider;
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

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public OpenCodeClient openCodeClient(OpenCodeClientConfig config,
                                         ObjectProvider<ObjectMapper> objectMapperProvider,
                                         ObjectProvider<OkHttpClient> httpClientProvider) {
        return new OpenCodeClient(config, objectMapperProvider.getIfAvailable(), httpClientProvider.getIfAvailable());
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = OpenCodeProperties.PREFIX, name = "startup-check-enabled", havingValue = "true", matchIfMissing = true)
    public OpenCodeCliStartupChecker openCodeCliStartupChecker(OpenCodeClientConfig config, OpenCodeProperties properties) {
        return new OpenCodeCliStartupChecker(config, properties.isFailFastOnUnavailable());
    }
}
