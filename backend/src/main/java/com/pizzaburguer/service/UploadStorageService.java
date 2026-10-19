package com.pizzaburguer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;

@Service
public class UploadStorageService {
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final long MAX_PIXELS = 40_000_000L;

    private final Path uploadDirectory;

    public UploadStorageService(@Value("${app.upload.dir:uploads}") String uploadDirectory) {
        this.uploadDirectory = Path.of(uploadDirectory).toAbsolutePath().normalize();
    }

    public String storeImage(MultipartFile file) {
        if (file.isEmpty() || file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A imagem deve ter no máximo 5 MB.");
        }

        String extension = detectImageExtension(file);
        String filename = UUID.randomUUID() + "." + extension;
        try (InputStream input = file.getInputStream()) {
            Files.createDirectories(uploadDirectory);
            Files.copy(input, uploadDirectory.resolve(filename));
        } catch (IOException exception) {
            throw new IllegalStateException("Não foi possível armazenar a imagem enviada.", exception);
        }
        return "/uploads/" + filename;
    }

    public String resourceLocation() {
        String location = uploadDirectory.toUri().toString();
        return location.endsWith("/") ? location : location + "/";
    }

    private String detectImageExtension(MultipartFile file) {
        try (InputStream stream = file.getInputStream();
             ImageInputStream input = ImageIO.createImageInputStream(stream)) {
            if (input == null) {
                throw invalidImage();
            }
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) {
                throw invalidImage();
            }

            ImageReader reader = readers.next();
            try {
                reader.setInput(input, true, true);
                String format = reader.getFormatName().toLowerCase(Locale.ROOT);
                String extension = switch (format) {
                    case "jpeg", "jpg" -> "jpg";
                    case "png" -> "png";
                    default -> throw invalidImage();
                };
                int width = reader.getWidth(0);
                int height = reader.getHeight(0);
                if (width <= 0 || height <= 0 || (long) width * height > MAX_PIXELS) {
                    throw invalidImage();
                }
                if (reader.read(0) == null) {
                    throw invalidImage();
                }
                return extension;
            } finally {
                reader.dispose();
            }
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O arquivo enviado não é uma imagem PNG ou JPEG.", exception);
        }
    }

    private ResponseStatusException invalidImage() {
        return new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Envie uma imagem PNG ou JPEG válida com dimensões suportadas.");
    }
}
