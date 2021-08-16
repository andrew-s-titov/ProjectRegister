package base;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(value = "/", loadOnStartup = 1)
public class LoginPage extends HttpServlet {
    @Override
    public void init() throws ServletException {
        super.init();
        DatabaseConnection.loadDriver();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        out.println("<html><body>");

        HttpSession session = req.getSession();
        session.setMaxInactiveInterval(0);
        Connection connection = (Connection) session.getAttribute("connection");
        if (connection == null) {
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            if (username == null || password == null) {
                // Prompt the user for username and password
                out.println("<form method=\"POST\" action=\"" + req.getContextPath() + "/\">");
                out.println("Type parameters to create MySQL connection:<p>");
                out.println("Username: <input type=\"text\" name=\"username\"><p>");
                out.println("Password: <input type=\"password\" name=\"password\"><p>");
                out.println("<input type=\"submit\" value=\"Login\">");
                out.println("</form>");
            } else {
                try {
                    connection = DatabaseConnection.establishConnection(username, password);
                    if (connection != null) {
                        connection = DatabaseConnection.getDatabaseConnection(connection, "ProjectsRegisterAST",
                                username, password);
                        session.setAttribute("connection", connection);

                        resp.sendRedirect(req.getContextPath() + "/projects_page");

                    } else {
                        out.println("Connection failed");
                    }
                } catch (SQLException e) {
                    out.println("Connection failed. Please, restart and check user and password");
                    out.println("</body></html>");
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
