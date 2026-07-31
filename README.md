# opencode-spring-boot-starter

Spring Boot Starter for [OpenCode](https://opencode.ai) Java SDK。自动配置 `OpenCodeClient`、`OpenCodeClientConfig` 和 CLI 启动探测。

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>opencode-spring-boot-starter</artifactId>
    <version>1.0.x.20260605-SNAPSHOT</version>
</dependency>
```

### 2. 配置

```yaml
opencode:
  server:
    enabled: true
    server-url: http://localhost:4096
    password: ${OPENCODE_SERVER_PASSWORD:}
    default-model: anthropic/claude-sonnet-4-5
    default-agent: build
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

属性前缀：`opencode.server`

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `enabled` | `boolean` | `true` | 启用/禁用 starter |
| `server-url` | `String` | `http://localhost:4096` | OpenCode Server 地址 |
| `username` | `String` | `opencode` | HTTP Basic Auth 用户名 |
| `password` | `String` | `null` | HTTP Basic Auth 密码 |
| `connect-timeout-millis` | `int` | `15000` | 连接超时 |
| `read-timeout-millis` | `int` | `300000` | 读超时 |
| `verify-ssl` | `boolean` | `true` | 校验 HTTPS 证书 |
| `cli-executable` | `String` | `opencode` | CLI 可执行文件路径 |
| `cli-timeout-seconds` | `int` | `300` | CLI 命令超时 |
| `startup-check-enabled` | `boolean` | `true` | 启动时探测 CLI |
| `fail-fast-on-unavailable` | `boolean` | `false` | CLI 不可用时启动失败 |
| `default-model` | `String` | `null` | 默认模型（`provider/model`） |
| `default-agent` | `String` | `null` | 默认 agent |

## 自动注册的 Bean

| Bean | 类型 | 条件 |
|------|------|------|
| `openCodeClientConfig` | `OpenCodeClientConfig` | `@ConditionalOnMissingBean` |
| `openCodeClient` | `OpenCodeClient` | `@ConditionalOnMissingBean`，`destroyMethod="close"` |
| `openCodeCliStartupChecker` | `OpenCodeCliStartupChecker` | `startup-check-enabled=true` |

所有 Bean 均支持 `@ConditionalOnMissingBean`，可自定义覆盖。

## 与 agent-invoker 集成

在 cloud-agents 项目中，通过 `OpenCodeAgentInvoker`（`providerCode="opencode"`）与 `AgentInvokerRouter` 集成：

```yaml
opencode:
  enabled: true
  server-url: http://localhost:4096
  agent: build
  model: anthropic/claude-sonnet-4-5
```

路由：`AgentInvokerRouter.route("opencode")` → `OpenCodeAgentInvoker`

## 前置条件

1. 安装 OpenCode：`curl -fsSL https://opencode.ai/install | bash`
2. 启动 Server：`opencode serve --port 4096`
3. 配置 provider API key：`opencode auth login`

## 发布

```bash
mvn clean deploy -DskipTests
```
