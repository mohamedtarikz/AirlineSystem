package airline.utils.Instructions.Passenger;
import airline.User.Passenger;
import airline.utils.IOSystem.PassengerOptions;

public class CancelFlight extends PassengerInstruction {
    private final String flightId;

    public CancelFlight(String flightNumber, Passenger passenger) {
        super(PassengerOptions.CANCEL_FLIGHT, passenger);
        this.flightId = flightNumber;
    }

    public String getFlightId() {
        return flightId;
    }
}
