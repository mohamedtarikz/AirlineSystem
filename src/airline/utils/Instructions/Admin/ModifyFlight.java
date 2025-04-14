package airline.utils.Instructions.Admin;
import airline.User.Admin;
import airline.utils.IOSystem.AdminOptions;

import java.time.LocalDateTime;

public class ModifyFlight extends AdminInstruction{
    private final String flightId;
    private final LocalDateTime departureTime;

    public ModifyFlight(String flightId, LocalDateTime departureTime, Admin admin) {
        super(AdminOptions.MODIFY_FLIGHT, admin);
        this.flightId = flightId;
        this.departureTime = departureTime;
    }

    public String getFlightId() {
        return flightId;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }
}
