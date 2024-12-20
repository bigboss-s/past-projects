import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShippingCompany implements Serializable {
    private static List<ShippingCompany> companies = new ArrayList<>();
    private static Set<String> names = new HashSet<>();

    public ShippingCompany(String name, String email, String phoneNumber) throws Exception {
        if (names.contains(name)){
            throw new Exception("Name :"+name+" already registered");
        }
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        deliveries = new ArrayList<>();

        names.add(name);
        companies.add(this);
    }

    private String name;
    private String email;
    private String phoneNumber;
    private List<Delivery> deliveries;

    public void addDelivery(Delivery delivery){
        if(deliveries.contains(delivery)){
            return;
        }
        this.deliveries.add(delivery);
        delivery.setCompany(this);
    }

    public static void writeExtent(ObjectOutputStream stream) throws IOException {
        stream.writeObject(companies);
    }

    public static void readExtent(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        companies = (ArrayList<ShippingCompany>) stream.readObject();
    }
}
