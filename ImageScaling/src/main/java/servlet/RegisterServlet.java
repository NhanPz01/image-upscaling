package servlet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(urlPatterns = "/register")
public class RegisterServlet extends HttpServlet {
    private static final String INSERT_USER = "INSERT INTO user (username, password) VALUES (?, ?)";
    private static final String FIND_USER = "SELECT * FROM user WHERE username = ?";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher requestDispatcher = req.getRequestDispatcher("register.jsp");
        requestDispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            PrintWriter out = resp.getWriter();
            resp.setContentType("text/html");
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/image_scaling",
                    "root",
                    "111111");
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_USER);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                out.println("<font color=red size=18>Username already exists!!!<br>");
                out.println("<a href=register.jsp>Try Again</a>");
            } else {
                PreparedStatement insertUserStatement = connection.prepareStatement(INSERT_USER);
                insertUserStatement.setString(1, username);
                insertUserStatement.setString(2, password);
                insertUserStatement.executeUpdate();
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("login.jsp");
                requestDispatcher.forward(req, resp);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}