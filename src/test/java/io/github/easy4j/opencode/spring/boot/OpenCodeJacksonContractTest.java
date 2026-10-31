package io.github.easy4j.opencode.spring.boot;

import io.github.easy4j.opencode.OpenCodeCliConfig;
import io.github.easy4j.opencode.OpenCodeClient;
import io.github.easy4j.opencode.OpenCodeHttpClientConfig;
import io.github.easy4j.opencode.api.model.Session;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.json.JsonMapper;

import java.lang.invoke.MethodType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Properties;
import java.util.jar.JarFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Offline JSON and bean contracts against the SDK binary actually resolved by Maven. */
class OpenCodeJacksonContractTest {

    // Deliberately unannotated: SDK @JsonIgnoreProperties must not mask the factory setting.
    public record Payload(String displayName) {}

    @Test
    void factoryMapperIgnoresUnknownFieldsWithoutDtoAnnotations() {
        ObjectMapper mapper = new OpenCodeAutoConfiguration().openCodeObjectMapper();
        String json = "{\"displayName\":\"offline\",\"futureField\":{\"value\":1}}";
        assertThat(mapper.readValue(json, Payload.class)).isEqualTo(new Payload("offline"));
        assertThatThrownBy(() -> JsonMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build()
                .readValue(json, Payload.class))
                .isInstanceOf(tools.jackson.databind.exc.UnrecognizedPropertyException.class);
    }

    @Test
    void factoryMapperRoundTripsSdkSessionWithWireNamesAndUnknownFields() {
        ObjectMapper mapper = new OpenCodeAutoConfiguration().openCodeObjectMapper();
        Session session = mapper.readValue("""
                {"id":"session-offline","title":"contract","parent_id":"parent-offline",
                 "created_at":"2026-01-01T00:00:00Z","updated_at":"2026-01-02T00:00:00Z",
                 "metadata":{"origin":"fixture"},"futureServerField":true}
                """, Session.class);
        assertThat(session.getParentId()).isEqualTo("parent-offline");
        assertThat(session.getCreatedAt()).isEqualTo("2026-01-01T00:00:00Z");
        var json = mapper.readTree(mapper.writeValueAsString(session));
        assertThat(json.get("id").asText()).isEqualTo("session-offline");
        assertThat(json.get("parent_id").asText()).isEqualTo("parent-offline");
        assertThat(json.get("created_at").asText()).isEqualTo("2026-01-01T00:00:00Z");
        assertThat(json.get("updated_at").asText()).isEqualTo("2026-01-02T00:00:00Z");
        assertThat(json.get("metadata").get("origin").asText()).isEqualTo("fixture");
        assertThat(json.has("parentId")).isFalse();
        assertThat(json.has("createdAt")).isFalse();
        assertThat(json.has("updatedAt")).isFalse();
        assertThat(json.has("futureServerField")).isFalse();
        assertThat(mapper.readValue(mapper.writeValueAsString(session), Session.class))
                .isEqualTo(session);
    }

    @Test
    void contextUsesFactoryMapperByDefault() {
        offlineRunner().run(context -> {
            assertThat(context).hasSingleBean(ObjectMapper.class);
            ObjectMapper mapper = context.getBean(ObjectMapper.class);
            assertThat(mapper).isSameAs(context.getBean("openCodeObjectMapper"));
            assertThat(mapper.readValue("{\"displayName\":\"default\",\"future\":1}",
                    Payload.class)).isEqualTo(new Payload("default"));
            assertClientMapperIdentity(context.getBean(OpenCodeClient.class), mapper);
        });
    }

    @Test
    void customJackson3MapperBacksOffAndRetainsIdentityAndConfiguration() {
        ObjectMapper custom = JsonMapper.builder()
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
        offlineRunner().withBean("customMapper", ObjectMapper.class, () -> custom)
                .run(context -> {
                    assertThat(context).hasSingleBean(ObjectMapper.class);
                    assertThat(context).doesNotHaveBean("openCodeObjectMapper");
                    ObjectMapper actual = context.getBean(ObjectMapper.class);
                    assertThat(actual).isSameAs(custom);
                    assertClientMapperIdentity(context.getBean(OpenCodeClient.class), custom);
                    assertThat(actual.readTree(actual.writeValueAsString(new Payload("custom")))
                            .get("display_name").asText()).isEqualTo("custom");
                    assertThat(actual.readValue("{\"display_name\":\"custom\"}", Payload.class))
                            .isEqualTo(new Payload("custom"));
                    assertThatThrownBy(() -> actual.readValue(
                            "{\"display_name\":\"custom\",\"future\":1}", Payload.class))
                            .isInstanceOf(tools.jackson.databind.exc.UnrecognizedPropertyException.class);
                });
    }

    private ApplicationContextRunner offlineRunner() {
        // Construct real SDK clients, but never probe a service or execute the CLI.
        return new ApplicationContextRunner()
                .withUserConfiguration(OpenCodeAutoConfiguration.class)
                .withPropertyValues("opencode.http.enabled=true",
                        "opencode.http.base-url=http://127.0.0.1:1",
                        "opencode.http.startup-check-enabled=false",
                        "opencode.cli.enabled=false", "opencode.cli.startup-check-enabled=false");
    }

    private static void assertClientMapperIdentity(OpenCodeClient client, ObjectMapper mapper) {
        // Read-only inspection verifies the actual constructor injection, not a mocked factory.
        Object httpClient = ReflectionTestUtils.getField(client, "httpClient");
        assertThat(httpClient).isNotNull();
        assertThat(ReflectionTestUtils.getField(httpClient, "objectMapper")).isSameAs(mapper);
    }

    @Test
    void reportsResolvedSdkBinaryProvenanceAndConstructorDescriptor() throws Exception {
        Path jarPath = Path.of(OpenCodeClient.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI());
        assertThat(Files.isRegularFile(jarPath)).as("SDK must resolve to a dependency JAR").isTrue();
        String digest = HexFormat.of().formatHex(
                MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(jarPath)));
        try (JarFile jar = new JarFile(jarPath.toFile())) {
            var entry = jar.getJarEntry(
                    "META-INF/maven/io.github.easy4j/opencode-java-sdk/pom.properties");
            assertThat(entry).as("embedded SDK Maven coordinates").isNotNull();
            Properties gav = new Properties();
            try (var input = jar.getInputStream(entry)) {
                gav.load(input);
            }
            assertThat(gav.getProperty("groupId")).isEqualTo("io.github.easy4j");
            assertThat(gav.getProperty("artifactId")).isEqualTo("opencode-java-sdk");
            assertThat(gav.getProperty("version")).isEqualTo("3.0.x.20260630-SNAPSHOT");
            System.out.println("SDK binary GAV=" + gav.getProperty("groupId") + ":"
                    + gav.getProperty("artifactId") + ":" + gav.getProperty("version"));
            System.out.println("SDK binary SHA-256=" + digest);
            // Allowlist only build identity fields; never dump settings, environment or repository URLs.
            var manifest = jar.getManifest();
            for (String key : new String[]{"Implementation-Version", "Build-Jdk-Spec",
                    "Build-Time", "Git-Commit", "Build-Revision"}) {
                String value = manifest == null ? null : manifest.getMainAttributes().getValue(key);
                System.out.println("SDK manifest " + key + "=" + (value == null ? "<absent>" : value));
            }
        }
        var constructor = OpenCodeClient.class.getConstructor(OpenCodeHttpClientConfig.class,
                OpenCodeCliConfig.class, ObjectMapper.class, OkHttpClient.class);
        System.out.println("SDK constructor=" + constructor.toGenericString());
        System.out.println("SDK constructor descriptor=" + MethodType.methodType(
                void.class, constructor.getParameterTypes()).toMethodDescriptorString());
        System.out.println("SDK source commit is not inferred from repository HEAD or SNAPSHOT GAV.");
    }
}
