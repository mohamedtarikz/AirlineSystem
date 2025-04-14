package airline.utils.Instructions.Admin;
import airline.User.Admin;
import airline.utils.IOSystem.AdminOptions;

public class RemoveFlight extends AdminInstruction {
    private final String flightId;

    public RemoveFlight(String flightNumber, Admin admin) {
        super(AdminOptions.REMOVE_FLIGHT, admin);
        this.flightId = flightNumber;
    }

    public String getFlightId() {
        return flightId;
    }
}
