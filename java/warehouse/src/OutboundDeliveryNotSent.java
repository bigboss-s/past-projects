import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class OutboundDeliveryNotSent extends OutboundDelivery{
    public OutboundDeliveryNotSent(String description, String destination, LocalDate expectedShipment) {
        super(description, destination);
        this.expectedShipment = expectedShipment;
    }

    public OutboundDeliveryNotSent(String destination, LocalDate expectedShipment) {
        this(null, destination, expectedShipment);
    }

    protected LocalDate expectedShipment;


    @Override
    public Object[] getFormatToRow() {
        return new Object[]{packageId, expectedShipment, destination, status};
    }

    @Override
    public String getDeliveryDateString() {
        return expectedShipment.toString();
    }

    @Override
    public String getDetailsFormatted() {
        return "<html>DETAILS:<br/>"+
                "Type: Outbound sent<br/>"+
                "Weight: "+calculateWeight()+"<br/>"+
                "Status: "+status+"<br/>"+
                "Expected shipment on: "+expectedShipment+"</html>";
    }
}
