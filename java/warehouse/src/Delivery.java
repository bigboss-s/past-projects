import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

public abstract class Delivery implements Serializable {
    private static List<Delivery> deliveries = new ArrayList<>();

    public Delivery(String description) {
        String id = Utils.generateId(10);
        String finalId = id;
        while (deliveries.stream().anyMatch(d -> Objects.equals(d.packageId, finalId))){
            id = Utils.generateId(10);
        }
        this.packageId = id;
        this.transports = new ArrayList<>();

        deliveries.add(this);
    }

    public Delivery() {
        this(null);
    }

    protected String packageId;
    private ShippingCompany company;
    protected List<Transport> transports;

    public static List<Delivery> getDeliveries() {
        return deliveries;
    }

    public abstract Object[] getFormatToRow();

    public void setCompany(ShippingCompany company) {
        if(this.company == company){
            return;
        }
        this.company = company;
        company.addDelivery(this);
    }

    public void addTransport(Transport transport){
        this.transports.add(transport);
    }

    public static String[] getDeliveryDateStrings(){
        String[] strings = new String[deliveries.size()];
        int i = 0;
        for (Delivery delivery : deliveries){
            strings[i] = delivery.getDeliveryDateString();
            i++;
        }
        return strings;
    }

    public abstract String getDeliveryDateString();
    public abstract String getDetailsFormatted();

    protected float calculateWeight(){
        float sum = 0;
        for (Transport transport : transports){
            sum += transport.merchandise.getWeight() * transport.quantity;
        }
        return sum;
    }

    public static void writeExtent(ObjectOutputStream stream) throws IOException {
        stream.writeObject(deliveries);
    }

    public static void readExtent(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        deliveries = (ArrayList<Delivery>) stream.readObject();
    }

}
