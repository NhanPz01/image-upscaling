package servlet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.Class;
import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = "/register")
public class RegisterServlet extends HttpServlet {
    private static final String INSERT_USER = "INSERT INTO user (username, password) VALUES (?, ?)";
    private static final String FIND_USER = "SELECT * FROM user WHERE username = ?";

    // Database credentials - should be stored securely, not hardcoded!

    private static final String DB_URL = "jdbc:mysql://localhost:3306/image_scaling";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Forward to registration form
        RequestDispatcher requestDispatcher = req.getRequestDispatcher("register.jsp");
        requestDispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                // Prepare statements once outside the loop
                PreparedStatement findUserStatement = connection.prepareStatement(FIND_USER);
                PreparedStatement insertUserStatement = connection.prepareStatement(INSERT_USER);

                String username = req.getParameter("username");
                String password = req.getParameter("password");

                // Check if username exists
                findUserStatement.setString(1, username);
                try (ResultSet resultSet = findUserStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // Username taken
                        req.setAttribute("errorMessage", "Username already exists!");
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("register.jsp");
                        requestDispatcher.forward(req, resp);
                        return; // Stop further processing
                    }
                }

                // If username is unique, register the user
                insertUserStatement.setString(1, username);
                insertUserStatement.setString(2, password); // Insecure! See note below.
                insertUserStatement.executeUpdate();

                // Redirect to login after successful registration
                resp.sendRedirect("login.jsp");

            } catch (SQLException e) {
                e.printStackTrace();
                // Handle database errors appropriately (e.g., log and display a generic error message)
                req.setAttribute("errorMessage", "An error occurred during registration.");
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("register.jsp");
                requestDispatcher.forward(req, resp);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            // Handle database errors appropriately (e.g., log and display a generic error message)
            req.setAttribute("errorMessage", "An error occurred during registration.");
            RequestDispatcher requestDispatcher = req.getRequestDispatcher("register.jsp");
            requestDispatcher.forward(req, resp);
        }
    }
}