package hotel;

public class Guest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Long id;

    public Guest(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getFullName() {
        return firstName +  " " + lastName;
    }

    public void updateContactInfo(String email, String phoneNumber) {}
}
