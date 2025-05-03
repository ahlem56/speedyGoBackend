-- Create new partner user with known password
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
    'partner2@speedygo.com',
    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG',  -- This is a known working hash for 'password123'
    'Partner',
    'User',
    '123 Partner Street',
    '1990-01-01',
    'PARTNER123456',
    NULL,
    'partner.emergency@speedygo.com'
WHERE NOT EXISTS (SELECT 1 FROM user WHERE user_email = 'partner2@speedygo.com');

-- Create simple user entry for the new partner
INSERT INTO simple_user (user_id)
SELECT user_id FROM user WHERE user_email = 'partner2@speedygo.com'
AND NOT EXISTS (SELECT 1 FROM simple_user WHERE user_id = user.user_id); 