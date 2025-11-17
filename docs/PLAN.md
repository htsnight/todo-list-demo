## 笔试解题思路（迭代计划）

### 第一天实现进度：
1. **后端开发环节搭建**：SpringBoot3(Java17)、数据持久化：Mysql-8.0.42、配置了Mysql连接、JPA自动建表和Sql日志等选项
2. **model层TodoItem实体目前包括**:id/title/description/completed/createdAt/updatedAt，通过 @PrePersist/@PreUpdate 自动维护时间戳，还未加入分类、优先级、截止日期等字段
3. **rest接口层**：TodoController实现基本的列表、创建、完成状态转换、删除功能
4. **业务与持久化**：TodoService 完成了 基本CRUD 核心逻辑、TodoItemRepository 继承 JpaRepository，提供数据访问能力。
5. **异常处理**：GlobalExceptionHandler 针对找不到任务和参数校验失败返回统一的 ProblemDetail JSON，以及自定义异常：表示根据ID找不到任务；

测试结果：通过PostMan检查创建、查询、删除三类接口的相应情况及功能正确，测试通过；
![img.png](img.png)
![img_1.png](img_1.png)
修改完成状态：
![img_2.png](img_2.png)
