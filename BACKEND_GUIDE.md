# Backend Infotech - Plateforme de vente en ligne (type Back Market)

## 📋 Vue d'ensemble

Backend Spring Boot complet pour une plateforme e-commerce de produits reconditionnés avec :
- ✅ Authentification JWT avec Spring Security
- ✅ Gestion des rôles (USER, SELLER, ADMIN)
- ✅ API REST complète pour produits, listings, panier, commandes, favoris, avis
- ✅ Base de données PostgreSQL avec jeu de données de test
- ✅ Documentation Swagger/OpenAPI

## 🚀 Démarrage rapide

### Prérequis
- Java 21
- PostgreSQL 14+
- Maven 3.8+

### Configuration

1. **Base de données PostgreSQL**
```bash
createdb infotech
```

2. **Configuration** (déjà dans `application.properties`)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/infotech
spring.datasource.username=emmanuel
spring.datasource.password=
```

3. **Lancer l'application**
```bash
./mvnw clean install
./mvnw spring-boot:run
```

L'application démarre sur `http://localhost:8080`

### Données de test

Au démarrage, la base est automatiquement remplie avec :
- 3 utilisateurs (admin, alice, bob)
- 5 catégories + sous-catégories
- 6 produits avec images
- 6 listings (offres de vente)
- Adresses, favoris, panier, commandes, avis

### Comptes de test

| Email | Password | Rôles |
|-------|----------|-------|
| admin@example.com | admin123 | ADMIN |
| alice@example.com | password123 | USER, SELLER |
| bob@example.com | password123 | USER |

---

## 🔐 Authentification JWT

### Comment ça marche ?

1. **S'inscrire** : `POST /api/auth/register`
2. **Se connecter** : `POST /api/auth/login`
3. **Recevoir un token JWT**
4. **Utiliser le token** dans les requêtes suivantes avec le header :
   ```
   Authorization: Bearer <votre-token-jwt>
   ```

### Exemple de connexion

**Requête :**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "password": "password123"
  }'
```

**Réponse :**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjIsInJvbGVzIjp..."
}
```

**Utilisation du token :**
```bash
curl http://localhost:8080/api/cart?userId=2 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

---

## 📡 API Endpoints

### 🔓 Endpoints publics (sans authentification)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/auth/register` | Créer un compte |
| POST | `/api/auth/register?seller=true` | Créer un compte vendeur |
| POST | `/api/auth/login` | Se connecter |
| GET | `/api/products` | Liste des produits (avec filtres) |
| GET | `/api/products/{id}` | Détail d'un produit |
| GET | `/api/categories` | Liste des catégories |
| GET | `/api/listings` | Liste des offres de vente |
| GET | `/api/reviews/product/{id}` | Avis d'un produit |

### 🔒 Endpoints protégés (authentification requise)

#### Panier
| Méthode | Endpoint | Description | Rôle requis |
|---------|----------|-------------|-------------|
| GET | `/api/cart?userId={id}` | Mon panier | USER+ |
| POST | `/api/cart?userId={id}` | Ajouter au panier | USER+ |
| PUT | `/api/cart/{id}?quantity={n}` | Modifier quantité | USER+ |
| DELETE | `/api/cart/{id}` | Retirer du panier | USER+ |
| DELETE | `/api/cart?userId={id}` | Vider le panier | USER+ |

#### Commandes
| Méthode | Endpoint | Description | Rôle requis |
|---------|----------|-------------|-------------|
| POST | `/api/orders` | Créer une commande | USER+ |
| GET | `/api/orders?userId={id}` | Mes commandes | USER+ |
| GET | `/api/orders/{id}` | Détail commande | USER+ |
| PATCH | `/api/orders/{id}/status?status=...` | Changer statut | SELLER/ADMIN |

#### Favoris
| Méthode | Endpoint | Description | Rôle requis |
|---------|----------|-------------|-------------|
| GET | `/api/favorites?userId={id}` | Mes favoris | USER+ |
| POST | `/api/favorites/{productId}?userId={id}` | Ajouter favori | USER+ |
| DELETE | `/api/favorites/{productId}?userId={id}` | Retirer favori | USER+ |
| GET | `/api/favorites/check/{productId}?userId={id}` | Vérifier si favori | USER+ |

#### Adresses
| Méthode | Endpoint | Description | Rôle requis |
|---------|----------|-------------|-------------|
| GET | `/api/addresses?userId={id}` | Mes adresses | USER+ |
| POST | `/api/addresses?userId={id}` | Créer adresse | USER+ |
| PUT | `/api/addresses/{id}?userId={id}` | Modifier adresse | USER+ |
| DELETE | `/api/addresses/{id}?userId={id}` | Supprimer adresse | USER+ |

#### Avis
| Méthode | Endpoint | Description | Rôle requis |
|---------|----------|-------------|-------------|
| POST | `/api/reviews` | Créer un avis | USER+ |
| DELETE | `/api/reviews/{id}?userId={id}` | Supprimer mon avis | USER+ |

### 👑 Endpoints admin

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/categories` | Créer catégorie |
| PUT | `/api/categories/{id}` | Modifier catégorie |
| DELETE | `/api/categories/{id}` | Supprimer catégorie |

---

## 🏗️ Architecture

### Structure du projet

```
src/main/java/com/n2s/infotech/
├── config/               # Configuration (Security, OpenAPI)
│   ├── SecurityConfig.java
│   └── OpenApiConfig.java
├── controller/           # Contrôleurs REST
│   ├── AuthController.java
│   ├── ProductController.java
│   ├── CartController.java
│   ├── OrderController.java
│   ├── FavoriteController.java
│   ├── ReviewController.java
│   └── AddressController.java
├── dto/                  # Data Transfer Objects
├── model/                # Entités JPA
│   ├── User.java
│   ├── Product.java
│   ├── Listing.java
│   ├── Order.java
│   ├── CartItem.java
│   └── ...
├── repository/           # Repositories Spring Data JPA
├── security/             # Sécurité JWT
│   ├── JwtService.java
│   ├── JwtAuthenticationFilter.java
│   └── CustomUserDetailsService.java
├── service/              # Logique métier
│   ├── AuthService.java
│   ├── ProductService.java
│   ├── CartService.java
│   ├── OrderService.java
│   └── ...
└── exception/            # Gestion des erreurs
```

### Modèle de données

**Entités principales :**
- `User` : Utilisateurs avec rôles (USER, SELLER, ADMIN)
- `SellerProfile` : Profil vendeur (nom boutique, description)
- `Category` : Catégories de produits (hiérarchie parent/enfant)
- `Product` : Produits (titre, description, marque, condition)
- `ProductImage` : Images de produits
- `Listing` : Offres de vente (prix, stock, vendeur)
- `CartItem` : Articles du panier
- `Order` : Commandes
- `OrderItem` : Items de commande
- `Favorite` : Favoris utilisateur
- `Review` : Avis produits
- `Address` : Adresses de livraison

---

## 🔒 Sécurité

### Système JWT

1. **JwtService** : Génère et valide les tokens JWT
   - Secret key : configuré dans `application.properties` (`jwt.secret`)
   - Expiration : 24h par défaut (`jwt.expiration`)
   - Claims : userId, roles

2. **JwtAuthenticationFilter** : Filtre chaque requête HTTP
   - Extrait le token du header `Authorization: Bearer <token>`
   - Valide le token
   - Authentifie l'utilisateur dans le contexte Spring Security

3. **CustomUserDetailsService** : Charge les utilisateurs depuis la DB
   - Convertit les rôles en `GrantedAuthority` Spring Security

4. **SecurityConfig** : Configuration Spring Security
   - Endpoints publics vs protégés
   - Stateless sessions (JWT)
   - BCrypt pour encoder les mots de passe

### Rôles et permissions

| Rôle | Permissions |
|------|-------------|
| **USER** | Panier, commandes, favoris, adresses, avis |
| **SELLER** | Tout ce que USER + gestion des listings, statut commandes |
| **ADMIN** | Tout + gestion catégories, produits |

---

## 🧪 Tests avec Swagger

Documentation interactive : `http://localhost:8080/swagger-ui.html`

### Scénario de test complet

1. **Se connecter**
   - POST `/api/auth/login` avec alice@example.com / password123
   - Copier le token JWT

2. **Cliquer sur "Authorize"** en haut de Swagger
   - Entrer : `Bearer <votre-token>`

3. **Tester les endpoints protégés**
   - GET `/api/cart?userId=2` (panier d'Alice)
   - POST `/api/favorites/1?userId=2` (ajouter produit 1 aux favoris)
   - POST `/api/orders` (créer une commande)

---

## 📦 Services implémentés

### AuthService
- `register(request, isSeller)` : Inscription avec encodage BCrypt
- `login(request)` : Connexion avec génération JWT
- `getUserByEmail(email)` : Récupération utilisateur

### ProductService
- `listProducts(pageable, search)` : Liste paginée avec recherche
- `getProduct(id)` : Détail produit
- `convertToDto(product)` : Conversion entité → DTO

### CartService
- `getUserCart(userId)` : Panier utilisateur
- `addToCart(userId, dto)` : Ajout avec vérification stock
- `updateQuantity(cartItemId, quantity)` : Modification quantité
- `removeFromCart(cartItemId)` : Suppression article
- `clearCart(userId)` : Vider panier

### OrderService
- `createOrder(request)` : Création commande avec réduction stock
- `getUserOrders(userId)` : Historique commandes
- `getOrderById(orderId)` : Détail commande
- `updateOrderStatus(orderId, status)` : Changer statut (SELLER/ADMIN)

### CategoryService
- `getAllCategories()` : Toutes les catégories
- `createCategory(dto)` : Création (ADMIN)
- `updateCategory(id, dto)` : Modification (ADMIN)

### FavoriteService
- `getUserFavorites(userId)` : Liste favoris
- `addFavorite(productId, userId)` : Ajout
- `removeFavorite(productId, userId)` : Suppression
- `isFavorite(productId, userId)` : Vérification

### ReviewService
- `getProductReviews(productId, pageable)` : Avis paginés
- `getProductReviewStats(productId)` : Stats (moyenne, total)
- `createReview(dto)` : Création avis
- `deleteReview(reviewId, userId)` : Suppression

### AddressService
- `getUserAddresses(userId)` : Adresses utilisateur
- `createAddress(userId, dto)` : Création avec gestion "default"
- `updateAddress(id, userId, dto)` : Modification
- `deleteAddress(id, userId)` : Suppression

---

## 🔍 Fonctionnalités avancées

### Recherche et filtrage produits
```
GET /api/products?search=iphone&categoryId=4&brand=Apple&condition=like new&minPrice=100&maxPrice=500&page=0&size=20
```

### Pagination
Tous les endpoints de liste supportent la pagination Spring Data :
- `page` : numéro de page (0-based)
- `size` : taille de page
- `sort` : tri (ex: `sort=price,asc`)

### Validation
- Validation Bean Validation sur tous les DTOs
- Messages d'erreur personnalisés
- Gestion globale des exceptions

---

## 🎯 Prochaine étape : Frontend

Ton backend est **prêt pour le frontend** ! Tu peux maintenant :

### 1. Tester l'API
- Avec Swagger : `http://localhost:8080/swagger-ui.html`
- Avec Postman/Insomnia
- Avec curl

### 2. Créer le frontend (React/Vue/Angular)

**Exemple d'appel depuis React :**

```javascript
// Login
const login = async (email, password) => {
  const response = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password })
  });
  const data = await response.json();
  localStorage.setItem('token', data.token); // Sauvegarder le token
};

// Récupérer les produits
const getProducts = async () => {
  const response = await fetch('http://localhost:8080/api/products');
  return response.json();
};

// Ajouter au panier (avec auth)
const addToCart = async (listingId, quantity, userId) => {
  const token = localStorage.getItem('token');
  const response = await fetch(`http://localhost:8080/api/cart?userId=${userId}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}` // ✅ Token JWT
    },
    body: JSON.stringify({ listingId, quantity })
  });
  return response.json();
};
```

### 3. Pages frontend recommandées

- `/` : Homepage avec produits mis en avant
- `/products` : Liste produits avec filtres
- `/products/:id` : Détail produit + avis
- `/cart` : Panier
- `/checkout` : Tunnel de commande
- `/orders` : Mes commandes
- `/favorites` : Mes favoris
- `/login` : Connexion
- `/register` : Inscription
- `/profile` : Mon profil + adresses

---

## 📝 Notes importantes

### Mots de passe
- **En production** : Utiliser BCrypt (déjà configuré ✅)
- **En dev** : Comptes de test avec mots de passe simples

### CORS
Si tu as des erreurs CORS depuis le frontend, ajoute dans `SecurityConfig` :
```java
http.cors(cors -> cors.configurationSource(request -> {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:3000")); // React
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);
    return config;
}));
```

### Variables d'environnement
Pour la production, externalise les secrets :
```bash
export JWT_SECRET=ta-cle-secrete-tres-longue
export DB_PASSWORD=ton-mot-de-passe-db
```

---

## 🛠️ Technologies utilisées

- **Spring Boot 3.3.5** (Java 21)
- **Spring Security** avec JWT
- **Spring Data JPA** (Hibernate)
- **PostgreSQL**
- **Lombok** (réduction boilerplate)
- **Bean Validation** (jakarta.validation)
- **Springdoc OpenAPI** (Swagger)
- **JJWT 0.12.5** (JWT)
- **BCrypt** (hash passwords)

---

## ✅ Checklist complète

- [x] Authentification JWT
- [x] Gestion des rôles (USER, SELLER, ADMIN)
- [x] Tous les services métier
- [x] Tous les contrôleurs REST
- [x] Sécurisation des endpoints
- [x] Repositories avec requêtes personnalisées
- [x] DTOs avec validation
- [x] Gestion des erreurs
- [x] Jeu de données de test
- [x] Documentation Swagger
- [x] Encodage BCrypt des passwords

**Le backend est 100% prêt pour le frontend ! 🚀**

