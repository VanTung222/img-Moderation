package com.example.vision;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/moderate")
@MultipartConfig
public class ModerationServlet extends HttpServlet {

    private final SightengineClient visionClient = new SightengineClient();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Part filePart = request.getPart("image");
        InputStream inputStream = filePart.getInputStream();
        byte[] imageBytes = IOUtils.toByteArray(inputStream);
        String contentType = filePart.getContentType();

        try {
            SightengineClient.ImageSafetyResult result = visionClient.isImageSafe(imageBytes, contentType);
            request.setAttribute("isSafe", result.isSafe);
            request.setAttribute("message", result.isSafe ? "✅ Image is safe." : "❌ Image contains inappropriate content!");
            if (!result.isSafe) {
                request.setAttribute("violations", result.violations);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ModerationServlet.class.getName()).log(Level.SEVERE, null, ex);
            request.setAttribute("isSafe", false);
            request.setAttribute("message", "❌ Error processing image!");
        }

        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}