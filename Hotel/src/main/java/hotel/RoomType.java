package hotel;

public class RoomType {
    private String typeName;
    private String description;
    private double basePricePerNight;
    private int maxCapacity;

    public RoomType(String typeName, String description, double basePricePerNight, int maxCapacity) {
        this.typeName = typeName;
        this.description = description;
        this.basePricePerNight = basePricePerNight;
        this.maxCapacity = maxCapacity;
    }

    public String getTypeName() {
        return typeName; }

    public String getDescription() {
        return description; }

    public double getBasePrice() {
        return basePricePerNight; }

    public int getMaxCapacity() {
        return maxCapacity; }

}