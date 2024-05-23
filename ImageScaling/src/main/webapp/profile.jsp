<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>

<!DOCTYPE html>
<html>
<head>
    <title>User Images</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
</head>
<body>
<div class="container">
    <header class="d-flex flex-wrap align-items-center justify-content-center justify-content-md-between py-3 mb-4 border-bottom">
        <a href="/" class="d-flex align-items-center col-md-3 mb-2 mb-md-0 text-dark text-decoration-none">
            <svg class="bi me-2" width="40" height="32" role="img" aria-label="Bootstrap"><use xlink:href="/"></use></svg>
        </a>

        <ul class="nav col-12 col-md-auto mb-2 justify-content-center mb-md-0">
            <li><a href="/login" class="nav-link px-2 link-secondary">Home</a></li>
            <li><a href="/profile" class="nav-link px-2 link-dark">Your Images</a></li>
        </ul>

    </header>
</div>
<div class="container">
    <h1>Your Images</h1>
    <div class="row">
        <c:forEach items="${images}" var="image">
            <div class="col-md-4 mb-3">
                <div class="card">
                    <img src="data:image/jpeg;base64,${image.base64Image}" class="card-img-top" alt="Uploaded Image">
                    <div class="card-body">
                        <p class="card-text">Image ID: ${image.id}</p>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>

</body>
</html>