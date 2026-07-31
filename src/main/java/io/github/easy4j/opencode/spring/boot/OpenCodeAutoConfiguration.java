package io.github.easy4j.opencode.spring.boot;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.easy4j.opencode.OpenCodeClient;
import io.github.easy4j.opencode.OpenCodeHttpClientConfig;
import io.github.easy4j.opencode.cli.OpenCodeCliExecutor;
import io.github.easy4j.opencode.cli.availability.OpenCodeCliAvailabilityChecker;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenCode 自动配置。
 * <p>
 * 注册 {@link OpenCodeClient} 门面 Bean，启动自检由 SDK 构造器统一管理。
 * </p>
 */
@Configuration
@ConditionalOnClass(OpenCodeClient.class)
@ConditionalOnProperty(prefix = OpenCodeProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(OpenCodeProperties.class)
public class OpenCodeAutoConfiguration {

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public OpenCodeClient openCodeClient(OpenCodeProperties properties,
                                          ObjectMapper objectMapper,
                                          OkHttpClient okHttpClient) {
        return new OpenCodeClient(
                properties.getHttp(),
                properties.getCli(),
                objectMapper,
                okHttpClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public OpenCodeCliAvailabilityChecker openCodeCliAvailabilityChecker() {
        return new OpenCodeCliAvailabilityChecker();
    }

    @Bean
    @ConditionalOnMissingBean
    public OpenCodeCliExecutor openCodeCliExecutor(OpenCodeProperties properties) {
        return new OpenCodeCliExecutor(properties.getCli());
    }

    @Bean
    @ConditionalOnMissingBean
    public OkHttpClient openCodeOkHttpClient(OpenCodeProperties properties) {
        OpenCodeHttpClientConfig http = properties.getHttp();
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(http.getConnectTimeoutMillis(),
                        java.util.concurrent.TimeUnit.MILLISECONDS)
                .readTimeout(http.getReadTimeoutMillis(),
                        java.util.concurrent.TimeUnit.MILLISECONDS);
        if (!http.isVerifySsl()) {
            builder.hostnameVerifier((hostname, session) -> true);
        }
        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper openCodeObjectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
