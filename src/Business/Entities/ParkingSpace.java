package Business.Entities;

/**
 * Represents a specific parking space inside the garage.
 * <p>
 * This class tracks where the spot is (ID and floor), its current status
 * (whether a car is parked there), the license plate of the parked car,
 * and what kind of space it is (like EV, standard, or disabled).
 */
public class ParkingSpace {

        /** The unique ID number for this parking space. */
        private final int id;

        /** The floor level where this spot is located. */
        private final int floor;

        /** Tracks if a car is in the spot (true if occupied, false if empty). */
        private boolean current_status;

        /** The license plate of the car parked here (empty or "-" if no car). */
        private String parked_license_plate;

        /** The type of parking spot (such as "Regular", "EV", or "Disabled"). */
        private final String type;


        /**
         * Creates a new parking space with all its details.
         *
         * @param id                   the unique ID of the spot
         * @param floor                the floor number where the spot is
         * @param current_status       true if a car is already parked there, false if free
         * @param parked_license_plate the license plate of the parked car, if there is one
         * @param type                 the classification type of the spot
         */
        public ParkingSpace (int id, int floor, boolean current_status, String parked_license_plate, String type) {
                this.id = id;
                this.floor = floor;
                this.current_status = current_status;
                this.type = type;
                this.parked_license_plate = parked_license_plate;
        }

        /**
         * Gets the unique ID of this parking space.
         *
         * @return the spot ID
         */
        public int getId() {
                return id;
        }

        /**
         * Gets the floor number where this spot is located.
         *
         * @return the floor number
         */
        public int getFloor() {
                return floor;
        }

        /**
         * Checks if a vehicle is currently parked in this spot.
         *
         * @return true if occupied, false if empty
         */
        public boolean isOccupied() {
                return current_status;
        }

        /**
         * Gets the restriction or type category of the spot.
         *
         * @return the spot type string
         */
        public String getType() {
                return type;
        }

        /**
         * Updates whether the spot is occupied or empty.
         *
         * @param occupied true to set the spot as full, false to set it as empty
         */
        public void setOccupied(boolean occupied) {
                this.current_status = occupied;
        }

        /**
         * Returns a clean text summary of the spot details, mostly used for drop-downs or logs.
         *
         * @return a formatted text string like "Slot 14 - Floor 2 (EV)"
         */
        @Override
        public String toString() {
                return "Slot " + id + " - Floor " + floor + " (" + type + ")";
        }

        /**
         * Gets the license plate of the vehicle currently using this spot.
         *
         * @return the license plate string, or a placeholder if empty
         */
        public String getParkedLicensePlate() {return parked_license_plate;}
}