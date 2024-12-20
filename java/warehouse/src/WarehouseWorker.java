import java.util.ArrayList;
import java.util.List;

public class WarehouseWorker extends User {

    public WarehouseWorker(String login, String name, String surname) throws Exception {
        super(login, name, surname);
        updates = new ArrayList<>();
    }

    private List<Update> updates;

    public void addUpdate(Update update){
        this.updates.add(update);
    }
}
