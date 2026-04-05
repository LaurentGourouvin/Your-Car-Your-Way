-- Insertion des rôles
INSERT INTO role (id, name, description) VALUES
    ('a0000000-0000-0000-0000-000000000001', 'CLIENT', 'Utilisateur client'),
    ('a0000000-0000-0000-0000-000000000002', 'SUPPORT', 'Agent support'),
    ('a0000000-0000-0000-0000-000000000003', 'ADMIN', 'Administrateur');

-- Insertion des utilisateurs de test
-- Mot de passe : password123 (hashé avec bcrypt strength 12)
INSERT INTO "user" (id, email, password_hash, first_name, last_name) VALUES
    ('b0000000-0000-0000-0000-000000000001', 'client@test.com', '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Jean', 'Dupont'),
    ('b0000000-0000-0000-0000-000000000002', 'agent@test.com', '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Marie', 'Support');

-- Assignation des rôles
INSERT INTO user_role (user_id, role_id) VALUES
    ('b0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001'),
    ('b0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000002');

-- Profil agent pour Marie
INSERT INTO agent_profile (user_id, available) VALUES
    ('b0000000-0000-0000-0000-000000000002', true);
