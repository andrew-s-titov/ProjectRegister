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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet(value = "/task_manager")
public class TaskManager extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession();

        String projectName = req.getParameter("project");
        String name = req.getParameter("name");
        String hours = req.getParameter("hours");
        String startDate = req.getParameter("startDate");
        String finishDate = req.getParameter("finishDate");
        String status = req.getParameter("status");

        String action = req.getParameter("action");

        try {
            Connection connection = (Connection) session.getAttribute("connection");
            TasksTable tasksTable = TasksTable.getInstance(connection);

            if (action.equalsIgnoreCase("new")) {
                if (!name.isBlank() && !startDate.isBlank() && !finishDate.isBlank() && !status.isBlank()
                        && startDate.matches("\\d{4}-\\d{2}-\\d{2}")
                        && startDate.matches("\\d{4}-\\d{2}-\\d{2}")
                        && !status.isBlank()) {
                    hours = hours.isBlank() ? "null" : hours;
                    tasksTable.addTask(projectName, name, hours, startDate, finishDate, status);
                    resp.sendRedirect(req.getContextPath() + "/tasks_page");
                } else {
                    out.println("<html><body>");
                    out.println("<h1>Something doesn't meet requirements. Return and try again.</h1>");
                    out.println("<form action=\"tasks_page\"><button>Go back to Tasks</button></form>");
                    out.println("</body></html>");
                }
            } else {
                String taskId = req.getParameter("taskId");
                if (action.equalsIgnoreCase("delete")) {
                    tasksTable.deleteTask(taskId);
                }
                if (action.equalsIgnoreCase("edit")) {
                    Statement st = connection.createStatement();
                    String query = String.format("SELECT * FROM %s WHERE id=%s;", tasksTable.getTableName(), taskId);
                    ResultSet rs = st.executeQuery(query);

                    if (rs.next()) {
                        String nameOld = rs.getString(3);
                        String hoursOld = rs.getString(4);
                        hoursOld = rs.wasNull() || hoursOld.isBlank() ? "null" : hoursOld;
                        String startDateOld = rs.getString(5);
                        String finishDateOld = rs.getString(6);
                        String statusOld = rs.getString(7);

                        name = name.isBlank() ? nameOld : name;
                        hours = hours.isBlank() ? hoursOld : hours;
                        startDate = startDate.isBlank() ? startDateOld : startDate;
                        finishDate = finishDate.isBlank() ? finishDateOld : finishDate;
                        status = status.isBlank() ? statusOld : status;

                        tasksTable.updateTask(taskId, name, hours, startDate, finishDate, status);
                    }
                    st.close();
                }
                resp.sendRedirect(req.getContextPath() + "/tasks_page");
            }
        } catch (SQLException e) {
            out.println(e.getMessage());
        }
    }
}