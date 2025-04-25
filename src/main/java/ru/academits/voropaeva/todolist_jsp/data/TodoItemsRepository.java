package ru.academits.voropaeva.todolist_jsp.data;

import java.util.List;

public interface TodoItemsRepository {
    List<TodoItem> getAll();

    void create(TodoItem item);

    void update(TodoItem item);

    void delete(int itemId);
}
