# TODO List 项目说明文档

## 1. 技术选型
- **编程语言**：Java 17，理由语言特性成熟、生态完善，配合 Spring Boot 能快速搭建稳定的 REST 服务；团队常用。  
- **框架/库**：Spring Boot 3，理由：单体结构即可覆盖 API + 数据访问 + 静态资源发布。  
              前端静态页（原生 HTML + Fetch）：需求以功能展示为主，用轻量页面即可；若后续需要组件化，可无缝迁移到 React/Vue。
- **数据库/存储**：MySQL 8，理由：可靠、熟悉，支持事务与复杂查询；此外，通过 Spring Data JPA 映射方便。  
- 替代方案对比：为何不选 MongoDB，理由：提醒/分类/排序需求在关系模型中表达更自然，且本人有mysql的经验 

## 2. 项目结构设计
- 整体架构说明：后端（Spring Boot 单体）：暴露 REST API（/api/todos）和元数据接口（/api/todos/meta/categories），负责任务 CRUD、提醒策略计算和持久化。
  数据库（MySQL）：通过 Spring Data JPA 映射 TodoItem 实体，存储任务信息、分类、优先级、提醒配置等。
  前端（静态 HTML + Fetch API）：部署在 src/main/resources/static/index.html，直接调用后端 API，提供创建、排序、提醒配置、描述展示等交互。。 
- 目录结构：  
  ```
  todoListDemo/
  ├── docs/                  # 方案/迭代说明
  ├── src/
  │   ├── main/
  │   │   ├── java/org/example/todolistdemo/
  │   │   │   ├── TodoListDemoApplication.java
  │   │   │   └── todo/      # 业务模块
  │   │   │       ├── controller/   # TodoController、元数据控制器
  │   │   │       ├── dto/          # 请求/响应对象
  │   │   │       ├── exception/    # 全局异常处理
  │   │   │       ├── model/        # 实体、枚举（TodoItem、ReminderRecurrence 等）
  │   │   │       ├── repository/   # JPA Repository
  │   │   │       └── service/      # 业务逻辑、提醒计算
  │   │   └── resources/
  │   │       ├── application.properties
  │   │       └── static/index.html  # 前端页面
  │   └── test/
  │       └── java/.../TodoListDemoApplicationTests.java
  ├── pom.xml
  └── ...（构建脚本等）
  ```
## 3. 需求细节与决策
- 描述是否必填？如何处理空输入？  
  1）标题为必填，后端通过 @NotBlank 校验，前端也提供必填提示。
  2）描述为可选：若为空，后端存 null；前端列表中不显示提示，确保界面简洁；有描述时用悬浮提示展示完整内容。
- 已完成的任务在 UI 或 CLI 中如何显示？  
  1）列表中完成的任务行会变灰并加删除线，右侧按钮文本变为“标记未完成”；未完成的按钮显示“标记完成”。
  2)删除按钮始终可用，状态切换后自动刷新列表与提醒调度。
- 任务排序逻辑。
  1)默认按创建时间（createdAt desc）返回，便于看到最新任务。
  2)用户可在前端选择 dueDate、priority、createdAt、updatedAt 任意字段，并指定升/降序；后端通过 Sort.by 动态映射。
- 扩展功能（提醒）设计思路。
  TodoItem 支持提醒时间、多个提前量（偏移）、以及三类重复策略（循环间隔、每日固定、每周固定）。
  后端 upcomingReminders(minutes) 会综合绝对时间、偏移与重复规则，返回未来窗口内需要通知的任务；
  前端轮询 + Notification API + 自定义 Toast 提供即时提醒。
  元数据接口 /api/todos/meta/categories 提供预设分类，前端也允许自定义分类；同类任务可按参数过滤并在 UI 中分组展示。

## 4. AI 使用说明
- 是否使用 AI 工具？（ChatGPT / Copilot / Cursor / 其他）
  在整个开发过程中主要借助 Cursor（整合了 ChatGPT）作为“对话式辅助”。
  它帮助我快速验证思路、生成初稿和定位问题，但最终实现均由我审查、调优并落地。
- 使用 AI 的环节：  
  - 代码片段生成：例如初版的 TodoController、提醒策略计算等，我会先描述接口需求，让 AI 给出示例，再根据项目实际（数据库字段、异常处理规范）进行修改和整合。
  - Bug 定位：当提醒逻辑或前端交互出现边缘问题时，会向 AI 描述症状，让它协助排查可能原因，再结合日志/测试验证 
- 整体而言，AI 帮助我提高迭代效率，但我始终对需求理解、技术决策和最终质量负责。 

## 5. 运行与测试方式
- 本地运行方式：
  1）安装 JDK 17 与 Maven 3.8+。
  2）启动 MySQL 8（或调整 application.properties 指向已有实例），确保账号具备建表权限（账户-密码需修改为你本地数据库）。
  3）在项目根目录执行：
  mvn clean package     mvn spring-boot:run
  4）启动后访问 http://localhost:8080/ 即可体验前端页面；REST API 位于 /api/todos。
- 已测试过的环境。  
  Windows 11 + JDK 17 + MySQL 8.0.42（本地开发环境）。
- 已知问题与不足。
  前端提醒功能依赖浏览器前台运行，页面关闭后本地提醒失效（可通过后端推送或第三方通知补强）。

## 6. 总结与反思
- 如果有更多时间，你会如何改进？  
  1）后端提醒通道：接入 Spring Scheduling / 消息队列，支持离线通知（邮件、WebPush），解决浏览器关闭后提醒失效的问题。
  2）多端同步：引入用户体系与 token，支持多设备协作、冲突解决；或提供导入/导出功能，便于切换环境。
  3）前端组件化：若需求继续增长，可迁移到 React/Vue + Vite，复用现有 API，同时加入状态管理、路由等。
- 你觉得这个实现的最大亮点是什么？
  1）提醒策略灵活但易用：支持单次、提前量、多种重复方式，同时通过前端 UI 折叠/预设让配置过程足够轻量。
  2）完整的迭代文档与自动化测试：从 PLAN、MockMvc 集成测试到交互说明，展现了需求理解与验证闭环，利于评审跟踪。
  3）分层清晰，可扩展性好：后端模块化（controller/service/repository/dto），前端与后端通过 REST 解耦，后续无论扩展前端还是切换数据库都比较平滑。