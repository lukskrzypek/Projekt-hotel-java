package org.example.hotel;

import hotel.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class NewReservationController {

    @FXML private TextField priceTextField;
    @FXML private ToggleGroup spaDurationGroup;
    @FXML private CheckBox spaCheckBox;
    @FXML private CheckBox mealPackageCheckBox;
    @FXML private CheckBox vegetarianCheckBox;
    @FXML private ComboBox<Guest> guestComboBox;
    @FXML private TextField dayCountTextField;
    @FXML private DatePicker datePickerFrom;
    @FXML private DatePicker datePickerTo;
    @FXML private ComboBox<Room> roomComboBox;
    @FXML private ComboBox<Integer> floorComboBox;
    @FXML private Button okButton;
    @FXML private HBox spaDurationContainer;
    

    private HelloController mainController;
    private HotelManager manager;

    public void setMainController(HelloController mainController) {
        this.mainController = mainController;
        this.manager = mainController.getManager();

        if (this.manager != null) {
            // Ładujemy piętra
            floorComboBox.getItems().setAll(manager.getFloors());

            if (!floorComboBox.getItems().isEmpty()) {
                floorComboBox.getSelectionModel().selectFirst();
                // Wymuszamy pokazanie wartości, żeby okienko nie było puste
                floorComboBox.setValue(floorComboBox.getSelectionModel().getSelectedItem());
            }

            roomComboBoxRefresh();
        }
    }

    @FXML
    public void initialize() {

        okButton.disableProperty().bind(
                guestComboBox.valueProperty().isNull().or(roomComboBox.valueProperty().isNull())
        );

        datePickerFrom.getEditor().setEditable(false);
        datePickerTo.getEditor().setEditable(false);
        spaDurationContainer.disableProperty().bind(spaCheckBox.selectedProperty().not());
        vegetarianCheckBox.disableProperty().bind(mealPackageCheckBox.selectedProperty().not());

        setDateDefault();
        roomNameConverter();
    }

    private void setDateDefault(){
        datePickerFrom.setValue(LocalDate.now());
        datePickerTo.setValue(LocalDate.now());
        dayCountTextField.setText(String.valueOf(ChronoUnit.DAYS.between(datePickerFrom.getValue(), datePickerTo.getValue())+1));
    }

    @FXML
    private void daysCountRefresh() {
        if (
                ChronoUnit.DAYS.between(datePickerFrom.getValue(), datePickerTo.getValue())>=0 &&
                !datePickerFrom.getValue().isBefore(LocalDate.now()) &&
                !datePickerTo.getValue().isBefore(LocalDate.now())
        ){
            dayCountTextField.setText(String.valueOf(ChronoUnit.DAYS.between(datePickerFrom.getValue(), datePickerTo.getValue())+1));
        }else{
            setDateDefault();
        }
        roomComboBoxRefresh();
        priceRefresh();
    }

    //Zmiana nazwy wyświetlanej w combobox na 'Pokój nr ...'
    private void roomNameConverter() {
        roomComboBox.setConverter(new StringConverter<Room>() {
            @Override
            public String toString(Room room) {
                return (room == null) ? "" : "Pokój nr " + room.getRoomNumber();
            }

            @Override
            public Room fromString(String s) { return null; }
        });
    }

    //Sprawdza czy pokój jest wolny przez wszystkie dni wybrane w rezerwacji
    private boolean roomFreeFromTo(Room room) {
        LocalDate start = datePickerFrom.getValue();
        LocalDate end = datePickerTo.getValue();

        if (start == null || end == null) return false;

        // Obliczamy liczbę dni do sprawdzenia (różnica + 1, aby objąć też dzień wyjazdu)
        long totalDays = ChronoUnit.DAYS.between(start, end) + 1;

        for (int i = 0; i < totalDays; i++) {
            LocalDate dateToCheck = start.plusDays(i);
            // Pytamy managera, czy pokój jest zajęty w tym konkretnym dniu
            if (manager.isRoomOccupiedOn(room, dateToCheck)) {
                return false; // Jeśli choć jeden dzień jest zajęty, pokój jest niedostępny
            }
        }
        return true; // Wszystkie dni są wolne
    }

    @FXML
    private void roomComboBoxRefresh() {
        if (floorComboBox == null || floorComboBox.getValue() == null) {
            return; // Zabezpieczenie przed błędem przy ładowaniu
        }

        Room previouslySelectedRoom = roomComboBox.getValue();
        List<Room> allRoomsOnFloor = manager.getRoomsForFloor(floorComboBox.getValue());

        // 2. Sprawdzamy, ilu gości mamy obecnie na liście w ComboBoxie
        int currentGuestCount = guestComboBox.getItems().size();

        // 3. Filtrowanie z nowym warunkiem dotyczącym pojemności (maxCapacity)
        List<Room> availableRooms = allRoomsOnFloor.stream()
                .filter(room -> roomFreeFromTo(room)) // Warunek 1: Czy wolny w terminie
                .filter(room -> room.getRoomType().getMaxCapacity() >= currentGuestCount) // Warunek 2: Czy pomieści obecnych gości
                .toList();

        roomComboBox.getItems().setAll(availableRooms);

        if (previouslySelectedRoom != null && availableRooms.contains(previouslySelectedRoom)) {
            // Jeśli wcześniej wybrany pokój nadal jest na liście (i pasuje do liczby osób), wybierz go ponownie
            roomComboBox.getSelectionModel().select(previouslySelectedRoom);
        } else {
            // Jeśli pokoju nie ma na liście (bo np. zmieniliśmy piętro lub jest za mały), wybierz pierwszy dostępny
            roomComboBox.getSelectionModel().selectFirst();
        }
        priceRefresh();
    }
    //Tymczasowa rezerwacja po to aby na biezaco obliczać koszty, przy okazji wykozystywana do koncowej rezerwacji
    private Reservation newReservationTemp(){
        try {
            Reservation res = new Reservation(roomComboBox.getValue(),guestComboBox.getValue(),datePickerFrom.getValue(),datePickerTo.getValue());

            //Dodatkowe uslugi
            //spa
            if (spaCheckBox.isSelected()){
                Toggle selectedToggle = spaDurationGroup.getSelectedToggle();
                Integer time = (Integer) selectedToggle.getUserData();
                double price = 2*time;
                SpaEntry spa = new SpaEntry("Spa", price,time );
                res.addService(spa);
            }
            //posilek
            if (mealPackageCheckBox.isSelected()){
                if(vegetarianCheckBox.isSelected()){
                    double price = 70;
                    MealPackage mealPackage = new MealPackage("Vege",price,true);
                    res.addService(mealPackage);
                }else{
                    double price = 50;
                    MealPackage mealPackage = new MealPackage("Standard",price,false);
                    res.addService(mealPackage);
                }
            }
            return res;
        }catch (Exception e){
            return null;
        }

    }

    //wyswietlanie ceny
    @FXML
    private void priceRefresh(){
        Reservation res = newReservationTemp();

        if (res == null) return;
        double totalCost = res.calculateTotalCost();
        priceTextField.setText(totalCost + " PLN");
    }


    //Dodwanie rezerwacji
    @FXML
    private void addReservation(){
        Reservation res=newReservationTemp();
        mainController.dodajRezerwacje(res,roomComboBox.getValue());
        Stage stage = (Stage) roomComboBox.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void cancelReservation(){
        Stage stage = (Stage) roomComboBox.getScene().getWindow();
        stage.close();
    }


    @FXML
    private void openNewGuestWindow() throws IOException {
        Room selectedRoom = roomComboBox.getValue();

        int limit = selectedRoom.getRoomType().getMaxCapacity();
        int currentGuests = guestComboBox.getItems().size();

        if (currentGuests >= limit) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Ten pokój pomieści maksymalnie " + limit + " osób!");
            alert.show();
            return;
        }


        FXMLLoader loader = new FXMLLoader(getClass().getResource("NewGuest.fxml"));
        Parent root = loader.load();

        NewGuestController guestController = loader.getController();
        guestController.setManager(this.manager);
        guestController.setReservationController(this);
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Nowy Gość");

        //blokuje okno pod spodem
        stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        stage.show();
    }

    @FXML
    public void addNewGuestToList(Guest guest){
        guestComboBox.getItems().add(guest);
        guestComboBox.getSelectionModel().select(guest);

        roomComboBoxRefresh();
    }
}
