-- Drop in correct order
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS parking_slots;
DROP TABLE IF EXISTS vehicles;
DROP TABLE IF EXISTS users;

-- Create tables
CREATE TABLE users(
      id INT AUTO_INCREMENT PRIMARY KEY,
      name VARCHAR(50) NOT NULL,
      surname VARCHAR(50) NOT NULL,
      email VARCHAR(100) NOT NULL UNIQUE,
      password VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE vehicles (
      license_plate VARCHAR(20) PRIMARY KEY,
      vehicle_type VARCHAR(30) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE parking_slots (
       identifier INT PRIMARY KEY,
       vehicle_type VARCHAR(30) NOT NULL,
       occupation_status BOOLEAN NOT NULL DEFAULT FALSE,
       reservation_status BOOLEAN NOT NULL DEFAULT FALSE
) ENGINE=InnoDB;

CREATE TABLE reservations (
      id INT AUTO_INCREMENT PRIMARY KEY,
      user_id INT NOT NULL,
      vehicle_license_plate VARCHAR(20) NOT NULL,
      parking_slot_id INT NOT NULL,
      date DATETIME NOT NULL,
      FOREIGN KEY (user_id) REFERENCES users(id),
      FOREIGN KEY (vehicle_license_plate) REFERENCES vehicles(license_plate),
      FOREIGN KEY (parking_slot_id) REFERENCES parking_slots(identifier)
) ENGINE=InnoDB;

-- Insert data (NO TRUNCATE needed because tables are new)
INSERT INTO users (name, surname, email, password) VALUES
       ('Admin', 'User', 'admin@lsparking.com', 'admin123'),
       ('Test', 'User', 'test@lsparking.com', 'test123');

INSERT INTO vehicles (license_plate, vehicle_type) VALUES
       ('1234ABC', 'car'),
       ('5678DEF', 'motorcycle');

INSERT INTO parking_slots (identifier, vehicle_type, occupation_status, reservation_status) VALUES
        (1, 'car', FALSE, FALSE),
        (2, 'car', FALSE, FALSE),
        (3, 'motorcycle', FALSE, FALSE),
        (4, 'motorcycle', FALSE, FALSE);