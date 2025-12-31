package org.example.hotel;

import hotel.Reservation;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class ReservationListController {

    @FXML private TableView<Reservation> reservationTable;

    // Definiujemy kolumny
    @FXML private TableColumn<Reservation, Integer> colRoom;
    @FXML private TableColumn<Reservation, String> colGuest;
    @FXML private TableColumn<Reservation, LocalDate> colFrom;
    @FXML private TableColumn<Reservation, LocalDate> colTo;
    @FXML private TableColumn<Reservation, String> colPrice;
    @FXML private TableColumn<Reservation, String> colEmail;
    @FXML private TableColumn<Reservation, String> colPhone;

    @FXML
    public void initialize() {
        // 1. Numer pokoju (wyciągamy z obiektu Room wewnątrz Reservation)
        colRoom.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getRoom().getRoomNumber()));

        // 2. Dane gościa (wyciągamy z obiektu Guest)
        colGuest.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getGuest().getFullName()));

        // 3. Daty (tutaj używamy PropertyValueFactory, bo nazwy pól w klasie się zgadzają)
        colFrom.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getCheckInDate()));
        colTo.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getCheckOutDate()));

        // 4. Cena (wywołujemy Twoją metodę calculateTotalCost())
        colPrice.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("%.2f PLN", data.getValue().calculateTotalCost())));

        // Email
        colEmail.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getGuest().getEmail()));

        //Telefon
        colPhone.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getGuest().getPhoneNumber()));
    }

    // Tę metodę wywołamy z HelloController, żeby przekazać listę rezerwacji
    public void setReservations(List<Reservation> reservations) {
        reservationTable.setItems(FXCollections.observableArrayList(reservations));
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) reservationTable.getScene().getWindow();
        stage.close();
    }
}