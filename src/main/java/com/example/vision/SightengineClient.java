package com.example.vision;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.nio.file.Files;

public class SightengineClient {

    private static final String API_USER = "1405169130";   // Thay bằng API User của bạn
    private static final String API_SECRET = "YCtJEMsNJVgYA9qtzxjcrMwvJ58CvyyV"; // Thay bằng API Secret

  public boolean isImageSafe(byte[] imageBytes, String contentType) throws IOException, InterruptedException {
    File tempFile = File.createTempFile("upload-", ".img");
    Files.write(tempFile.toPath(), imageBytes);

    String boundary = "Boundary-" + System.currentTimeMillis();
    var byteArrays = new ByteArrayOutputStream();
    var writer = new PrintWriter(new OutputStreamWriter(byteArrays));

    writer.append("--" + boundary).append("\r\n");
    writer.append("Content-Disposition: form-data; name=\"media\"; filename=\"" + tempFile.getName() + "\"\r\n");
    writer.append("Content-Type: " + contentType + "\r\n\r\n").flush(); // 👈 Sử dụng content type thực

    Files.copy(tempFile.toPath(), byteArrays);
    byteArrays.write("\r\n".getBytes());
    writer.append("--" + boundary + "--").append("\r\n").flush();

    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.sightengine.com/1.0/check.json" +
                    "?models=nudity,wad,offensive" +
                    "&api_user=" + API_USER +
                    "&api_secret=" + API_SECRET))
            .header("Content-Type", "multipart/form-data; boundary=" + boundary)
            .POST(HttpRequest.BodyPublishers.ofByteArray(byteArrays.toByteArray()))
            .build();

    HttpClient client = HttpClient.newHttpClient();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(response.body());

    double nudity = root.path("nudity").path("raw").asDouble(0);
    double weapon = root.path("weapon").asDouble(0.0);
    double alcohol = root.path("alcohol").asDouble(0.0);
    double drugs = root.path("drugs").asDouble(0.0);

    return nudity < 0.5 && weapon < 0.5 && alcohol < 0.5 && drugs < 0.5;
}



    // Multipart form-data utility
    private HttpRequest.BodyPublisher ofMimeMultipartData(File file, String user, String secret) throws IOException {
        var boundary = "Boundary-" + System.currentTimeMillis();
        var byteArrays = new ByteArrayOutputStream();
        var writer = new PrintWriter(new OutputStreamWriter(byteArrays));

        writer.append("--" + boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"media\"; filename=\"" + file.getName() + "\"\r\n");
        writer.append("Content-Type: image/jpeg\r\n\r\n").flush();
        Files.copy(file.toPath(), byteArrays);
        byteArrays.write("\r\n".getBytes());

        writer.append("--" + boundary + "--").append("\r\n").flush();

        return HttpRequest.BodyPublishers.ofByteArray(byteArrays.toByteArray());
    }
}
