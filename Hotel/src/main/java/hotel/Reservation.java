package hotel;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Reservation {
    private Room room;
    private Guest guest;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private List<AdditionalService> services;

    public Reservation(Room room, Guest guest, LocalDate checkInDate, LocalDate checkOutDate) {
        this.room = room;
        this.guest = guest;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.services = new ArrayList<>();
    }

    public void addService(AdditionalService service) {
        services.add(service);
    }

    public double calculateTotalCost() {
        // Obliczamy liczbę nocy
        long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);

        // Koszt pokoju
        double total = (nights + 1) * room.getRoomType().getBasePrice();

        // Koszt usług dodatkowych
        for (AdditionalService service : services) {
            total += service.getPrice();
        }

        return total;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public Room getRoom() { return room; }
    public Guest getGuest() { return guest; }
}