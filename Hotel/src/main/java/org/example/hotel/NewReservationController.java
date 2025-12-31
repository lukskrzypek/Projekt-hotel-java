package org.example.hotel;

import hotel.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

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
        res.calculateTotalCost();
        priceTextField.setText(res.calculateTotalCost()*Integer.parseInt(dayCountTextField.getText()) + " PLN");

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

}
