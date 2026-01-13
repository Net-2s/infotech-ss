package com.n2s.infotech.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration pour charger les variables du fichier .env
 * au demarrage de l'application
 */
@Configuration
public class DotEnvConfig {

    @PostConstruct
    public void loadEnv() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing() // Ne pas planter si .env n'existe pas
                    .load();

            // Charger les variables dans les proprietes systeme
            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
            });

            System.out.println("✅ Variables .env chargees avec succes");
        } catch (Exception e) {
            System.err.println("⚠️  Fichier .env non trouve, utilisation des variables d'environnement systeme");
        }
    }
}

