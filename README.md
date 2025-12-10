# Infotech - Plateforme de vente d'appareils reconditionnés (Style Back Market)

## 📋 Description

Backend complet d'une marketplace de produits reconditionnés permettant à plusieurs vendeurs de proposer leurs articles. L'architecture est inspirée de Back Market avec gestion multi-vendeurs, authentification JWT, panier, favoris, reviews, etc.

## 🚀 Technologies

- **Framework**: Spring Boot 3.5.3
- **Base de données**: PostgreSQL
- **Sécurité**: Spring Security + JWT (jjwt 0.11.5)
- **Documentation API**: SpringDoc OpenAPI (Swagger UI)
- **Validation**: Jakarta Validation
- **ORM**: Spring Data JPA / Hibernate
- **Build**: Maven

## 📦 Fonctionnalités

### ✅ Authentification & Autorisation
- Inscription utilisateur/vendeur avec email unique
- Login avec JWT Bearer token
- Hash des mots de passe (BCrypt)
- Rôles: USER, SELLER, ADMIN
- Protection des endpoints sensibles

### 🛍️ Catalogue Produits
- Liste paginée de produits
- Recherche textuelle (titre, marque, description)
- Filtres avancés (catégorie, marque, état, prix min/max)
- Spécifications JPA pour requêtes dynamiques
- Moyenne des notes et nombre d'avis par produit

### 📦 Gestion des Annonces (Listings)
- Plusieurs vendeurs peuvent vendre le même produit
- Prix, quantité, état personnalisés par vendeur
- Gestion du stock en temps réel
- Désactivation automatique si stock épuisé

### 🛒 Panier
- Ajout/suppression d'articles
- Mise à jour des quantités
- Persistance du panier en base
- Vidage automatique après commande

### ❤️ Favoris
- Ajout/suppression de produits favoris
- Vérification rapide si produit favori
- Liste complète des favoris utilisateur

### ⭐ Reviews & Ratings
- Notes de 1 à 5 étoiles
- Commentaires sur les produits
- Moyenne et total des avis
- Limitation : 1 avis par utilisateur/produit

### 📍 Adresses
- Gestion multi-adresses
- Adresse par défaut
- CRUD complet avec validation

### 🧾 Commandes
- Création de commande depuis panier ou direct
- Vérification du stock avant achat
- Calcul automatique du total
- Historique des commandes utilisateur
- Réduction automatique du stock

### 👥 Profils Vendeurs
- Informations boutique (nom, description, email)
- Lien avec compte utilisateur
- Gestion des listings du vendeur

### 🔐 Sécurité
- CORS configuré (Angular/React)
- Endpoints publics (GET produits/listings/categories)
- Endpoints protégés (panier, commandes, favoris)
- Endpoints admin (/api/admin/*)
- Gestion des exceptions globale avec messages clairs

## 🏗️ Architecture

```
com.n2s.infotech/
├── config/             # Configuration Spring Security, CORS
├── controller/         # REST Controllers
├── dto/                # Data Transfer Objects + validation
├── exception/          # Gestion globale des erreurs
├── init/               # Initialisation des données de test
├── model/              # Entités JPA
├── repository/         # Repositories Spring Data
├── security/           # JWT Provider, Filter, UserDetailsService
├── service/            # Services métier
└── specification/      # JPA Specifications pour filtres dynamiques
```

## 📊 Modèle de données

### Entités principales
- **User** : utilisateurs (buyer/seller/admin)
- **SellerProfile** : profil vendeur lié à un user
- **Category** : catégories de produits (auto-référencée pour hiérarchie)
- **Product** : fiches produit (modèle, marque, état)
- **ProductImage** : images des produits
- **Listing** : offres commerciales (produit + vendeur + prix)
- **Order / OrderItem** : commandes et lignes de commande
- **CartItem** : articles dans le panier
- **Favorite** : produits favoris d'un utilisateur
- **Address** : adresses de livraison
- **Review** : avis et notes produits

### Relations clés
- User 1-1 SellerProfile
- Product 1-N Listing (multi-vendeurs)
- Product 1-N ProductImage
- Product 1-N Review
- Order 1-N OrderItem
- OrderItem N-1 Listing (snapshot prix au moment de l'achat)

## 🔧 Installation & Démarrage

### Prérequis
- Java 21+
- Maven 3.9+
- PostgreSQL 13+

### Étapes

1. **Cloner le projet**
```bash
cd /Users/emmanuel/Documents/dev/github/infotech-ss
```

2. **Configurer la base de données**
Créer la base PostgreSQL :
```sql
CREATE DATABASE infotech;
```

Ajuster `src/main/resources/application.properties` si nécessaire :
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/infotech
spring.datasource.username=emmanuel
spring.datasource.password=
```

3. **Compiler & démarrer**
```bash
mvn clean install
mvn spring-boot:run
```

L'application démarre sur **http://localhost:8080**

4. **Accéder à Swagger UI**
Ouvrir : **http://localhost:8080/swagger-ui.html**

## 🧪 Données de test

Au démarrage, le `DataInitializer` crée automatiquement :

### Utilisateurs
| Email | Password | Rôles |
|-------|----------|-------|
| admin@local | admin | ADMIN, USER |
| seller@local | seller | SELLER, USER |

### Produits
- iPhone X (refurbished, catégorie Phones)
- MacBook Pro 2018 (used, catégorie Laptops)

### Listings
- iPhone X à 349.99€ (quantité: 5)
- MacBook Pro à 1199.00€ (quantité: 2)

## 📚 Endpoints principaux

### Auth
```
POST /api/auth/register       # Inscription
POST /api/auth/login          # Connexion → JWT
```

### Produits (public GET)
```
GET  /api/products                    # Liste paginée + filtres
GET  /api/products/{id}               # Détail produit
GET  /api/products/brands             # Liste des marques
GET  /api/products/conditions         # Liste des états
GET  /api/products/search?q=iphone    # Recherche textuelle
```

### Listings (public GET)
```
GET  /api/listings                    # Liste paginée
GET  /api/listings/{id}               # Détail listing
```

### Catégories
```
GET  /api/categories                  # Liste
POST /api/categories                  # Créer (admin)
```

### Panier (authentifié)
```
GET    /api/cart                      # Voir panier
POST   /api/cart                      # Ajouter article
PUT    /api/cart/{id}                 # Modifier quantité
DELETE /api/cart/{id}                 # Retirer article
DELETE /api/cart                      # Vider panier
```

### Favoris (authentifié)
```
GET    /api/favorites                 # Liste favoris
POST   /api/favorites/{productId}     # Ajouter
DELETE /api/favorites/{productId}     # Retirer
GET    /api/favorites/check/{productId}  # Vérifier si favori
```

### Reviews (GET public, POST authentifié)
```
GET  /api/reviews/product/{id}             # Avis d'un produit
GET  /api/reviews/product/{id}/stats       # Stats (moyenne, total)
POST /api/reviews                          # Créer avis
```

### Adresses (authentifié)
```
GET    /api/addresses                 # Liste
POST   /api/addresses                 # Créer
PUT    /api/addresses/{id}            # Modifier
DELETE /api/addresses/{id}            # Supprimer
```

### Commandes (authentifié)
```
GET  /api/orders                      # Historique
GET  /api/orders/{id}                 # Détail commande
POST /api/orders                      # Créer commande
```

### Admin (ROLE_ADMIN)
```
POST   /api/admin/products            # Créer produit
POST   /api/admin/listings            # Créer listing
GET    /api/admin/listings            # Liste admin
DELETE /api/admin/listings/{id}       # Supprimer listing
```

## 🔑 Utilisation JWT

1. **S'authentifier**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"seller@local","password":"seller"}'
```

Réponse :
```json
{"token":"eyJhbGciOiJIUzI1NiJ9..."}
```

2. **Appeler un endpoint protégé**
```bash
curl -X GET http://localhost:8080/api/cart \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

## 🎯 Améliorations futures

### Court terme
- [ ] Upload d'images (S3/local storage)
- [ ] Pagination côté vendeur (mes listings)
- [ ] Statistiques vendeur (ventes, CA)
- [ ] Emails de confirmation commande
- [ ] Filtres de prix sur listings (pas seulement produits)
- [ ] Système de notation vendeurs

### Moyen terme
- [ ] Paiement (Stripe/PayPal)
- [ ] Suivi de colis / statuts commande avancés
- [ ] Chat vendeur-acheteur
- [ ] Notifications push
- [ ] Recherche full-text (Elasticsearch)
- [ ] Cache Redis (produits populaires)
- [ ] Rate limiting

### Long terme
- [ ] Recommandations IA
- [ ] Comparaison de prix entre vendeurs
- [ ] Programme de fidélité
- [ ] Application mobile (API REST ready)
- [ ] Internationalisation (i18n)
- [ ] Analytics avancés

## 🐛 Résolution de problèmes

### Erreur de connexion DB
Vérifier que PostgreSQL est démarré :
```bash
psql -U emmanuel -d infotech
```

### Erreur JWT
Le token expire après 24h. Se reconnecter pour obtenir un nouveau token.

### Erreur CORS
Ajuster les origines autorisées dans `SecurityConfig.java` :
```java
configuration.setAllowedOrigins(List.of("http://localhost:4200", ...));
```

## 📝 Licence

Projet de démonstration - Tous droits réservés

## 👥 Auteur

N2S Infotech Team

