import javax.swing.*;
import java.time.LocalDate;
import java.util.Vector;

public class Main {
    public static void main(String[] args) {

        Utils.readExtent();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                WarehouseManagementGUI gui = new WarehouseManagementGUI();
                for (Merchandise merch : Merchandise.getMerchandise()){
                    gui.addInventoryRow(merch.getFormatToRow());
                }
                for (Delivery delivery : Delivery.getDeliveries()){
                    gui.addDeliveryRow(delivery.getFormatToRow());
                }
                gui.setVisible(true);
            }
        });
    }

}