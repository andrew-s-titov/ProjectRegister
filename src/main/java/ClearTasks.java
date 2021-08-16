import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import tables.TasksTable;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(value = "/clear_tasks")
public class ClearTasks extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession();
        Connection connection = (Connection) session.getAttribute("connection");
        try {
            TasksTable tasksTable = TasksTable.getInstance(connection);
            tasksTable.clearTable(connection);
            resp.sendRedirect(req.getContextPath() + "/tasks_page");

        } catch (SQLException e) {
            out.println(e.getMessage());
        }
    }
}
