package Business.Entities;

public class ParkingSpace {
        int id;
        int floor;
        String current_status;
        String reservation_status;
        String type;

        public ParkingSpace (int id, int floor, String current_status, String reservation_status, String type) {
                this.id = id;
                this.floor = floor;
                this.current_status = current_status;
                this.reservation_status = reservation_status;
                this.type = type;
        }

        public int getId() {
                return id;
        }

        public int getFloor() {
                return floor;
        }

        public String getCurrentStatus() {
                return current_status;
        }

        public String getReservationStatus() {
                return reservation_status;
        }

        public String getType() {
                return type;
        }
}

