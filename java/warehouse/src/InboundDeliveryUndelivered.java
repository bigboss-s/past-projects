import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class InboundDeliveryUndelivered extends InboundDelivery {
    public InboundDeliveryUndelivered(String description, String origin, LocalDate expectedDelivery) {
        super(description, origin);
        this.expectedDelivery = expectedDelivery;
    }

    public InboundDeliveryUndelivered(String origin, LocalDate expectedDelivery) {
        this(null, origin, expectedDelivery);
    }

    private LocalDate expectedDelivery;
    private boolean isRejected;
    private RejectionForm rejectionForm;

    public void reject(WarehouseWorker worker, String reason, int damage) throws Exception {
        rejectionForm = new RejectionForm(worker, reason, damage, LocalDate.now());
        isRejected = true;
    }

    @Override
    public Object[] getFormatToRow() {
        return new Object[]{packageId, expectedDelivery, origin, status};
    }

    @Override
    public String getDeliveryDateString() {
        return expectedDelivery.toString();
    }

    @Override
    public String getDetailsFormatted() {
        return "<html>DETAILS:<br/>"+
                "Type: Inbound Undelivered<br/>"+
                "Weight: "+calculateWeight()+"<br/>"+
                "Status: "+status+"<br/>"+
                "Is rejected?: "+isRejected+"<br/>"+
                (isRejected ? getRejectionFormString() : "Expected delivery on: "+expectedDelivery)+"</html>";
    }

    public boolean isRejected() {
        return isRejected;
    }

    private String getRejectionFormString() {
        return rejectionForm.getFormattedRejection();
    }

    private class RejectionForm implements Serializable {
        private WarehouseWorker author;
        private String reason;
        private int damage;
        private LocalDate date;

        public RejectionForm(WarehouseWorker author, String reason, int damage, LocalDate date) throws Exception {
            if (damage < 1 || damage > 10 ){
                throw new Exception("Enter damage in a scale 1 to 10");
            }
            this.author = author;
            this.reason = reason;
            this.damage = damage;
            this.date = date;
        }

        public String getFormattedRejection(){
            return "<html>REJECTION FORM:<br/>"+
                    "Author: "+author.name + " ("+author.login+")<br/>"+
                    "Reason: "+reason+"<br/>"+
                    "Damage (1 to 10): "+damage+"<br/>"+
                    "Rejected on: "+date.toString()+"</html>";
        }


    }

}
