public abstract class OutboundDelivery extends Delivery {
    public OutboundDelivery(String description, String destination) {
        super(description);
        this.destination = destination;
    }

    public OutboundDelivery(String destination) {
        this(null, destination);
    }

    protected String destination;
    protected OutboundDeliveryStatus status;

    public void setStatus(OutboundDeliveryStatus status){
        this.status = status;
    }
}
