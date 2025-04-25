package ru.academits.voropaeva.todolist_jsp.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ru.academits.voropaeva.todolist_jsp.data.TodoItem;
import ru.academits.voropaeva.todolist_jsp.data.TodoItemsInMemoryRepository;
import ru.academits.voropaeva.todolist_jsp.data.TodoItemsRepository;

import java.io.IOException;
import java.util.List;

@WebServlet("")
public class TodoListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        String createError = session.getAttribute("createError") != null
                ? session.getAttribute("createError").toString()
                : "";
        session.removeAttribute("createError");
        req.setAttribute("createError", createError);

        TodoItemsRepository todoItemsRepository = new TodoItemsInMemoryRepository();
        List<TodoItem> todoItems = todoItemsRepository.getAll();
        req.setAttribute("todoItems", todoItems);

        for (TodoItem todoItem : todoItems) {
            String updateError = session.getAttribute("updateError_" + todoItem.getId()) != null
                    ? session.getAttribute("updateError_" + todoItem.getId()).toString()
                    : "";
            session.removeAttribute("updateError_" + todoItem.getId());
            req.setAttribute("updateError_" + todoItem.getId(), updateError);
        }

        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getParameter("action");

        if (action == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing action parameter");

            return;
        }

        switch (action) {
            case "create" -> {
                String text = req.getParameter("text").trim();

                if (text.isEmpty()) {
                    HttpSession session = req.getSession();
                    session.setAttribute("createError", "Text must be not empty");
                } else {
                    TodoItemsRepository todoItemsRepository = new TodoItemsInMemoryRepository();
                    todoItemsRepository.create(new TodoItem(text));
                }
            }
            case "update" -> {
                try {
                    int id = validateId(req, resp);

                    String text = req.getParameter("text").trim();

                    if (text.isEmpty()) {
                        HttpSession session = req.getSession();
                        session.setAttribute("updateError_" + id, "Text must be not empty");
                    } else {
                        TodoItemsRepository todoItemsRepository = new TodoItemsInMemoryRepository();
                        todoItemsRepository.update(new TodoItem(id, text));
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
            case "delete" -> {
                try {
                    int id = validateId(req, resp);
                    TodoItemsRepository todoItemsRepository = new TodoItemsInMemoryRepository();
                    todoItemsRepository.delete(id);
                } catch (IllegalArgumentException ignored) {
                }
            }
            default -> resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action parameter");
        }

        resp.sendRedirect(getServletContext().getContextPath() + "/");
    }

    private int validateId(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idString = req.getParameter("id");

        if (idString == null) {
            String errorMessage = "Missing id parameter";

            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        try {
            return Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            String errorMessage = "Invalid id parameter";

            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, errorMessage);
            throw new NumberFormatException(errorMessage);
        }
    }
}