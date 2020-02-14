package dev.masa.masuiteteleports.core.objects;

import lombok.*;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.UUID;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class TeleportRequest {

    @NonNull
    private UUID sender;
    @NonNull
    private UUID receiver;
    @NonNull
    private TeleportRequestType type;
    private ScheduledTask timer;

    /**
     * Get the sender as player
     *
     * @return returns sender as player or null
     */
    public ProxiedPlayer getSenderAsPlayer() {
        return ProxyServer.getInstance().getPlayer(this.sender);
    }

    /**
     * Get the receiver as player
     *
     * @return returns receiver as player or null
     */
    public ProxiedPlayer getReceiverAsPlayer() {
        return ProxyServer.getInstance().getPlayer(this.receiver);
    }
}
