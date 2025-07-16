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

        boolean isSafe = false;
        try {
            isSafe = visionClient.isImageSafe(imageBytes);
        } catch (InterruptedException ex) {
            Logger.getLogger(ModerationServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        request.setAttribute("isSafe", isSafe);
        request.setAttribute("message", isSafe ? "✅ Ảnh an toàn." : "❌ Ảnh có nội dung không phù hợp!");
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}
