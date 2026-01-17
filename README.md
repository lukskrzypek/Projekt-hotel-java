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
