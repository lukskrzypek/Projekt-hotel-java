package hotel;

// Usługa SPA
public class SpaEntry extends AdditionalService {
    private int durationMinutes;

    public SpaEntry(String name, double price, int duration) {
        super(name, price);
        this.durationMinutes = duration;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }
}