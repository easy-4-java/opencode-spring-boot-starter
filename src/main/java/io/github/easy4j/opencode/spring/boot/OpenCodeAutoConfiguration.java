package io.github.easy4j.opencode.spring.boot;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.easy4j.opencode.OpenCodeClient;
import io.github.easy4j.opencode.OpenCodeCliConfig;
import io.github.easy4j.opencode.OpenCodeHttpClientConfig;
import io.github.easy4j.opencode.cli.OpenCodeCliExecutor;
import io.github.easy4j.opencode.cli.availability.OpenCodeCliAvailabilityChecker;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

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
    public OpenCodeClient openCodeClient(OpenCodeHttpClientConfig httpConfig,
                                         OpenCodeCliConfig cliConfig,
                                         ObjectMapper objectMapper,
                                         @Qualifier("openCodeOkHttpClient") OkHttpClient okHttpClient) {
        return new OpenCodeClient(httpConfig, cliConfig, objectMapper, okHttpClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public OpenCodeHttpClientConfig openCodeHttpClientConfig(OpenCodeProperties properties) {
        return properties.getHttp();
    }

    @Bean
    @ConditionalOnMissingBean
    public OpenCodeCliConfig openCodeCliConfig(OpenCodeProperties properties) {
        return properties.getCli();
    }

    @Bean
    @ConditionalOnMissingBean
    public OpenCodeCliAvailabilityChecker openCodeCliAvailabilityChecker() {
        return new OpenCodeCliAvailabilityChecker();
    }

    @Bean
    @ConditionalOnMissingBean
    public OpenCodeCliExecutor openCodeCliExecutor(OpenCodeCliConfig cliConfig) {
        return new OpenCodeCliExecutor(cliConfig);
    }

    @Bean("openCodeOkHttpClient")
    @ConditionalOnMissingBean(name = "openCodeOkHttpClient")
    public OkHttpClient openCodeOkHttpClient(OpenCodeHttpClientConfig http,
                                             ObjectProvider<OkHttpClient.Builder> builderProvider) {
        OkHttpClient.Builder baseBuilder = builderProvider.getIfAvailable(OkHttpClient.Builder::new);
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(Math.max(1, http.getMaxRequests()));
        dispatcher.setMaxRequestsPerHost(Math.max(1, http.getMaxRequestsPerHost()));
        OkHttpClient.Builder builder = baseBuilder.build().newBuilder()
                .dispatcher(dispatcher)
                .connectionPool(new ConnectionPool(
                        Math.max(1, http.getMaxIdleConnections()),
                        Math.max(1L, http.getKeepAliveDurationMillis()),
                        TimeUnit.MILLISECONDS))
                .connectTimeout(Math.max(0, http.getConnectTimeoutMillis()), TimeUnit.MILLISECONDS)
                .readTimeout(Math.max(0, http.getReadTimeoutMillis()), TimeUnit.MILLISECONDS)
                .writeTimeout(Math.max(0, http.getWriteTimeoutMillis()), TimeUnit.MILLISECONDS)
                .callTimeout(Math.max(0, http.getCallTimeoutMillis()), TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(http.isRetryOnConnectionFailure());
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
