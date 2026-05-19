-- Create users table
CREATE TABLE users (
       id INT AUTO_INCREMENT PRIMARY KEY,
       name VARCHAR(50) NOT NULL,
       surname VARCHAR(50) NOT NULL,
       email VARCHAR(100) NOT NULL UNIQUE,
       password VARCHAR(255) NOT NULL,
       role VARCHAR(255) NOT NULL
);

-- Create vehicles table
CREATE TABLE vehicles (
      license_plate VARCHAR(20) PRIMARY KEY,
      user_id       int(11) NOT NULL,
      vehicle_type VARCHAR(30) NOT NULL
);

-- Create parking_slots table
CREATE TABLE parking_slots (
   identifier INT PRIMARY KEY,
   floor      INT NOT NULL,
   vehicle_type VARCHAR(30) NOT NULL,
   occupation_status BOOLEAN NOT NULL DEFAULT FALSE,
   parked_license_plate VARCHAR(20) NULL DEFAULT NULL,
   FOREIGN KEY (parked_license_plate) REFERENCES vehicles(license_plate)
);

-- Create reservations table (ternary relationship: User 1 - Vehicle 1 - Parking Slot N)
CREATE TABLE reservations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    vehicle_license_plate VARCHAR(20) NOT NULL,
    parking_slot_id INT NOT NULL,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    is_cancelled BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (vehicle_license_plate) REFERENCES vehicles(license_plate),
    FOREIGN KEY (parking_slot_id) REFERENCES parking_slots(identifier)
);

-- migration 001 — add parking_log table
CREATE TABLE parking_log (
     id              INT AUTO_INCREMENT PRIMARY KEY,
     parking_slot_id INT         NOT NULL,
     license_plate   VARCHAR(20) NOT NULL,
     user_id         INT         NOT NULL,
     action          VARCHAR(10) NOT NULL,
     timestamp       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Insert test data
INSERT INTO users (name, surname, email, password, role) VALUES
     ('Admin', 'User', 'admin@lsparking.com', 'admin123', 'admin'),
     ('Test', 'User', 'test@lsparking.com', 'test123', 'user');

INSERT INTO vehicles (license_plate, user_id, vehicle_type) VALUES
    ('1234ABC', '1', 'car'),
    ('5678DEF', '2', 'motorcycle');

INSERT INTO parking_slots (identifier, vehicle_type, occupation_status, floor, parked_license_plate) VALUES
(1, 'car', FALSE, 0, NULL),
(2, 'car',  FALSE, 1, NULL),
(3, 'motorcycle', FALSE, 0, NULL),
(4, 'motorcycle', FALSE, 1, NULL);



