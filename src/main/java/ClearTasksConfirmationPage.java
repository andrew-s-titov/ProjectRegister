import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(value = "/clear_tasks_confirm")
public class ClearTasksConfirmationPage extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        out.println("<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\">");
        out.println("<title>Tasks Clear Confirmation</title>");
        out.println("</head><body><h1>Do you really want to clear all Tasks?</h1>");
        out.println("<h2>Projects data won't be erased.</h2>");
        out.println("<form action=\"clear_tasks\" method=\"GET\">");
        out.println("<button style=\"font-size:20px; width:25%; background-color:darkred; color:white\">");
        out.println("Yes, clear</button></form><br><br>");
        out.println("<form action=\"tasks_page\" method=\"GET\">");
        out.println("<button style=\"font-size:20px; width:25%\" autofocus>Cancel</button>");
        out.println("</form></body></html>");
    }
}