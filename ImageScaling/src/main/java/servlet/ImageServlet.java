package servlet;

import com.cloudinary.utils.ObjectUtils;
import util.CloudinaryUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/", "/index", "/user/images"})
public class ImageServlet extends HttpServlet {
    private static final String GET_USER_IMAGES = "SELECT * FROM image WHERE image.user_id LIKE ?";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/image_scaling",
                    "root",
                    "111111");
            String username = (String) req.getSession().getAttribute("username");
            PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_IMAGES);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<String> images = new ArrayList<>();
            while (resultSet.next()) {
                images.add(resultSet.getString("image_id"));
            }
            req.setAttribute("images", images);
            String path = req.getServletPath();
            if (path.equals("/") || path.equals("/index")) {
                req.getRequestDispatcher("index.jsp").forward(req, resp);
            } else if (path.equals("/user/images")) {
                req.getRequestDispatcher("user/images.jsp").forward(req, resp);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String publicId = req.getParameter("public_id");
        Map params = ObjectUtils.asMap(
                "public_id", "image_scaling/public_id",
                "overwrite", true,
                "resource_type", "auto"
        );
        Map uploadResult = CloudinaryUtils.upload(params);
    }
}