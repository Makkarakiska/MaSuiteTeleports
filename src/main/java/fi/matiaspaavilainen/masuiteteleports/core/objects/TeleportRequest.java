package fi.matiaspaavilainen.masuiteteleports.core.objects;

import fi.matiaspaavilainen.masuiteteleports.core.handlers.TeleportHandler;

import java.time.Instant;
import java.util.UUID;

public class TeleportRequest {

    private UUID sender;
    private UUID receiver;
    private Thread scheduler;

    private TeleportType type;
    private long created;
    private long ends;

    /**
     * An empty constructor for {@link TeleportRequest}
     */
    public TeleportRequest() {
    }

    /**
     * A constructor for {@link TeleportRequest}
     *
     * @param sender   unique id of the sender
     * @param receiver unique id of the receiver
     * @param type     request {@link TeleportRequest}
     * @param ends     when the request ends
     */
    public TeleportRequest(UUID sender, UUID receiver, TeleportType type, long ends) {
        this.sender = sender;
        this.receiver = receiver;
        this.type = type;
        this.created = Instant.now().toEpochMilli();
        this.ends = Instant.now().plusMillis(3000).toEpochMilli();
    }

    /**
     * Create request with given params
     *
     * @return created request
     */
    public TeleportRequest create() {
        TeleportHandler.requests.add(this);
        this.scheduler = new Thread(() -> {
            System.out.println(this.created - this.ends);
            if(this.ends - this.created  <= 0){
                this.cancel();
                System.out.println("Thread cancelled!");
            }

        });
        this.scheduler.start();

        return this;
    }

    public void cancel(){
        this.scheduler.interrupt();
    }

    public UUID getSender() {
        return sender;
    }

    public void setSender(UUID sender) {
        this.sender = sender;
    }

    public UUID getReceiver() {
        return receiver;
    }

    public void setReceiver(UUID receiver) {
        this.receiver = receiver;
    }

    public TeleportType getType() {
        return type;
    }

    public void setType(TeleportType type) {
        this.type = type;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getEnds() {
        return ends;
    }

    public void setEnds(long ends) {
        this.ends = ends;
    }
}

