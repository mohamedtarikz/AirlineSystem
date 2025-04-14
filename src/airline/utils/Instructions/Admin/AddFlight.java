package airline.utils.Instructions.Admin;
import airline.User.Admin;
import airline.utils.Flight.FlightSpecs;
import airline.utils.IOSystem.AdminOptions;

public class AddFlight extends AdminInstruction {
    private final FlightSpecs flightSpecs;

    public AddFlight(FlightSpecs flightSpecs, Admin admin) {
        super(AdminOptions.ADD_FLIGHT, admin);
        this.flightSpecs = flightSpecs;
    }

    public FlightSpecs getFlightSpecs() {
        return flightSpecs;
    }

}
