package com.n2s.infotech.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Configuration pour servir les fichiers statiques (images uploadées)
 */
@Configuration
@RequiredArgsConstructor
public class StaticResourceConfig implements WebMvcConfigurer {

    private final StorageProperties storageProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir les images uploadées localement
        if ("local".equals(storageProperties.getType())) {
            String uploadPath = Paths.get(storageProperties.getLocal().getDirectory())
                    .toAbsolutePath()
                    .toUri()
                    .toString();

            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations(uploadPath);
        }

        // Les ressources statiques par défaut
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}

