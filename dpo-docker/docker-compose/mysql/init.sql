CREATE DATABASE IF NOT EXISTS mydb;
USE mydb;

CREATE TABLE IF NOT EXISTS users (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(100)        NOT NULL,
    surname  VARCHAR(100)        NOT NULL,
    email    VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255)        NOT NULL
);

CREATE TABLE IF NOT EXISTS vehicles (
    license_plate    VARCHAR(10)  PRIMARY KEY,
    vehicle_type     VARCHAR(20)  NOT NULL
);

-- TODO: idk if id is auto
CREATE TABLE IF NOT EXISTS parking_slots (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    vehicle_type       VARCHAR(20)        NOT NULL,
    occupation_status  VARCHAR(20)        NOT NULL,
    reservation_status VARCHAR(20)        NOT NULL
);

CREATE TABLE IF NOT EXISTS reserves (
    user_id       INT         NOT NULL,
    license_plate VARCHAR(10) NOT NULL,
    slot_id       INT         NOT NULL,
    date          DATETIME    NOT NULL,

    PRIMARY KEY (user_id, license_plate, slot_id),

    FOREIGN KEY (user_id)       REFERENCES users(id),
    FOREIGN KEY (license_plate) REFERENCES vehicles(license_plate),
    FOREIGN KEY (slot_id)       REFERENCES parking_slots(identifier)
);