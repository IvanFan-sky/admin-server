# admin-server

后台管理服务模块。

## 技术栈

*   **框架:** Spring Boot 3.2.x
*   **语言:** Java 17
*   **数据库:** MySQL
*   **ORM:** MyBatis-Plus
*   **分页:** PageHelper
*   **对象映射:** MapStruct
*   **API 文档:** Springdoc OpenAPI (Swagger 3)
*   **缓存:** Redis (via Spring Cache)
*   **安全:** Spring Security (待集成 JWT)
*   **构建工具:** Maven
*   **日志:** SLF4J + Logback (默认)
*   **校验:** Jakarta Bean Validation
*   **监控:** Spring Boot Actuator

## 主要模块

*   用户管理
*   角色管理 (待实现)
*   权限管理 (待实现)
*   登录认证 (待实现)
*   操作日志 (待实现)
*   ... (根据 `requirements` 目录下的文档)

## 配置说明

*   核心配置文件: `src/main/resources/application.yml`
*   环境特定配置: `src/main/resources/application-{profile}.yml` (如 `dev`, `test`, `pro`)
*   当前激活环境通过 `spring.profiles.active` 在 `application.yml` 中设置。
*   **重要:** 首次运行时，请务必修改 `application.yml` (或对应环境的 profile 文件) 中的数据库和 Redis 连接信息。

## 运行

1.  **环境准备:**
    *   安装 JDK 17
    *   安装 Maven
    *   安装并启动 MySQL
    *   安装并启动 Redis
    *   创建对应的数据库 (例如 `admin_dev`) 并执行初始化脚本 (如果需要)。
2.  **编译打包:**
    ```bash
    mvn clean package
    ```
3.  **运行:**
    ```bash
    java -jar target/admin-server-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
    ```
    (将 `dev` 替换为你想要激活的环境 profile)

## API 文档

服务启动后，可以通过以下路径访问 Swagger UI:

`http://localhost:8080/swagger-ui.html` (端口号根据 `server.port` 配置)