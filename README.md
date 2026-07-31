# opencode-spring-boot-starter

Spring Boot Starter for [OpenCode](https://opencode.ai) Java SDK。自动配置 `OpenCodeClient` 和 CLI 启动探测。

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>opencode-spring-boot-starter</artifactId>
    <version>2.7.x.20260615-SNAPSHOT</version>
</dependency>
```

### 2. 配置

```yaml
opencode:
  enabled: true
  http:
    server-url: http://localhost:4096
    username: opencode
    password: ${OPENCODE_SERVER_PASSWORD:}
    default-model: anthropic/claude-sonnet-4-5
    default-agent: build
  cli:
    executable: opencode
    timeout: 300
    startup-check-enabled: true
    fail-fast-on-unavailable: false
```

### 3. 使用

```java
@Service
@RequiredArgsConstructor
public class MyService {

    private final OpenCodeClient openCodeClient;

    public String ask(String question) {
        Session session = openCodeClient.createSession("my-task");
        PromptResult result = openCodeClient.prompt(session.getId(), question);
        return result.getTextContent();
    }
}
```

## 配置属性

属性前缀：`opencode`

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `enabled` | `boolean` | `true` | 启用/禁用 starter |
| `http.server-url` | `String` | `http://localhost:4096` | OpenCode Server 地址 |
| `http.username` | `String` | `opencode` | HTTP Basic Auth 用户名 |
| `http.password` | `String` | `null` | HTTP Basic Auth 密码 |
| `http.connect-timeout-millis` | `int` | `15000` | 连接超时 |
| `http.read-timeout-millis` | `int` | `300000` | 读超时 |
| `http.verify-ssl` | `boolean` | `true` | 校验 HTTPS 证书 |
| `http.default-model` | `String` | `null` | 默认模型（`provider/model`） |
| `http.default-agent` | `String` | `null` | 默认 agent |
| `cli.executable` | `String` | `opencode` | CLI 可执行文件路径 |
| `cli.timeout` | `int` | `300` | CLI 命令超时（秒） |
| `cli.probe-timeout-seconds` | `int` | `5` | CLI 探测超时（秒） |
| `cli.startup-check-enabled` | `boolean` | `true` | 启动时探测 CLI |
| `cli.fail-fast-on-unavailable` | `boolean` | `false` | CLI 不可用时启动失败 |

## 自动注册的 Bean

| Bean | 类型 | 条件 |
|------|------|------|
| `openCodeClient` | `OpenCodeClient` | `@ConditionalOnMissingBean`，`destroyMethod="close"` |
| `openCodeCliAvailabilityChecker` | `OpenCodeCliAvailabilityChecker` | `@ConditionalOnMissingBean` |
| `openCodeCliStartupChecker` | `OpenCodeCliStartupChecker` | `opencode.cli.startup-check-enabled=true` |

所有 Bean 均支持 `@ConditionalOnMissingBean`，可自定义覆盖。

## 前置条件

1. 安装 OpenCode：`curl -fsSL https://opencode.ai/install | bash`
2. 启动 Server：`opencode serve --port 4096`
3. 配置 provider API key：`opencode auth login`

## 发布

```bash
mvn clean deploy -DskipTests
```
