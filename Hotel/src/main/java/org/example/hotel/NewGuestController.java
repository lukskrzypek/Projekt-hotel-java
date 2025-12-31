package org.example.hotel;

public class NewGuestController {

    private hotel.HotelManager manager;
    public void setManager(hotel.HotelManager manager) {
        this.manager = manager;
    }
    private NewReservationController reservationController;
    public void setReservationController(NewReservationController reservationController) {
        this.reservationController = reservationController;
    }


}
