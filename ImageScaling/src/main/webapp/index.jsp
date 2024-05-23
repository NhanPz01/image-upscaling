<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
    <title>Image Scaling</title>
</head>
<body>
<div class="container">
    <header class="d-flex flex-wrap align-items-center justify-content-center justify-content-md-between py-3 mb-4 border-bottom">
        <a href="/" class="d-flex align-items-center col-md-3 mb-2 mb-md-0 text-dark text-decoration-none">
            <svg class="bi me-2" width="40" height="32" role="img" aria-label="Bootstrap"><use xlink:href="/"></use></svg>
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
    <div class="row">
        <div class="col-md-6">
            <h1>Upload Image</h1>
            <form id="uploadForm" method="POST" enctype="multipart/form-data">
                <div class="form-group">
                    <label for="imageInput">Choose Image:</label>
                    <input type="file" class="form-control-file" id="imageInput" name="image">
                </div>
                <button type="submit" class="btn btn-primary">Upload</button>
            </form>
        </div>
        <div class="col-md-6">
            <h1>Upscaled Image</h1>
            <img id="upscaledImage" src="" alt="Upscaled Image" style="max-width: 100%; height: auto;">
        </div>
    </div>
</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.3/jquery.min.js"></script>
<script>
    $(document).ready(function() {
        $('#uploadForm').on('submit', function(event) {
            event.preventDefault();

            var formData = new FormData(this);

            $.ajax({
                url: 'http://127.0.0.1:5000/upscale', 
                type: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                success: function(response) {
                    if (response && response.image_base64) {
                        $('#upscaledImage').attr('src', 'data:image/jpeg;base64,' + response.image_base64);
                    } else {
                        console.error('Response does not contain image_base64');
                        alert('An error occurred while upscaling the image.');
                    }
                },
                error: function(error) {
                    console.error('Error:', error);
                    alert('An error occurred while upscaling the image.');
                }
            });
        });
    });
</script>
</body>
</html>