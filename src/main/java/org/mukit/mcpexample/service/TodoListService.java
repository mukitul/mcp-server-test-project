package org.mukit.mcpexample.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class TodoListService {

    private final Path todoListFile;

    private static final String TODO_LIST_FILENAME = "todo_list.txt";
    private static final String DEFAULT_DIRECTORY = "user.dir";

    public TodoListService(@Value("${todo.dir:}") String todoDir) {
        String baseDir = (todoDir == null || todoDir.isBlank()) ? System.getProperty(DEFAULT_DIRECTORY) : todoDir;

        Path dirPath = Paths.get(baseDir);
        this.todoListFile = dirPath.resolve(TODO_LIST_FILENAME);

        try {
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            if (!Files.exists(todoListFile)) {
                Files.createFile(todoListFile);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Failed to initialize todo-list file: " + ex.getMessage(), ex);
        }
    }

    @Tool(name = "readTodoList", description = "Read the entire TODO List")
    public String readTodoList() {
        try {
            if (!Files.exists(todoListFile)) {
                return "No TODO list found";
            }
            return Files.readString(todoListFile);
        } catch (Exception ex) {
            return "An error occurred while reading the file | Exception: " + ex.getMessage();
        }
    }

    @Tool(name = "addTodo", description = "Add a new task to the TODO List for a given day")
    public String addTodo(String day, String task) {
        try {
            if (!Files.exists(todoListFile)) {
                throw new Exception("File does not exist");
            }

            List<String> lines = Files.readAllLines(todoListFile);
            int index = findDaySectionIndex(lines, day);
            if (index == -1) {
                lines.add("=== TODOs for " + day + " ===");
                lines.add("- " + task);
            } else {
                while (++index < lines.size() && !lines.get(index).startsWith("===")) {
                }
                lines.add(index, "- " + task);
            }
            Files.write(todoListFile, lines);
            return "Added task to " + day + ": " + task;

        } catch (Exception ex) {
            return "An error occurred while writing the file | Exception: " + ex.getMessage();
        }
    }

    private int findDaySectionIndex(List<String> lines, String day) {
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).contains("=== TODOs for " + day)) return i;
        }
        return -1;
    }
}
