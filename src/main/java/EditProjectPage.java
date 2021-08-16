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

@WebServlet(value = "/edit_project_page")
public class EditProjectPage extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession();
        try {
            Connection connection = (Connection) session.getAttribute("connection");
            ProjectsTable projectsTable = ProjectsTable.getInstance(connection);
            String projectId = req.getParameter("edit");
            out.println("<html><body>");
            out.println("<h2>Select action for this Project:</h2>");

            Statement st = connection.createStatement();
            String query = String.format("SELECT * FROM %s WHERE id=%s;", projectsTable.getTableName(), projectId);
            ResultSet rs = st.executeQuery(query);

            ResultSetMetaData meta = rs.getMetaData();
            int columnsCount = meta.getColumnCount();

            out.println("<table style=\"width:50%\">");
            out.println(projectsTable.printTableHeaderHTML(connection));

            out.println("<tr>");
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
            out.println("</tr></table><br><br>");

            out.println("<form action=\"project_form\" method=\"GET\">");
            out.println("<input type=\"hidden\" name=\"action\" value=\"edit\">");
            out.println("<input type=\"hidden\" name=\"projectId\" value=\"");
            out.println(projectId);
            out.println("\">");
            out.println("<input type=\"submit\" style=\"font-size:20px; width:25%\" value=\"Edit\" />");
            out.println("</form>");

            out.println("<form action=\"project_manager\" method=\"POST\">");
            out.println("<input type=\"hidden\" name=\"action\" value=\"delete\">");
            out.println("<input type=\"hidden\" name=\"projectId\" value=\"");
            out.println(projectId);
            out.println("\">");
            out.println("<input type=\"submit\" style=\"font-size:20px; width:25%\" value=\"Delete\" />");
            out.println("</form>");
            out.println("<form action=\"projects_page\" method=\"GET\">");
            out.println("<button style=\"font-size:20px; width:25%\" autofocus>Cancel</button></form>");
            out.println("</body></html>");

            st.close();
        } catch (SQLException e) {
            out.println(e.getMessage());
        }
    }
}