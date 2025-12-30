package org.example.hotel;

import hotel.Guest;
import hotel.HotelManager;
import hotel.Reservation;
import hotel.Room;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class NewReservationController {

    @FXML private ComboBox<Guest> guestComboBox;
    @FXML private TextField dayCountTextField;
    @FXML private DatePicker datePickerFrom;
    @FXML private DatePicker datePickerTo;
    @FXML private ComboBox<Room> roomComboBox;
    @FXML private ComboBox<Integer> floorComboBox;
    

    private HelloController mainController;
    private HotelManager manager;

    //Manager to taka nasza baza danych i w tym momencie je przesylamy pomiędzy kontrolerami
    public void setMainController(HelloController mainController) {
        this.mainController = mainController;
    }
    public void setManager(HotelManager manager) {
        this.manager = manager;

        //Ładuje początkowe dane (te które pobieram z managera)
        //To mogło by znajdować się w initialize gdyby nie to że manager przypisywany jest pozniej
        floorComboBox.getItems().setAll(manager.getFloors());
        floorComboBox.getSelectionModel().selectFirst();
        roomComboBoxRefresh();
    }


    @FXML
    public void initialize() {
        setDateDefault();
        roomNameConverter();

        //Testowi goście
        Guest testowyGosc = new Guest("Jan", "Kowalski", "jan.kowalski@example.com");
        Guest testowyGosc2 = new Guest("Anna", "Nowak", "anna.nowak@example.com");
        guestComboBox.getItems().setAll(testowyGosc, testowyGosc2);

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

    }

    //Zmiana nazwy wyświetlanej w combobx na 'Pokój nr ...'
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
        int iloscDob = Integer.parseInt(dayCountTextField.getText());
        for (int i = 0; i < iloscDob; i++) {
            if(manager.isRoomOccupiedOn(room, datePickerTo.getValue().plusDays(i))){
                return false;
            }
        }
        return true;
    }

    @FXML
    private void roomComboBoxRefresh() {
        List<Room> allRoomsOnFloor = manager.getRoomsForFloor(floorComboBox.getValue());

        // Filtrowanie dostępnych pokojów
        List<Room> availableRooms = allRoomsOnFloor.stream().filter(room -> roomFreeFromTo(room)).toList();
        roomComboBox.getItems().setAll(availableRooms);
        roomComboBox.getSelectionModel().selectFirst();
    }

    //Dodwanie rezerwacji
    @FXML
    private void addReservation(){
        Reservation res = new Reservation(roomComboBox.getValue(),guestComboBox.getValue(),datePickerFrom.getValue(),datePickerTo.getValue());
        mainController.dodajRezerwacje(res,roomComboBox.getValue());
    }


}
