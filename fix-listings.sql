-- Script de migration pour corriger le problème des listings vides
-- À exécuter MANUELLEMENT dans pgAdmin ou psql quand vous voulez

-- Étape 1: Ajouter les contraintes UNIQUE manquantes
ALTER TABLE seller_profiles ADD CONSTRAINT IF NOT EXISTS seller_profiles_user_id_key UNIQUE (user_id);
ALTER TABLE seller_profiles ADD CONSTRAINT IF NOT EXISTS seller_profiles_contact_email_key UNIQUE (contact_email);

-- Étape 2: Insérer le profil vendeur Alice (nécessaire pour les listings)
INSERT INTO seller_profiles (shop_name, description, contact_email, user_id)
SELECT 'Alice Shop', 'Boutique de vêtements et accessoires', 'alice.shop@example.com', id
FROM users WHERE email='alice@example.com'
ON CONFLICT (user_id) DO NOTHING;

-- Étape 3: Insérer les listings pour tous les produits
INSERT INTO listings (product_id, seller_id, price, quantity, condition_note, active)
SELECT p.id, s.id, 19.90, 100, 'Neuf', TRUE
FROM products p, seller_profiles s
WHERE p.brand='N2S' AND p.model='TS-001' AND s.contact_email='alice.shop@example.com'
ON CONFLICT (product_id, seller_id) DO NOTHING;

INSERT INTO listings (product_id, seller_id, price, quantity, condition_note, active)
SELECT p.id, s.id, 19.90, 80, 'Neuf', TRUE
FROM products p, seller_profiles s
WHERE p.brand='N2S' AND p.model='TS-002' AND s.contact_email='alice.shop@example.com'
ON CONFLICT (product_id, seller_id) DO NOTHING;

INSERT INTO listings (product_id, seller_id, price, quantity, condition_note, active)
SELECT p.id, s.id, 49.90, 50, 'Neuf', TRUE
FROM products p, seller_profiles s
WHERE p.brand='N2S' AND p.model='HD-001' AND s.contact_email='alice.shop@example.com'
ON CONFLICT (product_id, seller_id) DO NOTHING;

INSERT INTO listings (product_id, seller_id, price, quantity, condition_note, active)
SELECT p.id, s.id, 14.90, 150, 'Neuf', TRUE
FROM products p, seller_profiles s
WHERE p.brand='N2S' AND p.model='CP-001' AND s.contact_email='alice.shop@example.com'
ON CONFLICT (product_id, seller_id) DO NOTHING;

INSERT INTO listings (product_id, seller_id, price, quantity, condition_note, active)
SELECT p.id, s.id, 699.00, 20, 'Comme neuf, reconditionné', TRUE
FROM products p, seller_profiles s
WHERE p.brand='Phonix' AND p.model='X-128' AND s.contact_email='alice.shop@example.com'
ON CONFLICT (product_id, seller_id) DO NOTHING;

INSERT INTO listings (product_id, seller_id, price, quantity, condition_note, active)
SELECT p.id, s.id, 129.00, 35, 'Neuf', TRUE
FROM products p, seller_profiles s
WHERE p.brand='SoundMax' AND p.model='SM-Pro' AND s.contact_email='alice.shop@example.com'
ON CONFLICT (product_id, seller_id) DO NOTHING;

-- Vérification
SELECT 'Nombre de seller_profiles:' as info, COUNT(*) as count FROM seller_profiles
UNION ALL
SELECT 'Nombre de listings:', COUNT(*) FROM listings
UNION ALL
SELECT 'Nombre de products:', COUNT(*) FROM products;

-- Afficher les listings créés
SELECT l.id, p.title, l.price, l.quantity, l.active
FROM listings l
JOIN products p ON l.product_id = p.id
ORDER BY l.id;

