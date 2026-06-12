-- USE SmarthomeDB;

--------------------------------------------------
-- USERS
--------------------------------------------------
INSERT INTO users (username, email, role, password_hash)
VALUES
    ('admin', 'admin@smarthome.com', 'Admin', '$2a$12$wLyZU0bA4OjlqtFs21Oh8eb0TUG/6mllW99/XM1t/uyC9xSNM0NWu'),
    ('john', 'john@gmail.com', 'ReadWriter', '$2a$12$MAqPpbZjY5oChuSrf6sF5O8enX3lZ9XEgRiJTmQvfoYMC0BFU2iU'),
    ('anna', 'anna@gmail.com', 'Reader', '$2a$12$MAqPpbZjY5oChuSrf6sF5O8enX3lZ9XEgRiJTmQvfoYMC0BFU2iU');

--------------------------------------------------
-- HOMES
--------------------------------------------------
INSERT INTO home (address, town, zipCode, user_id)
VALUES
    ('Main Street 10', 'Berlin', '10115', 2),
    ('Park Avenue 25', 'Munich', '80331', 3);

--------------------------------------------------
-- ROOMS
--------------------------------------------------
INSERT INTO room (name, floor, square, home_id)
VALUES
    ('Living Room', 'Ground Floor', 30.5, 1),
    ('Kitchen', 'Ground Floor', 15.0, 1),
    ('Bedroom', 'First Floor', 20.0, 1),

    ('Office', 'Ground Floor', 18.0, 2),
    ('Garage', 'Ground Floor', 25.0, 2);

--------------------------------------------------
-- DEVICE TYPES
--------------------------------------------------
INSERT INTO device_type (name, description)
VALUES
    ('Light', 'Smart lighting device'),
    ('Thermostat', 'Temperature control device'),
    ('Camera', 'Security camera'),
    ('Speaker', 'Smart speaker'),
    ('Door Lock', 'Smart lock');

--------------------------------------------------
-- DEVICES
--------------------------------------------------
INSERT INTO device
(name, room_id, device_type_id, installation_date, is_active)
VALUES
    ('Philips Hue Lamp', 1, 1, '2024-01-15', 1),
    ('Nest Thermostat', 1, 2, '2024-02-10', 1),
    ('Kitchen Light', 2, 1, '2024-03-05', 1),
    ('Bedroom Camera', 3, 3, '2024-04-12', 1),
    ('Alexa Speaker', 1, 4, '2024-05-01', 1),
    ('Garage Camera', 5, 3, '2024-01-20', 1),
    ('Office Lock', 4, 5, '2024-02-15', 0);

--------------------------------------------------
-- DEVICE_USER
--------------------------------------------------
INSERT INTO device_user (device_id, user_id)
VALUES
    (1, 2),
    (2, 2),
    (3, 2),
    (4, 2),
    (5, 2),

    (6, 3),
    (7, 3);

--------------------------------------------------
-- SCENARIOS
--------------------------------------------------
INSERT INTO scenario
(device_id, is_active, start_time, end_time)
VALUES
    ('Morning Lights', 1, '06:00', '08:00'),
    ('Night Security', 1, '22:00', '06:00'),
    ('Work Mode', 0, '09:00', '18:00');

--------------------------------------------------
-- DEVICE_SCENARIO
--------------------------------------------------
INSERT INTO device_scenario
(device_id, automation_id, role)
VALUES
    (1, 1, 'OUTPUT'),
    (3, 1, 'OUTPUT'),

    (4, 2, 'TRIGGER'),
    (6, 2, 'OUTPUT'),

    (7, 3, 'OUTPUT');

--------------------------------------------------
-- DEVICE STATE LOGS
--------------------------------------------------
INSERT INTO device_state_log
(device_id, user_id, timestamp, state_value)
VALUES
    (1, 2, '2025-01-01 07:00:00', 'ON'),
    (1, 2, '2025-01-01 23:00:00', 'OFF'),

    (2, 2, '2025-01-02 08:00:00', 'ON'),

    (4, 2, '2025-01-03 22:15:00', 'STANDBY'),

    (6, 3, '2025-01-04 20:00:00', 'ON'),
    (6, 3, '2025-01-04 23:30:00', 'OFF');