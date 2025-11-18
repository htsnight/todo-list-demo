package org.example.todolistdemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.todolistdemo.todo.dto.TodoItemRequest;
import org.example.todolistdemo.todo.dto.TodoItemResponse;
import org.example.todolistdemo.todo.model.CategoryPreset;
import org.example.todolistdemo.todo.model.TodoPriority;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TodoListDemoApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void todoCrudFlow() throws Exception {
        TodoItemRequest request = new TodoItemRequest(
                "编写接口",
                "完成 CRUD 逻辑",
                null,
                CategoryPreset.WORK,
                TodoPriority.HIGH,
                LocalDate.now().plusDays(1));
        TodoItemResponse created = createTodo(request);

        TodoItemRequest another = new TodoItemRequest(
                "整理文档",
                "补充 README",
                "学习",
                null,
                TodoPriority.LOW,
                LocalDate.now());
        TodoItemResponse second = createTodo(another);

        String listJson = mockMvc.perform(get("/api/todos?sortBy=dueDate&direction=asc"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<TodoItemResponse> todos =
                objectMapper.readerForListOf(TodoItemResponse.class).readValue(listJson);
        assertThat(todos).extracting(TodoItemResponse::category).contains("工作", "学习");
        assertThat(todos.get(0).id()).isEqualTo(second.id());

        String toggleJson = mockMvc.perform(patch("/api/todos/{id}/toggle", created.id()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        TodoItemResponse toggled = objectMapper.readValue(toggleJson, TodoItemResponse.class);
        assertThat(toggled.completed()).isTrue();
        assertThat(toggled.priority()).isEqualTo(TodoPriority.HIGH);

        mockMvc.perform(delete("/api/todos/{id}", created.id()))
                .andExpect(status().isNoContent());

        String afterDeleteJson = mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<TodoItemResponse> afterDelete =
                objectMapper.readerForListOf(TodoItemResponse.class).readValue(afterDeleteJson);
        assertThat(afterDelete).extracting(TodoItemResponse::id).doesNotContain(created.id());

        mockMvc.perform(delete("/api/todos/{id}", second.id()))
                .andExpect(status().isNoContent());
    }

    private TodoItemResponse createTodo(TodoItemRequest request) throws Exception {
        String body = mockMvc.perform(
                        post("/api/todos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(body, TodoItemResponse.class);
    }
}
