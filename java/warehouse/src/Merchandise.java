import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

public class Merchandise implements Serializable {
    private static List<Merchandise> merchandise = new ArrayList<>();

    public Merchandise(String name, float weight, int row, int column, int quantity) throws Exception {
        if (merchandise.stream().anyMatch(c -> c.name == name)){
            throw new Exception("Name: "+name+" already registered");
        }
        this.id = Utils.generateId(10);
        this.name = name;
        this.weight = weight;
        this.row = row;
        this.column = column;
        this.quantity = quantity;
        this.transports = new ArrayList<>();
        this.notes = new ArrayList<>();

        merchandise.add(this);
    }

    private String id;
    private String name;
    private float weight;
    private int row;
    private int column;
    private String description;
    private List<String> notes;
    private int quantity;
    private Category category;
    private List<Transport> transports;
    private List<Update> updates;

    public void setCategory(Category category){
        if (this.category == category){
            return;
        }
        if (this.category != null){
            this.category.deleteMerchandise(this);
        }
        this.category = category;
        category.addMerchandise(this);
    }

    public void addTransport(Transport transport){
        this.transports.add(transport);
    }

    public void addUpdate(Update update){
        this.updates.add(update);
    }

    public static List<Merchandise> getMerchandise() {
        return merchandise;
    }

    public Object[] getFormatToRow(){
        return new Object[]{id, name, category, new String(row + "/" + column), quantity, weight, description};
    }

    public int getRow() {
        return row;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getNotes() {
        return notes;
    }

    public void updateNotes(List<String> newNotes){
        notes = new ArrayList<>();
        for (String str : newNotes){
            notes.add(str);
        }
    }

    public float getWeight() {
        return weight;
    }

    public int getColumn() {
        return column;
    }

    public int getQuantity() {
        return quantity;
    }

    public Category getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public void editMerchandise(String name, float weight, int row, int column, int quantity) throws Exception {
        if(!this.name.equals(name)){
            if (merchandise.stream().anyMatch(c -> c.name == name)){
                throw new Exception("Name: "+name+" already registered");
            }
            this.name = name;
        }

        this.weight = weight;
        this.row = row;
        this.column = column;
        this.quantity = quantity;
    }

    public static Merchandise getByName(String name){
        return merchandise.stream().filter(m -> m.name.equals(name)).findFirst().orElse(null);
    }

    public String getDetailsFormatted(){
        return "<html>DETAILS:<br/>"+
                "Name: "+name+"<br/>"+
                "Category: "+category+"<br/>"+
                "Weight: "+weight+"<br/>"+
                "Row/Col: "+row+"/"+column+"<br/>"+
                "Quantity: "+quantity+"<br/>"+
                "Description: "+description+"<br/>"+
                "Notes: "+ Arrays.toString(notes.toArray())+"</html>";
    }

    public static void writeExtent(ObjectOutputStream stream) throws IOException {
        stream.writeObject(merchandise);
    }

    public static void readExtent(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        merchandise = (ArrayList<Merchandise>) stream.readObject();
    }
}
