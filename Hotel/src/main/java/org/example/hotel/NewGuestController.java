package org.example.hotel;

import hotel.Guest;
import hotel.HotelManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NewGuestController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneNumberField;
    @FXML public Button saveButton;
    @FXML public Button cancelButton;


    public HotelManager manager;

    public void setManager(HotelManager manager) {
        this.manager = manager;
    }

    private NewReservationController reservationController;

    public void setReservationController(NewReservationController reservationController) {
        this.reservationController = reservationController;
    }

    @FXML
    private void saveGuest() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String phoneNumber = phoneNumberField.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()) {
            return; // Przerywamy funkcję, gość nie zostanie dodany
        }

        if (!email.contains("@") || !email.contains(".")) {
            return;
        }

        if (!firstName.isEmpty() && !lastName.isEmpty()) {
            Guest newGuest = new Guest(firstName, lastName, email , phoneNumber);

            reservationController.addNewGuestToList(newGuest);

            closeWindow();
        }
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) firstNameField.getScene().getWindow();
        stage.close();
    }


}
