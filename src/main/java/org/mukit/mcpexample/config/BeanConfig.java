package org.mukit.mcpexample.config;

import org.mukit.mcpexample.service.TodoListService;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BeanConfig {

    @Bean
    public List<ToolCallback> todoListTools(TodoListService todoListService) {
        return List.of(ToolCallbacks.from(todoListService));
    }
}
