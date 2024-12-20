import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Category implements Serializable {
    private static List<Category> categories = new ArrayList<>();

    public Category(String name) throws Exception {
        if (categories.stream().anyMatch(c -> c.name == name)){
            throw new Exception("Name: "+name+" already registered");
        }
        this.name = name;
        merchandise = new ArrayList<>();

        categories.add(this);
    }

    private String name;
    private List<Merchandise> merchandise;

    public static void writeExtent(ObjectOutputStream stream) throws IOException {
        stream.writeObject(categories);
    }

    public static void readExtent(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        categories = (ArrayList<Category>) stream.readObject();
    }
    public void addMerchandise(Merchandise merchandise){
        if (this.merchandise.contains(merchandise)){
            return;
        }
        merchandise.setCategory(this);
        this.merchandise.add(merchandise);
    }

    @Override
    public String toString() {
        return name;
    }

    public static List<Category> getCategories() {
        return categories;
    }

    public static String[] getCategoryStrings(){
        String[] strings = new String[categories.size()];
        int i = 0;
        for (Category cat : categories){
            strings[i] = cat.name;
            i++;
        }
        return strings;
    }

    public static Category getByName(String name){
        return categories.stream().filter(c -> c.name.equals(name)).findFirst().orElse(null);
    }

    public void deleteMerchandise(Merchandise merchandise){
        this.merchandise.remove(merchandise);
    }
}
