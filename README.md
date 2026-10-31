<a id="readme-top"></a>

<div align="center">

# opencode-spring-boot-starter

**Spring Boot Starter for opencode**

[![Maven Central](https://img.shields.io/maven-central/v/io.github.easy4j/opencode-spring-boot-starter)](https://github.com/easy-4-java/opencode-spring-boot-starter)
[![Java](https://img.shields.io/badge/Java-21-orange)](#3-requirements-and-compatibility)
[![License](https://img.shields.io/badge/license-Apache-2.0-green)](https://www.apache.org/licenses/LICENSE-2.0)

[简体中文](./README.zh-CN.md) | [English](./README.md)

[Positioning](#1-positioning) · [Capabilities](#2-core-capabilities) ·
[Dependency](#5-dependency) · [Quick Start](#6-quick-start) ·
[Configuration](#7-configuration-reference) · [Versions](#9-version-lines-and-compatibility) ·
[Build](#10-build-and-test) · [License](#12-license)

</div>

---

> **Current Version**：`4.1.x.20260730-SNAPSHOT`<br>
> **JDK Baseline**：`21`<br>
> **Group ID**：`io.github.easy4j`<br>
> **Artifact ID**：`opencode-spring-boot-starter`<br>
> **License**：Apache License 2.0<br>

## 1. Positioning

**opencode-spring-boot-starter** is a Spring Boot starter that integrates **opencode** for applications using opencode. It provides auto-configuration, property binding, and ready-to-use beans so that applications can consume opencode capabilities with minimal setup.

| Dimension | Description |
|---|---|
| Type | Spring Boot Starter |
| Consumers | Spring Boot applications using opencode |
| Core Capabilities | auto-configuration, property binding, ready-to-use beans for opencode |
| JDK | `21` |
| Coordinates | `io.github.easy4j:opencode-spring-boot-starter:4.1.x.20260730-SNAPSHOT` |
| Config Prefix | `opencode` |

## 2. Core Capabilities

| Capability | Status | Description |
|---|:---:|---|
| Auto-configuration | ✅ Stable | Registers opencode beans automatically |
| Property Binding | ✅ Stable | Binds `opencode.*` to `OpenCodeProperties` |
| `OpenCodeClient` bean | ✅ Stable | Auto-registered via OpenCodeAutoConfiguration |

## 3. Requirements and Compatibility

| Dependency | Minimum | Evidence |
|---|---:|---|
| JDK | `21` | `pom.xml` |
| Spring Boot | `4.1.2` | `pom.xml` parent |
| Maven | `4.0.0-rc-5` | Maven Enforcer |


### 4.1.x Jackson 3 migration boundary

This branch uses Spring Boot 4.1.2, Java 21, Maven 4.0.0-rc-5 and
`io.github.easy4j:opencode-java-sdk:3.0.x.20260630-SNAPSHOT`.
The public `OpenCodeAutoConfiguration.openCodeObjectMapper()` return type and
`openCodeClient(..., ObjectMapper, ...)` parameter now use
`tools.jackson.databind.ObjectMapper` (Jackson 3), not
`com.fasterxml.jackson.databind.ObjectMapper` (Jackson 2). This is a source and
binary API boundary change: recompile direct callers and migrate custom mapper
beans, imports, modules and builder configuration to Jackson 3. A Jackson 2 bean
does not satisfy the Jackson 3 injection/backoff condition. If multiple Jackson 3
mappers exist, select one using Spring's normal candidate selection (for example
`@Primary`).

The starter's default mapper is built with `JsonMapper.builder()` and disables
`FAIL_ON_UNKNOWN_PROPERTIES`. An application-provided Jackson 3 mapper replaces
that default unchanged, so its own unknown-field and naming policies apply.
Jackson annotations used by SDK DTOs remain in `com.fasterxml.jackson.annotation`;
do not mechanically replace that annotation package.

`OpenCodeJacksonContractTest` exercises real factory-created JSON mapping,
SDK Session wire names/round trips, and custom mapper backoff and identity.
It makes no remote API calls or CLI invocations. Its test output records the
loaded SDK JAR's embedded GAV, SHA-256, allowlisted build metadata and constructor
binary descriptor. A mutable SNAPSHOT coordinate or current SDK repository HEAD
does not identify the source commit of those bytes. These focused contracts do
not establish full behavioral equivalence with the previously unresolvable
non-SNAPSHOT coordinate, nor cover every SDK DTO or remote endpoint.

## 4. Auto-configuration

The starter auto-configures the following beans:

| Bean | Condition | Missing Behavior |
|---|---|---|
| `OpenCodeClient` | classpath + property | not created |
| `OpenCodeCliAvailabilityChecker` | classpath + property | not created |
| `OpenCodeCliExecutor` | classpath + property | not created |
| `OkHttpClient` | classpath + property | not created |
| `ObjectMapper` | classpath + property | not created |

Auto-configuration registration:

- `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` (Spring Boot 2.7+ / 3.x / 4.x)
- `META-INF/spring.factories` (Spring Boot 2.x legacy)

## 5. Dependency

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>opencode-spring-boot-starter</artifactId>
    <version>4.1.x.20260730-SNAPSHOT</version>
</dependency>
```

This branch declares the SDK version explicitly in its POM:

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>opencode-java-sdk</artifactId>
    <version>3.0.x.20260630-SNAPSHOT</version>
</dependency>
```

## 6. Quick Start

### 6.1 Add dependency

Add the dependency above to your `pom.xml`.

### 6.2 Configure

```yaml
opencode:
  enabled: true
```

### 6.3 Use the bean

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

Then inject the auto-configured bean in your code:

```java
@Autowired
private OpenCodeClient openCodeClient;
```

## 7. Configuration Reference

### 7.1 Config Prefix

`opencode`

### 7.2 Configuration Items

| Property | Type | Default | Required | Description | Sensitive |
|---|---|---|:---:|---|:---:|
| `opencode.enabled` | boolean | `true` | No | Enable the starter | No |
<!-- additional properties below -->

## 8. Version Lines and Compatibility

| Branch | JDK | Spring Boot | Component Version | Status |
|---|---:|---:|---|:---:|
| `2.3.x` / `2.7.x` | `8+` | 2.3.x / 2.7.x | `1.0.x` | Maintenance |
| `3.0.x` ~ `3.5.x` | `17` | 3.x | `2.0.x` | Maintenance |
| `4.0.x` / `4.1.x` | `17+` | 4.x | `3.0.x` | Active |

## 9. Build and Test

```bash
mvn clean verify
```

## 10. Troubleshooting

| Symptom | Diagnosis | Resolution |
|---|---|---|
| Bean not created | Check auto-configuration report | Verify `opencode.enabled=true` and classpath |
| `ClassNotFoundException` | Missing dependency | Add the required module |
| Version conflict | `mvn dependency:tree` | Use BOM for version alignment |

## 11. Contribution

1. Fork the repository.
2. Create a feature branch.
3. Run `mvn clean verify` before submitting.
4. Submit a pull request.

## 12. License

This project is licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0).

---

<div align="center">

[Back to top](#readme-top) · [Issues](https://github.com/easy-4-java/opencode-spring-boot-starter/issues) · [Repository](https://github.com/easy-4-java/opencode-spring-boot-starter)

</div>
