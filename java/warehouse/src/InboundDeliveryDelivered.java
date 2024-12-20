import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class InboundDeliveryDelivered extends InboundDelivery{
    public InboundDeliveryDelivered(String description, String origin, LocalDate deliveredDate, WarehouseWorker employeeApproving) {
        super(description, origin);
        this.employeeApproving = employeeApproving;
        this.deliveredDate = deliveredDate;
    }

    public InboundDeliveryDelivered(String origin, LocalDate deliveredDate, WarehouseWorker employeeApproving) {
        this(null, origin, deliveredDate, employeeApproving);
    }

    public InboundDeliveryDelivered(InboundDeliveryUndelivered undelivered, LocalDate deliveredDate, WarehouseWorker employeeApproving){
        this( undelivered.origin, deliveredDate, employeeApproving);
    }

    private WarehouseWorker employeeApproving;
    private LocalDate deliveredDate;

    @Override
    public Object[] getFormatToRow() {
        return new Object[]{packageId, deliveredDate, origin, status};
    }

    @Override
    public String getDeliveryDateString() {
        return deliveredDate.toString();
    }

    @Override
    public String getDetailsFormatted() {
        return "<html>DETAILS:<br/>"+
                "Type: Inbound Delivered<br/>"+
                "Weight: "+calculateWeight()+"<br/>"+
                "Status: "+status+"<br/>"+
                "Delivery confirmed by: "+employeeApproving.login+"<br/>"+
                "Delivered on: "+ deliveredDate+"</html>";
    }


}
