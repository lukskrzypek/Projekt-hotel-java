package hotel;

public class Room {
    private int roomNumber;
    private int floor;
    private boolean isAvailable;
    private RoomType roomType;

    public Room(int roomNumber, int floor, RoomType roomType) {
        this.roomNumber = roomNumber;
        this.floor = floor;
        this.roomType = roomType;
        this.isAvailable = true; // Domyślnie nowy pokój jest wolny
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public int getFloor() {
        return floor;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void occupy() {
        this.isAvailable = false;
    }

    public void release() {
        this.isAvailable = true;
    }

    public RoomType getRoomType() {
        return roomType;
    }

}
