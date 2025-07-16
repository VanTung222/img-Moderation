package com.example.vision;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.Base64;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VisionAPIClient {

    private static final String API_KEY = "AIzaSyDFMskLsnxsxda2bzVmXYsjzvvUovKSPyo"; // Thay bằng API của bạn
    private static final String ENDPOINT = "https://vision.googleapis.com/v1/images:annotate?key=" + API_KEY;

    public boolean isImageSafe(byte[] imageBytes) throws IOException {
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        String jsonPayload = String.format(
                "{\n"
                + "  \"requests\": [\n"
                + "    {\n"
                + "      \"image\": { \"content\": \"%s\" },\n"
                + "      \"features\": [ { \"type\": \"SAFE_SEARCH_DETECTION\" } ]\n"
                + "    }\n"
                + "  ]\n"
                + "}", base64Image);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("API RESPONSE:\n" + response.body()); // <-- In ra JSON

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body());
            JsonNode annotation = root.path("responses").get(0).path("safeSearchAnnotation");

            if (annotation.isMissingNode()) {
                System.out.println("Missing safeSearchAnnotation.");
                return false;
            }

            String adult = annotation.path("adult").asText("UNKNOWN");
            String violence = annotation.path("violence").asText("UNKNOWN");

            System.out.println("adult: " + adult);
            System.out.println("violence: " + violence);

            return !("LIKELY".equals(adult) || "VERY_LIKELY".equals(adult)
                    || "LIKELY".equals(violence) || "VERY_LIKELY".equals(violence));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
