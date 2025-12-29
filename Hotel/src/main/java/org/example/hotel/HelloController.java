package org.example.hotel;

import hotel.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import java.time.LocalDate;
import javafx.scene.layout.VBox;

public class HelloController {
    @FXML private ComboBox<Integer> floorComboBox;
    @FXML private DatePicker viewDatePicker;
    @FXML private FlowPane roomsContainer; // To jest ten szary obszar z FXML

    private HotelManager manager = new HotelManager();

    @FXML
    public void initialize() {

        viewDatePicker.setOnAction(e -> renderMap());
        floorComboBox.getItems().addAll(0, 1);
        floorComboBox.setValue(0);
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
            Button roomButton = new Button("Pokój " + room.getRoomNumber()+ "-osobowy" + "\n" +
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

    @FXML
    private void handleOpenBookingDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nowa Rezerwacja");
        dialog.setHeaderText("Wprowadź dane pobytu");

        // Elementy GUI
        DatePicker dateFrom = new DatePicker(LocalDate.now());
        DatePicker dateTo = new DatePicker(LocalDate.now().plusDays(1));

        ComboBox<String> guestNameCombo = new ComboBox<>();
        guestNameCombo.setEditable(true);
        guestNameCombo.setPromptText("Imię i Nazwisko gościa");

        ComboBox<Room> roomCombo = new ComboBox<>();
        roomCombo.getItems().addAll(manager.getRoomsForFloor(floorComboBox.getValue()));
        roomCombo.setPromptText("Wybierz pokój");

        CheckBox spaBox = new CheckBox("Dodaj wejście do SPA (SpaEntry)");
        CheckBox mealBox = new CheckBox("Dodaj pakiet wyżywienia (MealPackage)");

        roomCombo.setConverter(new javafx.util.StringConverter<Room>() {
            @Override
            public String toString(Room room) {
                return (room == null) ? "" : "Pokój " + room.getRoomNumber() + " (" + room.getRoomType().getTypeName() + ")";
            }

            @Override
            public Room fromString(String string) {
                return null; // Niepotrzebne przy samym wyświetlaniu
            }
        });

        VBox layout = new VBox(10,
                new Label("Od:"), dateFrom,
                new Label("Do:"), dateTo,
                new Label("Gość:"), guestNameCombo,
                new Label("Pokój:"), roomCombo,
                new Separator(),
                new Label("Dodatki:"),
                spaBox, mealBox
        );
        layout.setPadding(new javafx.geometry.Insets(20));
        dialog.getDialogPane().setContent(layout);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK && roomCombo.getValue() != null) {
                Room room = roomCombo.getValue();
                String guestName = guestNameCombo.getEditor().getText();

                // 1. Tworzymy obiekt rezerwacji
                Reservation res = new Reservation(
                        "ID-" + System.currentTimeMillis(),
                        room,
                        new Guest(guestName, "", ""),
                        dateFrom.getValue(),
                        dateTo.getValue()
                );

                // 2. Dodajemy do managera
                manager.addReservation(res);

                room.occupy();

                renderMap();

                System.out.println("Zarezerwowano pokój " + room.getRoomNumber() + " dla " + guestName);
            }
        });
    }
}