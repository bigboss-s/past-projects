import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class User implements Serializable {
    private static List<User> users = new ArrayList<>();
    public User(String login, String name, String surname) throws Exception {
        if (users.stream().anyMatch(u -> u.name == name)){
            throw new Exception("Login "+login+" already registered");
        }

        this.login = login;
        this.name = name;
        this.surname = surname;

        users.add(this);
    }

    protected String login;
    protected String name;
    protected String surname;

    public static WarehouseWorker getFirstWorker(){
        for (User user : users){
            if (user instanceof WarehouseWorker){
                return (WarehouseWorker) user;
            }
        }
        return null;
    }

    public static void writeExtent(ObjectOutputStream stream) throws IOException {
        stream.writeObject(users);
    }

    public static void readExtent(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        users = (ArrayList<User>) stream.readObject();
    }
}
