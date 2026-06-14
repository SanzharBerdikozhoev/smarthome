-- use SmarthomeDB;

CREATE TABLE users
(
    id            INT PRIMARY KEY IDENTITY (1,1),
    username      VARCHAR(50)  NOT NULL UNIQUE,
    email         VARCHAR(100) NOT NULL UNIQUE,
    role          VARCHAR(20)  NOT NULL DEFAULT 'READER',
    password_hash VARCHAR(255) NOT NULL,
    created_at    DATETIME     NOT NULL DEFAULT GETDATE(),

    CONSTRAINT ck_user_role
        CHECK (role IN ('ADMIN', 'WRITER', 'READER'))
);

CREATE TABLE home
(
    id      INT PRIMARY KEY IDENTITY (1,1),
    address VARCHAR(150) NOT NULL,
    town    VARCHAR(150) NOT NULL,
    zip_code VARCHAR(5)   NOT NULL,
    user_id INT          NOT NULL,

    CONSTRAINT fk_home_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);

CREATE TABLE room
(
    id      INT PRIMARY KEY IDENTITY (1,1),
    name    VARCHAR(50) NOT NULL,
    floor   VARCHAR(30) NOT NULL,
    square  FLOAT       NOT NULL CHECK (square > 0),
    home_id INT         NOT NULL,

    CONSTRAINT fk_room_home
        FOREIGN KEY (home_id)
            REFERENCES home (id)
            ON DELETE CASCADE
);

CREATE TABLE device_type
(
    id          INT PRIMARY KEY IDENTITY (1,1),
    name        VARCHAR(50)  NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL
);

CREATE TABLE device
(
    id                INT PRIMARY KEY IDENTITY (1,1),
    name              VARCHAR(100) NOT NULL,
    room_id           INT          NOT NULL,
    device_type_id    INT          NOT NULL,
    installation_date DATE         NOT NULL,
    is_active         BIT          NOT NULL DEFAULT 1,

    CONSTRAINT fk_device_room
        FOREIGN KEY (room_id)
            REFERENCES room (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_device_device_type
        FOREIGN KEY (device_type_id)
            REFERENCES device_type (id)
            ON DELETE CASCADE
);

CREATE TABLE device_state_log
(
    id          INT PRIMARY KEY IDENTITY (1,1),
    device_id   INT         NOT NULL,
    user_id     INT         NOT NULL,
    timestamp   DATETIME    NOT NULL DEFAULT GETDATE(),
    state_value VARCHAR(20) NOT NULL,

    CONSTRAINT fk_state_log_device
        FOREIGN KEY (device_id)
            REFERENCES device (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_state_log_users
        FOREIGN KEY (user_id)
            REFERENCES users (id),

    CONSTRAINT ck_state_value
        CHECK (state_value IN ('ON', 'OFF', 'STANDBY'))
);

CREATE TABLE scenario
(
    id          INT PRIMARY KEY IDENTITY (1,1),
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    is_active   BIT          NOT NULL DEFAULT 1,
    start_time  TIME         NOT NULL,
    end_time    TIME         NOT NULL
);

CREATE TABLE device_user
(
    device_id      INT  NOT NULL,
    user_id        INT  NOT NULL,
    assigned_since DATE NOT NULL DEFAULT CAST(GETDATE() AS DATE),

    PRIMARY KEY (device_id, user_id),

    CONSTRAINT fk_device_user_device
        FOREIGN KEY (device_id)
            REFERENCES device (id)
            ON DELETE NO ACTION,

    CONSTRAINT fk_device_user_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE NO ACTION
);

CREATE TABLE device_scenario
(
    device_id     INT         NOT NULL,
    automation_id INT         NOT NULL,
    role          VARCHAR(30) NOT NULL DEFAULT 'OUTPUT',

    PRIMARY KEY (device_id, automation_id),

    CONSTRAINT fk_device_automation_device
        FOREIGN KEY (device_id)
            REFERENCES device (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_device_automation_automation
        FOREIGN KEY (automation_id)
            REFERENCES scenario (id)
            ON DELETE CASCADE,

    CONSTRAINT ck_automation_role
        CHECK (role IN (
                        'TRIGGER',
                        'OUTPUT',
                        'CONDITION_SENSOR'
            ))
);