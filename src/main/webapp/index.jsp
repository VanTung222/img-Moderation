<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Image Moderation</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <style>
        .file-upload {
            border: 2px dashed #d1d5db;
            transition: all 0.3s ease;
        }
        .file-upload:hover {
            border-color: #3b82f6;
            background-color: #f8fafc;
        }
        .fade-in {
            animation: fadeIn 0.5s ease-in-out;
        }
        @keyframes fadeIn {
            0% { opacity: 0; transform: translateY(10px); }
            100% { opacity: 1; transform: translateY(0); }
        }
    </style>
</head>
<body class="bg-gray-100 font-sans">
    <div class="min-h-screen flex items-center justify-center p-4">
        <div class="bg-white rounded-2xl shadow-xl p-8 w-full max-w-md">
            <h2 class="text-2xl font-bold text-center text-gray-800 mb-6">Image Moderation</h2>
            
            <!-- Upload Form -->
            <form action="moderate" method="post" enctype="multipart/form-data" class="space-y-6">
                <div class="file-upload rounded-lg p-6 text-center">
                    <label for="image" class="block text-sm font-medium text-gray-700 mb-2">
                        Drag and drop or click to upload an image
                    </label>
                    <input type="file" id="image" name="image" required accept="image/*"
                           class="w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4
                                  file:rounded-full file:border-0 file:text-sm file:font-semibold
                                  file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100"/>
                </div>
                <button type="submit"
                        class="w-full bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 
                               transition duration-300 font-semibold">
                    Check Image
                </button>
            </form>

            <!-- Result Display -->
            <c:if test="${not empty message}">
                <div class="mt-6 p-4 rounded-lg fade-in
                            ${isSafe ? 'bg-green-100 border-green-400 text-green-700' : 'bg-red-100 border-red-400 text-red-700'} 
                            border">
                    <h3 class="text-lg font-semibold">${message}</h3>
                    <c:if test="${not empty violations}">
                        <p class="mt-2 text-sm">Violations detected:</p>
                        <ul class="list-disc list-inside text-sm">
                            <c:forEach var="violation" items="${violations}">
                                <li>${violation}</li>
                            </c:forEach>
                        </ul>
                    </c:if>
                </div>
            </c:if>
        </div>
    </div>

    <!-- JavaScript for Drag and Drop Highlight -->
    <script>
        const fileUpload = document.querySelector('.file-upload');
        const input = document.querySelector('#image');

        fileUpload.addEventListener('dragover', (e) => {
            e.preventDefault();
            fileUpload.classList.add('border-blue-500', 'bg-blue-50');
        });

        fileUpload.addEventListener('dragleave', () => {
            fileUpload.classList.remove('border-blue-500', 'bg-blue-50');
        });

        fileUpload.addEventListener('drop', (e) => {
            e.preventDefault();
            fileUpload.classList.remove('border-blue-500', 'bg-blue-50');
            input.files = e.dataTransfer.files;
        });
    </script>
</body>
</html>