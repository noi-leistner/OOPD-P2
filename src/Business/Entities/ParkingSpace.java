package Business.Entities;

public class ParkingSpace {
        int code;
        int floor;
        String current_status;
        String reservation_status;
        String type;
        int spaceId;

        public int getCode() {
                return code;
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

        public int getSpaceId() {
                return spaceId;
        }
}

