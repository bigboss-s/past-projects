import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Transport implements Serializable {
    private static List<Transport> transports = new ArrayList<>();
    protected int quantity;
    protected Delivery delivery;
    protected Merchandise merchandise;

    public Transport(int quantity, Delivery delivery, Merchandise merchandise) {
        this.quantity = quantity;
        this.delivery = delivery;
        this.merchandise = merchandise;

        transports.add(this);

        delivery.addTransport(this);
        merchandise.addTransport(this);
    }

    public static List<Transport> getTransports() {
        return transports;
    }

    public String[] getFormatToRow(){
        String[] detailStrings = new String[4];
        detailStrings[0] = String.valueOf(merchandise.getId());
        detailStrings[1] = merchandise.getName();
        detailStrings[2] = String.valueOf(quantity);
        detailStrings[3] = String.valueOf(merchandise.getWeight());

        return detailStrings;
    }

    public static void writeExtent(ObjectOutputStream stream) throws IOException {
        stream.writeObject(transports);
    }

    public static void readExtent(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        transports = (ArrayList<Transport>) stream.readObject();
    }
}
