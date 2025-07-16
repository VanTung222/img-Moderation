<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Upload Image</title>
</head>
<body>
    <h2>Upload an Image to Moderate</h2>
    <form action="moderate" method="post" enctype="multipart/form-data">
        <input type="file" name="image" required accept="image/*"/>
        <button type="submit">Check</button>
    </form>

    <c:if test="${not empty message}">
        <h3 style="color: ${isSafe ? 'green' : 'red'}">${message}</h3>
    </c:if>
</body>
</html>
