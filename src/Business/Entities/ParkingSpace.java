package Business.Entities;

public class ParkingSpace {
        private int id;
        private int floor;
        private boolean current_status;
        private String parked_license_plate;
        private String type;


        public ParkingSpace (int id, int floor, boolean current_status, String parked_license_plate, String type) {
                this.id = id;
                this.floor = floor;
                this.current_status = current_status;
                this.type = type;
                this.parked_license_plate = parked_license_plate;
        }

        public int getId() {
                return id;
        }

        public int getFloor() {
                return floor;
        }

        public boolean isOccupied() {
                return current_status;
        }

        public String getType() {
                return type;
        }

        public void setOccupied(boolean occupied) {
            this.current_status = occupied;
        }

        @Override
        public String toString() {
                return "Slot " + id + " - Floor " + floor + " (" + type + ")";
        }

        public String getParkedLicensePlate() {return parked_license_plate;}

        public void setParkedLicensePlate(String plate) {
                this.parked_license_plate = plate;
        }
}