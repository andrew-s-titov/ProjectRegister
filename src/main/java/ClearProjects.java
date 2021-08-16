import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import tables.ProjectsTable;
import tables.TasksTable;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(value = "/clear_projects")
public class ClearProjects extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession();
        Connection connection = (Connection) session.getAttribute("connection");
        try {
            ProjectsTable projectsTable = ProjectsTable.getInstance(connection);
            TasksTable tasksTable = TasksTable.getInstance(connection);
            projectsTable.clearTable(connection);
            tasksTable.clearTable(connection);
            resp.sendRedirect(req.getContextPath() + "/projects_page");

        } catch (SQLException e) {
            out.println(e.getMessage());
        }
    }
}
