import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import tables.ProjectsTable;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(value = "/projects_page")
public class ProjectsPage extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession();
        Connection connection = (Connection) session.getAttribute("connection");
        try {
            ProjectsTable projectsTable = ProjectsTable.getInstance(connection);
            out.println(projectsTable.printTableHTML(connection, "edit_project_page", "edit"));
            out.println(generateHTML());

        } catch (SQLException e) {
            out.println(e.getMessage());
        }
    }

    private String generateHTML() {
        StringBuilder str = new StringBuilder();
        str.append("<html><body>");
        str.append("<form action=\"project_form\">");
        str.append("<button style=\"font-size:20px; width:25%\" name=\"action\" value=\"new\">Add new Project</button>");
        str.append("</form>");
        str.append("<form action=\"tasks_page\">");
        str.append("<button style=\"font-size:20px; width:25%\">Open Tasks</button>");
        str.append("</form>");
        str.append("<form action=\"clear_projects_confirm\">");
        str.append("<button style=\"font-size:20px; width:25%\">Clear Projects and Tasks</button>");
        str.append("</form>");
        str.append("<form action=\"exit\">");
        str.append("<button style=\"font-size:20px; width:25%; background-color:darkorange\">Exit ProjectRegister</button>");
        str.append("</form></body></html>");
        return str.toString();
    }
}
