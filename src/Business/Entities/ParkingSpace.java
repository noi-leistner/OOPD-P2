package Business.Entities;

public class ParkingSpace {
        int code;
        int floor;
        int current_status;
        int reservation_status;
        String type;
        int spaceId;

        public int getCode() {
                return code;
        }

        public int getFloor() {
                return floor;
        }

        public int getCurrentStatus() {
                return current_status;
        }

        public int getReservationStatus() {
                return reservation_status;
        }

        public String getType() {
                return type;
        }
        public int getSpaceId() {return spaceId;}

        public void setSpaceId(int spaceId) {this.spaceId = spaceId;}

        public void setCode(int code) {this.code = code;}

        public void setFloor(int floor) {this.floor = floor;}

        public void setCurrentStatus(int current_status) {this.current_status = current_status;}

        public void setReservationStatus(int reservation_status) {this.reservation_status = reservation_status;}

        public void setType(String type) {this.type = type;}
}


