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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet(value = "/project_manager")
public class ProjectManager extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession();
        Connection connection = (Connection) session.getAttribute("connection");

        String name = req.getParameter("name");
        String shortName = req.getParameter("shortName");
        String description = req.getParameter("description");

        String action = req.getParameter("action");

        try {
            ProjectsTable projectsTable = ProjectsTable.getInstance(connection);
            TasksTable tasksTable = TasksTable.getInstance(connection);

            if (action.equalsIgnoreCase("new")) {
                if (!name.isBlank() && !shortName.isBlank() && name.length() <= 255 && shortName.length() <= 20) {
                    description = description.isBlank() ? "null" : description;
                    projectsTable.addNewProject(name, shortName, description);
                    resp.sendRedirect(req.getContextPath() + "/projects_page");
                } else {
                    out.println("<html><body>");
                    out.println("<h1>Name or Short name doesn't meet requirements. Return and try again.</h1>");
                    out.println("<form action=\"projects_page\"><button>Go back to Projects</button></form>");
                    out.println("</body></html>");
                }
            } else {
                String projectId = req.getParameter("projectId");

                if (action.equalsIgnoreCase("delete")) {
                    Statement st = connection.createStatement();
                    String query = String.format("SELECT %s FROM %s WHERE id=%s;",
                            projectsTable.getShortNameColumnName(), projectsTable.getTableName(), projectId);
                    ResultSet rs = st.executeQuery(query);

                    String projectName;
                    if (rs.next()) {
                        projectName = rs.getString(1);
                        String projectColumnName = tasksTable.getProjectColumnName();
                        String upd = String.format("DELETE FROM %s WHERE %s=\"%s\";", tasksTable.getTableName(),
                                projectColumnName, projectName);
                        st.executeUpdate(upd);
                        projectsTable.getProjectsShortNamesSet().remove(projectName);
                    }
                    projectsTable.deleteProject(projectId);
                    st.close();
                }

                if (action.equalsIgnoreCase("edit")) {
                    Statement st = connection.createStatement();
                    String query = String.format("SELECT * FROM %s WHERE id=%s;",
                            projectsTable.getTableName(), projectId);
                    ResultSet rs = st.executeQuery(query);
                    if (rs.next()) {
                        String nameOld = rs.getString(2);
                        String shortNameOld = rs.getString(3);
                        String descriptionOld = rs.getString(4);
                        descriptionOld = rs.wasNull() || descriptionOld.isBlank() ? "null" : descriptionOld;

                        name = name.isBlank() ? nameOld : name;
                        shortName = shortName.isBlank() ? shortNameOld : shortName;
                        description = description.isBlank() ? descriptionOld : description;

                        if (!shortName.equals(shortNameOld)) {
                            projectsTable.getProjectsShortNamesSet().remove(shortNameOld);
                            projectsTable.getProjectsShortNamesSet().add(shortName);
                            tasksTable.editProjectShortName(shortName, shortNameOld);
                        }

                        projectsTable.updateProject(projectId, name, shortName, description);
                    }
                    st.close();
                }
                resp.sendRedirect(req.getContextPath() + "/projects_page");
            }
        } catch (SQLException e) {
            out.println(e.getMessage());
        }
    }
}
