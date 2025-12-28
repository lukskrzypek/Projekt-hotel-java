package hotel;

public abstract class AdditionalService {
    private String serviceName;
    private double price;

    public AdditionalService(String serviceName, double price) {
        this.serviceName = serviceName;
        this.price = price;
    }

    public String getName() {
        return serviceName;
    }

    public double getPrice() {
        return price;
    }
}
