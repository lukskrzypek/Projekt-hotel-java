package hotel;

public class MealPackage extends AdditionalService {
    private boolean isVegetarian;

    public MealPackage(String name, double price, boolean isVegetarian) {
        super(name, price);
        this.isVegetarian = isVegetarian;
    }

    public boolean isVegetarian() {
        return isVegetarian;
    }
}
