#!/bin/bash

# Script de démarrage pour le développement
# Ce script crée les dossiers nécessaires et démarre l'application

echo "🚀 Démarrage de l'application Infotech..."

# Créer les dossiers pour le stockage des images
echo "📁 Création des dossiers de stockage..."
mkdir -p uploads/products
mkdir -p uploads/products/thumbnails
mkdir -p uploads/users
mkdir -p uploads/users/thumbnails
mkdir -p uploads/categories
mkdir -p uploads/categories/thumbnails

echo "✅ Dossiers créés avec succès"

# Démarrer l'application en mode dev
echo "🔧 Démarrage de l'application en mode développement..."
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

echo "✅ Application démarrée sur http://localhost:8080"
echo "📸 Images accessibles sur http://localhost:8080/uploads/"
echo "📚 Documentation API : http://localhost:8080/swagger-ui.html"

