package hotel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HotelManager {
    private List<Room> allRooms = new ArrayList<>();
    private List<Reservation> allReservations = new ArrayList<>();
    private List<Integer> floors = new ArrayList<>();

    public HotelManager() {
        // Piętro 0 - Standardy
        floors.addAll(List.of(0, 1));
        for (int i = 1; i <= 5; i++) {
            RoomType type = new RoomType("Standard", "Zwykły", i*150.0, i);
            allRooms.add(new Room(i, 0, type));
        }
        // Piętro 1 - Deluxe
        for (int i = 1; i <= 5; i++) {
            RoomType type = new RoomType("Deluxe", "Luksusowy", i*300.0, i);
            allRooms.add(new Room(100 + i, 1, type));
        }
    }

    public void addReservation(Reservation res) {
        allReservations.add(res);
    }

    public List<Room> getRoomsForFloor(int floor) {
        return allRooms.stream()
                .filter(r -> r.getFloor() == floor)
                .collect(Collectors.toList());
    }

    public Room getRoomByNumber(int number) {
        return allRooms.stream()
                .filter(room -> room.getRoomNumber() == number)
                .findFirst()
                .orElse(null); // Zwróci null, jeśli pokój o takim numerze nie istnieje
    }


    // Kluczowa metoda dla kalendarza na mapie
    public boolean isRoomOccupiedOn(Room room, LocalDate date) {
        for (Reservation res : allReservations) {
            if (res.getRoom().equals(room)) {
                // Sprawdzamy czy data jest pomiędzy przyjazdem a wyjazdem
                if (!date.isBefore(res.getCheckInDate()) && !date.isAfter(res.getCheckOutDate())) {
                    return true;
                }
            }
        }
        return false;
    }


    public List<Integer> getFloors() {
        return floors;
    }

    public List<Reservation> getAllReservations() {
        return allReservations;
    }
}