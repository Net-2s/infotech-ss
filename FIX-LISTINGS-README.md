# PROBLÈME: Table LISTINGS vide - SOLUTION

## 🔴 PROBLÈME
Quand vous essayez d'ajouter un produit au panier, vous avez l'erreur:
```
RuntimeException: Listing not found
```

## 🔍 CAUSE
La table `listings` est vide car:
1. La table `seller_profiles` n'a pas de contrainte UNIQUE sur `user_id`
2. Le script `data.sql` utilise `ON CONFLICT DO NOTHING` mais sans contrainte, ça ne marche pas
3. Sans profil vendeur, impossible de créer des listings
4. Sans listings, impossible d'ajouter au panier

## ✅ SOLUTION

### Exécutez le script SQL que j'ai créé:

**Option 1: Via Terminal**
```bash
psql -U emmanuel -d infotech -f fix-listings.sql
```

**Option 2: Via pgAdmin**
1. Ouvrez pgAdmin
2. Connectez-vous à la base `infotech`
3. Clic droit sur la base → Query Tool
4. Menu File → Open → Sélectionnez `fix-listings.sql`
5. Appuyez sur F5 (ou cliquez sur Execute)

### Ce que fait le script:
1. ✅ Ajoute la contrainte UNIQUE sur `seller_profiles(user_id)`
2. ✅ Insère le profil vendeur "Alice Shop"
3. ✅ Crée les 6 listings pour tous les produits
4. ✅ Affiche un résumé pour vérifier

## 📋 VÉRIFICATION

Après avoir exécuté le script, vous devriez voir:
- 1 profil vendeur (Alice Shop)
- 6 listings (un pour chaque produit)
- 6 produits

## 🎯 RÉSULTAT

Après cela, vous pourrez:
✅ Voir les listings via l'API: `GET /api/listings`
✅ Ajouter des produits au panier: `POST /api/user/cart`
✅ Créer des commandes

## 📝 POUR LE FUTUR

J'ai corrigé le fichier `data.sql` pour que ça fonctionne automatiquement aux prochains redémarrages de l'application.

