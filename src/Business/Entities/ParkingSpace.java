package Business.Entities;

public class ParkingSpace {
        int id;
        int floor;
        boolean current_status;
        String type;

        public ParkingSpace (int id, int floor, boolean current_status, String type) {
                this.id = id;
                this.floor = floor;
                this.current_status = current_status;
                this.type = type;
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
}