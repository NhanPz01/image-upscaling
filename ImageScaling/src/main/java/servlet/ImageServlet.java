package servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.json.JSONObject;


@WebServlet(urlPatterns = {"/", "/index", "/user/images"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50) // 50MB
public class ImageServlet extends HttpServlet {
    private static final String GET_USER_IMAGES = "SELECT * FROM image";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
        	System.out.println("invoked");
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/image_scaling",
                    "root",
                    "");
            PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_IMAGES);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Image> images = new ArrayList<>();
            while (resultSet.next()) {
            	int id = resultSet.getInt("id");
                Blob imageBlob = resultSet.getBlob("url");
                if (imageBlob != null) {
                    byte[] imageBytes = imageBlob.getBytes(1, (int) imageBlob.length());
                    String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                    images.add(new Image(id, base64Image));
                    System.out.println(images);
                }
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
        // Verify request method
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Only POST requests are allowed.");
            return;
        }

        // Get uploaded image as a Part
        javax.servlet.http.Part filePart = req.getPart("image");
        if (filePart == null || filePart.getSize() == 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No image file uploaded.");
            return;
        }
        
        try {
            // Read image data into a byte array
            InputStream fileContent = filePart.getInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileContent.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            byte[] imageData = outputStream.toByteArray();

            // Send image data to Python Flask API
            URL url = new URL("http://127.0.0.1:5000/upscale"); 
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/octet-stream");
            conn.setDoOutput(true);

            // Send image data in the request body
            try (OutputStream os = conn.getOutputStream()) {
                os.write(imageData);
            }
            
            // Get response from Flask API
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { 
                // Read response JSON
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
             while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());
                String imageBase64 = jsonResponse.getString("image_base64");

                // Store the image in the database
                String username = (String) req.getSession().getAttribute("username");
                if(username != null) {
                	try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection connection = DriverManager.getConnection(
                                "jdbc:mysql://localhost:3306/image_scaling",
                                "root",
                                "");
                        PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO image (url, user_id) VALUES (?, ?)");
                        byte[] imageBytes = Base64.getDecoder().decode(imageBase64);
                        insertStatement.setBlob(1, new ByteArrayInputStream(imageBytes));
                        insertStatement.setString(2, username);
                        insertStatement.executeUpdate();
                        
                    } catch (ClassNotFoundException | SQLException e) {
                        e.printStackTrace();
                    }
                }

                // Forward base64 image to JSP for display
                req.setAttribute("imageBase64", imageBase64);
                req.getRequestDispatcher("index.jsp").forward(req, resp);

            } else {
                resp.sendError(responseCode, "Flask API returned an error. Response Code: " + responseCode);
            }
        } catch (IOException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while processing the image.");
            e.printStackTrace(); 
        }
    }
}

class Image {
    private int id;
    private String base64Image;

    public Image(int id, String base64Image) {
        this.id = id;
        this.base64Image = base64Image;
    }

    public int getId() {
        return id;
    }

    public String getBase64Image() {
        return base64Image;
    }
}