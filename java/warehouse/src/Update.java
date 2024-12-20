import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Update implements Serializable {
    private static List<Update> updates = new ArrayList<>();
    private LocalDate date;
    private Merchandise merchandise;
    private WarehouseWorker worker;

    public Update(LocalDate date, Merchandise merchandise, WarehouseWorker worker) {
        this.date = date;
        this.merchandise = merchandise;
        this.worker = worker;

        merchandise.addUpdate(this);
        worker.addUpdate(this);
    }

    public static void writeExtent(ObjectOutputStream stream) throws IOException {
        stream.writeObject(updates);
    }

    public static void readExtent(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        updates = (ArrayList<Update>) stream.readObject();
    }
}
