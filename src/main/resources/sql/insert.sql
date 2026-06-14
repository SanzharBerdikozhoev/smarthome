-- USE SmarthomeDB;

--------------------------------------------------
-- 1. USERS (1 Admin, 3 Regular Users)
--------------------------------------------------
INSERT INTO users (username, email, role, password_hash)
VALUES
    ('admin', 'admin@smarthome.com', 'ADMIN', '$2a$12$wLyZU0bA4OjlqtFs21Oh8eb0TUG/6mllW99/XM1t/uyC9xSNM0NWu'),
    ('john', 'john@gmail.com', 'WRITER', '$2a$12$MAqPpbZjY5oChuSrf6sF5O8enX3lZ9XEgRiJTmQvfoYMC0BFU2iU'),
    ('anna', 'anna@gmail.com', 'READER', '$2a$12$MAqPpbZjY5oChuSrf6sF5O8enX3lZ9XEgRiJTmQvfoYMC0BFU2iU'),
    ('alex', 'alex@gmail.com', 'READER', '$2a$12$MAqPpbZjY5oChuSrf6sF5O8enX3lZ9XEgRiJTmQvfoYMC0BFU2iU');

--------------------------------------------------
-- 2. HOMES (Админ тут отсутствует)
--------------------------------------------------
INSERT INTO home (address, town, zip_code, user_id)
VALUES
    ('Main Street 10', 'Berlin', '10115', 2), -- id: 1 (John's 1st home)
    ('Park Avenue 25', 'Munich', '80331', 2), -- id: 2 (John's 2nd home)
    ('Baker Street 221', 'London', 'NW16X', 3),-- id: 3 (Anna's home)
    ('Sunset Blvd 100', 'Los Angeles', '90001', 4);-- id: 4 (Alex's home)

--------------------------------------------------
-- 3. ROOMS
--------------------------------------------------
INSERT INTO room (name, floor, square, home_id)
VALUES
    -- Дом 1 (Берлин - John)
    ('Living Room', 'Ground Floor', 30.5, 1), -- id: 1
    ('Kitchen', 'Ground Floor', 15.0, 1),     -- id: 2
    ('Bedroom', 'First Floor', 20.0, 1),      -- id: 3

    -- Дом 2 (Мюнхен - John)
    ('Office', 'Ground Floor', 18.0, 2),      -- id: 4
    ('Garage', 'Ground Floor', 25.0, 2),      -- id: 5

    -- Дом 3 (Лондон - Anna)
    ('Studio', '1st Floor', 45.2, 3),         -- id: 6

    -- Дом 4 (Лос-Анджелес - Alex)
    ('Bed Room', '2nd Floor', 22.0, 4);       -- id: 7

--------------------------------------------------
-- 4. DEVICE TYPES
--------------------------------------------------
INSERT INTO device_type (name, description)
VALUES
    ('Light', 'Smart lighting device'),
    ('Thermostat', 'Temperature control device'),
    ('Camera', 'Security camera'),
    ('Speaker', 'Smart speaker'),
    ('Door Lock', 'Smart lock');

--------------------------------------------------
-- 5. DEVICES
--------------------------------------------------
INSERT INTO device (name, room_id, device_type_id, installation_date, is_active)
VALUES
    -- Устройства в домах John (Дома 1 и 2, комнаты 1-5)
    ('Philips Hue Lamp', 1, 1, '2024-01-15', 1), -- id: 1
    ('Nest Thermostat', 1, 2, '2024-02-10', 1),  -- id: 2
    ('Kitchen Light', 2, 1, '2024-03-05', 1),     -- id: 3
    ('Bedroom Camera', 3, 3, '2024-04-12', 1),    -- id: 4
    ('Alexa Speaker', 1, 4, '2024-05-01', 1),     -- id: 5
    ('Office Lock', 4, 5, '2024-02-15', 1),       -- id: 6
    ('Garage Camera', 5, 3, '2024-01-20', 1),     -- id: 7

    -- Устройства Anna (Дом 3, комната 6)
    ('Studio Light', 6, 1, '2024-06-01', 1),      -- id: 8

    -- Устройства Alex (Дом 4, комната 7)
    ('AC Thermostat', 7, 2, '2024-07-11', 0);     -- id: 9

--------------------------------------------------
-- 6. DEVICE_USER (Связь пользователей с устройствами)
--------------------------------------------------
INSERT INTO device_user (device_id, user_id)
VALUES
    -- John управляет своими устройствами (1-7)
    (1, 2), (2, 2), (3, 2), (4, 2), (5, 2), (6, 2), (7, 2),
    -- Anna управляет своим светом (8)
    (8, 3),
    -- Alex управляет своим кондиционером (9)
    (9, 4);

--------------------------------------------------
-- 7. SCENARIOS (Исправлены имена колонок)
--------------------------------------------------
INSERT INTO scenario (name, description, is_active, start_time, end_time)
VALUES
    ('Morning Lights', 'Turn on lights softly in the morning', 1, '06:00:00', '08:00:00'), -- id: 1
    ('Night Security', 'Activate cameras and locks at night', 1, '22:00:00', '06:00:00'),   -- id: 2
    ('Work Mode', 'Optimize climate for working hours', 0, '09:00:00', '18:00:00');         -- id: 3

--------------------------------------------------
-- 8. DEVICE_SCENARIO
--------------------------------------------------
INSERT INTO device_scenario (device_id, automation_id, role)
VALUES
    (1, 1, 'OUTPUT'),
    (3, 1, 'OUTPUT'),

    (4, 2, 'TRIGGER'),
    (7, 2, 'OUTPUT'), -- Изменено на id 7 (Garage Camera)

    (6, 3, 'OUTPUT');

--------------------------------------------------
-- 9. DEVICE STATE LOGS
--------------------------------------------------
INSERT INTO device_state_log (device_id, user_id, timestamp, state_value)
VALUES
    (1, 2, '2025-01-01 07:00:00', 'ON'),
    (1, 2, '2025-01-01 23:00:00', 'OFF'),
    (2, 2, '2025-01-02 08:00:00', 'ON'),
    (4, 2, '2025-01-03 22:15:00', 'STANDBY'),
    (8, 3, '2025-01-04 20:00:00', 'ON'),
    (8, 3, '2025-01-04 23:30:00', 'OFF'),
    (9, 4, '2025-01-05 12:00:00', 'STANDBY');