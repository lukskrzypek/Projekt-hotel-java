### Diagram klas ogólny pogląd
```
@startuml

skinparam classAttributeIconSize 0
skinparam monochrome false
skinparam shadowing true
skinparam linetype ortho
hide empty members

title Diagram Klas: System Hotelowy 

package "hotel" #DDDDDD {
    class HotelManager
    class Guest
    class Room
    class RoomType
    class Reservation
    abstract class AdditionalService
    class SpaEntry
    class MealPackage
}

package "hotelgui" {
    class HelloApplication
    class HelloController
    class NewReservationController
    class NewGuestController
    class ReservationListController
}

HotelManager "1" o-- "*" Room : stores & manages >
HotelManager "1" o-- "*" Reservation : stores & manages >

AdditionalService <|-- SpaEntry
AdditionalService <|-- MealPackage
Room "*" --> "1" RoomType : has type >
Reservation "*" --> "1" Room : books >
Reservation "*" o-- "*" AdditionalService : includes >
Reservation "*" --> "1" Guest : assigned to >

HelloController --> HotelManager
NewReservationController --> HotelManager
NewGuestController --> HotelManager

HelloApplication --> HelloController
HelloController ..> NewReservationController
HelloController ..> ReservationListController
NewReservationController ..> NewGuestController

NewReservationController --> HelloController
NewGuestController --> NewReservationController

@enduml
```
### Diagram klas (dokładnie rozpisany)
```
@startuml

skinparam classAttributeIconSize 0
skinparam monochrome false
skinparam shadowing true
skinparam linetype ortho

title Diagram Klas: System Rezerwacji Hotelowej

abstract class AdditionalService {
  - String serviceName
  - double price
  + AdditionalService(String serviceName, double price)
  + getPrice(): double
}

class SpaEntry extends AdditionalService {
  - int durationMinutes
  + SpaEntry(String name, double price, int duration)
  + getDurationMinutes(): int
}

class MealPackage extends AdditionalService {
  - boolean isVegetarian
  + MealPackage(String name, double price, boolean isVegetarian)
  + isVegetarian(): boolean
}

class Guest {
  - String firstName
  - String lastName
  - String email
  - String phoneNumber
  + Guest(String firstName, String lastName, String email, String phoneNumber)
  + getFullName(): String
  + getEmail(): String
  + getPhoneNumber(): String
}

class RoomType {
  - String typeName
  - String description
  - double basePricePerNight
  - int maxCapacity
  + RoomType(String name, String desc, double price, int capacity)
  + getTypeName(): String
  + getDescription(): String
  + getBasePrice(): double
  + getMaxCapacity(): int
}

class Room {
  - int roomNumber
  - int floor
  - boolean isAvailable
  - RoomType roomType
  + Room(int roomNumber, int floor, RoomType roomType)
  + getRoomNumber(): int
  + getFloor(): int
  + isAvailable(): boolean
  + occupy(): void
  + release(): void
  + getRoomType(): RoomType
}

class Reservation {
  - Room room
  - Guest guest
  - LocalDate checkInDate
  - LocalDate checkOutDate
  - List<AdditionalService> services
  + Reservation(Room room, Guest guest, LocalDate checkIn, LocalDate checkOut)
  + addService(AdditionalService service): void
  + calculateTotalCost(): double
  + getCheckInDate(): LocalDate
  + getCheckOutDate(): LocalDate
  + getRoom(): Room
  + getGuest(): Guest
}

' Relacje
Room "*" --> "1" RoomType : has type >
Guest "1" <-- "*" Reservation : associated guest <
Reservation "*" --> "1" Room : books >
Reservation "1" o-- "*" AdditionalService : includes services >

@enduml
```
### Diagram przypadków użycia
```
@startuml

left to right direction
actor "Pracownik Hotelu" as Staff

package "System Rezerwacji Hotelowej" {

    package "Zarządzanie Widokiem i Mapą" {
        usecase "Podgląd mapy pokoi" as UC_ViewMap
        usecase "Zmiana piętra" as UC_SelectFloor
        usecase "Wybór daty podglądu" as UC_SelectDate
        usecase "Przeglądanie listy wszystkich rezerwacji" as UC_ViewList
    }

    package "Proces Rezerwacji" {
        usecase "Tworzenie nowej rezerwacji" as UC_CreateRes
        usecase "Wybór pokoju" as UC_SelectRoom
        usecase "Wybór terminu (Od-Do)" as UC_SelectDates
        usecase "Konfiguracja usług dodatkowych" as UC_AddServices
        usecase "Podgląd ceny w czasie rzeczywistym" as UC_PriceRefresh
        
        usecase "Dodanie pakietu SPA" as UC_Spa
        usecase "Dodanie pakietu wyżywienia" as UC_Meal
    }

    package "Zarządzanie Gośćmi" {
        usecase "Dodanie nowego gościa do bazy" as UC_AddGuest
        usecase "Wprowadzenie danych kontaktowych\n(Email, Telefon)" as UC_InputGuestData
    }
}

Staff --> UC_ViewMap
Staff --> UC_CreateRes
Staff --> UC_ViewList

UC_ViewMap ..> UC_SelectFloor : <<include>>
UC_ViewMap ..> UC_SelectDate : <<include>>
note bottom of UC_SelectDate : Sprawdzanie zajętości pokoju\nw HotelManager (isRoomOccupiedOn)

UC_CreateRes ..> UC_SelectRoom : <<include>>
UC_CreateRes ..> UC_SelectDates : <<include>>
UC_CreateRes ..> UC_AddServices : <<include>>
UC_CreateRes ..> UC_PriceRefresh : <<include>>

UC_AddServices <|-- UC_Spa
UC_AddServices <|-- UC_Meal

UC_CreateRes ..> UC_AddGuest : <<extend>>
UC_AddGuest ..> UC_InputGuestData : <<include>>
note right of UC_InputGuestData : Walidacja poprawności\nemail i pól niepustych

@enduml
```
### Diagram sekwencji Inicjalizacja aplikacji i mapy pokoi
```
@startuml
actor Pracownik
participant HelloApplication
participant HelloController
participant HotelManager

Pracownik -> HelloApplication: start(stage)
HelloApplication -> HelloController: initialize()
create HotelManager
HelloController -> HotelManager: new HotelManager()
note right: Konstruktor tworzy pokoje i piętra

HelloController -> HotelManager: getFloors()
HelloController -> HotelManager: getRoomsForFloor(selectedFloor)

loop dla każdego pokoju
    HelloController -> HotelManager: isRoomOccupiedOn(room, date)
    HotelManager --> HelloController: boolean (occupied)
end

HelloController -> HelloController: renderMap()
@enduml
```
### Diagram sekwencji Tworzenie nowej rezerwacji
```
@startuml
actor Pracownik
participant HelloController
participant NewReservationController
participant Reservation
participant HotelManager

Pracownik -> HelloController: openNewReservationWindow()
HelloController -> NewReservationController: setMainController(this)
NewReservationController -> HelloController: getManager()

Pracownik -> NewReservationController: Wybór parametrów (data, pokój, usługi)
NewReservationController -> NewReservationController: priceRefresh()

Pracownik -> NewReservationController: addReservation()
create Reservation
NewReservationController -> Reservation: new Reservation(...)

NewReservationController -> HelloController: dodajRezerwacje(reservation, room)
HelloController -> HotelManager: addReservation(reservation)
HelloController -> HelloController: renderMap()
note right: Odświeżenie widoku mapy pokoi
@enduml
```
### Diagram sekwencji Dodawanie nowego gościa
```
@startuml
actor Pracownik
participant NewReservationController
participant NewGuestController
participant Guest

Pracownik -> NewReservationController: openNewGuestWindow()
NewReservationController -> NewGuestController: setReservationController(this)

Pracownik -> NewGuestController: saveGuest()
create Guest
NewGuestController -> Guest: new Guest(firstName, lastName, email, phone)

NewGuestController -> NewReservationController: addNewGuestToList(newGuest)
NewReservationController -> NewReservationController: guestComboBox.add(newGuest)
NewReservationController -> NewReservationController: roomComboBoxRefresh()
note right: Odświeżenie listy pokoi

NewGuestController -> NewGuestController: closeWindow()
@enduml
```
