-- Seed de base pour tests front - Script rejouable sans doublons
-- Passwords BCrypt: "password123" sauf admin = "admin123"

-- Utilisateurs
INSERT INTO users (email, password, display_name, created_at) VALUES
('admin@example.com', '$2a$10$XcN/5xtpDyqQxCpqvJGpjOq9p8vZCfJzZPqVfUhGDXxA6YrPXqp4W', 'Admin Root', NOW()),
('alice@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Alice Martin', NOW()),
('bob@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Bob Durand', NOW())
ON CONFLICT (email) DO NOTHING;

-- Rôles
INSERT INTO user_roles (user_id, roles)
SELECT id, 'ROLE_ADMIN' FROM users WHERE email = 'admin@example.com'
ON CONFLICT ON CONSTRAINT user_roles_user_id_roles_key DO NOTHING;

INSERT INTO user_roles (user_id, roles)
SELECT id, 'ROLE_USER' FROM users WHERE email = 'alice@example.com'
ON CONFLICT ON CONSTRAINT user_roles_user_id_roles_key DO NOTHING;

INSERT INTO user_roles (user_id, roles)
SELECT id, 'ROLE_SELLER' FROM users WHERE email = 'alice@example.com'
ON CONFLICT ON CONSTRAINT user_roles_user_id_roles_key DO NOTHING;

INSERT INTO user_roles (user_id, roles)
SELECT id, 'ROLE_USER' FROM users WHERE email = 'bob@example.com'
ON CONFLICT ON CONSTRAINT user_roles_user_id_roles_key DO NOTHING;

-- Profils vendeurs
INSERT INTO seller_profiles (shop_name, description, contact_email, user_id)
VALUES ('Alice Shop', 'Boutique de vêtements et accessoires', 'alice.shop@example.com',
        (SELECT id FROM users WHERE email='alice@example.com'))
ON CONFLICT (user_id) DO NOTHING;

-- Catégories
INSERT INTO categories (name, description) VALUES
('Vêtements', 'Vêtements pour hommes et femmes'),
('Accessoires', 'Accessoires de mode'),
('Electronique', 'Appareils et gadgets'),
('Maison', 'Articles pour la maison'),
('Sports', 'Equipements sportifs')
ON CONFLICT (name) DO NOTHING;

-- Sous-catégories
INSERT INTO categories (name, description, parent_id) VALUES
('T-Shirts', 'T-shirts coton et techniques', (SELECT id FROM categories WHERE name='Vêtements' LIMIT 1)),
('Sweats & Hoodies', 'Sweats à capuche et pulls', (SELECT id FROM categories WHERE name='Vêtements' LIMIT 1)),
('Casquettes', 'Casquettes et bonnets', (SELECT id FROM categories WHERE name='Accessoires' LIMIT 1)),
('Smartphones', 'Téléphones intelligents', (SELECT id FROM categories WHERE name='Electronique' LIMIT 1)),
('Audio', 'Casques et enceintes', (SELECT id FROM categories WHERE name='Electronique' LIMIT 1))
ON CONFLICT (name) DO NOTHING;

-- Produits
INSERT INTO products (title, description, category_id, brand, model, condition) VALUES
('T-Shirt Blanc', 'T-Shirt coton blanc 180g/m²', (SELECT id FROM categories WHERE name='T-Shirts' LIMIT 1), 'N2S', 'TS-001', 'new'),
('T-Shirt Noir', 'T-Shirt coton noir 180g/m²', (SELECT id FROM categories WHERE name='T-Shirts' LIMIT 1), 'N2S', 'TS-002', 'new'),
('Hoodie Gris', 'Sweat à capuche gris confortable', (SELECT id FROM categories WHERE name='Sweats & Hoodies' LIMIT 1), 'N2S', 'HD-001', 'new'),
('Casquette Bleue', 'Casquette bleue logo brodé', (SELECT id FROM categories WHERE name='Casquettes' LIMIT 1), 'N2S', 'CP-001', 'new'),
('Smartphone X', 'Smartphone 128Go, écran OLED', (SELECT id FROM categories WHERE name='Smartphones' LIMIT 1), 'Phonix', 'X-128', 'like new'),
('Casque Audio Pro', 'Casque circum-aural haute fidélité', (SELECT id FROM categories WHERE name='Audio' LIMIT 1), 'SoundMax', 'SM-Pro', 'new')
ON CONFLICT (brand, model) DO NOTHING;

-- Images produits
INSERT INTO product_images (url, alt_text, product_id) VALUES
('https://res.cloudinary.com/dfwfwhsl6/image/upload/e_gen_background_replace:prompt_Light%20blue%20background%20with%20soft%20reflections/samples/shoe.jpg', 'T-Shirt Blanc', (SELECT id FROM products WHERE brand='N2S' AND model='TS-001' LIMIT 1)),
('/images/ts_noir.jpg', 'T-Shirt Noir', (SELECT id FROM products WHERE brand='N2S' AND model='TS-002' LIMIT 1)),
('/images/hoodie_gris.jpg', 'Hoodie Gris', (SELECT id FROM products WHERE brand='N2S' AND model='HD-001' LIMIT 1)),
('/images/casquette_bleue.jpg', 'Casquette Bleue', (SELECT id FROM products WHERE brand='N2S' AND model='CP-001' LIMIT 1)),
('/images/smartphone_x.jpg', 'Smartphone X', (SELECT id FROM products WHERE brand='Phonix' AND model='X-128' LIMIT 1)),
('/images/casque_pro.jpg', 'Casque Audio Pro', (SELECT id FROM products WHERE brand='SoundMax' AND model='SM-Pro' LIMIT 1))
ON CONFLICT (url) DO NOTHING;

-- Listings (offres de vente)
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

-- Adresses
INSERT INTO addresses (user_id, full_name, street, city, postal_code, country, phone, is_default)
SELECT id, 'Alice Martin', '12 Rue des Fleurs', 'Paris', '75001', 'France', '+33123456789', TRUE
FROM users WHERE email='alice@example.com'
ON CONFLICT DO NOTHING;

INSERT INTO addresses (user_id, full_name, street, city, postal_code, country, phone, is_default)
SELECT id, 'Bob Durand', '8 Quai de la Charente', 'Lyon', '69002', 'France', '+33411223344', TRUE
FROM users WHERE email='bob@example.com'
ON CONFLICT DO NOTHING;

-- Favoris
INSERT INTO favorites (user_id, product_id, added_at)
SELECT u.id, p.id, NOW()
FROM users u, products p
WHERE u.email='alice@example.com' AND p.brand='N2S' AND p.model='TS-001'
ON CONFLICT DO NOTHING;

INSERT INTO favorites (user_id, product_id, added_at)
SELECT u.id, p.id, NOW()
FROM users u, products p
WHERE u.email='alice@example.com' AND p.brand='N2S' AND p.model='HD-001'
ON CONFLICT DO NOTHING;

