-- Insert roles if they don't exist
INSERT INTO roles (role_name) 
SELECT 'ROLE_ADMIN' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'ROLE_ADMIN');
INSERT INTO roles (role_name) 
SELECT 'ROLE_PARTNER' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'ROLE_PARTNER');
INSERT INTO roles (role_name) 
SELECT 'ROLE_USER' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'ROLE_USER');

-- Create admin user
INSERT INTO user (
    user_email, 
    user_password, 
    user_first_name,
    user_last_name,
    user_address,
    user_birth_date,
    user_cin,
    user_profile_photo,
    emergency_contact_email
)
SELECT 
    'admin@speedygo.com',
    '$2a$10$rDmFN6ZJvwFqMz1qKq.1/.X3TqX5Q5Q5Q5Q5Q5Q5Q5Q5Q5Q5Q',
    'Admin',
    'User',
    '123 Admin Street',
    '1990-01-01',
    'ADMIN123456',
    NULL,
    'admin.emergency@speedygo.com'
WHERE NOT EXISTS (SELECT 1 FROM user WHERE user_email = 'admin@speedygo.com');

-- Create admin entry
INSERT INTO admin (user_id)
SELECT user_id FROM user WHERE user_email = 'admin@speedygo.com'
AND NOT EXISTS (SELECT 1 FROM admin WHERE user_id = user.user_id);

-- Create partner user
INSERT INTO user (
    user_email, 
    user_password, 
    user_first_name,
    user_last_name,
    user_address,
    user_birth_date,
    user_cin,
    user_profile_photo,
    emergency_contact_email
)
SELECT 
    'partner@speedygo.com',
    '$2a$10$rDmFN6ZJvwFqMz1qKq.1/.X3TqX5Q5Q5Q5Q5Q5Q5Q5Q5Q5Q5Q',
    'Partner',
    'User',
    '123 Partner Street',
    '1990-01-01',
    'PARTNER123456',
    NULL,
    'partner.emergency@speedygo.com'
WHERE NOT EXISTS (SELECT 1 FROM user WHERE user_email = 'partner@speedygo.com');

-- Create simple user entry
INSERT INTO simple_user (user_id)
SELECT user_id FROM user WHERE user_email = 'partner@speedygo.com'
AND NOT EXISTS (SELECT 1 FROM simple_user WHERE user_id = user.user_id);

-- Create regular user
INSERT INTO user (
    user_email, 
    user_password, 
    user_first_name,
    user_last_name,
    user_address,
    user_birth_date,
    user_cin,
    user_profile_photo,
    emergency_contact_email
)
SELECT 
    'user@speedygo.com',
    '$2a$10$rDmFN6ZJvwFqMz1qKq.1/.X3TqX5Q5Q5Q5Q5Q5Q5Q5Q5Q5Q5Q',
    'Regular',
    'User',
    '123 User Street',
    '1990-01-01',
    'USER123456',
    NULL,
    'user.emergency@speedygo.com'
WHERE NOT EXISTS (SELECT 1 FROM user WHERE user_email = 'user@speedygo.com');

-- Create simple user entry for regular user
INSERT INTO simple_user (user_id)
SELECT user_id FROM user WHERE user_email = 'user@speedygo.com'
AND NOT EXISTS (SELECT 1 FROM simple_user WHERE user_id = user.user_id);

-- Assign roles to users
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin' AND r.role_name = 'ROLE_ADMIN'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = u.id AND role_id = r.id);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'partner' AND r.role_name = 'ROLE_PARTNER'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = u.id AND role_id = r.id);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'user' AND r.role_name = 'ROLE_USER'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = u.id AND role_id = r.id);

-- Grant necessary permissions
INSERT INTO permissions (permission_name)
SELECT 'READ_COMMISSION' WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE permission_name = 'READ_COMMISSION');
INSERT INTO permissions (permission_name)
SELECT 'WRITE_COMMISSION' WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE permission_name = 'WRITE_COMMISSION');
INSERT INTO permissions (permission_name)
SELECT 'READ_PARTNER' WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE permission_name = 'READ_PARTNER');
INSERT INTO permissions (permission_name)
SELECT 'WRITE_PARTNER' WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE permission_name = 'WRITE_PARTNER');

-- Assign permissions to roles
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.role_name = 'ROLE_ADMIN'
AND p.permission_name IN ('READ_COMMISSION', 'WRITE_COMMISSION', 'READ_PARTNER', 'WRITE_PARTNER')
AND NOT EXISTS (SELECT 1 FROM role_permissions WHERE role_id = r.id AND permission_id = p.id);

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.role_name = 'ROLE_PARTNER'
AND p.permission_name IN ('READ_COMMISSION', 'READ_PARTNER')
AND NOT EXISTS (SELECT 1 FROM role_permissions WHERE role_id = r.id AND permission_id = p.id);

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.role_name = 'ROLE_USER'
AND p.permission_name IN ('READ_PARTNER')
AND NOT EXISTS (SELECT 1 FROM role_permissions WHERE role_id = r.id AND permission_id = p.id); 