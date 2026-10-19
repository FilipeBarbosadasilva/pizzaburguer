package com.pizzaburguer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UploadStorageServiceTest {
    @TempDir
    Path uploadDirectory;

    @Test
    void storesVerifiedPngWithGeneratedName() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB), "png", bytes);
        MockMultipartFile file = new MockMultipartFile(
                "file", "../../unsafe.png", "text/plain", bytes.toByteArray());

        String url = new UploadStorageService(uploadDirectory.toString()).storeImage(file);

        assertTrue(url.matches("/uploads/[0-9a-f-]{36}\\.png"));
        assertTrue(Files.exists(uploadDirectory.resolve(url.substring("/uploads/".length()))));
    }

    @Test
    void rejectsFilesThatAreNotSupportedImageFormats() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "image.png", "image/png", "not an image".getBytes());

        assertThrows(ResponseStatusException.class,
                () -> new UploadStorageService(uploadDirectory.toString()).storeImage(file));
    }
}
