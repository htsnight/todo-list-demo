## TODO List Demo

一个使用 Spring Boot + MySQL 构建的待办管理应用，支持分类、优先级、提醒策略（提前量、循环/每日/每周重复）以及静态 HTML 前端。

### 环境要求
- JDK 17
- Maven 3.8+
- MySQL 8.x（或修改配置指向兼容实例）

### 运行步骤
1. 克隆项目并进入根目录 `todoListDemo/`。
2. 根据本地环境修改 `src/main/resources/application.properties` 中的数据库 URL/用户名/密码（默认 `todo_demo` 库，`createDatabaseIfNotExist=true`）。
3. 安装依赖并启动：
   ```bash
   mvn clean package
   mvn spring-boot:run
   ```
4. 打开浏览器访问 `http://localhost:8080/` 使用前端界面；REST API 位于 `/api/todos`。

### 测试
运行 `mvn test`，会自动启用 `test` profile 并使用 H2 内存数据库，MockMvc 用例覆盖创建/排序/提醒等核心流程。

### 展示
![img.png](img.png)
