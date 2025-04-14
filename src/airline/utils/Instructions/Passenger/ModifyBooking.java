package airline.utils.Instructions.Passenger;
import airline.User.Passenger;
import airline.utils.IOSystem.PassengerOptions;

public class ModifyBooking extends PassengerInstruction {
    private final String flightId;
    private final Integer newNumSeats;

    public ModifyBooking(String bookingID, Integer flightNumber, Passenger passenger) {
        super(PassengerOptions.MODIFY_BOOKING, passenger);
        this.flightId = bookingID;
        this.newNumSeats = flightNumber;
    }

    public String getFlightId() {
        return flightId;
    }

    public Integer getNewNumSeats() {
        return newNumSeats;
    }
}
