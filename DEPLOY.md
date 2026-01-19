# Guide de déploiement - Infotech SS

## 📊 ÉTAPE 1 : DÉPLOYER SUR SUPABASE

### 1.1 Créer le projet Supabase

1. Aller sur https://supabase.com
2. Se connecter / S'inscrire avec GitHub
3. Cliquer sur **"New Project"**
4. Remplir :
   - Project name : `infotech-ss`
   - Database Password : **NOTEZ CE MOT DE PASSE !**
   - Region : `Europe (Frankfurt)`
   - Plan : Free tier
5. Cliquer sur **"Create new project"** (attend ~2 minutes)

### 1.2 Importer le schéma

1. Dans le dashboard Supabase, aller dans **SQL Editor**
2. Cliquer sur **"New query"**
3. Ouvrir le fichier `schema-supabase.sql` de votre projet
4. Copier TOUT le contenu
5. Coller dans l'éditeur SQL de Supabase
6. Cliquer sur **"Run"** (ou Cmd/Ctrl + Enter)
7. ✅ Vous devriez voir "Success. No rows returned"

### 1.3 (Optionnel) Importer les données

Si vous voulez migrer vos données de développement :

1. Dans le SQL Editor, créer une **nouvelle requête**
2. Ouvrir le fichier `data-supabase.sql`
3. Copier le contenu
4. Coller et **Run**

### 1.4 Récupérer les informations de connexion

1. Aller dans **Settings** → **Database**
2. Copier la **Connection string** (mode URI) :
   ```
   postgresql://postgres.xxx:[PASSWORD]@aws-0-eu-central-1.pooler.supabase.com:6543/postgres
   ```
3. **NOTEZ CETTE URL** - vous en aurez besoin pour Render

---

## 🚀 ÉTAPE 2 : DÉPLOYER SUR RENDER

### 2.1 Préparer votre code

✅ Les fichiers suivants sont déjà configurés :
- `render.yaml` - Configuration Render
- `src/main/resources/application-prod.properties` - Configuration production

### 2.2 Push sur GitHub

```bash
# Ajouter tous les fichiers
git add .

# Commit
git commit -m "Prepare for deployment with Supabase and Render"

# Push sur GitHub (créez un repo si nécessaire)
git push origin main
```

### 2.3 Créer le service sur Render

1. Aller sur https://render.com
2. Se connecter / S'inscrire avec GitHub
3. Cliquer sur **"New +" → "Web Service"**
4. Connecter votre repository GitHub `infotech-ss`
5. Configurer le service :
   - **Name** : `infotech-ss-backend`
   - **Region** : `Frankfurt (EU Central)`
   - **Branch** : `main`
   - **Runtime** : `Java`
   - **Build Command** : `./mvnw clean package -DskipTests`
   - **Start Command** : `java -Dserver.port=$PORT -jar target/*.jar --spring.profiles.active=prod`
   - **Plan** : Free

### 2.4 Configurer les variables d'environnement

Dans **Environment** → **Environment Variables**, ajouter :

```bash
# Base de données Supabase
DATABASE_URL=postgresql://postgres.xxx:[PASSWORD]@aws-0-eu-central-1.pooler.supabase.com:6543/postgres

# JWT
JWT_SECRET=VotreCleSuperSecreteJWTMinimum256BitsIciPourLaProduction
JWT_EXPIRATION=86400000

# Email (Gmail SMTP)
MAIL_USERNAME=votre-email@gmail.com
MAIL_PASSWORD=votre-app-password
MAIL_FROM=votre-email@gmail.com

# Cloudinary
CLOUDINARY_CLOUD_NAME=votre_cloud_name
CLOUDINARY_API_KEY=votre_api_key
CLOUDINARY_API_SECRET=votre_api_secret

# Stripe
STRIPE_SECRET_KEY=sk_live_VOTRE_CLE_LIVE (ou sk_test pour les tests)
STRIPE_PUBLIC_KEY=pk_live_VOTRE_CLE_LIVE (ou pk_test pour les tests)

# Spring Profile
SPRING_PROFILES_ACTIVE=prod
```

### 2.5 Déployer

1. Cliquer sur **"Create Web Service"**
2. Render va automatiquement :
   - Cloner votre repo
   - Builder avec Maven
   - Démarrer votre application
3. ⏳ Attendez ~10 minutes pour le premier déploiement

### 2.6 Vérifier le déploiement

Une fois déployé, votre API sera disponible à :
```
https://infotech-ss-backend.onrender.com
```

Testez :
```bash
curl https://infotech-ss-backend.onrender.com/actuator/health
```

Devrait retourner : `{"status":"UP"}`

---

## 🔧 CONFIGURATION FRONTEND

Mettre à jour l'URL de l'API dans votre frontend :

```javascript
const API_URL = "https://infotech-ss-backend.onrender.com/api"
```

---

## ✅ CHECKLIST POST-DÉPLOIEMENT

- [ ] Base de données Supabase créée
- [ ] Schéma importé dans Supabase
- [ ] Connection string Supabase récupérée
- [ ] Code pushé sur GitHub
- [ ] Service Render créé et configuré
- [ ] Variables d'environnement ajoutées sur Render
- [ ] Déploiement réussi (status UP)
- [ ] Test de l'endpoint /actuator/health
- [ ] Frontend configuré avec la nouvelle URL
- [ ] Test complet du flow (login, création produit, paiement)

---

## 🐛 DÉPANNAGE

### Erreur de connexion à la base de données

Vérifiez que :
- La connection string Supabase est correcte
- Le mot de passe ne contient pas de caractères spéciaux non encodés
- La variable `DATABASE_URL` est bien définie sur Render

### Build échoue sur Render

- Vérifiez les logs de build
- Assurez-vous que `mvnw` a les permissions d'exécution :
  ```bash
  git update-index --chmod=+x mvnw
  git commit -m "Fix mvnw permissions"
  git push
  ```

### Application crash au démarrage

- Vérifiez les logs dans Render
- Assurez-vous que toutes les variables d'environnement sont définies
- Vérifiez que `SPRING_PROFILES_ACTIVE=prod`

---

## 📚 RESSOURCES

- [Documentation Supabase](https://supabase.com/docs)
- [Documentation Render](https://render.com/docs)
- [Migration PostgreSQL vers Supabase](https://supabase.com/docs/guides/resources/migrating-to-supabase/postgres)
