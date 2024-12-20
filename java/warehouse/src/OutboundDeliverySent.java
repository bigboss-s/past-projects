import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class OutboundDeliverySent extends OutboundDelivery {
    public OutboundDeliverySent(String description, String destination, WarehouseWorker employeeConfirming, LocalDate shipment) {
        super(description, destination);
        this.employeeConfirming = employeeConfirming;
        this.shipment = shipment;
    }

    public OutboundDeliverySent(String destination, WarehouseWorker employeeConfirming, LocalDate shipment) {
        this(null, destination, employeeConfirming, shipment);
    }

    public OutboundDeliverySent(OutboundDeliveryNotSent notSent, WarehouseWorker employeeConfirming, LocalDate shipment){
        this(notSent.destination, employeeConfirming, shipment);
    }

    protected WarehouseWorker employeeConfirming;
    protected LocalDate shipment;

    @Override
    public Object[] getFormatToRow() {
        return new Object[]{packageId, shipment, destination, status};
    }

    @Override
    public String getDeliveryDateString() {
        return shipment.toString();
    }

    @Override
    public String getDetailsFormatted() {
        return "<html>DETAILS:<br/>"+
                "Type: Outbound sent<br/>"+
                "Weight: "+calculateWeight()+"<br/>"+
                "Status: "+status+"<br/>"+
                "Delivery confirmed by: "+employeeConfirming.login+"<br/>"+
                "Sent on: "+shipment+"</html>";
    }
}
