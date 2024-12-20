import java.time.LocalDate;

public abstract class InboundDelivery extends Delivery {
    public InboundDelivery(String description, String origin) {
        super(description);
        this.origin = origin;
    }

    public InboundDelivery(String origin) {
        this(null, origin);
    }

    protected String origin;
    protected InboundDeliveryStatus status;

    public void setStatus(InboundDeliveryStatus status) {
        this.status = status;
    }
}
