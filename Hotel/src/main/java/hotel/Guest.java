package hotel;

public class Guest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    public Guest(String firstName, String lastName, String email,  String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return firstName +  " " + lastName;
    }

    @Override
    public String toString() {
        return getFullName();
    }

    public String getEmail() {
        return email;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
}

