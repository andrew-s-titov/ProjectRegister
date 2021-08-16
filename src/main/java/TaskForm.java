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
import java.sql.*;
import java.util.Set;

@WebServlet(value = "/task_form")
public class TaskForm extends HttpServlet {

    // Should use JSP

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession();
        try {
            Connection connection = (Connection) session.getAttribute("connection");

            ProjectsTable projectsTable = ProjectsTable.getInstance(connection);
            TasksTable tasksTable = TasksTable.getInstance(connection);

            String taskId = req.getParameter("taskId");
            String action = req.getParameter("action");

            out.println("<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\">");

            if (action.equalsIgnoreCase("new")) {
                out.println("<title>Creating new Task</title></head><body>");
            }
            if (action.equalsIgnoreCase("edit")) {
                out.println("<title>EDIT TASK:</title></head><body>");
                out.println("<h1>Editing fields of the Task:</h1>");
                Statement st = connection.createStatement();
                String query = String.format("SELECT * FROM %s WHERE id=%s;", tasksTable.getTableName(), taskId);
                ResultSet rs = st.executeQuery(query);
                out.println("<table style=\"width:50%\">");
                out.println(tasksTable.printTableHeaderHTML(connection));
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

            out.println("<form action=\"task_manager\" method=\"POST\">");
            out.println("<table style=\"border:hidden; width:50%\">");
            out.println("<caption>FILL IN THE FIELDS");
            if (action.equalsIgnoreCase("edit")) {
                out.println(" YOU WANT TO EDIT");
            }
            out.println("</caption><tbody>");

            if (action.equalsIgnoreCase("new")) {
                out.println("<tr><td>Project short name:</td><td><select name=\"project\" required=\"required\">)");
                Set<String> namesSet = projectsTable.getProjectsShortNamesSet();
                for (String s : namesSet) {
                    out.println("<option>");
                    out.println(s);
                    out.println("</option>");
                }
                out.println("</select></td></tr>");
            }

            out.println("<tr><td>Task name:</td>");
            out.println("<td><input type=\"text\" name=\"name\" size=\"50\"");

            if (action.equalsIgnoreCase("new")) {
                out.println("required=\"required\" ");
            }

            out.println("maxlength=\"100\"/></td></tr>");
            out.println("<tr><td>Estimated time (hours):</td>");
            out.println("<td><input type=\"number\" name=\"hours\" min=\"0\" max=\"8760\"/></td></tr>");
            out.println("<tr><td>Start date:</td><td>");
            out.println("<input type=\"date\" name=\"startDate\" min=\"2000-01-01\" max=\"2099-12-31\"");

            if (action.equalsIgnoreCase("new")) {
                out.println(" required=\"required\"");
            }

            out.println("/></td></tr>");
            out.println("<tr><td>Finish date:</td><td>");
            out.println("<input type=\"date\" name=\"finishDate\" min=\"2000-01-01\" max=\"2099-12-31\"");

            if (action.equalsIgnoreCase("new")) {
                out.println(" required=\"required\"");
            }

            out.println("/></td></tr>");
            out.println("<tr><td>Status:</td><td><select name=\"status\"");

            if (action.equalsIgnoreCase("new")) {
                out.println(" required=\"required\"");
            }

            out.println(">");

            String[] statuses = tasksTable.getStatuses();
            if (action.equalsIgnoreCase("edit")) {
                out.println("<option></option>");
            }
            for (String s : statuses) {
                out.println("<option>");
                out.println(s);
                out.println("</option>");
            }

            out.println("</select></td></tr></tbody></table><br><br>");

            out.println("<input type=\"hidden\" name=\"action\" value=\"" + action + "\">");
            if (action.equalsIgnoreCase("edit")) {
                out.println("<input type=\"hidden\" name=\"taskId\" value=\"" + taskId + "\">");
            }
            out.println("<input type=\"submit\" style=\"font-size:20px; width:25%\" value=\"Save\" />");
            out.println("<br><br><input type=\"reset\" style=\"font-size:20px; width:25%\" value=\"Reset\"/></form>");
            out.println("<br><form action=\"tasks_page\" method=\"GET\">");
            out.println("<button style=\"font-size:20px; width:25%\">Cancel and return to Tasks</button>");
            out.println("</form>");
            out.println("</body></html>");

        } catch (SQLException e) {
            out.println(e.getMessage());
        }
    }
}