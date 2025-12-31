package org.example.hotel;

import hotel.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.time.LocalDate;
import javafx.stage.Stage;

public class HelloController {
    @FXML private ComboBox<Integer> floorComboBox;
    @FXML private DatePicker viewDatePicker;
    @FXML private FlowPane roomsContainer; // To jest ten szary obszar z FXML
    @FXML public Button listaRezerwacjiButton;

    private HotelManager manager = new HotelManager();

    @FXML
    public void initialize() {

        viewDatePicker.setOnAction(e -> renderMap());
        floorComboBox.getItems().setAll(manager.getFloors());
        floorComboBox.getSelectionModel().selectFirst();
        viewDatePicker.setValue(LocalDate.now());

        // Akcja przy zmianie piętra
        floorComboBox.setOnAction(e -> renderMap());

        renderMap();
    }
    //Fukcja do mapy
    private void renderMap() {
        roomsContainer.getChildren().clear();

        int selectedFloor = floorComboBox.getValue();
        LocalDate selectedDate = viewDatePicker.getValue(); // Pobiera datę z kalendarza na mapie

        for (Room room : manager.getRoomsForFloor(selectedFloor)) {
            Button roomButton = new Button("Pokój nr. " + room.getRoomNumber()+ "\n"+room.getRoomType().getMaxCapacity()+" osobowy" + "\n" +
                    room.getRoomType().getTypeName());
            roomButton.setPrefSize(130, 100);
            roomButton.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

            // Sprawdzamy managera zamiast samej zmiennej isAvailable
            boolean occupied = manager.isRoomOccupiedOn(room, selectedDate);

            if (occupied) {
                roomButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
            } else {
                roomButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
            }

            roomsContainer.getChildren().add(roomButton);
        }
    }

    //Otwieranie nowego okna do rezerwacji
    public void openNewReservationWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("NewReservation.fxml"));

        Parent root = loader.load();
        NewReservationController secondaryController = loader.getController();
        secondaryController.setMainController(this);
        secondaryController.setManager(this.manager);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Dodaj nową rezerwację");
        stage.show();

        //Ustawienie na sztywno odpowiedniej wielkości okna
        stage.setResizable(false);
        stage.setMinWidth(400);
        stage.setMaxWidth(400);
        stage.setMinHeight(600);
        stage.setMaxHeight(600);
    }

    public void dodajRezerwacje(Reservation reservation, Room room) {
        manager.addReservation(reservation);
        room.occupy();
        renderMap();
    }

    @FXML
    public void openReservationList() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ReservationList.fxml"));
        Parent root = loader.load();

        ReservationListController controller = loader.getController();
        // Przekazujemy listę wszystkich rezerwacji z managera
        controller.setReservations(manager.getAllReservations());

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Wykaz wszystkich rezerwacji");
        stage.show();
    }
}