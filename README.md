```
@startuml

' Ustawienia stylistyczne, aby diagram wyglądał nowocześnie
skinparam classAttributeIconSize 0
skinparam monochrome false
skinparam shadowing true
skinparam linetype ortho

title Diagram Klas: System Rezerwacji Hotelowej (Java context)

' Klasy bazowe i abstrakcyjne
abstract class AdditionalService {
  - String serviceName
  - double price
  + AdditionalService(String name, double price)
  + getPrice(): double
  + getName(): String
}

' Konkretne implementacje usług
class SpaEntry extends AdditionalService {
  - int durationMinutes
  + SpaEntry(String name, double price, int duration)
}

class MealPackage extends AdditionalService {
  - boolean isVegetarian
  + MealPackage(String name, double price, boolean veg)
}

' Główne klasy domenowe
class Guest {
  - Long id
  - String firstName
  - String lastName
  - String email
  - String phoneNumber
  + Guest(String fname, String lname, String email)
  + getFullName(): String
  + updateContactInfo(String email, String phone)
}

class RoomType {
  - String typeName
  - String description
  - double basePricePerNight
  - int maxCapacity
  + getBasePrice(): double
}

class Room {
  - int roomNumber
  - int floor
  - boolean isAvailable
  + occupy()
  + release()
  + isAvailable(): boolean
}

class Reservation {
  - String reservationId
  - LocalDate checkInDate
  - LocalDate checkOutDate
  - LocalDateTime createdAt
  - String reservationStatus
  + calculateTotalCost(): double
  + confirm()
  + cancel()
  + addService(AdditionalService service)
}

' Definiowanie relacji (Asocjacje, Agregacje, Kompozycje)

' Pokój ma określony typ (wiele pokoi może mieć ten sam typ)
Room "*" --> "1" RoomType : has type >

' Gość składa rezerwacje (jeden gość, wiele rezerwacji)
Guest "1" -- "*" Reservation : makes >

' Rezerwacja dotyczy konkretnego pokoju
Reservation "*" --> "1" Room : books >

' Rezerwacja zawiera listę usług dodatkowych (Agregacja)
Reservation "1" o-- "*" AdditionalService : includes services >

@enduml
```
