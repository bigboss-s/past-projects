import javax.swing.*;
import java.io.*;
import java.time.LocalDate;
import java.util.Random;

public class Utils {
    private static String extentFilePath = "warehouseExtent";
    public static String generateId(int length){
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return builder.toString();
    }

    public static String[] addEmptyString(String[] strings) {
        String[] newStrings = new String[strings.length + 1];
        System.arraycopy(strings, 0, newStrings, 1, strings.length);
        newStrings[0] = "";
        return newStrings;
    }

    public static String[] appendString(String[] strings, String strToAppend) {
        String[] newStrings = new String[strings.length + 1];
        System.arraycopy(strings, 0, newStrings, 0, strings.length);
        newStrings[strings.length] = strToAppend;
        return newStrings;
    }

    public static void saveExtent(){
        try{
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(extentFilePath));
            Category.writeExtent(out);
            Delivery.writeExtent(out);
            Merchandise.writeExtent(out);
            ShippingCompany.writeExtent(out);
            Transport.writeExtent(out);
            Update.writeExtent(out);
            User.writeExtent(out);
            JOptionPane.showMessageDialog(null, "Extent saved successfully");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error while saving extent:\n "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void readExtent(){
        try{
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(extentFilePath));
            Category.readExtent(in);
            Delivery.readExtent(in);
            Merchandise.readExtent(in);
            ShippingCompany.readExtent(in);
            Transport.readExtent(in);
            Update.readExtent(in);
            User.readExtent(in);
            JOptionPane.showMessageDialog(null, "Extent read successfully");
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error while reading extent:\n "+e.getMessage()+"\n Creating new extent file", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    public static User getCurrentUser(){
        return User.getFirstWorker();
    }

    public static void createSampleData(){
        try {
            User admin1 = new SystemAdministrator("admin1", "John", "Admin");
            User admin2 = new SystemAdministrator("admin2", "Mark", "Adminhero");
            User worker1 = new WarehouseWorker("worker1", "Paul", "Workman");
            User worker2 = new WarehouseWorker("worker2", "Plastic", "Wrapper");
            User worker3 = new WarehouseWorker("worker3", "Paper", "Tissue");

            Merchandise book1 = new Merchandise("Book Titanic: Real Story", 400, 1, 1, 150);
            book1.setDescription("This book is awesome");
            Merchandise book2 = new Merchandise("Book Physics 1", 650, 1, 2, 100);
            book2.setDescription("This book is boring");
            Merchandise movie1 = new Merchandise("Movie Star Wars: 13", 100, 2, 1, 170);
            movie1.setDescription("Quite the movie");
            Merchandise movie2 = new Merchandise("Movie Godmother", 60, 2, 2, 40);
            Merchandise album1 = new Merchandise("Album Bach: Guess who's Bach", 100, 3, 1, 500);
            album1.setDescription("Return of the great Bach");
            Merchandise album2 = new Merchandise("Album Biden: DemoRap", 50, 3, 2, 10);

            Category books = new Category("Books");
            Category movies = new Category("Movies");
            Category albums = new Category("Albums");

            Delivery unid1 = new InboundDeliveryUndelivered("The first undelivered delivery", "Western Warehouse", LocalDate.now().plusDays(2));
            ((InboundDeliveryUndelivered)unid1).setStatus(InboundDeliveryStatus.EXPECTED);
            Delivery unid2 = new InboundDeliveryUndelivered("Southern Warehouse", LocalDate.now().plusDays(1));
            ((InboundDeliveryUndelivered)unid2).setStatus(InboundDeliveryStatus.LATE);
            Delivery unid3 = new InboundDeliveryUndelivered("Southern Warehouse", LocalDate.now().minusDays(2));
            ((InboundDeliveryUndelivered)unid3).reject((WarehouseWorker)worker1, "It's broken", 7);
            ((InboundDeliveryUndelivered)unid3).setStatus(InboundDeliveryStatus.REJECTED);

            Delivery did1 = new InboundDeliveryDelivered("Southern warehouse", LocalDate.now().minusDays(10), (WarehouseWorker) worker1);
            ((InboundDeliveryDelivered)did1).setStatus(InboundDeliveryStatus.DELIVERED);
            Delivery did2 = new InboundDeliveryDelivered("Northern warehouse", LocalDate.now().minusDays(15), (WarehouseWorker) worker1);
            ((InboundDeliveryDelivered)did2).setStatus(InboundDeliveryStatus.DELIVERED);
            Delivery did3 = new InboundDeliveryDelivered("Careful: GLASS", "Eastern warehouse", LocalDate.now().minusDays(17), (WarehouseWorker) worker3);
            ((InboundDeliveryDelivered)did3).setStatus(InboundDeliveryStatus.DELIVERED);

            Delivery odns1 = new OutboundDeliveryNotSent("Some shop", LocalDate.now().plusDays(1));
            ((OutboundDeliveryNotSent)odns1).setStatus(OutboundDeliveryStatus.IN_PREPARATION);
            Delivery odns2 = new OutboundDeliveryNotSent("Some other shop", LocalDate.now().plusDays(2));
            ((OutboundDeliveryNotSent)odns2).setStatus(OutboundDeliveryStatus.IN_PREPARATION);

            Delivery ods1 = new OutboundDeliverySent("This one stinks", "Another shop", (WarehouseWorker) worker2, LocalDate.now().minusDays(2));
            ((OutboundDeliverySent)ods1).setStatus(OutboundDeliveryStatus.SENT);
            Delivery ods2 = new OutboundDeliverySent("This one really stinks", "Some shop", (WarehouseWorker) worker1, LocalDate.now().minusDays(5));
            ((OutboundDeliverySent)ods2).setStatus(OutboundDeliveryStatus.SENT);

            ShippingCompany sc1 = new ShippingCompany("HHL", "hhl@comp.com", "123321123");
            ShippingCompany sc2 = new ShippingCompany("Balls", "love@balls.com", "998877789");

            new Transport(50, unid1, movie1);
            new Transport(150, unid1, movie2);
            new Transport(200, unid2, album1);
            new Transport(20, unid3, album2);
            new Transport(15, did1, movie2);
            new Transport(30, did2, movie1);
            new Transport(40, did3, book2);
            new Transport(1, odns1, book1);
            new Transport(1, odns1, book2);
            new Transport(3, odns2, album1);
            new Transport(2, ods1, book1);
            new Transport(1, ods2, movie1);

            books.addMerchandise(book1);
            books.addMerchandise(book2);
            movies.addMerchandise(movie1);
            movies.addMerchandise(movie2);
            albums.addMerchandise(album1);
            albums.addMerchandise(album2);

            sc1.addDelivery(unid1);
            sc1.addDelivery(unid2);
            sc1.addDelivery(unid3);
            sc1.addDelivery(did1);
            sc1.addDelivery(did2);
            sc1.addDelivery(did3);

            sc2.addDelivery(odns1);
            sc2.addDelivery(odns2);
            sc2.addDelivery(ods1);
            sc2.addDelivery(ods2);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
