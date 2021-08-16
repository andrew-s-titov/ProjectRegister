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

@WebServlet(value = "/tasks_page")
public class TasksPage extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession();
        Connection connection = (Connection) session.getAttribute("connection");
        try {
            TasksTable tasksTable = tables.TasksTable.getInstance(connection);
            out.println(tasksTable.printTableHTML(connection, "edit_task_page", "edit"));
            out.println(generateHTML());

        } catch (SQLException e) {
            out.println(e.getMessage());
        }
    }

    public String generateHTML() {
        StringBuilder str = new StringBuilder();
        str.append("<html><body>");
        str.append("<form action=\"task_form\">");
        str.append("<button style=\"font-size:20px; width:25%\" name=\"action\" value=\"new\">Add new Task</button>");
        str.append("</form>");
        str.append("<form action=\"clear_tasks_confirm\">");
        str.append("<button style=\"font-size:20px; width:25%\">Clear all Tasks</button>");
        str.append("</form>");
        str.append("<form action=\"projects_page\">");
        str.append("<button style=\"font-size:20px; width:25%\">Return to Projects</button>");
        str.append("</form></body></html>");
        return str.toString();
    }
}