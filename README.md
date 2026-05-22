# The Parking — P2_OOPD

> Role-based parking management system with real-time traffic simulation, built with Java Swing and MySQL.

## Screenshots

_No screenshots committed. Add application captures here._

## Tech Stack

| Layer        | Technology                      |
|--------------|---------------------------------|
| UI           | Java Swing (MVC, CardLayout)    |
| Persistence  | JDBC + MySQL 8                  |
| Auth         | jBCrypt (password hashing)      |
| Local DB     | XAMPPok  + phpMyAdmin           |
| JDBC driver  | mysql-connector-java-8.0.23.jar |
| IDE          | IntelliJ IDEA                   |
| Java version | 26 (preview)                    |

## Architecture

Three-package MVC under `src/`:
- **`Presentation.views`** — `MainWindow` (JFrame, CardLayout root), `AuthPanel`, `DashboardPanel` (role-aware sidebar), and one panel per feature (entry/exit, slots, reservations, occupancy chart, status).
- **`Presentation.controllers`** — thin wiring layer (`AuthController`, `EntryExitController`, `ParkingSpaceController`, `ReservationController`, `StatusController`); no business logic.
- **`Business`** — domain managers (`AuthManager`, `ParkingLotManager`, `VehicleManager`, `ReservationManager`, `ParkingLogManager`, `SimulationManager`) and a `SessionManager` singleton; entities under `Business.Entities`.
- **`Persistance`** — DAO interfaces with `*DAOSql` implementations; `ConfigDAO` owns the JDBC connection and reads `config.json`.

All DAOs are wired manually in `MainWindow` constructor — no DI framework.

## Installation

### Prerequisites

- Java 26+
- IntelliJ IDEA Community
- MySQL 8 on port 3306 (XAMPP or Docker)

### Steps

1. Clone the repo.
2. Open the project in IntelliJ; mark `src/` as **Sources Root**.
3. Add `lib/mysql-connector-java-8.0.23.jar` as a project dependency (**File → Project Structure → Libraries**).
4. Start MySQL and create the database:
   ```sql
   mysql -u root -p < mysql/schema.sql
   ```
   Or import `mysql/schema.sql` via phpMyAdmin. This creates `mydb` and seeds test data.
5. Edit `src/Persistance/ConfigDAO.java` lines 13–15 to match your MySQL credentials (see [Database credentials](#database-credentials)).
6. Edit `src/config.json` to set `vehicle_entry_time` (see [config.json](#configjson)).
7. Run `Main.java`.

**Test accounts seeded by `schema.sql`:**

| Email                   | Password  | Role  |
|-------------------------|-----------|-------|
| admin@lsparking.com     | admin123  | admin |
| test@lsparking.com      | test123   | user  |

## Configuration

### `config.json`

Located at `src/config.json`, loaded as a classpath resource.

| Key                  | Type   | Effect                                                                 | Example           |
|----------------------|--------|------------------------------------------------------------------------|-------------------|
| `db_port`            | int    | Stored in `Config` entity; **not read by `ConfigDAO`** (see note below)| `3306`            |
| `db_host`            | string | Stored in `Config` entity; **not read by `ConfigDAO`**                 | `"localhost"`     |
| `db_name`            | string | Stored in `Config` entity; **not read by `ConfigDAO`**                 | `"mydb"`          |
| `db_username`        | string | Stored in `Config` entity; **not read by `ConfigDAO`**                 | `"user"`          |
| `db_password`        | string | Stored in `Config` entity; **not read by `ConfigDAO`**                 | `"userpassword"`  |
| `admin_password`     | string | Stored in `Config` entity; **not read by `ConfigDAO`**                 | `"adminpassword"` |
| `user_email`         | string | Not mapped in `Config.java`; currently unused                          | `"example@gmail.com"` |
| `vehicle_entry_time` | int    | **Actively used.** Upper bound (seconds) of the random delay between simulation ticks: `delay = random.nextInt(vehicle_entry_time) + 1` | `60` |
| `max_stay_minutes`   | int    | Not mapped in `Config.java` (entity has `vehicle_delay` instead); currently unenforced | `3` |

> **Note:** Only `vehicle_entry_time` is actually parsed from `config.json` at runtime (`ConfigDAO.getVehicleEntryTime()`). All other fields are dead configuration — DB credentials must be changed directly in `ConfigDAO.java`.

### Database credentials

Hardcoded in `src/Persistance/ConfigDAO.java:13–15`:

```java
private static final String URL  = "jdbc:mysql://localhost:3306/mydb?connectionTimeZone=Europe/Madrid";
private static final String USER = "root";
private static final String PASS = "";
```

Change these three constants to match your MySQL setup. The `.env` file in the repo root targets a Docker/phpMyAdmin setup and is **not** read by the Java application.

## Database Schema

| Table           | Columns                                                                                       | Key constraints                                                                 |
|-----------------|-----------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------|
| `users`         | `id` PK AUTO_INCREMENT, `name`, `surname`, `email` UNIQUE, `password` (BCrypt), `role`       | —                                                                               |
| `vehicles`      | `license_plate` PK, `user_id`, `vehicle_type`                                                 | —                                                                               |
| `parking_slots` | `identifier` PK, `floor`, `vehicle_type`, `occupation_status` BOOLEAN, `parked_license_plate`| FK → `vehicles(license_plate)`                                                  |
| `reservations`  | `id` PK AUTO_INCREMENT, `user_id`, `vehicle_license_plate`, `parking_slot_id`, `start_date`, `end_date`, `is_cancelled` BOOLEAN | FK → `users`, `vehicles`, `parking_slots`     |
| `parking_log`   | `id` PK AUTO_INCREMENT, `parking_slot_id`, `license_plate`, `user_id`, `action` (ENTRY/EXIT), `timestamp` | —                                                                  |

Full DDL: `mysql/schema.sql`.

## Features

**Admin**
- Add, edit, and delete parking slots (floor, vehicle type)
- View and cancel any active reservation
- Live last-hour occupancy chart (minute-resolution, pulls from `parking_log`)
- Current parking status panel with per-slot reservation details

**Client**
- Vehicle entry: walks plate through reservation-check → walk-in path
- Vehicle exit: clears slot and logs departure
- Create, edit, and cancel own reservations with overlap detection
- Same occupancy chart and status panel as admin
- Pop-up notification on login if any reservation was cancelled by admin

**Shared**
- Email + BCrypt password authentication
- Role-based sidebar (Admin / Client views)
- Account deletion (cascades vehicles, reservations, slot vacations)

## Simulation

`SimulationManager` runs a single daemon thread that wakes on a random interval `[1, vehicle_entry_time]` seconds (read from `config.json`). On each tick: if the lot is full → force an exit; if empty → force an entry; otherwise 50 % entry / 50 % exit. Simulated vehicles are inserted with `user_id = -1` and plates in the format `{4 digits}{3 consonants}` (e.g., `4821BCF`). After every tick, `SwingUtilities.invokeLater` fires the registered callback to refresh the slot table and occupancy chart without blocking the EDT.


