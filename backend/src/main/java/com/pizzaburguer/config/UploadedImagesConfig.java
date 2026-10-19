package com.pizzaburguer.config;

import com.pizzaburguer.service.UploadStorageService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class UploadedImagesConfig implements WebMvcConfigurer {
    private final UploadStorageService uploadStorageService;

    public UploadedImagesConfig(UploadStorageService uploadStorageService) {
        this.uploadStorageService = uploadStorageService;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadStorageService.resourceLocation());
    }
}
