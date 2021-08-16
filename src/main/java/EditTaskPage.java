import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import tables.TasksTable;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(value = "/edit_task_page")
public class EditTaskPage extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession();
        try {
            Connection connection = (Connection) session.getAttribute("connection");
            TasksTable tasksTable = TasksTable.getInstance(connection);
            String taskId = req.getParameter("edit");
            out.println("<html><body>");
            out.println("<h2>Select action for this Task:</h2>");

            Statement st = connection.createStatement();
            String query = String.format("SELECT * FROM %s WHERE id=%s;", tasksTable.getTableName(), taskId);
            ResultSet rs = st.executeQuery(query);

            ResultSetMetaData meta = rs.getMetaData();
            int columnsCount = meta.getColumnCount();

            out.println("<table style=\"width:50%\">");
            out.println(tasksTable.printTableHeaderHTML(connection));

            out.println("<tr>");
            while (rs.next()) {
                for (int i = 1; i <= columnsCount; i++) {
                    out.println("<td>");
                    out.println(rs.getString(i));
                    out.println("</td>");
                }
            }
            out.println("</tr></table>");
            out.println("<br><h3>This won't affect the related Project</h3><br>");

            out.println("<form action=\"task_form\" method=\"GET\">");
            out.println("<input type=\"hidden\" name=\"action\" value=\"edit\">");
            out.println("<input type=\"hidden\" name=\"taskId\" value=\"");
            out.println(taskId);
            out.println("\">");
            out.println("<input type=\"submit\" style=\"font-size:20px; width:25%\" value=\"Edit\" />");
            out.println("</form>");

            out.println("<form action=\"task_manager\" method=\"POST\">");
            out.println("<input type=\"hidden\" name=\"action\" value=\"delete\">");
            out.println("<input type=\"hidden\" name=\"taskId\" value=\"");
            out.println(taskId);
            out.println("\">");
            out.println("<input type=\"submit\" style=\"font-size:20px; width:25%\" value=\"Delete\" />");
            out.println("</form>");
            out.println("<form action=\"tasks_page\" method=\"GET\">");
            out.println("<button style=\"font-size:20px; width:25%\" autofocus>Cancel</button></form>");
            out.println("</body></html>");

            st.close();
        } catch (SQLException e) {
            out.println(e.getMessage());
        }
    }
}