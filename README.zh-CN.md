<a id="readme-top"></a>

<div align="center">

# opencode-spring-boot-starter

**Spring Boot Starter，集成 opencode**

[![Maven Central](https://img.shields.io/maven-central/v/io.github.easy4j/opencode-spring-boot-starter)](https://github.com/easy-4-java/opencode-spring-boot-starter)
[![Java](https://img.shields.io/badge/Java-21-orange)](#3-运行要求与兼容性)
[![License](https://img.shields.io/badge/license-Apache-2.0-green)](https://www.apache.org/licenses/LICENSE-2.0)

[English](./README.md) | [简体中文](./README.zh-CN.md)

[项目定位](#1-项目定位) · [核心能力](#2-核心能力) ·
[引入依赖](#5-引入依赖) · [快速开始](#6-快速开始) ·
[配置参考](#7-配置参考) · [版本线](#8-版本线与兼容性) ·
[构建测试](#9-构建与测试) · [许可证](#12-许可证)

</div>

---

> **当前版本**：`4.1.x.20260730-SNAPSHOT`<br>
> **JDK 基线**：`21`<br>
> **Group ID**：`io.github.easy4j`<br>
> **Artifact ID**：`opencode-spring-boot-starter`<br>
> **许可证**：Apache License 2.0<br>

## 1. 项目定位

**opencode-spring-boot-starter** 是一个面向 使用 opencode 的应用 的 Spring Boot Starter，用于将 **opencode** 集成到 Spring Boot 应用中。它提供自动装配、属性绑定与开箱即用的 Bean，使应用以最小配置即可使用 opencode 的全部能力。

| 维度 | 说明 |
|---|---|
| 类型 | Spring Boot Starter |
| 消费方 | 使用 opencode 的 Spring Boot 应用 |
| 核心能力 | 自动装配、属性绑定、开箱即用的 opencode Bean |
| JDK | `21` |
| 坐标 | `io.github.easy4j:opencode-spring-boot-starter:4.1.x.20260730-SNAPSHOT` |
| 配置前缀 | `opencode` |

## 2. 核心能力

| 能力 | 状态 | 说明 |
|---|:---:|---|
| 自动装配 | ✅ 稳定 | 自动注册 opencode 相关 Bean |
| 属性绑定 | ✅ 稳定 | 绑定 `opencode.*` 到 `OpenCodeProperties` |
| `OpenCodeClient` Bean | ✅ 稳定 | 通过 OpenCodeAutoConfiguration 自动注册 |

## 3. 运行要求与兼容性

| 依赖 | 最低版本 | 证据来源 |
|---|---:|---|
| JDK | `21` | `pom.xml` |
| Spring Boot | `4.1.2` | `pom.xml` parent |
| Maven | `4.0.0-rc-5` | Maven Enforcer |


### 4.1.x 的 Jackson 3 迁移边界

本分支使用 Spring Boot 4.1.2、Java 21、Maven 4.0.0-rc-5，以及
`io.github.easy4j:opencode-java-sdk:3.0.x.20260630-SNAPSHOT`。
公开方法 `OpenCodeAutoConfiguration.openCodeObjectMapper()` 的返回类型和
`openCodeClient(..., ObjectMapper, ...)` 的参数类型已从 Jackson 2 的
`com.fasterxml.jackson.databind.ObjectMapper` 改为 Jackson 3 的
`tools.jackson.databind.ObjectMapper`。这是源码和二进制 API 边界变化：
直接调用方需要重新编译，自定义 mapper Bean、导入、模块及 builder 配置需要迁移。
Jackson 2 Bean 不满足 Jackson 3 的注入/自动装配回退条件；存在多个 Jackson 3
mapper 时，应通过 Spring 常规候选选择机制（例如 `@Primary`）明确选择。

默认 mapper 通过 `JsonMapper.builder()` 创建并关闭 `FAIL_ON_UNKNOWN_PROPERTIES`。
应用提供的 Jackson 3 mapper 将原样替代默认 Bean，其未知字段和命名策略由应用负责。
SDK DTO 的 Jackson 注解仍位于 `com.fasterxml.jackson.annotation`，不要机械替换该包名。

`OpenCodeJacksonContractTest` 使用真实工厂产物验证 JSON 处理、SDK Session
协议字段命名/往返转换，以及自定义 mapper 回退和实例一致性，不调用远端 API 或 CLI。
测试输出记录实际加载 SDK JAR 的内嵌 GAV、SHA-256、白名单构建元数据和构造器二进制描述符。
可变 SNAPSHOT 坐标或当前 SDK 仓库 HEAD 均不能证明这些字节对应的源码提交。
这些窄合同测试不代表与此前无法解析的非 SNAPSHOT 坐标行为完全等价，也不覆盖所有 DTO 或远端接口。

## 4. 自动装配

Starter 自动装配以下 Bean：

| Bean | 条件 | 缺失时行为 |
|---|---|---|
| `OpenCodeClient` | classpath + property | 不创建 |
| `OpenCodeCliAvailabilityChecker` | classpath + property | 不创建 |
| `OpenCodeCliExecutor` | classpath + property | 不创建 |
| `OkHttpClient` | classpath + property | 不创建 |
| `ObjectMapper` | classpath + property | 不创建 |

自动装配注册：

- `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`（Spring Boot 2.7+ / 3.x / 4.x）
- `META-INF/spring.factories`（Spring Boot 2.x 传统方式）

## 5. 引入依赖

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>opencode-spring-boot-starter</artifactId>
    <version>4.1.x.20260730-SNAPSHOT</version>
</dependency>
```

本分支在 POM 中显式指定 SDK 版本：

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>opencode-java-sdk</artifactId>
    <version>3.0.x.20260630-SNAPSHOT</version>
</dependency>
```

## 6. 快速开始

### 6.1 引入依赖

在 `pom.xml` 中添加上述依赖。

### 6.2 配置

```yaml
opencode:
  enabled: true
```

### 6.3 使用 Bean

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

在业务代码中注入自动装配的 Bean：

```java
@Autowired
private OpenCodeClient openCodeClient;
```

## 7. 配置参考

### 7.1 配置前缀

`opencode`

### 7.2 配置项

| 属性 | 类型 | 默认值 | 必填 | 说明 | 敏感 |
|---|---|---|:---:|---|:---:|
| `opencode.enabled` | boolean | `true` | 否 | 是否启用 Starter | 否 |
<!-- 更多属性见下方 -->

## 8. 版本线与兼容性

| 分支 | JDK | Spring Boot | 组件版本 | 状态 |
|---|---:|---:|---|:---:|
| `2.3.x` / `2.7.x` | `8+` | 2.3.x / 2.7.x | `1.0.x` | 维护中 |
| `3.0.x` ~ `3.5.x` | `17` | 3.x | `2.0.x` | 维护中 |
| `4.0.x` / `4.1.x` | `17+` | 4.x | `3.0.x` | 活跃开发 |

## 9. 构建与测试

```bash
mvn clean verify
```

## 10. 排障

| 症状 | 诊断 | 解决 |
|---|---|---|
| Bean 未创建 | 查看自动装配报告 | 确认 `opencode.enabled=true` 与 classpath |
| `ClassNotFoundException` | 缺少依赖 | 引入对应模块 |
| 版本冲突 | `mvn dependency:tree` | 使用 BOM 统一版本 |

## 11. 贡献

1. Fork 本仓库。
2. 创建特性分支。
3. 提交前运行 `mvn clean verify`。
4. 提交 Pull Request。

## 12. 许可证

本项目采用 [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0) 许可证。

---

<div align="center">

[返回顶部](#readme-top) · [问题反馈](https://github.com/easy-4-java/opencode-spring-boot-starter/issues) · [仓库地址](https://github.com/easy-4-java/opencode-spring-boot-starter)

</div>
