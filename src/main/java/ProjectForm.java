import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import tables.ProjectsTable;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(value = "/project_form")
public class ProjectForm extends HttpServlet {

    // Should use JSP

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession();

        String projectId = req.getParameter("projectId");
        String action = req.getParameter("action");
        try {
            Connection connection = (Connection) session.getAttribute("connection");
            ProjectsTable projectsTable = ProjectsTable.getInstance(connection);

            out.println("<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\">");

            if (action.equalsIgnoreCase("new")) {
                out.println("<title>Creating new Project</title></head><body>");
            }
            if (action.equalsIgnoreCase("edit")) {
                out.println("<title>Project Editing</title></head><body>");
                out.println("<h1>Editing fields of the Project:</h1>");
                Statement st = connection.createStatement();
                String query = String.format("SELECT * FROM %s WHERE id=%s;", projectsTable.getTableName(), projectId);
                ResultSet rs = st.executeQuery(query);
                out.println("<table style=\"width:50%\">");
                out.println(projectsTable.printTableHeaderHTML(connection));
                out.println("<tr>");
                ResultSetMetaData meta = rs.getMetaData();
                int columnsCount = meta.getColumnCount();
                while (rs.next()) {
                    for (int i = 1; i <= columnsCount; i++) {
                        String value = rs.getString(i);
                        if (rs.wasNull() || value.equalsIgnoreCase("null")) {
                            value = "";
                        }
                        out.println("<td>");
                        out.println(value);
                        out.println("</td>");
                    }
                }
                st.close();
                out.println("</tr></table><br><br>");
            }

            out.println("<form action=\"project_manager\" method=\"POST\">");
            out.println("<table style=\"border:hidden; width:50%\">");
            out.println("<caption>FILL IN THE FIELDS");
            if (action.equalsIgnoreCase("edit")) {
                out.println(" YOU WANT TO EDIT");
            }
            out.println("</caption><tbody>");

            out.println("<tr><td>Project name:</td>");
            out.println("<td><input name=\"name\" ");
            if (action.equalsIgnoreCase("new")) {
                out.println("required=\"required\" ");
            }
            out.println("maxlength=\"255\" size=\"80\"/></td></tr>");

            out.println("<tr><td>Project short name:</td>");
            out.println("<td><input name=\"shortName\" ");
            if (action.equalsIgnoreCase("new")) {
                out.println("required=\"required\" ");
            }
            out.println("maxlength=\"20\" size=\"80\"/></td></tr>");

            out.println("<tr><td>Description:</td>");
            out.println("<td><textarea name=\"description\" wrap=\"soft\" placeholder=\"optional field\" "
                    + "rows=\"5\" cols=\"70\"></textarea></td></tr>");
            out.println("</tbody></table><br><br>");

            out.println("<input type=\"hidden\" name=\"action\" value=\"" + action + "\">");
            if (action.equalsIgnoreCase("edit")) {
                out.println("<input type=\"hidden\" name=\"projectId\" value=\"" + projectId + "\">");
            }
            out.println("<input type=\"submit\" style=\"font-size:20px; width:25%\" value=\"Save\" />");
            out.println("<br><br><input type=\"reset\" style=\"font-size:20px; width:25%\" value=\"Reset\"/></form>");
            out.println("<br><form action=\"projects_page\" method=\"GET\">");
            out.println("<button style=\"font-size:20px; width:25%\">Cancel and return to Projects</button>");
            out.println("</form>");
            out.println("</body></html>");

        } catch (SQLException e) {
            out.println(e.getMessage());
        }
    }
}