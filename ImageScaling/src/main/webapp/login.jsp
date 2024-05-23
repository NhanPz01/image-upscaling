<%--
  Created by IntelliJ IDEA.
  User: nguye
  Date: 5/23/2024
  Time: 8:44 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
    <title>Image Scaling</title>
</head>
<body>
<div class="container">
    <header class="d-flex flex-wrap align-items-center justify-content-center justify-content-md-between py-3 mb-4 border-bottom">
        <a href="/" class="d-flex align-items-center col-md-3 mb-2 mb-md-0 text-dark text-decoration-none">
            <svg class="bi me-2" width="40" height="32" role="img" aria-label="Bootstrap">
                <use xlink:href="/"></use>
            </svg>
        </a>

        <ul class="nav col-12 col-md-auto mb-2 justify-content-center mb-md-0">
            <li><a href="/" class="nav-link px-2 link-secondary">Home</a></li>
            <li><a href="#" class="nav-link px-2 link-dark">Your Images</a></li>
            <li><a href="#" class="nav-link px-2 link-dark">Upload</a></li>
        </ul>

        <div class="col-md-3 text-end">
            <button type="button" class="btn btn-outline-primary me-2"><a href="/login">Login</a></button>
            <button type="button" class="btn btn-primary"><a href="/register">Sign-up</a></button>
        </div>
    </header>
</div>
<div class="container">
    <h1>Login</h1>
    <form action="login" method="post">
        <!-- Username input -->
        <div data-mdb-input-init class="form-outline mb-4">
            <input type="text" id="username" class="form-control" name="username"/>
            <label class="form-label" for="username">Username</label>
        </div>

        <!-- Password input -->
        <div data-mdb-input-init class="form-outline mb-4">
            <input type="password" id="password" class="form-control" name="password"/>
            <label class="form-label" for="password">Password</label>
        </div>

        <%
            if (request.getAttribute("errorMessage") != null) {
        %>
        <script>
            alert('<%= request.getAttribute("errorMessage") %>');
        </script>
        <%
            }
        %>

        <!-- Submit button -->
        <button type="submit" data-mdb-button-init data-mdb-ripple-init class="btn btn-primary btn-block mb-4">Sign in
        </button>

        <!-- Register buttons -->
        <div class="text-center">
            <p>Not a member? <a href="/register">Register</a></p>
        </div>
    </form>
</div>
</body>
</html>
